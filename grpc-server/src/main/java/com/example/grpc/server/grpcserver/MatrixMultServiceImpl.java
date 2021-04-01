
package com.example.grpc.server.grpcserver;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
@GrpcService
public class MatrixMultServiceImpl extends MatrixMultServiceGrpc.MatrixMultServiceImplBase{
    @Override
    public void addBlock(MMRequest request, StreamObserver<MMReply> responseObserver) {
        int size = request.getMatrixA().getRowCount();
        int i, j;
        Matrix.Builder C = Matrix.newBuilder();
        for (i = 0; i < size; i++) {
            Row.Builder r = Row.newBuilder();
            for (j = 0; j < size; j++) {
                r.addColumn(request.getMatrixA().getRow(i).getColumn(j) + request.getMatrixB().getRow(i).getColumn(j));
            }
            C.addRow(i,r.build());
        }
        C.build();
        MMReply.Builder response = MMReply.newBuilder();
        response.setMatrixC(C);
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void multBlock(MMRequest request, StreamObserver<MMReply> responseObserver) {
        int size = request.getMatrixA().getRowCount();
        int [][] C = new int[size][size];

        for (int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                C[x][y]=0;
                for(int k=0;k<size;k++){
                    C[x][y] += request.getMatrixA().getRow(x).getColumn(k)
                            * request.getMatrixB().getRow(k).getColumn(y);
                }
            }
        }

        Matrix.Builder result = Matrix.newBuilder();

        for (int i = 0; i < C.length; i++) {
            Row.Builder row = Row.newBuilder();
            for (int j = 0; j < C[i].length; j++) {
                row.addColumn(C[i][j]);
            }
            result.addRow(i, row.build());
        }
        responseObserver.onNext( MMReply.newBuilder().setMatrixC( result.build() ).build());
        responseObserver.onCompleted();
    }
}



