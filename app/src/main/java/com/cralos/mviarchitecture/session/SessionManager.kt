package com.cralos.mviarchitecture.session

import android.app.Application
import com.cralos.mviarchitecture.persistence.AuthTokenDao

class SessionManager
constructor(
    val authTokenDao : AuthTokenDao,
    val application: Application
){}