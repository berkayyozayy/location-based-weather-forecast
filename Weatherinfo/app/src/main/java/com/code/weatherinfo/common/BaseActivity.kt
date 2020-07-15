package com.code.weatherinfo.common

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.code.weatherinfo.R
import com.code.weatherinfo.room.BerkayDatabase
import com.code.weatherinfo.room.UserDao
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.HttpException
import java.util.regex.Pattern


data class ApiException(@SerializedName("message") val mMessage: String) : Throwable()

open class BaseActivity : AppCompatActivity() {


    private var berkayDatabase: BerkayDatabase? = null

    lateinit var baseContext: BaseActivity

    internal lateinit var containter: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseContext = this

        initDatabase()
    }

    fun showLoading(show: Boolean) {
        try {

            containter = findViewById<LinearLayout>(R.id.container)
            if (show) {
                if (containter != null) {
                    containter.bringToFront()
                    containter.setVisibility(View.VISIBLE)
                }
            } else {
                if (containter != null) {
                    containter.setVisibility(View.GONE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initDatabase() {
        berkayDatabase = BerkayDatabase.getDatabase(this@BaseActivity)
    }

    fun getUserDao(): UserDao {
        return berkayDatabase?.userDao()!!
    }


    fun showMessage(message: String) {
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_SHORT).show()
    }

    fun View.snackBar(@StringRes id: Int) {
        Snackbar.make(this, id, Snackbar.LENGTH_LONG).show()
    }

    fun String.isEmailValid() =
        Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        ).matcher(this).matches()


    protected fun <T> Single<T>.checkApiErrorSingle(): Single<T> =
        onErrorResumeNext { throwable ->
            parseHttpExceptionSingle(throwable)
        }

    protected fun Completable.checkApiErrorCompletable(): Completable =
        onErrorResumeNext { throwable ->
            parseHttpExceptionCompletable(throwable)
        }

    protected fun <T> Observable<T>.checkApiErrorObservable(): Observable<T> =
        doOnError { throwable ->
            parseHttpExceptionObservable(throwable)
        }

    private fun parseHttpExceptionObservable(throwable: Throwable): Observable<Any> {
        if (throwable !is HttpException) {
            return Observable.error(throwable)
        }
        val error = getError(throwable)
        if (error != null) {
            return Observable.error(ApiException(error))
        }

        return Observable.error(throwable)
    }

    private fun <T> parseHttpExceptionSingle(throwable: Throwable): Single<T> {
        //Timber.e("throwable = $throwable")
        if (throwable is ApiException) {
            return Single.error(throwable)
        }
        if (throwable !is HttpException) {
            return Single.error(throwable)
        }
        val error = getError(throwable)
        // Timber.e("error = $error")
        if (error != null) {
            return Single.error(ApiException(error))
        }

        return Single.error(throwable)
    }

    private fun parseHttpExceptionCompletable(throwable: Throwable): Completable {
        if (throwable !is HttpException) {
            return Completable.error(throwable)
        }
        val error = getError(throwable)
        if (error != null) {
            return Completable.error(ApiException(error))
        }
        return Completable.error(throwable)
    }

    private fun getError(throwable: Throwable): String? {
        val parser = JsonParser()
        val body = parser.parse((throwable as HttpException).response()?.errorBody()?.string())
            .asJsonObject
        return null
    }


}
