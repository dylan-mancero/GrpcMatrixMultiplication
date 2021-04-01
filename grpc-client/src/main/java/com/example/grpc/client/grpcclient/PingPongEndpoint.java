package com.example.grpc.client.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.example.grpc.client.grpcclient.MatrixPool;
@RestController
public class PingPongEndpoint {    

	GRPCClientService grpcClientService;
	MatrixPool matrixPool = new MatrixPool();
	@Autowired
    	public PingPongEndpoint(GRPCClientService grpcClientService) {
		this.grpcClientService = grpcClientService;
	}
	@GetMapping("/ping") public String ping() {
		return grpcClientService.ping();
	}
	@PostMapping("/uploadMatrices") public String uploadMatrices(@RequestParam("Matrices") MultipartFile file) throws IOException {
		BufferedReader isReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
		String line = isReader.lines().collect(Collectors.joining("\n"));
		String[] lines = line.split("\n\n");
		String MatrixA = lines[0];
		String MatrixB = lines[1];
		if (!MatrixPool.checkDimensions(MatrixA,MatrixB)) {
			return "Matrix is of wrong dimensions....ERROR 400";
		}
		matrixPool.save(MatrixA,MatrixB);
		return "Stored matrices....200 OKAY";
	}
	@GetMapping("/getMatrixA") public String getMatrixA() {
		int[][]A = matrixPool.getA();
		if (A == null)
			return "Matrix has not been uploaded!....ERROR 500";
		return Arrays.deepToString(A)
				+"\n OKAY..200";
	}
	@GetMapping("/getMatrixB") public String getMatrixB() {
		int[][]B = matrixPool.getB();
		if (B == null)
			return "Matrix has not been uploaded!....ERROR 500";
		return Arrays.deepToString(B)
				+"\n OKAY..200";
	}
	@DeleteMapping("/deleteMatrices") public String deleteMatrices() {
		if (!matrixPool.deleteMatrices()) {
			return "ERROR, matrices not defined....404";
		}
		return "Matrices deleted...200..OKAY";
	}
	@GetMapping("/multiply") public String multiplyMatrices(@RequestParam String deadline) {
		int deadlineInt = Integer.parseInt(deadline);
		return grpcClientService.multiplyMatrix(matrixPool.getA(), matrixPool.getB(), deadlineInt);
	}
}
