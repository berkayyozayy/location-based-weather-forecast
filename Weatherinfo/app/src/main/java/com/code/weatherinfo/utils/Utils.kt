package com.code.weatherinfo.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.code.weatherinfo.room.User
import com.google.gson.Gson
import java.util.regex.Pattern


object Utils {


    fun saveDataInPreference(context: Context, key: String, value: String) {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putString(key, value)
        myEdit.apply()
    }


    fun getDataInPreference(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        return sharedPreferences.getString(key, "").toString()
    }


    fun saveUserObject(context: Context, key: String, user: User?) {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        val gson = Gson()
        val jsonUser = gson.toJson(user)
        myEdit.putString(key, jsonUser)
        myEdit.apply()
    }

    fun getUserObject(context: Context, key: String): User? {
        val sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE)
        var userString = sharedPreferences.getString(key, "").toString()
        val gson = Gson()
        var user: User = gson.fromJson(userString, User::class.java)

        return user
    }




}