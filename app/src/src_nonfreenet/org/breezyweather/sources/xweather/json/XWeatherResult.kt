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
 * Top-level envelope returned by the XWeather API.
 * When querying a single station through `/observations/{id}`, `response` is a single object.
 */
@Serializable
data class XWeatherResult(
    val success: Boolean? = null,
    val error: XWeatherError? = null,
    val response: XWeatherLocationResult? = null,
)

@Serializable
data class XWeatherError(
    val code: String? = null,
    val description: String? = null,
)

@Serializable
data class XWeatherLocationResult(
    @SerialName("id") val id: String? = null,
    @SerialName("loc") val loc: XWeatherLoc? = null,
    @SerialName("place") val place: XWeatherPlace? = null,
    @SerialName("ob") val ob: XWeatherObservation? = null,
)

@Serializable
data class XWeatherLoc(
    @SerialName("lat") val lat: Double? = null,
    @SerialName("long") val long: Double? = null,
)
