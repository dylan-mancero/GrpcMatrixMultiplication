package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MMRequest;
import com.example.grpc.server.grpcserver.MMReply;
import com.example.grpc.server.grpcserver.MatrixMultServiceGrpc;
import com.example.grpc.server.grpcserver.Matrix;
import com.example.grpc.server.grpcserver.MatrixMultServiceGrpc.MatrixMultServiceBlockingStub;
import com.example.grpc.server.grpcserver.MatrixMultServiceGrpc.MatrixMultServiceStub;
import com.example.grpc.server.grpcserver.Row;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

@Service
public class GRPCClientService {
	private int DEADLINE;
    public String ping() {
        	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();        
		PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);        
		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());        
		channel.shutdown();        
		return helloResponse.getPong();
    }
    public String multiplyMatrix(int[][]A, int[][]B, int deadline) {
    	this.DEADLINE = deadline;
    	//all servers
		ManagedChannel channel1 = ManagedChannelBuilder.forAddress("3.85.133.217", 9090).usePlaintext().build();
		ManagedChannel channel2 = ManagedChannelBuilder.forAddress("54.91.241.179", 9090).usePlaintext().build();
		ManagedChannel channel3 = ManagedChannelBuilder.forAddress("52.87.199.35", 9090).usePlaintext().build();
		ManagedChannel channel4 = ManagedChannelBuilder.forAddress("54.209.248.91", 9090).usePlaintext().build();
		ManagedChannel channel5 = ManagedChannelBuilder.forAddress("54.81.164.162", 9090).usePlaintext().build();
		ManagedChannel channel6 = ManagedChannelBuilder.forAddress("54.236.236.71", 9090).usePlaintext().build();
		ManagedChannel channel7 = ManagedChannelBuilder.forAddress("3.90.87.185", 9090).usePlaintext().build();
		ManagedChannel channel8 = ManagedChannelBuilder.forAddress("54.88.166.86", 9090).usePlaintext().build();

		MatrixMultServiceBlockingStub stub = MatrixMultServiceGrpc.newBlockingStub(channel1);
		MatrixMultServiceBlockingStub stub2 = MatrixMultServiceGrpc.newBlockingStub(channel2);
		MatrixMultServiceBlockingStub stub3 = MatrixMultServiceGrpc.newBlockingStub(channel3);
		MatrixMultServiceBlockingStub stub4 = MatrixMultServiceGrpc.newBlockingStub(channel4);
		MatrixMultServiceBlockingStub stub5 = MatrixMultServiceGrpc.newBlockingStub(channel5);
		MatrixMultServiceBlockingStub stub6 = MatrixMultServiceGrpc.newBlockingStub(channel6);
		MatrixMultServiceBlockingStub stub7 = MatrixMultServiceGrpc.newBlockingStub(channel7);
		MatrixMultServiceBlockingStub stub8 = MatrixMultServiceGrpc.newBlockingStub(channel8);

		StubsPool stubsPool = new StubsPool();
		Stack<MatrixMultServiceBlockingStub> allStubs = new Stack<>();
		DeadlineFootprintScaling deadlineFootprintScaling = new DeadlineFootprintScaling();

		allStubs.add(stub);allStubs.add(stub2);allStubs.add(stub3);allStubs.add(stub4);allStubs.add(stub5);
		allStubs.add(stub6);allStubs.add(stub7);allStubs.add(stub8);
		stubsPool.addStub(stub);

		int NUM_SERVER = 1;
		int SIZE = A.length;
		int numBlockCalls = 10;

		int[][] A11 = new int[SIZE / 2][SIZE / 2];
		int[][] A12 = new int[SIZE / 2][SIZE / 2];
		int[][] A21 = new int[SIZE / 2][SIZE / 2];
		int[][] A22 = new int[SIZE / 2][SIZE / 2];
		int[][] B11 = new int[SIZE / 2][SIZE / 2];
		int[][] B12 = new int[SIZE / 2][SIZE / 2];
		int[][] B21 = new int[SIZE / 2][SIZE / 2];
		int[][] B22 = new int[SIZE / 2][SIZE / 2];

		for (int i = 0; i < SIZE / 2; i++) {
			for (int j = 0; j < SIZE / 2; j++) {

				A11[i][j] = A[i][j]; // top left
				A12[i][j] = A[i][j + SIZE / 2]; // top right
				A21[i][j] = A[i + SIZE / 2][j]; // bottom left
				A22[i][j] = A[i + SIZE / 2][j + SIZE / 2]; // bottom right

				B11[i][j] = B[i][j]; // top left
				B12[i][j] = B[i][j + SIZE / 2]; // top right
				B21[i][j] = B[i + SIZE / 2][j]; // bottom left
				B22[i][j] = B[i + SIZE / 2][j + SIZE / 2]; // bottom right
			}
		}

		Matrix QA11 = MatrixPool.makeProtoFrom2D(A11);
		Matrix QA12 = MatrixPool.makeProtoFrom2D(A12);
		Matrix QA21 = MatrixPool.makeProtoFrom2D(A21);
		Matrix QA22 = MatrixPool.makeProtoFrom2D(A22);
		Matrix QB11 = MatrixPool.makeProtoFrom2D(B11);
		Matrix QB12 = MatrixPool.makeProtoFrom2D(B12);
		Matrix QB21 = MatrixPool.makeProtoFrom2D(B21);
		Matrix QB22 = MatrixPool.makeProtoFrom2D(B22);

		//START OF FOOTPRINT TRACKING
		deadlineFootprintScaling.setStart();

		MMReply M1C1 = stubsPool.getNext().multBlock(MMRequest.newBuilder().setMatrixA(QA11).setMatrixB(QB11).build());

		deadlineFootprintScaling.setEndTime();

		//GETS THE NUMBER OF SERVERS NEEDED
		int numberOfServersNeeded = deadlineFootprintScaling.calcNumOfServers(numBlockCalls,DEADLINE);

		System.out.println(numberOfServersNeeded + " Servers are needed");
		if (numberOfServersNeeded>=8) {
			for (MatrixMultServiceBlockingStub current: allStubs)
				stubsPool.addStub(current);
		}
		else if (numberOfServersNeeded>1) {
			for (int i = 0; i<numberOfServersNeeded; i++) {
				stubsPool.addStub(allStubs.pop());
			}
		}

		MMReply M2C1 = stubsPool.getNext().multBlock(MMRequest.newBuilder().setMatrixA(QA12).setMatrixB(QB21).build());
		MMReply M1C2 = stubsPool.getNext().multBlock(MMRequest.newBuilder().setMatrixA(QA11).setMatrixB(QB12).build());
		MMReply M2C2 = stubsPool.getNext().multBlock(MMRequest.newBuilder().setMatrixA(QA12).setMatrixB(QB22).build());
		MMReply M1C3 = stubsPool.getNext().multBlock(MMRequest.newBuilder().setMatrixA(QA21).setMatrixB(QB11).build());
		MMReply M2C3 = stubsPool.getNext().multBlock(MMRequest.newBuilder().setMatrixA(QA22).setMatrixB(QB21).build());
		MMReply M1C4 = stubsPool.getNext().multBlock(MMRequest.newBuilder().setMatrixA(QA21).setMatrixB(QB12).build());
		MMReply M2C4 = stubsPool.getNext().multBlock(MMRequest.newBuilder().setMatrixA(QA22).setMatrixB(QB22).build());

		MMReply C1 = stubsPool.getNext()
				.addBlock(MMRequest.newBuilder().setMatrixA(M1C1.getMatrixC()).setMatrixB(M2C1.getMatrixC()).build());
		MMReply C2 = stubsPool.getNext()
				.addBlock(MMRequest.newBuilder().setMatrixA(M1C2.getMatrixC()).setMatrixB(M2C2.getMatrixC()).build());
		MMReply C3 = stubsPool.getNext()
				.addBlock(MMRequest.newBuilder().setMatrixA(M1C3.getMatrixC()).setMatrixB(M2C3.getMatrixC()).build());
		MMReply C4 = stubsPool.getNext()
				.addBlock(MMRequest.newBuilder().setMatrixA(M1C4.getMatrixC()).setMatrixB(M2C4.getMatrixC()).build());

		int[][] C1array = MatrixPool.make2DFromProto(C1.getMatrixC());
		int[][] C2array = MatrixPool.make2DFromProto(C2.getMatrixC());
		int[][] C3array = MatrixPool.make2DFromProto(C3.getMatrixC());
		int[][] C4array = MatrixPool.make2DFromProto(C4.getMatrixC());

		int[][]result = MatrixPool.joinFromQuarters(C1array, C2array, C3array, C4array);
		return Arrays.deepToString(result);
	}
}
