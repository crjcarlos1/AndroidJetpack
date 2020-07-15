package com.cralos.mviarchitecture.repository.main

import android.util.Log
import com.cralos.mviarchitecture.api.main.OpenApiMainService
import com.cralos.mviarchitecture.persistence.AccountPropertiesDao
import com.cralos.mviarchitecture.session.SessionManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {

    private val TAG = "AppDebug"
    private val repositoryJob: Job? = null

    fun cancelActiveJobs(){
        Log.d(TAG, "AuthRepository: canceling on going jobs...")
    }

}