package com.code.weatherinfo.Login

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.code.weatherinfo.room.User
import com.code.weatherinfo.room.UserDao
import com.code.weatherinfo.utils.Utils
import com.code.weatherinfo.utils.applySchedulers
import com.code.weatherinfo.utils.isEmailValid
import com.sallinggroup.logistics.model.ValidationMessageResponse
import kotlinx.android.synthetic.main.activity_login.*

class LoginViewModel : ViewModel() {

    private val validationMessage = MutableLiveData<ValidationMessageResponse>()

    val loading by lazy { MutableLiveData<Boolean>() }
    val loginResponse: MutableLiveData<User> by lazy { MutableLiveData<User>() }


    fun getValidationMessage(): LiveData<ValidationMessageResponse> = validationMessage
    lateinit var user: UserDao

    fun setDataInModel(user: UserDao) {
        this.user = user

    }

    fun validateResponse(email: String, password: String) {
        if (email.length == 0) {
            validationMessage.postValue(ValidationMessageResponse("Please enter email"))
            return
        } else if (!email.isEmailValid()) {
            validationMessage.postValue(ValidationMessageResponse("Please enter valid email address"))
            return
        } else if (password.length == 0) {
            validationMessage.postValue(ValidationMessageResponse("Please enter password"))
            return
        }


        callLoginRequest(email, password)
    }

    fun callLoginRequest(email: String, password: String) {
        loading.value = true
        user.getUser(email, password)
            .applySchedulers()
            .subscribe({
                loading.value = false
                //Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                // moveToDashBoard(it)
                loginResponse.value = it

            }, {
                loading.value = false
                loginResponse.value = null
                Log.e("Error = ${it.message}", "")
                //Toast.makeText(this, "User not existed", Toast.LENGTH_SHORT).show()
            })
    }

    override fun onCleared() {
        super.onCleared()
    }
}