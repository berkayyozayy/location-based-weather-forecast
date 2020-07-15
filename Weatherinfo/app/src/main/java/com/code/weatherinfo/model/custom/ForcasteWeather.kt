package com.code.weatherinfo.model.custom

import com.google.gson.annotations.SerializedName

data class ForcasteWeather(
    @SerializedName("id") val id : Int,
    @SerializedName("main") val main : String,
    @SerializedName("description") val description : String,
    @SerializedName("icon") val icon : String
)