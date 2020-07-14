package com.cralos.mviarchitecture.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.cralos.mviarchitecture.api.auth.OpenApiAuthService
import com.cralos.mviarchitecture.api.auth.network_responses.LoginResponse
import com.cralos.mviarchitecture.api.auth.network_responses.RegistrationResponse
import com.cralos.mviarchitecture.models.AccountProperties
import com.cralos.mviarchitecture.models.AuthToken
import com.cralos.mviarchitecture.persistence.AccountPropertiesDao
import com.cralos.mviarchitecture.persistence.AuthTokenDao
import com.cralos.mviarchitecture.repository.NetworkBoundResource
import com.cralos.mviarchitecture.session.SessionManager
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.Response
import com.cralos.mviarchitecture.ui.ResponseType
import com.cralos.mviarchitecture.ui.auth.state.AuthViewState
import com.cralos.mviarchitecture.ui.auth.state.LoginFields
import com.cralos.mviarchitecture.ui.auth.state.RegistrationFields
import com.cralos.mviarchitecture.util.AbsentLiveData
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.cralos.mviarchitecture.util.GenericApiResponse
import com.cralos.mviarchitecture.util.PreferenceKeys
import com.cralos.mviarchitecture.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import javax.inject.Inject

@InternalCoroutinesApi
class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharePresEditor: SharedPreferences.Editor
) {
    private val TAG = "AppDebug"
    private var repositoryJob: Job? = null

    fun attempLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldErrors = LoginFields(email, password).isValidForLogin()
        if (!loginFieldErrors.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginFieldErrors, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet(), true
        ) {

            // nt use in this case
            override suspend fun createCacheRequestAndReturn() {
                TODO("Not yet implemented")
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<LoginResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")

                //incorrect login credentials counts as a 200 response from server, so need to handle that
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) { // 'response' -> campo del json
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //don't care about result. Just insert if doesn't exist  because foreing key relationship
                //with auth token table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1 if failture
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                //verify result
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                //save in sharePreferences
                saveAuthenticatedUserToPrefes(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

        }.asLiveData()

    }

    fun attempRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {
        val registrationFieldErrors =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()

        if (!registrationFieldErrors.equals(RegistrationFields.RegistrationError.none())) {
            return returnErrorResponse(registrationFieldErrors, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet(), true
        ) {

            override suspend fun createCacheRequestAndReturn() {
                TODO("Not yet implemented")
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<RegistrationResponse>) {
                Log.d(TAG, "handleApiSuccessResponse: $response")
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                //don't care about result. Just insert if doesn't exist  because foreing key relationship
                //with auth token table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                //will return -1 if failture
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                //verify result
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                        )
                    )
                }

                saveAuthenticatedUserToPrefes(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.asLiveData()

    }

    fun checkPreviusAtuhUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUserEmail: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail.isNullOrBlank()) {
            Log.d(TAG, "checkPreviusAtuhUser: no previously authenticated user found...")
            return returnNoTokenFound()
        }

        return object : NetworkBoundResource<Void, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false
        ) {
            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                    Log.d(TAG, "checkPreviusAtuhUser: searching for token: $accountProperties ")
                    accountProperties?.let {
                        if (accountProperties.pk > -1) {
                            authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                                if (authToken != null) {
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            )
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }
                    Log.d(TAG, "checkPreviusAtuhUser: Auth token not found...")
                    onCompleteJob(
                        DataState.data(
                            data = null, response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None()
                            )
                        )
                    )
                }
            }

            //not use in this case
            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<Void>) {

            }

            //not use in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.asLiveData()

    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None())
                )
            }
        }
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "AuthRepository: Cancelling on going jobs...")
        repositoryJob?.cancel()
    }

    private fun returnErrorResponse(
        errorMessage: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    response = Response(
                        message = errorMessage,
                        responseType = responseType
                    )
                )
            }
        }
    }


    private fun saveAuthenticatedUserToPrefes(email: String) {
        sharePresEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharePresEditor.apply()
    }

}