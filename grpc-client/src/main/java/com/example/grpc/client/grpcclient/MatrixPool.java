package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.Matrix;
import com.example.grpc.server.grpcserver.Row;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatrixPool {
    int [][] matrixA;
    int [][] matrixB;
    public MatrixPool() {
    }
    public static Boolean checkDimensions(String A, String B){
        List<String> rows = Arrays.asList(A.split("\n"));
        List<String> rowsB = Arrays.asList(A.split("\n"));

        int rowDim = rows.size();
        if (!powerOfTwoBitwise(rowDim))
            return false;
        if (rowDim!=rowsB.size())
            return false;


        for (String column:rows) {
            if (column.split(" ").length!=rowDim)
                return false;
        }
        for (String column:rowsB) {
            if (column.split(" ").length!=rowDim)
                return false;
        }
        return true;
    }
    public void save(String A, String B) {
        List<String> rowsA = Arrays.asList(A.split("\n"));
        List<String> rowsB = Arrays.asList(B.split("\n"));

        int rowSize = rowsA.size();
        int colSize = rowsA.get(0).split(" ").length;

        this.matrixA = new int[rowSize][rowSize];
        this.matrixB = new int[rowSize][rowSize];

        for (int i = 0; i<rowSize; i++) {
            for (int j = 0;j<colSize; j++) {
                List<String> items = Arrays.asList(rowsA.get(i).split(" "));
                List<String>  itemsB = Arrays.asList(rowsB.get(i).split(" "));
                this.matrixA[i][j] = Integer.parseInt(items.get(j));
                this.matrixB[i][j] = Integer.parseInt(itemsB.get(j));
            }
        }
    }

    public int[][] getA() { return this.matrixA; }

    public int[][] getB() { return this.matrixB; }

    public Boolean deleteMatrices() {
        if (this.matrixA == null || this.matrixB == null)
            return false;
        this.matrixA = null;
        this.matrixB = null;
        return true;
    }

    public static Matrix makeProtoFrom2D(int [][] A) {
        Matrix.Builder matrix = Matrix.newBuilder();
        int size = A.length;
        for (int i = 0; i < size; i++) {
            Row.Builder row = Row.newBuilder();
            for (int j = 0; j < A[i].length; j++) {
                row.addColumn(A[i][j]);
            }
            matrix.addRow(i, row.build());
        }
        return matrix.build();
    }

    public static int [][] make2DFromProto(Matrix matrix) {
        int size = matrix.getRowCount();
        int C[][]= new int[size][size];

        for (int i=0; i<size; i++)
        {
            for (int j=0;j < size;j++)
            {
                C[i][j] = matrix.getRow(i).getColumn(j);
            }
        }
        return C;
    }

    public static int [][] joinFromQuarters(int[][] Q1, int[][] Q2, int[][] Q3, int[][] Q4) {
        int SIZE = Q1.length*4;
        int[][] combinedMatrix = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE / 2; i++) {
            for (int j = 0; j < SIZE / 2; j++) {

                combinedMatrix[i][j] = Q1[i][j]; // top left
                combinedMatrix[i][j + SIZE / 2] = Q2[i][j]; // top right
                combinedMatrix[i + SIZE / 2][j] = Q3[i][j]; // bottom left
                combinedMatrix[i + SIZE / 2][j + SIZE / 2] = Q4[i][j]; // bottom right
            }
        }
        return combinedMatrix;
    }
    private static boolean powerOfTwoBitwise(int n){
        return (n & n-1) == 0;
    }
}
