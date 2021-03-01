package de.zwei.shibi

import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.thread

class Application {
    companion object {
        val logger = LoggerFactory.getLogger("Application")

        @JvmStatic
        fun main(args: Array<String>) {
            TimeZone.setDefault(TimeZone.getTimeZone("Berlin"))

            val protobufServer = GRPCProtobufServer()
            val restyProtobufServer = RestyProtobufServer()

            thread {
                logger.info("Start Resty Protobuf Service")
                restyProtobufServer.start()
            }

            thread {
                logger.info("Start gRPC Service")
                protobufServer.start()
                protobufServer.blockUntilShutdown()
            }
        }
    }
}
