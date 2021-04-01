package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MMRequest;
import com.example.grpc.server.grpcserver.MMReply;
import com.example.grpc.server.grpcserver.MatrixMultServiceGrpc;
import com.example.grpc.server.grpcserver.Matrix;
import com.example.grpc.server.grpcserver.Row;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();
		MatrixMultServiceGrpc.MatrixMultServiceBlockingStub stub = MatrixMultServiceGrpc.newBlockingStub(channel);
		int SIZE = A.length;

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

		Matrix A11m = MatrixPool.makeProtoFrom2D(A11);
		Matrix A12m = MatrixPool.makeProtoFrom2D(A12);
		Matrix A21m = MatrixPool.makeProtoFrom2D(A21);
		Matrix A22m = MatrixPool.makeProtoFrom2D(A22);
		Matrix B11m = MatrixPool.makeProtoFrom2D(B11);
		Matrix B12m = MatrixPool.makeProtoFrom2D(B12);
		Matrix B21m = MatrixPool.makeProtoFrom2D(B21);
		Matrix B22m = MatrixPool.makeProtoFrom2D(B22);

		MMReply M1C1 = stub.multBlock(MMRequest.newBuilder().setMatrixA(A11m).setMatrixB(B11m).build());
		MMReply M2C1 = stub.multBlock(MMRequest.newBuilder().setMatrixA(A12m).setMatrixB(B21m).build());

		MMReply M1C2 = stub.multBlock(MMRequest.newBuilder().setMatrixA(A11m).setMatrixB(B12m).build());
		MMReply M2C2 = stub.multBlock(MMRequest.newBuilder().setMatrixA(A12m).setMatrixB(B22m).build());

		MMReply M1C3 = stub.multBlock(MMRequest.newBuilder().setMatrixA(A21m).setMatrixB(B11m).build());
		MMReply M2C3 = stub.multBlock(MMRequest.newBuilder().setMatrixA(A22m).setMatrixB(B21m).build());

		MMReply M1C4 = stub.multBlock(MMRequest.newBuilder().setMatrixA(A21m).setMatrixB(B12m).build());
		MMReply M2C4 = stub.multBlock(MMRequest.newBuilder().setMatrixA(A22m).setMatrixB(B22m).build());


		MMReply C1=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C1.getMatrixC()).setMatrixB(M2C1.getMatrixC()).build());
		MMReply C2=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C2.getMatrixC()).setMatrixB(M2C2.getMatrixC()).build());
		MMReply C3=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C3.getMatrixC()).setMatrixB(M2C3.getMatrixC()).build());
		MMReply C4=stub.addBlock(MMRequest.newBuilder().setMatrixA(M1C4.getMatrixC()).setMatrixB(M2C4.getMatrixC()).build());

		int[][] C1array = MatrixPool.make2DFromProto(C1.getMatrixC());
		int[][] C2array = MatrixPool.make2DFromProto(C2.getMatrixC());
		int[][] C3array = MatrixPool.make2DFromProto(C3.getMatrixC());
		int[][] C4array = MatrixPool.make2DFromProto(C4.getMatrixC());

		int[][] finalArray = new int[SIZE][SIZE];

		for (int i = 0; i < SIZE / 2; i++) {
			for (int j = 0; j < SIZE / 2; j++) {

				finalArray[i][j] = C1array[i][j]; // top left
				finalArray[i][j] = C2array[i][j + SIZE / 2]; // top right
				finalArray[i][j] = C3array[i + SIZE / 2][j]; // bottom left
				finalArray[i][j] = C4array[i + SIZE / 2][j + SIZE / 2]; // bottom right

			}
		}

		System.out.println("Final Answer");
		System.out.println(Arrays.deepToString(finalArray));
		channel.shutdown();

		return Arrays.deepToString(finalArray);
	}
}
