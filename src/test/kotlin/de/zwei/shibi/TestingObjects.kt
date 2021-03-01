package de.zwei.shibi

import de.zwei.shibi.model.restyprotobuf.TimetableSearch
import de.zwei.shibi.model.restyprotobuf.TripSearch
import de.zwei.shibi.proto.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object TestingObjects {
    val actualTestingSource = "DB"

    val actualTripSearch = TripSearch(
        originID = "8000105",
        destinationID = "8002757",
        time = LocalDateTime.now().plus(2, ChronoUnit.HOURS).toString(),
        isArrivalTime = false
    )

    val actualTripSearchProtobuf = TripServiceOuterClass.TripSourcePostRequest.newBuilder()
        .setSource(actualTestingSource)
        .setSearch(
            SearchOuterClass.Search.newBuilder()
                .setOriginID("8000105")
                .setDestinationID("8002757")
                .setTime(LocalDateTime.now().plus(2, ChronoUnit.HOURS).toString())
                .setIsArrivalTime(false)
        ).build()

    val actualTimetableSearch = TimetableSearch(
        stationID = "8000105",
        lookupTime = LocalDateTime.now().plus(2, ChronoUnit.HOURS).toString(),
        isArrivalTime = false
    )

    val actualTimetableSearchProtobuf = TimetableServiceOuterClass.TimetableSourcePostRequest.newBuilder()
        .setSource(actualTestingSource)
        .setTimetableSearch(
            TimetableSearchOuterClass.TimetableSearch.newBuilder()
                .setStationID("8000105")
                .setLookupTime(LocalDateTime.now().plus(2, ChronoUnit.HOURS).toString())
                .setIsArrivalTime(false)
        ).build()

    val actualTimetableSuggestionSearchProtobuf = TimetableServiceOuterClass.TimetableSuggestionsSourcePostRequest.newBuilder()
        .setSource(actualTestingSource)
        .setTimetableSearch(
            TimetableSearchOuterClass.TimetableSearch.newBuilder()
                .setStationID("8000105")
                .setLookupTime(LocalDateTime.now().plus(2, ChronoUnit.HOURS).toString())
                .setIsArrivalTime(false)
        ).build()
}
