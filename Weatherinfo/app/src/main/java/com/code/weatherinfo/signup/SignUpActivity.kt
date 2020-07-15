package com.code.weatherinfo.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.code.weatherinfo.Login.LoginViewModel
import com.code.weatherinfo.MainActivity
import com.code.weatherinfo.R
import com.code.weatherinfo.common.BaseActivity
import com.code.weatherinfo.room.User
import com.code.weatherinfo.utils.Utils
import com.code.weatherinfo.utils.applySchedulers
import com.code.weatherinfo.weatherforecast.WeatherForecastActivity
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {


    lateinit var signupViewModel: SignupViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initUI()
    }

    private fun initUI() {
        if (supportActionBar != null) {
            supportActionBar?.setTitle("Sign up")
            supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        signupViewModel = ViewModelProviders.of(this).get(SignupViewModel::class.java)
        signupViewModel.setDataInModel(getUserDao())
        signupViewModel.loading.observe(this, loadingObserver)
        signupViewModel.loginResponse.observe(this, loginResponseObserver)
        signupViewModel.getValidationMessage().observe(this, Observer {
            showMessage(it.message)
        })

    }

    private val loadingObserver = Observer<Boolean> {
        if (it) {
            showLoading(true)
        } else {
            showLoading(false)
        }
    }

    private val loginResponseObserver = Observer<Any> {
        try {
            if (it == -1) {
                showMessage("User Already existed")
            } else {
                showMessage("User Created")
                moveTODashBoard(it as User)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun onSignUpClick(view: View) {
        val user = User(
            email = txtEmailAddress.text.toString(),
            password = txtPassword.text.toString()
        )
        signupViewModel.validateResponse(user)
    }

    private fun moveTODashBoard(validUser: User) {
        val intent = Intent(this@SignUpActivity, WeatherForecastActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val bundle = Bundle()
        bundle.putParcelable("user", validUser)
        intent.putExtras(bundle)
        Utils.saveUserObject(this, "user", validUser)
        Utils.saveDataInPreference(this, "isLogin", "true")
        startActivity(intent)
        finish()

    }

}
