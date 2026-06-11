/*
 * This file is part of Breezy Weather.
 */

package org.breezyweather.sources.xweather.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XWeatherLocationResult(
    @SerialName("id") val id: String? = null,
    @SerialName("loc") val loc: XWeatherPlace? = null,
    // Your JSON uses "periods" for the data list
    @SerialName("periods") val periods: List<XWeatherObservation>? = null
)

@Serializable
data class XWeatherObservation(
    val timestamp: Long? = null,
    @SerialName("tempC") val tempC: Float? = null,
    @SerialName("tempF") val tempF: Float? = null,
    @SerialName("feelslikeC") val feelslikeC: Float? = null,
    @SerialName("dewpointC") val dewpointC: Float? = null,
    @SerialName("humidity") val humidity: Int? = null,
    @SerialName("windSpeedKPH") val windSpeedKPH: Float? = null,
    @SerialName("windGustKPH") val windGustKPH: Float? = null,
    @SerialName("windDir") val windDir: String? = null,
    @SerialName("windDirDEG") val windDirDEG: Int? = null,
    @SerialName("pressureMB") val pressureMB: Float? = null,
    @SerialName("precipMM") val precipMM: Float? = null,
    @SerialName("weather") val weather: String? = null,
    @SerialName("weatherPrimary") val weatherPrimary: String? = null,
    @SerialName("cloudsCoded") val cloudsCoded: String? = null,
    @SerialName("icon") val icon: String? = null,
    @SerialName("uvi") val uvi: Float? = null,
    @SerialName("visibilityKM") val visibilityKM: Float? = null,
    @SerialName("sky") val sky: Int? = null
)
