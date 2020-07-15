package com.cralos.mviarchitecture.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.cralos.mviarchitecture.api.main.OpenApiMainService
import com.cralos.mviarchitecture.models.AccountProperties
import com.cralos.mviarchitecture.models.AuthToken
import com.cralos.mviarchitecture.persistence.AccountPropertiesDao
import com.cralos.mviarchitecture.repository.NetworkBoundResource
import com.cralos.mviarchitecture.session.SessionManager
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.main.account.state.AccountViewState
import com.cralos.mviarchitecture.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

@InternalCoroutinesApi
class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {

    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToTheInternet(), true, true
            ) {

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.pk,
                        cacheObject.email,
                        cacheObject.username
                    )
                }
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main) {
                    //finish by viewing the db cache
                    result.addSource(loadFromCache()) { viewState ->
                        onCompleteJob(DataState.data(data = viewState, response = null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties("Token ${authToken.token}")
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "AuthRepository: canceling on going jobs...")
    }

}