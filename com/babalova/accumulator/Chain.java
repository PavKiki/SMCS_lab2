package com.babalova.accumulator;

public class Chain {
    private int transcatNum;
    private int priority;
    private double time;
    private int currentBlock;
    private int nextBlock;
    
    public Chain() {
    }

    public int getTranscatNum() {
        return transcatNum;
    }

    public void setTranscatNum(int transcatNum) {
        this.transcatNum = transcatNum;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(int currentBlock) {
        this.currentBlock = currentBlock;
    }

    public int getNextBlock() {
        return nextBlock;
    }

    public void setNextBlock(int nextBlock) {
        this.nextBlock = nextBlock;
    }

    @Override
    public String toString() {
        return "[" + transcatNum + "," + priority + "," + String.format("%.1f", time) + "," + currentBlock + "," + nextBlock + "]";
    }

    @Override
    public Chain clone() {
        Chain chain = new Chain();
        chain.setCurrentBlock(currentBlock);
        chain.setNextBlock(nextBlock);
        chain.setPriority(priority);
        chain.setTime(time);
        chain.setTranscatNum(transcatNum);
        return chain;
    }

    public Chain cloneInPast(int currentBlock, int nextBlock) {
        Chain chainInPast = this.clone();
        chainInPast.setCurrentBlock(currentBlock);
        chainInPast.setNextBlock(nextBlock);
        return chainInPast;
    }

    public Chain cloneInPast(int currentBlock, int nextBlock, double time) {
        Chain chainInPast = this.clone();
        chainInPast.setCurrentBlock(currentBlock);
        chainInPast.setNextBlock(nextBlock);
        chainInPast.setTime(time);
        return chainInPast;
    }

    public boolean equals(Chain other) {
        boolean ans = (transcatNum == other.transcatNum) && (priority == other.priority) && ((float)time == (float)other.time) && 
            (currentBlock == other.currentBlock) && (nextBlock == other.nextBlock);
        return ans;
    }
}
