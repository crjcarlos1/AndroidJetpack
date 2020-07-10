package com.cralos.mviarchitecture.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cralos.mviarchitecture.models.AccountProperties
import com.cralos.mviarchitecture.models.AuthToken

@Database(
    entities = [
        AuthToken::class, AccountProperties::class
    ],
    version = 1
)
abstract class AppDataBase : RoomDatabase(){

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}