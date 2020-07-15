package com.code.weatherinfo.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.amitshekhar.utils.Utils
import com.code.weatherinfo.Login.LoginActivity
import com.code.weatherinfo.R
import com.code.weatherinfo.weatherforecast.WeatherForecastActivity
import okhttp3.internal.Util

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        try {
            supportActionBar?.hide()
        } catch (e: Throwable) {
            e.stackTrace
        }

        Handler().postDelayed(Runnable {
            var intent: Intent? = null
            var user = com.code.weatherinfo.utils.Utils.getDataInPreference(this, "isLogin")
            if (user.equals("true")) {
                intent = Intent(this@SplashActivity, WeatherForecastActivity::class.java)
            } else {
                intent = Intent(this@SplashActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 1500)
    }
}
