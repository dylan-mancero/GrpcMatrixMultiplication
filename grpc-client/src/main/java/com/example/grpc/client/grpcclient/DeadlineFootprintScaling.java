package com.example.grpc.client.grpcclient;

public class DeadlineFootprintScaling {
    private long start;
    private long endTime;

    public DeadlineFootprintScaling() {
    }

    public void setStart() {
        start = System.currentTimeMillis();
    }

    public void setEndTime() {
        endTime = System.currentTimeMillis();
    }

    public int calcNumOfServers(int numBlockCalls, int DEADLINE) {

        long footprint = endTime-start;
        try {
            return (int) Math.ceil(((float)footprint * (float)numBlockCalls) / (float)DEADLINE);
        }finally {
            this.start = 0;
            this.endTime = 0;
        }
    }
}
