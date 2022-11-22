package com.babalova.accumulator;

import java.util.*;

public class ChainStorage {
    private List<Chain> current = new ArrayList<Chain>();
    private List<Chain> future = new ArrayList<Chain>();
    
    public ChainStorage() {
    }

    public List<Chain> getCurrent() {
        return current;
    }

    public List<Chain> getFuture() {
        return future;
    }

    public void addToCurrent(Chain chain) {
        this.current.add(chain);
    }

    public void addToFuture(Chain chain) {
        this.future.add(chain);
    }

    // public void replaceToFuture(Chain chain) {
    //     int index;
    //     for (index = 0; index < future.size(); index++) {
    //         if ((future.get(index).equals(chain))
    //         // if (future.get(index).getTranscatNum() == chain.getTranscatNum()) {
    //         //     if (future.get(index).getTime() > chain.getTime()) {
    //         //         future.remove(index);
    //         //         this.addToFuture(chain);
    //         //     }
    //         //     return;
    //         // }
    //     }
    //     this.addToFuture(chain);
    // }

    public boolean isThereThisChainInCurrent(Chain chain) {
        boolean ans = false;
        for (Chain ch: current) {
            ans = ans || chain.equals(ch);
        }
        return ans;
    }

    public boolean isThereThisChainInFuture(Chain chain) {
        boolean ans = false;
        for (Chain ch: future) {
            ans = ans || chain.equals(ch);
        }
        return ans;
    }
}
