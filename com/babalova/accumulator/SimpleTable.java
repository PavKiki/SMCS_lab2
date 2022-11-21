package com.babalova.accumulator;

public class SimpleTable {
    private double enterChannelQueue;
    private double exitChannelQueue;
    private double enterServerQueue;
    private double exitServerQueue;

    private double startServerProcessing;
    private double startChannelProcessing;

    public SimpleTable() {
    }

    public SimpleTable(double enterChannelQueue, double exitChannelQueue, double enterServerQueue,
            double exitServerQueue, double startServerProcessing, double startChannelProcessing) {
        this.enterChannelQueue = enterChannelQueue;
        this.exitChannelQueue = exitChannelQueue;
        this.enterServerQueue = enterServerQueue;
        this.exitServerQueue = exitServerQueue;
        this.startServerProcessing = startServerProcessing;
        this.startChannelProcessing = startChannelProcessing;
    }

    public double getEnterChannelQueue() {
        return enterChannelQueue;
    }

    public void setEnterChannelQueue(double enterChannelQueue) {
        this.enterChannelQueue = enterChannelQueue;
    }

    public double getExitChannelQueue() {
        return exitChannelQueue;
    }

    public void setExitChannelQueue(double exitChannelQueue) {
        this.exitChannelQueue = exitChannelQueue;
    }

    public double getEnterServerQueue() {
        return enterServerQueue;
    }

    public void setEnterServerQueue(double enterServerQueue) {
        this.enterServerQueue = enterServerQueue;
    }

    public double getExitServerQueue() {
        return exitServerQueue;
    }

    public void setExitServerQueue(double exitServerQueue) {
        this.exitServerQueue = exitServerQueue;
    }

    public double getStartServerProcessing() {
        return startServerProcessing;
    }

    public void setStartServerProcessing(double startServerProcessing) {
        this.startServerProcessing = startServerProcessing;
    }

    public double getStartChannelProcessing() {
        return startChannelProcessing;
    }

    public void setStartChannelProcessing(double startChannelProcessing) {
        this.startChannelProcessing = startChannelProcessing;
    }

    
}
