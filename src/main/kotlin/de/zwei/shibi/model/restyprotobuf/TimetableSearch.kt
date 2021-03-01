/**
 * Open European Public Transit Format (OEPTF)
 * An european standard for public transit.
 *
 * OpenAPI spec version: 1.0.0
 * Contact: tristan.marsell@axitera.de
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
package de.zwei.shibi.model.restyprotobuf

/**
 * This object represents the search body given to the timetable search.
 * @param stationID The ID of your station/place you want the timetable from
 * @param lookupTime Datetime where you want to search for
 * @param isArrivalTime Shows if your time is for departure or arrival of your first timetable object.
 */
data class TimetableSearch(

    /* The ID of your station/place you want the timetable from */
    val stationID: kotlin.String? = null,
    /* Datetime where you want to search for */
    val lookupTime: kotlin.String? = null,
    /* Shows if your time is for departure or arrival of your first timetable object. */
    val isArrivalTime: kotlin.Boolean? = null
)
