package de.zwei.shibi

import de.schildbach.pte.dto.QueryDeparturesResult
import de.schildbach.pte.dto.QueryTripsResult
import de.schildbach.pte.dto.SuggestLocationsResult
import de.zwei.shibi.caller.PTESearchCaller
import de.zwei.shibi.proto.PlaceServiceOuterClass
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ShibiRequestPTETest {

    @Test
    @DisplayName("Get trip from PTE with TripSearch body")
    fun getTripResult() {
        assertDoesNotThrow {
            runBlocking {
                val expectedTripSearch = PTESearchCaller.getPTETrips(
                    TestingObjects.actualTestingSource,
                    TestingObjects.actualTripSearch
                )

                assertNotNull(expectedTripSearch)
                assertNotNull(expectedTripSearch.trips)
                assertEquals(expectedTripSearch.status, QueryTripsResult.Status.OK)
            }
        }
    }

    @Test
    @DisplayName("Get trip from PTE with TripSearch protobuf body")
    fun getTripResultProtobuf() {
        assertDoesNotThrow {
            runBlocking {
                val expectedTripSearch = PTESearchCaller.getPTETrips(TestingObjects.actualTripSearchProtobuf)

                assertNotNull(expectedTripSearch)
                assertNotNull(expectedTripSearch.trips)
                assertEquals(expectedTripSearch.status, QueryTripsResult.Status.OK)
            }
        }
    }

    @Test
    @DisplayName("Get timetable from PTE with TimetableSearch body")
    fun getTimetable() {
        assertDoesNotThrow {
            runBlocking {
                val expectedTripSearch = PTESearchCaller.getPTEDepartures(TestingObjects.actualTestingSource, TestingObjects.actualTimetableSearch)

                assertNotNull(expectedTripSearch)
                assertNotNull(expectedTripSearch.stationDepartures)
                assertEquals(expectedTripSearch.status, QueryDeparturesResult.Status.OK)
            }
        }
    }

    @Test
    @DisplayName("Get timetable from PTE with TimetableSearch protobuf body")
    fun getTimetableProtobuf() {
        assertDoesNotThrow {
            runBlocking {
                val expectedTripSearch = PTESearchCaller.getPTEDepartures(TestingObjects.actualTimetableSearchProtobuf)

                assertNotNull(expectedTripSearch)
                assertNotNull(expectedTripSearch.stationDepartures)
                assertEquals(expectedTripSearch.status, QueryDeparturesResult.Status.OK)
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf("Fra", "Mai", "Düs", "Kob", "Dres", "Berl"))
    @DisplayName("Get places from PTE with a list of queries")
    fun getPlaceQueries(query: String) {
        assertDoesNotThrow {
            runBlocking {
                val expectedTripSearch = PTESearchCaller.getPTEPlaces(TestingObjects.actualTestingSource, query)

                assertNotNull(expectedTripSearch)
                assertNotNull(expectedTripSearch.locations)
                assertEquals(expectedTripSearch.status, SuggestLocationsResult.Status.OK)
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = arrayOf("Fra", "Mai", "Düs", "Kob", "Dres", "Berl"))
    @DisplayName("Get places from PTE with a list of generated PlaceSearch protobuf objects")
    fun getPlaceQueriesProtobuf(query: String) {
        val testObject = PlaceServiceOuterClass.PlaceSearchSourceGetRequest.newBuilder()
            .setSource(TestingObjects.actualTestingSource)
            .setQuery(query)
            .build()

        assertDoesNotThrow {
            runBlocking {
                val expectedTripSearch = PTESearchCaller.getPTEPlaces(testObject)

                assertNotNull(expectedTripSearch)
                assertNotNull(expectedTripSearch.locations)
                assertEquals(expectedTripSearch.status, SuggestLocationsResult.Status.OK)
            }
        }
    }
}
