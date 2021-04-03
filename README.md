# GRPC matrix multiplication from REST interface
Starting point for using GRPC with Spring. Based upon https://github.com/sajeerzeji/SpringBoot-GRPC

## From Server terminal:
cd GrpcMatrixMultiplication/grpc-server
mvn package
chmod 777 mvnw
./mvnw spring-boot:run

## From Client terminal
cd GrpcMatrixMultiplication/grpc-client
mvn package
chmod 777 mvnw
./mvnw spring-boot:run

you can now send requests to the ip of the client on port 8082, for example:
"localhost":8082/getMatrixA
from POSTMAN you could upload both matrices as a txt file with key "Matrices"
matrices are seperated by 2 new lines "\n\n"
each row seperated by 1 new line
each element by a space.
