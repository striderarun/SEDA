package com.arun.seda.thread;

import java.util.concurrent.ThreadPoolExecutor;

import com.arun.seda.stages.Stage;
import com.arun.seda.core.Dispatcher;
import com.arun.seda.core.RuntimeStage;

/**
 * Default {@link ThreadPoolExecutorFactory} that creates a {@link DispatcherThreadPoolExecutor} configured according to
 * the specified {@link Stage} and {@link Dispatcher}.
 * 
 */
public class DefaultThreadPoolExecutorFactory
    implements ThreadPoolExecutorFactory {

    public ThreadPoolExecutor create(Dispatcher dispatcher, RuntimeStage runtimeStage) {
        Stage stage = runtimeStage.getStage();
        ThreadPoolExecutor executor;
        if (stage.getMaxQueueSize() > 0) {
            executor = new DispatcherThreadPoolExecutor(dispatcher.getContext() + "_" + stage.getId() + "_ST#",
                stage.getCoreThreads(), stage.getMaxThreads(), stage.getMaxQueueSize(), new LoadSheddingPolicy(runtimeStage));
        } else {
            executor = new DispatcherThreadPoolExecutor(dispatcher.getContext() + "_" + stage.getId() + "_ST#",
                stage.getCoreThreads(), stage.getMaxThreads());
        }
        return executor;
    }
}
