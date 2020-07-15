package com.code.weatherinfo.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.code.weatherinfo.MainActivity
import com.code.weatherinfo.R
import com.code.weatherinfo.common.BaseActivity
import com.code.weatherinfo.room.User
import com.code.weatherinfo.signup.SignUpActivity
import com.code.weatherinfo.utils.Utils
import com.code.weatherinfo.utils.applySchedulers
import com.code.weatherinfo.weatherforecast.WeatherForecastActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {

    var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    companion object {
        lateinit var mGoogleSignInClient: GoogleSignInClient;
    }

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.code.weatherinfo.R.layout.activity_login)
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        initUI()
    }

    private fun initUI() {
        try {
            supportActionBar?.hide()
        } catch (e: Throwable) {
            e.stackTrace
        }
        sign_in_button.setOnClickListener {
            onGoogleSignUpClick()
        }

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        loginViewModel.setDataInModel(getUserDao())
        loginViewModel.loading.observe(this, loadingObserver)
        loginViewModel.loginResponse.observe(this, loginResponseObserver)
        loginViewModel.getValidationMessage().observe(this, Observer {
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

    private val loginResponseObserver = Observer<User> {
        try {
            if (it!=null) {
                moveToDashBoard(it)
            } else{
                showMessage("User is not existed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun onLoginClick(view: View) {
        loginViewModel.validateResponse(
            txtEmailAddress.text.toString(),
            txtPassword.text.toString()
        )
    }

    private fun moveToDashBoard(validUser: User?) {
        val intent = Intent(this@LoginActivity, WeatherForecastActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        if (validUser != null) {
            val bundle = Bundle()
            bundle.putParcelable("user", validUser)
            intent.putExtras(bundle)
            Utils.saveDataInPreference(this,"isLogin","true")
            Utils.saveUserObject(this, "user", validUser)
        }
        startActivity(intent)
        finish()

    }

    fun onSignUpClick(view: View) {
        val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun onGoogleSignUpClick() {
        showLoading(true)
        val signInIntent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, 1)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        showLoading(false)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            var userName = account?.displayName
            var email = account?.email
            var user = User(
                userId = 1,
                email = email,
                password = "123"
            )
            showMessage("Hello, $userName you login into the Successfully")
            moveToDashBoard(user)
            // Signed in successfully, show authenticated UI.
            //updateUI(account)
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }


}
