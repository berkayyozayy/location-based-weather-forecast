package com.code.weatherinfo.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {

    @Insert
    fun insert(user: User):io.reactivex.Single<Long>

    @Query("SELECT * FROM user_table where email =:email AND password=:password")
    fun getUser(email: String, password: String): io.reactivex.Single<User>

    @Query("SELECT COUNT(*) FROM user_table where email =:email")
    fun checkUserExist(email: String?): io.reactivex.Single<Int>


}