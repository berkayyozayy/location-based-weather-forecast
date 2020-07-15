package com.code.weatherinfo.model.custom

import com.google.gson.annotations.SerializedName

data class ForcasteList(
    @SerializedName("dt") val dt: Int,
    @SerializedName("main") val main: CurrentDayForcaste,
    @SerializedName("weather") val weather: List<ForcasteWeather>,
    @SerializedName("dt_txt") val dt_txt: String
)