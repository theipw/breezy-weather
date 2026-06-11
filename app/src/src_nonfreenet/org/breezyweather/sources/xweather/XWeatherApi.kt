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

package org.breezyweather.sources.xweather

import io.reactivex.rxjava3.core.Observable
import org.breezyweather.sources.xweather.json.XWeatherResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * XWeather (AerisWeather) data API.
 * Example: https://data.api.xweather.com/observations/KMSP?format=json&plimit=1&filter=1min&client_id=...&client_secret=...
 */
interface XWeatherApi {
    @GET("observations/{station}")
    fun getObservation(
        @Path("station") station: String,
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("format") format: String = "json",
        @Query("plimit") plimit: Int = 1,
        @Query("filter") filter: String = "1min",
    ): Observable<XWeatherResult>
}
