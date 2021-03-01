package de.zwei.shibi.services

import de.zwei.shibi.caller.PTESearchCaller
import de.zwei.shibi.mapper.PTEOeptfMapper
import de.zwei.shibi.proto.TripResultOuterClass
import de.zwei.shibi.proto.TripServiceGrpcKt
import de.zwei.shibi.proto.TripServiceOuterClass
import org.slf4j.LoggerFactory

class ShibiTripSearchService : TripServiceGrpcKt.TripServiceCoroutineImplBase() {
    val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun tripSourcePost(request: TripServiceOuterClass.TripSourcePostRequest): TripResultOuterClass.TripResult {
        val requestTime = System.currentTimeMillis() / 1000L

        return try {
            val tripsResult = PTESearchCaller.getPTETrips(request)
            PTEOeptfMapper.remapTripResult(tripsResult, request, requestTime)
        } catch (e: Exception) {
            logger.error(e.stackTraceToString())
            super.tripSourcePost(request)
        }
    }

    override suspend fun tripMoreSourcePost(request: TripServiceOuterClass.TripMoreSourcePostRequest): TripResultOuterClass.TripResult {
        return super.tripMoreSourcePost(request)
    }
}
