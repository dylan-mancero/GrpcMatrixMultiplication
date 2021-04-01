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

@Service
public class GRPCClientService {
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
    public String multiplyMatrix(int[][]A, int[][]B) {
		ManagedChannel channel1 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel2 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel3 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel4 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel5 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel6 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel7 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
		ManagedChannel channel8 = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();


		MatrixMultServiceBlockingStub stub = MatrixMultServiceGrpc.newBlockingStub(channel1);
		MatrixMultServiceBlockingStub stub2 = MatrixMultServiceGrpc.newBlockingStub(channel2);
		MatrixMultServiceBlockingStub stub3 = MatrixMultServiceGrpc.newBlockingStub(channel3);
		MatrixMultServiceBlockingStub stub4 = MatrixMultServiceGrpc.newBlockingStub(channel4);
		MatrixMultServiceBlockingStub stub5 = MatrixMultServiceGrpc.newBlockingStub(channel5);
		MatrixMultServiceBlockingStub stub6 = MatrixMultServiceGrpc.newBlockingStub(channel6);
		MatrixMultServiceBlockingStub stub7 = MatrixMultServiceGrpc.newBlockingStub(channel7);
		MatrixMultServiceBlockingStub stub8 = MatrixMultServiceGrpc.newBlockingStub(channel8);

		StubsPool stubsPool = new StubsPool();
		List<MatrixMultServiceBlockingStub> allStubs = new ArrayList<>();
		int NUM_SERVER = 0;
		int[][] result = recursiveMult(A, B, stubsPool, allStubs, NUM_SERVER,stub);
		return Arrays.deepToString(result);
	}
	private int[][] recursiveMult(int[][] A, int[][] B, StubsPool stubsPool,
								 List<MatrixMultServiceBlockingStub> allStubs, int num_server, MatrixMultServiceBlockingStub stub) {
    	int SIZE = A.length;
    	if (SIZE > 4) {
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
			int[][] M1C1d = recursiveMult(A11, B11, stubsPool, allStubs, num_server++,stub);
			int[][] M2C1d = recursiveMult(A12, B21, stubsPool, allStubs, num_server++,stub);

			int[][] M1C2d = recursiveMult(A11, B12, stubsPool, allStubs,num_server++,stub);
			int[][] M2C2d = recursiveMult(A12, B22,stubsPool, allStubs,num_server++,stub);

			int[][] M1C3d = recursiveMult(A21, B11, stubsPool, allStubs,num_server++,stub);
			int[][] M2C3d = recursiveMult(A22, B21,stubsPool, allStubs,num_server++,stub);

			int[][] M1C4d = recursiveMult(A21, B12,stubsPool, allStubs,num_server++,stub);
			int[][] M2C4d = recursiveMult(A22, B22,stubsPool, allStubs,num_server++,stub);

			Matrix M1C1 =  MatrixPool.makeProtoFrom2D(M1C1d);
			Matrix M1C2 =  MatrixPool.makeProtoFrom2D(M1C2d);
			Matrix M1C3 =  MatrixPool.makeProtoFrom2D(M1C3d);
			Matrix M1C4 =  MatrixPool.makeProtoFrom2D(M1C4d);

			Matrix M2C1 =  MatrixPool.makeProtoFrom2D(M2C1d);
			Matrix M2C2 =  MatrixPool.makeProtoFrom2D(M2C2d);
			Matrix M2C3 =  MatrixPool.makeProtoFrom2D(M2C2d);
			Matrix M2C4 =  MatrixPool.makeProtoFrom2D(M2C2d);


			MMReply C1=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C1).setMatrixB(M2C1).build());
			MMReply C2=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C2).setMatrixB(M2C2).build());
			MMReply C3=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C3).setMatrixB(M2C3).build());
			MMReply C4=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C4).setMatrixB(M2C4).build());

			int[][] C1array = MatrixPool.make2DFromProto(C1.getMatrixC());
			int[][] C2array = MatrixPool.make2DFromProto(C2.getMatrixC());
			int[][] C3array = MatrixPool.make2DFromProto(C3.getMatrixC());
			int[][] C4array = MatrixPool.make2DFromProto(C4.getMatrixC());

			return MatrixPool.joinFromQuarters(C1array, C2array, C3array, C4array);
		}else {
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

			MMReply M1C1 = stub.multBlock(MMRequest.newBuilder().setMatrixA(QA11).setMatrixB(QB11).build());
			MMReply M2C1 = stub.multBlock(MMRequest.newBuilder().setMatrixA(QA12).setMatrixB(QB21).build());

			MMReply M1C2 = stub.multBlock(MMRequest.newBuilder().setMatrixA(QA11).setMatrixB(QB12).build());
			MMReply M2C2 = stub.multBlock(MMRequest.newBuilder().setMatrixA(QA12).setMatrixB(QB22).build());

			MMReply M1C3 = stub.multBlock(MMRequest.newBuilder().setMatrixA(QA21).setMatrixB(QB11).build());
			MMReply M2C3 = stub.multBlock(MMRequest.newBuilder().setMatrixA(QA22).setMatrixB(QB21).build());

			MMReply M1C4 = stub.multBlock(MMRequest.newBuilder().setMatrixA(QA21).setMatrixB(QB12).build());
			MMReply M2C4 = stub.multBlock(MMRequest.newBuilder().setMatrixA(QA22).setMatrixB(QB22).build());


			MMReply C1=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C1.getMatrixC()).setMatrixB(M2C1.getMatrixC()).build());
			MMReply C2=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C2.getMatrixC()).setMatrixB(M2C2.getMatrixC()).build());
			MMReply C3=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C3.getMatrixC()).setMatrixB(M2C3.getMatrixC()).build());
			MMReply C4=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C4.getMatrixC()).setMatrixB(M2C4.getMatrixC()).build());

			int[][] C1array = MatrixPool.make2DFromProto(C1.getMatrixC());
			int[][] C2array = MatrixPool.make2DFromProto(C2.getMatrixC());
			int[][] C3array = MatrixPool.make2DFromProto(C3.getMatrixC());
			int[][] C4array = MatrixPool.make2DFromProto(C4.getMatrixC());

			return MatrixPool.joinFromQuarters(C1array, C2array, C3array, C4array);
		}
	}
}
