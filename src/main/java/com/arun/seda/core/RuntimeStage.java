package com.arun.seda.core;

import java.util.concurrent.ThreadPoolExecutor;

import com.arun.seda.controller.StageController;
import com.arun.seda.core.impl.DispatcherImpl;
import com.arun.seda.event.EventHandler;
import com.arun.seda.stages.Stage;

/**
 * {@link RuntimeStage} represents the {@link DispatcherImpl} internal configuration metadata to route, track stats,
 * execute.
 * 
 * @see {@link Stage}
 * @see {@link EventHandler}
 * @see {@link ThreadPoolExecutor}
 *
 */
public class RuntimeStage {

    private final Stage stage;
    private final String context;
    private final String id;
    @SuppressWarnings("rawtypes")
    private final EventHandler eventHandler;


    private final StageController controller;

    public RuntimeStage(Stage stage) {
        this.stage = stage;
        this.context = stage.getContext();
        this.id = stage.getId();
        this.eventHandler = stage.getEventHandler();
        this.controller = stage.getController();
    }

    public Stage getStage() {
        return this.stage;
    }

    public String getId() {
        return this.id;
    }

    public String getContext() {
        return this.context;
    }

    @SuppressWarnings("rawtypes")
    public EventHandler getEventHandler() {
        return this.eventHandler;
    }

    public StageController getController() {
        return this.controller;
    }

}
