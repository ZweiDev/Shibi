package de.zwei.shibi.mapper

import de.schildbach.pte.dto.*
import de.zwei.shibi.model.restyprotobuf.TimetableSearch
import de.zwei.shibi.proto.*
import java.util.*

object PTEOeptfMapper {
    fun remapAdditionalInformation(key: String, value: String): AdditionalInformationOuterClass.AdditionalInformation {
        val protoAdditionalInformation = AdditionalInformationOuterClass.AdditionalInformation.newBuilder()

        protoAdditionalInformation.key = key
        protoAdditionalInformation.value = value

        return protoAdditionalInformation.build()
    }

    fun remapLocation(location: Location): LocationOuterClass.Location {
        val protoLocation = LocationOuterClass.Location.newBuilder()

        protoLocation.latitude = location.latAsDouble
        protoLocation.longitude = location.lonAsDouble

        return protoLocation.build()
    }

    fun remapPlace(location: Location): PlaceOuterClass.Place {
        val protoPlace = PlaceOuterClass.Place.newBuilder()

        if (location.name != null && location.place != null) {
            protoPlace.name = "${location.place}, ${location.name}"
        } else {
            protoPlace.name = location.uniqueShortName()
        }
        if (location.hasName()) {
            protoPlace.colloquialName = location.name
        }
        if (location.hasCoord()) {
            protoPlace.location = remapLocation(location)
        }
        if (location.hasId()) {
            protoPlace.stationID = location.id
        }
        if (location.type != null) {
            protoPlace.placeType = remapPlaceType((if (location.name != null) location.name!! else ""), location.type)
        }
        if (location.products != null) {
            for (product in location.products!!) {
                protoPlace.addVehicleTypes(remapVehicleTypeProduct(product))
            }
        }

        return protoPlace.build()
    }

    fun remapPlaceType(name: String, locationType: LocationType): PlaceOuterClass.Place.PlaceTypeEnum {
        if (locationType == LocationType.STATION) {
            return PlaceOuterClass.Place.PlaceTypeEnum.STATION
        }
        if (locationType == LocationType.COORD) {
            return PlaceOuterClass.Place.PlaceTypeEnum.COORDINATE
        }
        if (locationType == LocationType.ADDRESS && name.indexOf("POI") != -1) {
            return PlaceOuterClass.Place.PlaceTypeEnum.POINT_OF_INTEREST
        }

        return PlaceOuterClass.Place.PlaceTypeEnum.EXTRAORDINATE_PLACE
    }

    fun remapTime(date: Date): TimeOuterClass.Time {
        val protoTime = TimeOuterClass.Time.newBuilder()

        protoTime.time = date.toString()
        protoTime.unixTime = date.toInstant().epochSecond.toDouble()

        return protoTime.build()
    }

    fun remapStopover(leg: Trip.Public, location: Location, position: Position?): StopoverOuterClass.Stopover {
        val protoStopover = StopoverOuterClass.Stopover.newBuilder()

        protoStopover.departureTime = remapTime(leg.getDepartureTime(true))
        protoStopover.arrivalTime = remapTime(leg.getArrivalTime(true))
        if (leg.departureDelay != null) {
            protoStopover.departureRealtime = remapTime(leg.getDepartureTime(false))
        }
        if (leg.arrivalDelay != null) {
            protoStopover.arrivalRealtime = remapTime(leg.getArrivalTime(true))
        }
        protoStopover.place = remapPlace(location)

        if (position != null) {
            protoStopover.lane = position.name
        }

        return protoStopover.build()
    }

    fun remapStopoverFromStop(stop: Stop): StopoverOuterClass.Stopover {
        val protoStopover = StopoverOuterClass.Stopover.newBuilder()

        protoStopover.departureTime = remapTime(stop.departureTime)
        protoStopover.arrivalTime = remapTime(stop.arrivalTime)
        if (stop.departureDelay != null) {
            protoStopover.departureRealtime = remapTime(stop.getDepartureTime(false))
        }
        if (stop.arrivalDelay != null) {
            protoStopover.arrivalRealtime = remapTime(stop.getArrivalTime(false))
        }
        protoStopover.place = remapPlace(stop.location)

        return protoStopover.build()
    }

