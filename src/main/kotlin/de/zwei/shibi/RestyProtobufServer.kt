package de.zwei.shibi

import com.google.protobuf.util.JsonFormat
import de.zwei.shibi.caller.PTESearchCaller
import de.zwei.shibi.mapper.PTEOeptfMapper
import de.zwei.shibi.model.restyprotobuf.Error
import de.zwei.shibi.model.restyprotobuf.TimetableSearch
import de.zwei.shibi.model.restyprotobuf.TripSearch
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RestyProtobufServer {
    val logger = LoggerFactory.getLogger(this.javaClass)

    val HTTP_PORT: String = System.getenv("HTTP_PORT") ?: "5080"

    val server = embeddedServer(
        Jetty,
        port = (HTTP_PORT.toInt()),
    ) {
        routing {
            post("/trip/{source}") {
                val requestTime = System.currentTimeMillis()
                val source = call.parameters["source"]
                val request = call.receive<TripSearch>()

                try {
                    if (source != null) {
                        withContext(IO) {
                            val pteTrips = PTESearchCaller.getPTETrips(source, request)

                            val finalTrips = PTEOeptfMapper.remapTripResult(pteTrips, requestTime)
                            val jsonFinalTrips = JsonFormat.printer().print(finalTrips)

                            call.respondText(
                                contentType = ContentType.parse("application/json")
                            ) {
                                jsonFinalTrips
                            }
                        }
                    } else {
                        val error = Error(
                            "No Source given"
                        )

                        call.respond(error)
                    }
                } catch (e: Exception) {
                    val error = Error(
                        "Internal Server Error",
                        e.stackTraceToString()
                    )
                    call.respond(error)
                }
            }

            post("/timetable/{source}") {
                val requestTime = System.currentTimeMillis()
                val source = call.parameters["source"]
                val request = call.receive<TimetableSearch>()

                try {
                    if (source != null) {
                        withContext(IO) {
                            val pteDepartures = PTESearchCaller.getPTEDepartures(source, request)

                            val finalTrips = PTEOeptfMapper.remapTimetable(pteDepartures, request, requestTime)
                            val jsonFinalTrips = JsonFormat.printer().print(finalTrips)

                            call.respondText(
                                contentType = ContentType.parse("application/json")
                            ) {
                                jsonFinalTrips
                            }
                        }
                    } else {
                        val error = Error(
                            "No Source given"
                        )

                        call.respond(error)
                    }
                } catch (e: Exception) {
                    val error = Error(
                        "Internal Server Error",
                        e.stackTraceToString()
                    )
                    call.respond(error)
                }
            }

            post("/timetable/suggestions/{source}") {
                val requestTime = System.currentTimeMillis()
                val source = call.parameters["source"]
                val request = call.receive<TimetableSearch>()

                try {
                    if (source != null) {
                        withContext(IO) {
                            val pteDepartures = PTESearchCaller.getPTEDepartures(source, request)

                            val finalTrips = PTEOeptfMapper.remapTimetableSuggestions(pteDepartures, request, requestTime)
                            val jsonFinalTrips = JsonFormat.printer().print(finalTrips)

                            call.respondText(
                                contentType = ContentType.parse("application/json")
                            ) {
                                jsonFinalTrips
                            }
                        }
                    } else {
                        val error = Error(
                            "No Source given"
                        )

                        call.respond(error)
                    }
                } catch (e: Exception) {
                    val error = Error(
                        "Internal Server Error",
                        e.stackTraceToString()
                    )
                    call.respond(error)
                }
            }

            get("/place/search/{source}") {
                val requestTime = System.currentTimeMillis()
                val source = call.parameters["source"]
                val requestQuery = call.request.queryParameters["q"] ?: call.request.queryParameters["query"]

                try {
                    if (source != null && requestQuery != null) {
                        withContext(IO) {
                            val ptePlaces = PTESearchCaller.getPTEPlaces(source, requestQuery)

                            val finalTrips = PTEOeptfMapper.remapPlaceSearch(ptePlaces, requestTime)
                            val jsonFinalTrips = JsonFormat.printer().print(finalTrips)

                            call.respondText(
                                contentType = ContentType.parse("application/json")
                            ) {
                                jsonFinalTrips
                            }
                        }
                    } else {
                        val error = Error(
                            "No Source or query given"
                        )

                        call.respond(error)
                    }
                } catch (e: Exception) {
                    val error = Error(
                        "Internal Server Error",
                        e.stackTraceToString()
                    )
                    call.respond(error)
                }
            }

            get("/place/nearby/{source}") {
                val requestTime = System.currentTimeMillis()
                val source = call.parameters["source"]
                val requestLatitude = call.request.queryParameters["latitude"] ?: call.request.queryParameters["lat"]
                val requestLongitude = call.request.queryParameters["longitude"] ?: call.request.queryParameters["lon"]

                try {
                    if (source != null && requestLatitude != null && requestLongitude != null) {
                        withContext(IO) {
                            val ptePlaces = PTESearchCaller.getPTEPlacesFromLocation(source, requestLatitude.toDouble(), requestLongitude.toDouble())

                            val finalTrips = PTEOeptfMapper.remapPlaceNearbySearch(ptePlaces, requestTime)
                            val jsonFinalTrips = JsonFormat.printer().print(finalTrips)

                            call.respond(jsonFinalTrips)
                        }
                    } else {
                        val error = Error(
                            "No Source or coordinates given"
                        )

                        call.respond(error)
                    }
                } catch (e: Exception) {
                    val error = Error(
                        "Internal Server Error",
                        e.stackTraceToString()
                    )
                    call.respond(error)
                }
            }
        }

        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
                setPrettyPrinting()
            }
        }
    }

    fun start() {
        initShutdownRuntime()
        server.start(wait = true)
        logger.info("HTTP server started, listening on $HTTP_PORT")
    }

    private fun initShutdownRuntime() {
        Runtime.getRuntime().addShutdownHook(
            Thread {
                logger.info("*** shutting down RestyProtobufServer since JVM is shutting down")
                this@RestyProtobufServer.stop()
                logger.info("*** server shut down")
            }
        )
    }

    fun stop() {
        server.stop(5000, 10000, TimeUnit.MILLISECONDS)
    }
}
