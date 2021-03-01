package de.zwei.shibi

import com.google.protobuf.util.JsonFormat
import de.schildbach.pte.dto.QueryTripsResult
import de.zwei.shibi.proto.PlaceResultOuterClass
import de.zwei.shibi.proto.TimetableOuterClass
import de.zwei.shibi.proto.TripResultOuterClass
import io.ktor.client.*
import io.ktor.client.engine.jetty.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.concurrent.thread
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShibiRestyIntegrationTest {
    val restyProtobufServer = RestyProtobufServer()

    val httpClient = HttpClient(Jetty) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                serializeNulls()
                disableHtmlEscaping()
            }
        }
    }

    @BeforeAll
    fun launchServer() {
        thread {
            this@ShibiRestyIntegrationTest.restyProtobufServer.start()
        }

        runBlocking {
            delay(2000)
        }
    }

    @AfterAll
    fun stopServer() {
        restyProtobufServer.stop()
    }

    @Test
    @DisplayName("Integration test (TripService) for Shibi")
    fun integrationTestTrips() {
        assertDoesNotThrow {
            runBlocking {
                val expectedTripResultBuilder = TripResultOuterClass.TripResult.newBuilder()

                val expectedResponse = httpClient.post<String> {
                    url("http://localhost:5080/trip/${TestingObjects.actualTestingSource}")
                    contentType(ContentType.Application.Json)
                    body = TestingObjects.actualTripSearch
                }
                JsonFormat.parser().merge(expectedResponse, expectedTripResultBuilder)

                val expectedTripResult = expectedTripResultBuilder.build()

                assertNotNull(expectedTripResult)
                assertTrue(expectedTripResult.tripsCount > 0)
            }
        }
    }

    @Test
    @DisplayName("Integration test (TimetableService) for Shibi")
    fun integrationTestTimetable() {
        assertDoesNotThrow {
            runBlocking {
                val expectedTimetableResultBuilder = TimetableOuterClass.Timetable.newBuilder()

                val expectedResponse = httpClient.post<String> {
                    url("http://localhost:5080/timetable/${TestingObjects.actualTestingSource}")
                    contentType(ContentType.Application.Json)
                    body = TestingObjects.actualTimetableSearch
                }
                JsonFormat.parser().merge(expectedResponse, expectedTimetableResultBuilder)

                val expectedTimetableResult = expectedTimetableResultBuilder.build()

                assertNotNull(expectedTimetableResult)
                assertTrue(expectedTimetableResult.tripsCount > 0)
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf("Fra", "Mai", "Düs", "Kob", "Dres", "Berl"))
    @DisplayName("Parametized Integration test (TimetableService) for Shibi")
    fun integrationTestPlaces(query: String) {
        assertDoesNotThrow {
            runBlocking {
                val expectedPlaceResultBuilder = PlaceResultOuterClass.PlaceResult.newBuilder()

                val expectedResponse = httpClient.get<String> {
                    url("http://localhost:5080/place/search/${TestingObjects.actualTestingSource}?q=$query")
                }
                JsonFormat.parser().merge(expectedResponse, expectedPlaceResultBuilder)

                val expectedPlaceResult = expectedPlaceResultBuilder.build()

                assertNotNull(expectedPlaceResult)
                assertTrue(expectedPlaceResult.placesCount > 0)
            }
        }
    }
}