    fun remapVehicleTypeProduct(product: Product): PlaceOuterClass.Place.VehicleTypesEnum {
        if (product == Product.HIGH_SPEED_TRAIN) {
            return PlaceOuterClass.Place.VehicleTypesEnum.HIGH_SPEED_TRAIN
        }
        if (product == Product.REGIONAL_TRAIN) {
            return PlaceOuterClass.Place.VehicleTypesEnum.REGIONAL_TRAIN
        }
        if (product == Product.SUBURBAN_TRAIN) {
            return PlaceOuterClass.Place.VehicleTypesEnum.SUBURBAN_TRAIN
        }
        if (product == Product.SUBWAY) {
            return PlaceOuterClass.Place.VehicleTypesEnum.SUBWAY
        }
        if (product == Product.TRAM) {
            return PlaceOuterClass.Place.VehicleTypesEnum.TRAM
        }
        if (product == Product.BUS) {
            return PlaceOuterClass.Place.VehicleTypesEnum.BUS
        }
        if (product == Product.ON_DEMAND) {
            return PlaceOuterClass.Place.VehicleTypesEnum.ONDEMAND
        }

        return PlaceOuterClass.Place.VehicleTypesEnum.MISC
    }

    fun remapVehicleTypeLine(line: Line): VehicleOuterClass.Vehicle.VehicleTypeEnum {
        if (line.product == Product.HIGH_SPEED_TRAIN) {
            if (line.name != null) {
                if (line.name!!.indexOf("ICE") != -1) {
                    return VehicleOuterClass.Vehicle.VehicleTypeEnum.INTERCITY_EXPRESS_TRAIN
                }
                if (line.name!!.indexOf("TGV") != -1) {
                    return VehicleOuterClass.Vehicle.VehicleTypeEnum.INTERCITY_EXPRESS_TRAIN
                }
                if (line.name!!.indexOf("IC") != -1) {
                    return VehicleOuterClass.Vehicle.VehicleTypeEnum.INTERCITY_TRAIN
                }
                if (line.name!!.indexOf("THA") != -1) {
                    return VehicleOuterClass.Vehicle.VehicleTypeEnum.INTERCITY_TRAIN
                }
                if (line.name!!.indexOf("EC") != -1) {
                    return VehicleOuterClass.Vehicle.VehicleTypeEnum.EUROCITY_TRAIN
                }

                return VehicleOuterClass.Vehicle.VehicleTypeEnum.EUROCITY_TRAIN
            }
        }
        if (line.product == Product.REGIONAL_TRAIN) {
            if (line.name != null) {
                if (line.name!!.indexOf("RE") != -1) {
                    return VehicleOuterClass.Vehicle.VehicleTypeEnum.REGIONAL_EXPRESS_TRAIN
                }
                if (line.name!!.indexOf("RB") != -1) {
                    return VehicleOuterClass.Vehicle.VehicleTypeEnum.REGIONAL_TRAIN
                }

                return VehicleOuterClass.Vehicle.VehicleTypeEnum.REGIONAL_TRAIN
            }
        }
        if (line.product == Product.SUBURBAN_TRAIN) {
            return VehicleOuterClass.Vehicle.VehicleTypeEnum.SUBURBAN_TRAIN
        }
        if (line.product == Product.SUBWAY) {
            return VehicleOuterClass.Vehicle.VehicleTypeEnum.SUBWAY
        }
        if (line.product == Product.TRAM) {
            return VehicleOuterClass.Vehicle.VehicleTypeEnum.TRAM
        }
        if (line.product == Product.BUS) {
            return VehicleOuterClass.Vehicle.VehicleTypeEnum.BUS
        }
        if (line.product == Product.ON_DEMAND) {
            return VehicleOuterClass.Vehicle.VehicleTypeEnum.ONDEMAND
        }

        return VehicleOuterClass.Vehicle.VehicleTypeEnum.MISC
    }

    fun remapVehicle(line: Line): VehicleOuterClass.Vehicle {
        val protoLine = VehicleOuterClass.Vehicle.newBuilder()

        protoLine.name = line.name
        if (line.network != null) {
            protoLine.provider = line.network
        }
        if (line.message != null) {
            protoLine.addAdditionalInformation(remapAdditionalInformation("message", line.message!!))
        }

        protoLine.vehicleType = remapVehicleTypeLine(line)

        return protoLine.build()
    }

