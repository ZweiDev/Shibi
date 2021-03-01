package de.zwei.shibi.caller

import de.schildbach.pte.NetworkProvider
import de.schildbach.pte.dto.*
import de.zwei.shibi.model.restyprotobuf.TimetableSearch
import de.zwei.shibi.model.restyprotobuf.TripSearch
import de.zwei.shibi.proto.PlaceServiceOuterClass
import de.zwei.shibi.proto.TimetableServiceOuterClass
import de.zwei.shibi.proto.TripServiceOuterClass
import de.zwei.shibi.tools.PTESourceSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

object PTESearchCaller {
    val logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun getPTEPlacesFromLocation(request: PlaceServiceOuterClass.PlaceNearbySourceGetRequest): NearbyLocationsResult {
        val networkProvider = PTESourceSelector.getPTESource(request.source)

        val locationType: Set<LocationType> = EnumSet.of(LocationType.ANY)
        val nearbyLocation = Location.coord(Point.fromDouble(request.latitude, request.longitude))

        return withContext(Dispatchers.IO) {
            networkProvider!!.queryNearbyLocations(
                locationType,
                nearbyLocation,
                5000,
                5
            )
        }
    }

    suspend fun getPTEPlacesFromLocation(source: String, latitude: Double, longitude: Double): NearbyLocationsResult {
        val networkProvider = PTESourceSelector.getPTESource(source)

        val locationType: Set<LocationType> = EnumSet.of(LocationType.ANY)
        val nearbyLocation = Location.coord(Point.fromDouble(latitude, longitude))

        return withContext(Dispatchers.IO) {
            networkProvider!!.queryNearbyLocations(
                locationType,
                nearbyLocation,
                5000,
                5
            )
        }
    }

    suspend fun getPTEPlaces(request: PlaceServiceOuterClass.PlaceSearchSourceGetRequest): SuggestLocationsResult {
        val networkProvider = PTESourceSelector.getPTESource(request.source)

        val locationType: Set<LocationType> = EnumSet.of(LocationType.ANY)

        return withContext(Dispatchers.IO) {
            networkProvider!!.suggestLocations(
                request.query,
                locationType,
                5
            )
        }
    }

    suspend fun getPTEPlaces(source: String, query: String): SuggestLocationsResult {
        val networkProvider = PTESourceSelector.getPTESource(source)

        val locationType: Set<LocationType> = EnumSet.of(LocationType.ANY)

        return withContext(Dispatchers.IO) {
            networkProvider!!.suggestLocations(
                query,
                locationType,
                5
            )
        }
    }

    suspend fun getPTEDepartures(request: TimetableServiceOuterClass.TimetableSourcePostRequest): QueryDeparturesResult {
        val networkProvider = PTESourceSelector.getPTESource(request.source)

        val time = LocalDateTime.parse(request.timetableSearch.lookupTime)

        return withContext(Dispatchers.IO) {
            networkProvider!!.queryDepartures(
                request.timetableSearch.stationID,
                Date.from(time.toInstant(ZoneOffset.of("+02:00"))),
                10,
                true
            )
        }
    }

    suspend fun getPTEDepartures(request: TimetableServiceOuterClass.TimetableSuggestionsSourcePostRequest): QueryDeparturesResult {
        val networkProvider = PTESourceSelector.getPTESource(request.source)

        val time = LocalDateTime.parse(request.timetableSearch.lookupTime)

        return withContext(Dispatchers.IO) {
            networkProvider!!.queryDepartures(
                request.timetableSearch.stationID,
                Date.from(time.toInstant(ZoneOffset.of("+02:00"))),
                10,
                true
            )
        }
    }

    suspend fun getPTEDepartures(source: String, request: TimetableSearch): QueryDeparturesResult {
        val networkProvider = PTESourceSelector.getPTESource(source)

        val time = LocalDateTime.parse(request.lookupTime)

        return withContext(Dispatchers.IO) {
            networkProvider!!.queryDepartures(
                request.stationID,
                Date.from(time.toInstant(ZoneOffset.of("+02:00"))),
                10,
                true
            )
        }
    }

    suspend fun getPTETrips(request: TripServiceOuterClass.TripSourcePostRequest): QueryTripsResult {
        val networkProvider = PTESourceSelector.getPTESource(request.source)

        val originLocation = Location(LocationType.STATION, request.search.originID)
        val destinationLocation = Location(LocationType.STATION, request.search.destinationID)
        val time = LocalDateTime.parse(request.search.time)
        val tripOptions = TripOptions(
            Product.ALL,
            NetworkProvider.Optimize.LEAST_DURATION,
            NetworkProvider.WalkSpeed.NORMAL,
            NetworkProvider.Accessibility.NEUTRAL,
            null
        )

        logger.debug("Request from ${request.search.originID} to ${request.search.destinationID}")

        return withContext(Dispatchers.IO) {
            networkProvider!!.queryTrips(
                originLocation,
                null,
                destinationLocation,
                Date.from(time.toInstant(ZoneOffset.of("+02:00"))),
                !request.search.isArrivalTime,
                tripOptions
            )
        }
    }

    suspend fun getPTETrips(source: String, request: TripSearch): QueryTripsResult {
        val networkProvider = PTESourceSelector.getPTESource(source)

        val originLocation = Location(LocationType.STATION, request.originID)
        val destinationLocation = Location(LocationType.STATION, request.destinationID)
        val time = LocalDateTime.parse(request.time)
        val tripOptions = TripOptions(
            Product.ALL,
            NetworkProvider.Optimize.LEAST_DURATION,
            NetworkProvider.WalkSpeed.NORMAL,
            NetworkProvider.Accessibility.NEUTRAL,
            null
        )

        logger.debug("Request from ${request.originID} to ${request.destinationID}")

        return withContext(Dispatchers.IO) {
            networkProvider!!.queryTrips(
                originLocation,
                null,
                destinationLocation,
                Date.from(time.toInstant(ZoneOffset.of("+02:00"))),
                request.isArrivalTime == false,
                tripOptions
            )
        }
    }
}
