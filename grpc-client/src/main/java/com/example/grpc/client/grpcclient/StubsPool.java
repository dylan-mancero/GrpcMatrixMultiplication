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
        MatrixMultServiceBlockingStub head = this.queue.remove();
        try{
            return head;
        }finally {
            this.queue.add(head);
        }
    }
    public void addStub(MatrixMultServiceBlockingStub stubby) {
        System.out.println("Servers is being added");
        this.queue.add(stubby);
    }
}