package com.arun.seda.core;

import com.arun.seda.event.EventHandler;

/**
 * Declares {@link Dispatcher} awareness for an {@link EventHandler} mainly.
 *
 */
public interface DispatcherAware {

    Dispatcher getDispatcher();

    void setDispatcher(Dispatcher dispatcher);

}
