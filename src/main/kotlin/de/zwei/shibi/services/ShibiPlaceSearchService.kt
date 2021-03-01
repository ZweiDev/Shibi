package de.zwei.shibi.services

import de.zwei.shibi.caller.PTESearchCaller
import de.zwei.shibi.mapper.PTEOeptfMapper
import de.zwei.shibi.proto.PlaceResultOuterClass
import de.zwei.shibi.proto.PlaceServiceGrpcKt
import de.zwei.shibi.proto.PlaceServiceOuterClass
import org.slf4j.LoggerFactory

class ShibiPlaceSearchService : PlaceServiceGrpcKt.PlaceServiceCoroutineImplBase() {
    val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun placeNearbySourceGet(request: PlaceServiceOuterClass.PlaceNearbySourceGetRequest): PlaceResultOuterClass.PlaceResult {
        val requestTime = System.currentTimeMillis() / 1000L

        return try {
            logger.debug("Getting places for location ${request.latitude},${request.longitude} of source ${request.source}")
            val places = PTESearchCaller.getPTEPlacesFromLocation(request)
            PTEOeptfMapper.remapPlaceNearbySearch(places, requestTime)
        } catch (e: Exception) {
            logger.error(e.stackTraceToString())
            super.placeNearbySourceGet(request)
        }
    }

    override suspend fun placeSearchSourceGet(request: PlaceServiceOuterClass.PlaceSearchSourceGetRequest): PlaceResultOuterClass.PlaceResult {
        val requestTime = System.currentTimeMillis() / 1000L

        return try {
            logger.debug("Getting places for query ${request.query} of source ${request.source}")
            val places = PTESearchCaller.getPTEPlaces(request)
            PTEOeptfMapper.remapPlaceSearch(places, requestTime)
        } catch (e: Exception) {
            logger.error(e.stackTraceToString())
            super.placeSearchSourceGet(request)
        }
    }
}
