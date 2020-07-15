package com.code.tourism.network

import com.code.weatherinfo.model.custom.ForcastResult
import com.code.weatherinfo.model.custom.current.CurrentWeatherResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url


interface APIService {

    //http://api.openweathermap.org/data/2.5/forecas
    //http://api.openweathermap.org/data/2.5/onecall?lat=37.0827&lon=37.3213&exclude=hourly,daily,minutely&appid=33f460211260849dbf62b92431917b95

    @GET
    fun getWeatherStatistics(@Url url: String): Observable<ForcastResult>


    @GET
    fun getCurrentWeather(@Url url: String): Observable<CurrentWeatherResult>



}