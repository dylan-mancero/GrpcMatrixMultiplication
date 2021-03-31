package com.example.grpc.client.grpcclient;

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
    private static boolean powerOfTwoBitwise(int n){
        return (n & n-1) == 0;
    }
}
