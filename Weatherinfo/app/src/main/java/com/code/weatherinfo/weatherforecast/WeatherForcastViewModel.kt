package com.code.weatherinfo.weatherforecast

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.code.tourism.network.ApiClient
import com.code.weatherinfo.model.custom.ForcasteList
import com.code.weatherinfo.model.custom.current.CurrentWeatherResult
import com.code.weatherinfo.room.User
import com.code.weatherinfo.room.UserDao
import com.code.weatherinfo.utils.Utils
import com.code.weatherinfo.utils.applySchedulers
import com.code.weatherinfo.utils.isEmailValid
import com.sallinggroup.logistics.model.ValidationMessageResponse
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_weather_forecast.*
import java.util.ArrayList

class WeatherForcastViewModel : ViewModel() {

    private val validationMessage = MutableLiveData<ValidationMessageResponse>()
    val loading by lazy { MutableLiveData<Boolean>() }

    fun getValidationMessage(): LiveData<ValidationMessageResponse> = validationMessage

    val statisticsWeeklyResponse: MutableLiveData<ArrayList<ForcasteList>> by lazy { MutableLiveData<ArrayList<ForcasteList>>() }

    val currentWeatherResponse: MutableLiveData<CurrentWeatherResult> by lazy { MutableLiveData<CurrentWeatherResult>() }
    var listFiltered: ArrayList<ForcasteList> = ArrayList()

    fun callStatisticsWeeklyAPI(lat: String, lon: String){
        listFiltered.clear()
        loading.value = true
        var url = "http://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&units=metric&appid=33f460211260849dbf62b92431917b95"
        ApiClient.getClient.getWeatherStatistics(url)
            .applySchedulers()
            .subscribe({
                if (it != null && it.list.size > 0) {
                    for (i in 0 until it.list.size) {
                        var item = it.list.get(i)
                        val strs = item.dt_txt.split(" ").toTypedArray()
                        if (!checkElementExist(strs[0])) {
                            listFiltered.add(item)
                        }
                    }

                    var sortedList: ArrayList<ForcasteList> = ArrayList<ForcasteList>()
                    for (i in (listFiltered.size - 1) downTo 0) {
                        sortedList.add(listFiltered.get(i))
                    }
                    //forcasteAdapter.updateForcateItems(sortedList)
                    statisticsWeeklyResponse.value = sortedList
                }

                loading.value = false
            }, {
                loading.value = false
                Log.e("Error = ${it.message}", "")
                validationMessage.postValue(ValidationMessageResponse("Something went while fetching weather data"))

            }

            )

    }

    private fun checkElementExist(toCheck: String): Boolean {
        if (listFiltered.size > 0) {
            for (i in 0 until listFiltered.size) {
                val item = listFiltered.get(i)
                val strs = item.dt_txt.split(" ").toTypedArray()
                if (strs[0].equals(toCheck)) {
                    return true
                }
            }
        }
        return false

    }

    fun callCurrentWeatherAPI(lat: String, lon: String){
        loading.value = true
        var url = "http://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$lon&exclude=hourly,daily,minutely&units=metric&appid=33f460211260849dbf62b92431917b95"

        ApiClient.getClient.getCurrentWeather(url)
            .applySchedulers()
            .subscribe({
                loading.value = false
                currentWeatherResponse.value =it
            }, {
                loading.value = false
                Log.e("Error = ${it.message}", "")
                //showMessage("Something went while fetching weather data")
                validationMessage.postValue(ValidationMessageResponse("Something went while fetching weather data"))
            }

            )

    }

    override fun onCleared() {
        super.onCleared()
    }
}