    fun remapSubTrip(leg: Trip.Public): SubTripOuterClass.SubTrip {
        val protoSubTrip = SubTripOuterClass.SubTrip.newBuilder()

        protoSubTrip.origin = remapStopover(leg, leg.departure, leg.departurePosition)
        protoSubTrip.destination = remapStopover(leg, leg.arrival, leg.arrivalPosition)
        protoSubTrip.departureTime = remapTime(leg.departureTime)
        protoSubTrip.arrivalTime = remapTime(leg.arrivalTime)
        if (leg.departureDelay != null) {
            protoSubTrip.departureRealtime = remapTime(leg.getDepartureTime(false))
        }
        if (leg.arrivalDelay != null) {
            protoSubTrip.arrivalRealtime = remapTime(leg.getArrivalTime(false))
        }
        protoSubTrip.vehicle = remapVehicle(leg.line)

        if (leg.intermediateStops != null) {
            for (stop in leg.intermediateStops!!) {
                protoSubTrip.addStopovers(remapStopoverFromStop(stop))
            }
        }
        if (leg.message != null) {
            protoSubTrip.addAdditionalInformation(remapAdditionalInformation("message", leg.message!!))
        }

        return protoSubTrip.build()
    }

    fun remapTrip(trip: Trip): TripOuterClass.Trip {
        val protoTrip = TripOuterClass.Trip.newBuilder()

        protoTrip.origin = remapPlace(trip.from)
        protoTrip.destination = remapPlace(trip.to)
        protoTrip.departure = remapTime(trip.firstDepartureTime)
        protoTrip.arrival = remapTime(trip.lastArrivalTime)
        protoTrip.amountOfExchange = trip.legs.size

        for (leg in trip.legs) {
            if (leg is Trip.Public) {
                protoTrip.addSubtrips(remapSubTrip(leg))
            }
        }

        return protoTrip.build()
    }

    fun remapTimetableTrip(departure: Departure, startLocation: Location): TimetableTripOuterClass.TimetableTrip {
        val protoTimetableTrip = TimetableTripOuterClass.TimetableTrip.newBuilder()

        protoTimetableTrip.origin = remapPlace(startLocation)
        if (departure.destination != null) {
            protoTimetableTrip.travelDestination = remapPlace(departure.destination!!)
        }
        protoTimetableTrip.time = remapTime(departure.time)
        if (departure.position != null) {
            protoTimetableTrip.lane = departure.position!!.name
        }
        protoTimetableTrip.vehicle = remapVehicle(departure.line)
        if (departure.message != null) {
            protoTimetableTrip.addAdditionalInformation(remapAdditionalInformation("message", departure.message!!))
        }

        return protoTimetableTrip.build()
    }

    fun remapTripResult(
        tripResult: QueryTripsResult,
        request: TripServiceOuterClass.TripSourcePostRequest,
        requestTime: Long
    ): TripResultOuterClass.TripResult {
        val protoTripResult = TripResultOuterClass.TripResult.newBuilder()

        protoTripResult.serverRequestTimestamp = requestTime.toDouble()
        protoTripResult.searchBody = request.search
        protoTripResult.origin = remapPlace(tripResult.from)
        protoTripResult.destination = remapPlace(tripResult.to)

        for (trip in tripResult.trips) {
            protoTripResult.addTrips(remapTrip(trip))
        }

        protoTripResult.serverResponseTimestamp = (System.currentTimeMillis() / 1000L).toDouble()

        return protoTripResult.build()
    }

    fun remapTripResult(
        tripResult: QueryTripsResult,
        requestTime: Long
    ): TripResultOuterClass.TripResult {
        val protoTripResult = TripResultOuterClass.TripResult.newBuilder()

        protoTripResult.serverRequestTimestamp = requestTime.toDouble()
        protoTripResult.origin = remapPlace(tripResult.from)
        protoTripResult.destination = remapPlace(tripResult.to)

        for (trip in tripResult.trips) {
            protoTripResult.addTrips(remapTrip(trip))
        }

        protoTripResult.serverResponseTimestamp = (System.currentTimeMillis() / 1000L).toDouble()

        return protoTripResult.build()
    }

    fun remapTimetable(
        departuresResult: QueryDeparturesResult,
        request: TimetableServiceOuterClass.TimetableSourcePostRequest,
        requestTime: Long
    ): TimetableOuterClass.Timetable {
        val protoTimetable = TimetableOuterClass.Timetable.newBuilder()
        val station = departuresResult.findStationDepartures(request.timetableSearch.stationID)

        protoTimetable.place = remapPlace(station.location)

        for (departure in station.departures) {
            protoTimetable.addTrips(remapTimetableTrip(departure, station.location))
        }

        protoTimetable.serverRequestTimestamp = requestTime.toDouble()
        protoTimetable.serverResponseTimestamp = System.currentTimeMillis().toDouble()

        return protoTimetable.build()
    }

