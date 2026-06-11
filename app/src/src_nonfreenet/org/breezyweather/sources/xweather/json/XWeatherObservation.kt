/*
 * This file is part of Breezy Weather.
 *
 * Breezy Weather is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, version 3 of the License.
 *
 * Breezy Weather is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Breezy Weather. If not, see <https://www.gnu.org/licenses/>.
 */

package org.breezyweather.sources.xweather.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The `ob` (observation) object returned by the XWeather observations endpoint.
 * See https://www.xweather.com/docs/weather-api/endpoints/observations
 */
@Serializable
data class XWeatherObservation(
    @SerialName("timestamp") val timestamp: Long? = null,
    @SerialName("dateTimeISO") val dateTimeISO: String? = null,
    @SerialName("tempC") val tempC: Double? = null,
    @SerialName("feelslikeC") val feelslikeC: Double? = null,
    @SerialName("dewpointC") val dewpointC: Double? = null,
    @SerialName("humidity") val humidity: Int? = null,
    @SerialName("windSpeedKPH") val windSpeedKPH: Double? = null,
    @SerialName("windGustKPH") val windGustKPH: Double? = null,
    @SerialName("windDirDEG") val windDirDEG: Int? = null,
    @SerialName("pressureMB") val pressureMB: Double? = null,
    @SerialName("precipMM") val precipMM: Double? = null,
    @SerialName("weather") val weather: String? = null,
    @SerialName("weatherPrimaryCoded") val weatherPrimaryCoded: String? = null,
    @SerialName("cloudsCoded") val cloudsCoded: String? = null,
    @SerialName("sky") val sky: Int? = null,
    @SerialName("visibilityKM") val visibilityKM: Double? = null,
    @SerialName("uvi") val uvi: Double? = null,
)
