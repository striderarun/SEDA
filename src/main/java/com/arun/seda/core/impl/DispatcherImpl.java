package com.arun.seda.core.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import com.arun.seda.core.InvalidStageException;
import com.arun.seda.stages.Stage;
import com.arun.seda.stages.StageAware;
import com.arun.seda.controller.StageController;
import com.arun.seda.core.Dispatcher;
import com.arun.seda.core.DispatcherAware;
import com.arun.seda.core.RuntimeStage;
import com.arun.seda.event.Event;
import com.arun.seda.event.EventHandler;
import com.arun.seda.message.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main {@link Dispatcher} implementation.<br/>
 *
 */
public class DispatcherImpl implements Dispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherImpl.class);

    // stages map
    private Map<String, RuntimeStage> stagesMap;

    private String context;
    private List<Stage> stages;

    // control variables
    private volatile boolean started = false;
    private volatile boolean shutdownRequired = false;

    private boolean throwExceptionOnInvalidStage = true;

    public boolean execute(String stage, Message message) throws InvalidStageException {
        return this.execute(new Event(stage, message));
    }

    public boolean execute(Event event) throws InvalidStageException {
        final String stage = event.getStage();

        if (this.shutdownRequired) {
            LOGGER.info("Stage execution rejected for stage [" + stage + "], shutdown required!");
            return false;
        }

        RuntimeStage runtimeStage = this.stagesMap.get(stage);
        if (runtimeStage == null) {
            if (this.throwExceptionOnInvalidStage) {
                throw new InvalidStageException(stage, "Stage is undefined!");
            } else {
                return false;
            }
        }

        // delegate the execution to the underlying stage-controller
        runtimeStage.getController().execute(event);
        return true;
    }

    public void start() {

        this.stagesMap = new LinkedHashMap<>(this.stages.size());

        for (Stage stage : this.stages) {
            final String stageId = stage.getId();
            final RuntimeStage runtimeStage = new RuntimeStage(stage);

            // minimal validation
            EventHandler<?> eventHandler = stage.getEventHandler();
            if (eventHandler == null) {
                throw new RuntimeException(String.format("EventHandler cannot be null, invalid configuration for stage %s %s", stage.getId(), this.context));
            }

            // dependency injection
            if (eventHandler instanceof StageAware) {
                ((StageAware) eventHandler).setStage(stage);
            }
            if (eventHandler instanceof DispatcherAware) {
                ((DispatcherAware) eventHandler).setDispatcher(this);
            }

            // start the stage-controller
            StageController stageController = runtimeStage.getController();
            stageController.setDispatcher(this);
            stageController.setRuntimeStage(runtimeStage);
            stageController.start();

            this.stagesMap.put(stageId, runtimeStage);
        }

        this.started = true;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public List<Stage> getStages() {
        return Collections.unmodifiableList(this.stages);
    }

    public void stop() {
        this.shutdownRequired = true;

        // shutdown the threadpools...
        for (Entry<String, RuntimeStage> entry : this.stagesMap.entrySet()) {
            String stage = entry.getKey();
            RuntimeStage runtimeStage = entry.getValue();

            runtimeStage.getController().stop();

            LOGGER.info("Stopping stage-controller for [" + stage + "]");
        }

        this.started = false;
        this.shutdownRequired = false;
    }

    @Override
    public boolean isRunning() {
        return this.started && !this.shutdownRequired;
    }

}
