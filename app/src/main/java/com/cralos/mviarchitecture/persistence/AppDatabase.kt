package com.cralos.mviarchitecture.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cralos.mviarchitecture.models.AccountProperties
import com.cralos.mviarchitecture.models.AuthToken
import com.cralos.mviarchitecture.models.BlogPost

@Database(
    entities = [
        AuthToken::class, AccountProperties::class,BlogPost::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getBlogPostDao(): BlogPostDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}