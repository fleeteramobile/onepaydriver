

import com.google.gson.annotations.SerializedName

data class Route(
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline,
    @SerializedName("legs") val legs: List<Leg> // A route can have multiple legs (segments)
)


data class OverviewPolyline(
    @SerializedName("points") val points: String // This is the *encoded* polyline string
)

data class Leg(
    @SerializedName("steps") val steps: List<Step>,
    @SerializedName("distance") val distance: TextValue,
    @SerializedName("duration") val duration: TextValue,
    @SerializedName("start_address") val startAddress: String,
    @SerializedName("end_address") val endAddress: String,
    @SerializedName("start_location") val startLocation: LatLngLiteral,
    @SerializedName("end_location") val endLocation: LatLngLiteral
)

data class Step(
    @SerializedName("polyline") val polyline: OverviewPolyline,
    @SerializedName("start_location") val startLocation: LatLngLiteral,
    @SerializedName("end_location") val endLocation: LatLngLiteral,
    @SerializedName("html_instructions") val htmlInstructions: String,
    @SerializedName("distance") val distance: TextValue,
    @SerializedName("duration") val duration: TextValue,
    @SerializedName("travel_mode") val travelMode: String
)

data class TextValue(
    @SerializedName("text") val text: String, // e.g., "10.5 km"
    @SerializedName("value") val value: Int // e.g., 10500 (meters)
)

data class LatLngLiteral(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)


