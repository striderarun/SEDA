package com.arun.seda.controller;

import com.arun.seda.event.Event;
import com.arun.seda.stages.Stage;
import com.arun.seda.core.DispatcherAware;
import com.arun.seda.core.Lifecycle;
import com.arun.seda.core.RuntimeStage;
import com.arun.seda.message.Message;

/**
 * The {@link StageController} is the responsible for the execution of the given {@link Event}, in this case, only the
 * {@link Message} because there is an instance of this class per defined {@link Stage}.
 * 
 */
public interface StageController extends DispatcherAware, Lifecycle {

    /**
     * Execution handler for the given stage.
     * 
     * @param event
     */
    void execute(Event event);

    /**
     * Runtime configuration of the running {@link Stage} ({@link RuntimeStage}).
     * 
     * @param runtimeStage
     */
    void setRuntimeStage(RuntimeStage runtimeStage);

}
