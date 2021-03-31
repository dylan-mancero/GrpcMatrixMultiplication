package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
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
		int MAX = A.length;

		int[][] A11 = new int[MAX / 2][MAX / 2];
		int[][] A12 = new int[MAX / 2][MAX / 2];
		int[][] A21 = new int[MAX / 2][MAX / 2];
		int[][] A22 = new int[MAX / 2][MAX / 2];
		int[][] B11 = new int[MAX / 2][MAX / 2];
		int[][] B12 = new int[MAX / 2][MAX / 2];
		int[][] B21 = new int[MAX / 2][MAX / 2];
		int[][] B22 = new int[MAX / 2][MAX / 2];

		for (int i = 0; i < MAX / 2; i++) {
			for (int j = 0; j < MAX / 2; j++) {

				A11[i][j] = A[i][j]; // top left
				A12[i][j] = A[i][j + MAX / 2]; // top right
				A21[i][j] = A[i + MAX / 2][j]; // bottom left
				A22[i][j] = A[i + MAX / 2][j + MAX / 2]; // bottom right

				B11[i][j] = B[i][j]; // top left
				B12[i][j] = B[i][j + MAX / 2]; // top right
				B21[i][j] = B[i + MAX / 2][j]; // bottom left
				B22[i][j] = B[i + MAX / 2][j + MAX / 2]; // bottom right
			}
		}
    	return "done";
	}
}
