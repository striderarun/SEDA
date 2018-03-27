package com.arun.seda.core;

/**
 * Defines the lifecycle of a daemon object.
 *
 */
public interface Lifecycle {

    /**
     * Starts the object implementation's lifecycle.
     */
    void start();

    /**
     * Stops the object implementation's lifecycle.
     */
    void stop();

    /**
     * @return is the daemon running now?
     */
    boolean isRunning();

}
