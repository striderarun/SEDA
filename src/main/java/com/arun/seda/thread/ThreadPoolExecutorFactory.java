package com.arun.seda.thread;

import java.util.concurrent.ThreadPoolExecutor;

import com.arun.seda.stages.Stage;
import com.arun.seda.core.Dispatcher;
import com.arun.seda.core.RuntimeStage;

/**
 * Creates a custom {@link ThreadPoolExecutor} for a given {@link Stage} and contextual {@link Dispatcher}.
 * 
 */
public interface ThreadPoolExecutorFactory {

    ThreadPoolExecutor create(Dispatcher dispatcher, RuntimeStage runtimeStage);

}
