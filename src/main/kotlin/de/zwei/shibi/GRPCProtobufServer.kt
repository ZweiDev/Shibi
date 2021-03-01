package de.zwei.shibi

import de.zwei.shibi.services.ShibiPlaceSearchService
import de.zwei.shibi.services.ShibiTimetableService
import de.zwei.shibi.services.ShibiTripSearchService
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import org.slf4j.LoggerFactory

class GRPCProtobufServer() {
    val logger = LoggerFactory.getLogger(this.javaClass)

    var GRPC_PORT: String = System.getenv("GRPC_PORT") ?: "5111"

    val server: Server = ServerBuilder
        .forPort(GRPC_PORT.toInt())
        .addService(ProtoReflectionService.newInstance())
        .addService(ShibiTripSearchService())
        .addService(ShibiTimetableService())
        .addService(ShibiPlaceSearchService())
        .build()

    fun start() {
        server.start()
        logger.info("gRPC server started, listening on $GRPC_PORT")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.info("*** shutting down gRPC server since JVM is shutting down")
                this@GRPCProtobufServer.stop()
                logger.info("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }
}
