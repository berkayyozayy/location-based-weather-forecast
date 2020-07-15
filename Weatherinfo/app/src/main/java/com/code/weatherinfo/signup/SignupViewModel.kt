package com.code.weatherinfo.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.code.weatherinfo.room.User
import com.code.weatherinfo.room.UserDao
import com.code.weatherinfo.utils.applySchedulers
import com.code.weatherinfo.utils.isEmailValid
import com.sallinggroup.logistics.model.ValidationMessageResponse
import io.reactivex.schedulers.Schedulers

class SignupViewModel : ViewModel() {

    private val validationMessage = MutableLiveData<ValidationMessageResponse>()

    val loading by lazy { MutableLiveData<Boolean>() }
    val loginResponse: MutableLiveData<Any> by lazy { MutableLiveData<Any>() }


    fun getValidationMessage(): LiveData<ValidationMessageResponse> = validationMessage
    lateinit var user: UserDao

    fun setDataInModel(user: UserDao) {
        this.user = user

    }

    fun validateResponse(validUser: User) {
        if (validUser.email?.length == 0) {
            validationMessage.postValue(ValidationMessageResponse("Please enter email"))
            return
        } else if (!validUser.email!!.isEmailValid()) {
            validationMessage.postValue(ValidationMessageResponse("Please enter valid email address"))
            return
        } else if (validUser.password!!.length == 0) {
            validationMessage.postValue(ValidationMessageResponse("Please enter password"))
            return
        }


        signUpRequest(validUser)
    }

    fun signUpRequest(validUser: User) {
        loading.value = true
        user.checkUserExist(validUser.email)
            .flatMap {
                if (it > 0) {
                    io.reactivex.Single.just(-1)
                } else {
                    insertUser(validUser).subscribeOn(Schedulers.io())
                }
            }.applySchedulers()
            .subscribe({
                loading.value =false
                loginResponse.value = validUser

            }, {
                loading.value =false
                loginResponse.value = null
                Log.e("Error = ${it.message}", "")
                validationMessage.postValue(ValidationMessageResponse("Error creating User"))
            })
    }

    private fun insertUser(validUser: User): io.reactivex.Single<Long> {
        return user.insert(validUser)
    }

    override fun onCleared() {
        super.onCleared()
    }
}