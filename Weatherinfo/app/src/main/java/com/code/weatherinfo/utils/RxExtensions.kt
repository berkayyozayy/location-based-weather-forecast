package com.code.weatherinfo.utils

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


private const val REQUEST_DELAY = 400L

//fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable {
//    compositeDisposable.add(this)
//    return this
//}

fun <T> Single<T>.addRequestDelay(): Single<T> {
    return zipWith(Single.timer(REQUEST_DELAY, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()), BiFunction { t1, _ -> t1 })
}

fun <T> Observable<T>.applySchedulers(): Observable<T> {
    return observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
}

fun <T> Maybe<T>.applySchedulers(): Maybe<T> {
    return observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
}

fun <T> Single<T>.applySchedulers(): Single<T> {
    return observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
}

fun Completable.applySchedulers(): Completable {
    return observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
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