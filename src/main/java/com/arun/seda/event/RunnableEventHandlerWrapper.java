package com.arun.seda.event;

import java.util.ArrayList;
import java.util.Collection;

import com.arun.seda.stages.RoutingOutcome;
import com.arun.seda.core.Dispatcher;
import com.arun.seda.core.RuntimeStage;
import com.arun.seda.message.JoinHandler;
import com.arun.seda.message.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunnableEventHandlerWrapper implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunnableEventHandlerWrapper.class);
    private final Dispatcher dispatcher;
    private final RuntimeStage runtimeStage;
    private final Message message;

    @SuppressWarnings("rawtypes")
    private final EventHandler eventHandler;
    private final Event event;

    public RunnableEventHandlerWrapper(Dispatcher dispatcher, RuntimeStage runtimeStage, Event event) {
        this.dispatcher = dispatcher;
        this.runtimeStage = runtimeStage;
        this.message = event.getMessage();
        this.eventHandler = runtimeStage.getEventHandler();
        this.event = event;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        long time = 0;
        time = System.nanoTime();

        Message returnMessage = null;
        RoutingOutcome routingOutcome = null;
        try {
            // execution
            routingOutcome = this.eventHandler.execute(this.message);
            time = System.nanoTime();
            if (routingOutcome != null) {
                // get the returnMessage out of the outcome...
                returnMessage = routingOutcome.getReturnMessage();

                // extract the events an fire them up!
                if (!routingOutcome.isEmpty()) {
                    Collection<Event> outcomeEvents = routingOutcome.getEvents();
                    if (routingOutcome.hasJoinEvent()) {
                        // configures an outcomeEvents list and attaches it to a JoinHandler firing the joinEvent.
                        outcomeEvents = this.createWrappedJoinEventOf(outcomeEvents, routingOutcome.getJoinEvent());
                    }

                    // real firing here...
                    this.fireEvents(outcomeEvents);
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Error ocurred while executing the EventHandler for stage [" + this.runtimeStage.getId() + "]", t);
        } finally {
            JoinHandler joinHandler = this.event.getJoinHandler();
            if (joinHandler != null) {
                joinHandler.finished(this.event, returnMessage);
                LOGGER.debug("Firing up joinHandler for stage [" + this.runtimeStage.getId() + "]");
            }
        }
    }

    private Collection<Event> createWrappedJoinEventOf(Collection<Event> outcomeEvents, Event targetEvent) {
        Collection<Event> joinEventWrappedCollection = new ArrayList<Event>(outcomeEvents.size());
        JoinHandler joinHandler = new JoinHandler(this.dispatcher, targetEvent);
        for (Event event : outcomeEvents) {
            // create a JoinEvent wrapping the JoinHandler...
            joinEventWrappedCollection.add(new Event(event, joinHandler));

            // register child, if not registered it could leak a join thread
            joinHandler.register(this.eventHandler);
        }
        return joinEventWrappedCollection;
    }

    private void fireEvents(Collection<Event> outcomeEvents) {
        for (Event e : outcomeEvents) {
            this.dispatcher.execute(e);
        }
    }

    public Event getEvent() {
        return this.event;
    }

}
