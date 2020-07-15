package com.code.weatherinfo.weatherforecast

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.code.tourism.network.ApiClient
import com.code.weatherinfo.Login.LoginActivity
import com.code.weatherinfo.common.BaseActivity
import com.code.weatherinfo.model.custom.ForcasteList
import com.code.weatherinfo.model.custom.current.CurrentWeatherResult
import com.code.weatherinfo.room.User
import com.code.weatherinfo.utils.Utils
import com.code.weatherinfo.utils.applySchedulers
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import io.nlopez.smartlocation.SmartLocation
import kotlinx.android.synthetic.main.activity_weather_forecast.*
import java.util.*


class WeatherForecastActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var permissionsToRequest: ArrayList<String>
    private val permissionsRejected = ArrayList<String>()
    private val permissions = ArrayList<String>()
    private val ALL_PERMISSIONS_RESULT = 101
    private val PLACES_API_RESULT = 102
    lateinit var latLng: LatLng
    private lateinit var mMap: GoogleMap

    lateinit var forcasteAdapter: ForcasteAdapter
    lateinit var listFiltered: ArrayList<ForcasteList>
    lateinit var list: ArrayList<ForcasteList>

    lateinit var weatherForcastViewModel: WeatherForcastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.code.weatherinfo.R.layout.activity_weather_forecast)
        initUI()
    }

    private fun initUI() {

        permissions.add(ACCESS_FINE_LOCATION)
        permissions.add(ACCESS_COARSE_LOCATION)

        permissionsToRequest = findUnAskedPermissions(permissions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size > 0) {
                requestPermissions(permissionsToRequest!!.toTypedArray(), ALL_PERMISSIONS_RESULT)
            } else {
                fetchLocation()
            }
        } else {
            fetchLocation()
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(com.code.weatherinfo.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val apiKey = getString(com.code.weatherinfo.R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        try {
            supportActionBar?.hide()
        } catch (e: Throwable) {
            e.stackTrace
        }

        textPlaceSearch.setOnTouchListener(View.OnTouchListener { v, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                onSearchCalled()
            }

            true // return is important...
        })

        btnLogout.setOnClickListener {
            onLogoutClicked()
        }

        listFiltered = ArrayList<ForcasteList>()
        list = ArrayList<ForcasteList>()
        setForcasteList(list)

        weatherForcastViewModel =
            ViewModelProviders.of(this).get(WeatherForcastViewModel::class.java)
        weatherForcastViewModel.loading.observe(this, loadingObserver)
        weatherForcastViewModel.currentWeatherResponse.observe(this, currentWeatherResponse)
        weatherForcastViewModel.statisticsWeeklyResponse.observe(this, weeklyStatisticsObserver)
        weatherForcastViewModel.getValidationMessage().observe(this, androidx.lifecycle.Observer {
            showMessage(it.message)
        })

    }

    private val loadingObserver = androidx.lifecycle.Observer<Boolean> {
        if (it) {
            showLoading(true)
        } else {
            showLoading(false)
        }
    }

    private val weeklyStatisticsObserver = androidx.lifecycle.Observer<ArrayList<ForcasteList>> {
        if (it.size > 0) {
            forcasteAdapter.updateForcateItems(it)
        }
    }

    private val currentWeatherResponse = androidx.lifecycle.Observer<CurrentWeatherResult> {
        try {
            if (it != null && it.current != null) {
                current_temp_value.text = it.current.temp.toString()
                current_humidity_value.text = it.current.humidity.toString()
                if (it.current.weather != null) {
                    current_weather_value.text = it.current.weather[0].description
                } else {
                    current_weather_value.text = ""
                }
                layout_weather_forcast.visibility = View.VISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun setForcasteList(items: ArrayList<ForcasteList>) {
        rv_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        try {
            forcasteAdapter = ForcasteAdapter(this, items)
            rv_list.adapter = forcasteAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun onLogoutClicked() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        Utils.saveDataInPreference(this, "isLogin", "false")
        startActivity(intent)
        finish()
    }

    fun onSearchCalled() {
        val fields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        val intent =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
        startActivityForResult(intent, PLACES_API_RESULT)

        //setCountry("TR")
    }

    private fun findUnAskedPermissions(wanted: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()
        for (perm in wanted) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }
        return result
    }

    private fun hasPermission(permission: String): Boolean {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
            }
        }
        return true
    }

    private fun canMakeSmores(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }


    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {

            ALL_PERMISSIONS_RESULT -> {
                for (perms in permissionsToRequest!!) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms)
                    }
                }

                if (permissionsRejected.size > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(dialog: DialogInterface, which: Int) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                permissionsRejected.toTypedArray(),
                                                ALL_PERMISSIONS_RESULT
                                            )
                                        }
                                    }
                                })
                            return
                        }
                    }

                } else {
                    fetchLocation()
                }
            }


        }
    }


    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@WeatherForecastActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun fetchLocation() {
        showMessage("Getting your location")
        showLoading(true)
        SmartLocation.with(this).location()
            .oneFix()
            .start { location ->
                showLoading(false)
                val latLngString = location.latitude.toString() + "," + location.longitude
                latLng = LatLng(location.latitude, location.longitude)
                mMap.isMyLocationEnabled = true
                val currentLocationLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLocationLatLng).title("Your current location"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocationLatLng, 18f))
                //getWeeklyStatistics(location.latitude.toString(), location.longitude.toString())
                weatherForcastViewModel.callStatisticsWeeklyAPI(location.latitude.toString(),
                    location.longitude.toString()
                )

                weatherForcastViewModel.callCurrentWeatherAPI( location.latitude.toString(), location.longitude.toString())
            }
    }


    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACES_API_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                Log.i(
                    "WeatherForecastActivity",
                    "Place: " + place.name + ", " + place.id + ", " + place.address
                )
                //Toast.makeText(this@WeatherForecastActivity, "ID: " + place.id + "address:" + place.address + "Name:" + place.name + " latlong: " + place.latLng, Toast.LENGTH_LONG).show()
                val address = place.address
                // do query with address
                if (place != null) {
                    weatherForcastViewModel.callCurrentWeatherAPI(
                        place.latLng?.latitude.toString(),
                        place.latLng?.longitude.toString()
                    )
                    weatherForcastViewModel.callStatisticsWeeklyAPI(place.latLng?.latitude.toString(),
                        place.latLng?.longitude.toString()
                    )

                    mMap.clear()
                    val currentLocationLatLng = LatLng(place.latLng?.latitude?.toDouble()!!, place.latLng?.longitude?.toDouble()!!)
                    mMap.addMarker(MarkerOptions().position(currentLocationLatLng).title("Your current location"))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 18f))
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Toast.makeText(
                    this@WeatherForecastActivity,
                    "Error: " + status.statusMessage,
                    Toast.LENGTH_LONG
                ).show()
                Log.i("WeatherForecastActivity", status.statusMessage!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }


}
