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

import android.content.Context
import breezyweather.domain.location.model.Location
import breezyweather.domain.source.SourceFeature
import breezyweather.domain.weather.model.UV
import breezyweather.domain.weather.model.Wind
import breezyweather.domain.weather.reference.WeatherCode
import breezyweather.domain.weather.wrappers.CurrentWrapper
import breezyweather.domain.weather.wrappers.TemperatureWrapper
import breezyweather.domain.weather.wrappers.WeatherWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import org.breezyweather.BuildConfig
import org.breezyweather.R
import org.breezyweather.common.exceptions.ApiKeyMissingException
import org.breezyweather.common.exceptions.InvalidOrIncompleteDataException
import org.breezyweather.common.preference.EditTextPreference
import org.breezyweather.common.preference.Preference
import org.breezyweather.domain.settings.SourceConfigStore
import org.breezyweather.sources.xweather.json.XWeatherObservation
import org.breezyweather.unit.distance.Distance.Companion.meters
import org.breezyweather.unit.pressure.Pressure.Companion.hectopascals
import org.breezyweather.unit.ratio.Ratio.Companion.percent
import org.breezyweather.unit.speed.Speed.Companion.kilometersPerHour
import org.breezyweather.unit.temperature.Temperature.Companion.celsius
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class XWeatherService @Inject constructor(
    @ApplicationContext context: Context,
    @Named("JsonClient") client: Retrofit.Builder,
) : XWeatherServiceStub() {

    override val privacyPolicyUrl = "https://www.xweather.com/privacy-policy"

    private val mApi by lazy {
        client
            .baseUrl(XWEATHER_BASE_URL)
            .build()
            .create(XWeatherApi::class.java)
    }

    override val attributionLinks = mapOf(
        weatherAttribution to "https://www.xweather.com/"
    )

    override fun requestWeather(
        context: Context,
        location: Location,
        requestedFeatures: List<SourceFeature>,
    ): Observable<WeatherWrapper> {
        if (!isConfigured) {
            return Observable.error(ApiKeyMissingException())
        }

        return mApi.getObservation(
            station = stationId,
            clientId = getClientIdOrDefault(),
            clientSecret = getClientSecretOrDefault()
        ).map { result ->
            val ob = result.response?.ob
            if (result.success != true || ob == null) {
                throw InvalidOrIncompleteDataException()
            }
            WeatherWrapper(
                current = getCurrent(ob)
            )
        }
    }

    private fun getCurrent(
        ob: XWeatherObservation,
    ): CurrentWrapper {
        return CurrentWrapper(
            weatherText = ob.weather,
            weatherCode = getWeatherCode(ob.weatherPrimaryCoded, ob.cloudsCoded),
            temperature = TemperatureWrapper(
                temperature = ob.tempC?.celsius,
                feelsLike = ob.feelslikeC?.celsius
            ),
            wind = Wind(
                degree = ob.windDirDEG?.toDouble(),
                speed = ob.windSpeedKPH?.kilometersPerHour,
                gusts = ob.windGustKPH?.kilometersPerHour
            ),
            uV = ob.uvi?.let { UV(index = it) },
            relativeHumidity = ob.humidity?.toDouble()?.percent,
            dewPoint = ob.dewpointC?.celsius,
            pressure = ob.pressureMB?.hectopascals,
            cloudCover = ob.sky?.toDouble()?.percent,
            // Do not use the kilometers Kotlin extension. Someone claimed a copyright on it. See #2786
            visibility = ob.visibilityKM?.let { (it * 1000).meters }
        )
    }

    /**
     * The `weatherPrimaryCoded` field is in the format `coverage:intensity:weather`.
     * The third (weather) part holds the phenomenon. We fall back to the cloud coverage
     * code (`cloudsCoded`) when no precipitation/obscuration phenomenon is present.
     * See https://www.xweather.com/docs/weather-api/reference/weather-codes
     */
    private fun getWeatherCode(
        weatherPrimaryCoded: String?,
        cloudsCoded: String?,
    ): WeatherCode? {
        val phenomenon = weatherPrimaryCoded?.split(":")?.getOrNull(2)?.takeIf { it.isNotEmpty() }
        return when (phenomenon) {
            "A" -> WeatherCode.HAIL
            "BD", "BN", "BR", "H", "K" -> WeatherCode.HAZE
            "BS", "BY" -> WeatherCode.SNOW
            "F", "IF", "ZF" -> WeatherCode.FOG
            "L", "R", "RW", "UP" -> WeatherCode.RAIN
            "ZL", "ZR", "ZY" -> WeatherCode.SLEET
            "IP", "RS", "SI", "WM" -> WeatherCode.SLEET
            "S", "SW", "IC" -> WeatherCode.SNOW
            "T" -> WeatherCode.THUNDERSTORM
            else -> getCloudWeatherCode(cloudsCoded)
        }
    }

    private fun getCloudWeatherCode(
        cloudsCoded: String?,
    ): WeatherCode? {
        return when (cloudsCoded) {
            "CL", "FW" -> WeatherCode.CLEAR
            "SC" -> WeatherCode.PARTLY_CLOUDY
            "BK", "OV" -> WeatherCode.CLOUDY
            else -> null
        }
    }

    // CONFIG
    private val config = SourceConfigStore(context, id)

    private var stationId: String
        set(value) {
            config.edit().putString("station_id", value).apply()
        }
        get() = config.getString("station_id", null) ?: ""

    private var clientId: String
        set(value) {
            config.edit().putString("client_id", value).apply()
        }
        get() = config.getString("client_id", null) ?: ""

    private var clientSecret: String
        set(value) {
            config.edit().putString("client_secret", value).apply()
        }
        get() = config.getString("client_secret", null) ?: ""

    private fun getClientIdOrDefault(): String {
        return clientId.ifEmpty { BuildConfig.XWEATHER_CLIENT_ID }
    }

    private fun getClientSecretOrDefault(): String {
        return clientSecret.ifEmpty { BuildConfig.XWEATHER_CLIENT_SECRET }
    }

    override val isConfigured
        get() = stationId.isNotEmpty() &&
            getClientIdOrDefault().isNotEmpty() &&
            getClientSecretOrDefault().isNotEmpty()

    override val isRestricted
        get() = getClientIdOrDefault().isEmpty() || getClientSecretOrDefault().isEmpty()

    override fun getPreferences(context: Context): List<Preference> {
        return listOf(
            EditTextPreference(
                titleId = R.string.settings_weather_source_xweather_station_id,
                summary = { _, content ->
                    content.ifEmpty {
                        context.getString(R.string.settings_source_xweather_station_id_summary)
                    }
                },
                content = stationId,
                onValueChanged = {
                    stationId = it
                }
            ),
            EditTextPreference(
                titleId = R.string.settings_weather_source_xweather_client_id,
                summary = { c, content ->
                    content.ifEmpty {
                        c.getString(R.string.settings_source_default_value)
                    }
                },
                content = clientId,
                onValueChanged = {
                    clientId = it
                }
            ),
            EditTextPreference(
                titleId = R.string.settings_weather_source_xweather_client_secret,
                summary = { c, content ->
                    content.ifEmpty {
                        c.getString(R.string.settings_source_default_value)
                    }
                },
                content = clientSecret,
                onValueChanged = {
                    clientSecret = it
                }
            )
        )
    }

    companion object {
        private const val XWEATHER_BASE_URL = "https://data.api.xweather.com/"
    }
}
