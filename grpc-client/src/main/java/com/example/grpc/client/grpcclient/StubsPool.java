package com.example.grpc.client.grpcclient;
import com.example.grpc.server.grpcserver.MatrixMultServiceGrpc.MatrixMultServiceBlockingStub;
import com.example.grpc.server.grpcserver.MatrixMultServiceGrpc.MatrixMultServiceStub;

import java.util.LinkedList;
import java.util.Queue;

public class StubsPool {
    Queue<MatrixMultServiceBlockingStub> queue;
    public StubsPool() {
        this.queue = new LinkedList<>();
    }
    public MatrixMultServiceBlockingStub getNext() {
        return this.queue.remove();
    }
    public void addStub(MatrixMultServiceBlockingStub stubby) {
        this.queue.add(stubby);
    }
}