package com.arun.seda.message;

import com.arun.seda.event.Event;

import java.util.Collection;

/**
 * This extension of {@link Message} represents multiple source {@link Event}s.
 * 
 */
public interface EventCollection extends Message {

    /**
     * @return the collected {@link Event}s.
     */
    Collection<Event> getEvents();

}