    fun remapTimetable(
        departuresResult: QueryDeparturesResult,
        request: TimetableSearch,
        requestTime: Long
    ): TimetableOuterClass.Timetable {
        val protoTimetable = TimetableOuterClass.Timetable.newBuilder()
        val station = departuresResult.findStationDepartures(request.stationID)

        protoTimetable.place = remapPlace(station.location)

        for (departure in station.departures) {
            protoTimetable.addTrips(remapTimetableTrip(departure, station.location))
        }

        protoTimetable.serverRequestTimestamp = requestTime.toDouble()
        protoTimetable.serverResponseTimestamp = System.currentTimeMillis().toDouble()

        return protoTimetable.build()
    }

    fun remapSingleTimetable(
        station: StationDepartures,
        requestTime: Long
    ): TimetableOuterClass.Timetable {
        val protoTimetable = TimetableOuterClass.Timetable.newBuilder()

        protoTimetable.place = remapPlace(station.location)

        for (departure in station.departures) {
            protoTimetable.addTrips(remapTimetableTrip(departure, station.location))
        }

        protoTimetable.serverRequestTimestamp = requestTime.toDouble()
        protoTimetable.serverResponseTimestamp = System.currentTimeMillis().toDouble()

        return protoTimetable.build()
    }

    fun remapTimetableSuggestions(
        departuresResult: QueryDeparturesResult,
        request: TimetableServiceOuterClass.TimetableSuggestionsSourcePostRequest,
        requestTime: Long
    ): TimetableSuggestionsOuterClass.TimetableSuggestions {
        val protoTimetableSuggestions = TimetableSuggestionsOuterClass.TimetableSuggestions.newBuilder()

        protoTimetableSuggestions.searchPlace = remapPlace(
            departuresResult.findStationDepartures(request.timetableSearch.stationID).location
        )

        for (departureLocation in departuresResult.stationDepartures) {
            protoTimetableSuggestions.addTimetables(
                remapSingleTimetable(
                    departureLocation,
                    System.currentTimeMillis()
                )
            )
        }

        protoTimetableSuggestions.serverRequestTimestamp = requestTime.toDouble()
        protoTimetableSuggestions.serverResponseTimestamp = System.currentTimeMillis().toDouble()

        return protoTimetableSuggestions.build()
    }

    fun remapTimetableSuggestions(
        departuresResult: QueryDeparturesResult,
        request: TimetableSearch,
        requestTime: Long
    ): TimetableSuggestionsOuterClass.TimetableSuggestions {
        val protoTimetableSuggestions = TimetableSuggestionsOuterClass.TimetableSuggestions.newBuilder()

        protoTimetableSuggestions.searchPlace = remapPlace(
            departuresResult.findStationDepartures(request.stationID).location
        )

        for (departureLocation in departuresResult.stationDepartures) {
            protoTimetableSuggestions.addTimetables(
                remapSingleTimetable(
                    departureLocation,
                    System.currentTimeMillis()
                )
            )
        }

        protoTimetableSuggestions.serverRequestTimestamp = requestTime.toDouble()
        protoTimetableSuggestions.serverResponseTimestamp = System.currentTimeMillis().toDouble()

        return protoTimetableSuggestions.build()
    }

    fun remapPlaceSearch(
        suggestLocationsResult: SuggestLocationsResult,
        requestTime: Long
    ): PlaceResultOuterClass.PlaceResult {
        val protoPlaceSearch = PlaceResultOuterClass.PlaceResult.newBuilder()

        if (suggestLocationsResult.suggestedLocations != null) {
            for (place in suggestLocationsResult.suggestedLocations) {
                protoPlaceSearch.addPlaces(remapPlace(place.location))
            }
        }

        protoPlaceSearch.serverRequestTimestamp = requestTime.toDouble()
        protoPlaceSearch.serverResponseTimestamp = System.currentTimeMillis().toDouble()

        return protoPlaceSearch.build()
    }

    fun remapPlaceNearbySearch(
        nearbyLocationsResult: NearbyLocationsResult,
        requestTime: Long
    ): PlaceResultOuterClass.PlaceResult {
        val protoPlaceSearch = PlaceResultOuterClass.PlaceResult.newBuilder()

        if (nearbyLocationsResult.locations != null) {
            for (place in nearbyLocationsResult.locations) {
                protoPlaceSearch.addPlaces(remapPlace(place))
            }
        }

        protoPlaceSearch.serverRequestTimestamp = requestTime.toDouble()
        protoPlaceSearch.serverResponseTimestamp = System.currentTimeMillis().toDouble()

        return protoPlaceSearch.build()
    }
}
