package com.cralos.mviarchitecture.session

import android.app.Application
import com.cralos.mviarchitecture.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao : AuthTokenDao,
    val application: Application
){}