package com.example.vincent.babynursinglayouts.models;

/**
 * Created by vincent on 3/14/17.
 */

public class PumpingEntry {

    int totalPumps;
    float averageWeight, totalWeight;

    public PumpingEntry(int totalPumps, float averageWeight, float totalWeight){
        this.totalPumps = totalPumps;
        this.averageWeight = averageWeight;
        this.totalWeight = totalWeight;
    }


    public void setTotalPumps(int totalPumps) {
        this.totalPumps = totalPumps;
    }

    public void setAverageWeight(float averageWeight) {
        this.averageWeight = averageWeight;
    }

    public void setTotalWeight(float totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getTotalPumps() {
        return totalPumps;
    }

    public float getAverageWeight() {
        return averageWeight;
    }

    public float getTotalWeight() {
        return totalWeight;
    }


}
