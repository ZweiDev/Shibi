package de.zwei.shibi.services

import de.zwei.shibi.caller.PTESearchCaller
import de.zwei.shibi.mapper.PTEOeptfMapper
import de.zwei.shibi.proto.TimetableOuterClass
import de.zwei.shibi.proto.TimetableServiceGrpcKt
import de.zwei.shibi.proto.TimetableServiceOuterClass
import de.zwei.shibi.proto.TimetableSuggestionsOuterClass
import org.slf4j.LoggerFactory

class ShibiTimetableService : TimetableServiceGrpcKt.TimetableServiceCoroutineImplBase() {
    val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun timetableSourcePost(request: TimetableServiceOuterClass.TimetableSourcePostRequest): TimetableOuterClass.Timetable {
        val requestTime = System.currentTimeMillis() / 1000L

        return try {
            logger.debug("Getting timetable for station ${request.timetableSearch.stationID} of source ${request.source}")
            val departuresResult = PTESearchCaller.getPTEDepartures(request)
            PTEOeptfMapper.remapTimetable(departuresResult, request, requestTime)
        } catch (e: Exception) {
            logger.error(e.stackTraceToString())
            super.timetableSourcePost(request)
        }
    }

    override suspend fun timetableSuggestionsSourcePost(request: TimetableServiceOuterClass.TimetableSuggestionsSourcePostRequest): TimetableSuggestionsOuterClass.TimetableSuggestions {
        val requestTime = System.currentTimeMillis() / 1000L

        return try {
            logger.debug("Getting timetable suggestions for station ${request.timetableSearch.stationID} of source ${request.source}")
            val departuresResult = PTESearchCaller.getPTEDepartures(request)
            PTEOeptfMapper.remapTimetableSuggestions(departuresResult, request, requestTime)
        } catch (e: Exception) {
            logger.error(e.stackTraceToString())
            super.timetableSuggestionsSourcePost(request)
        }
    }
}
