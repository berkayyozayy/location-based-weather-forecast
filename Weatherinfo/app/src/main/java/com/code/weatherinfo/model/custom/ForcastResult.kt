package com.code.weatherinfo.model.custom

import com.google.gson.annotations.SerializedName

class ForcastResult (

    @SerializedName("cod") val cod : Int,
    @SerializedName("message") val message : Int,
    @SerializedName("cnt") val cnt : Int,
    @SerializedName("list") val list : ArrayList<ForcasteList>

)