package com.cralos.mviarchitecture.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.cralos.mviarchitecture.api.auth.OpenApiAuthService
import com.cralos.mviarchitecture.models.AuthToken
import com.cralos.mviarchitecture.persistence.AccountPropertiesDao
import com.cralos.mviarchitecture.persistence.AuthTokenDao
import com.cralos.mviarchitecture.session.SessionManager
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.Response
import com.cralos.mviarchitecture.ui.ResponseType
import com.cralos.mviarchitecture.ui.auth.state.AuthViewState
import com.cralos.mviarchitecture.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.cralos.mviarchitecture.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    fun attempLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.login(email, password)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is GenericApiResponse.ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = AuthViewState(
                                        authToken = AuthToken(
                                            response.body.pk,
                                            response.body.token
                                        )
                                    ),
                                    response = null
                                )
                            }
                            is GenericApiResponse.ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                            is GenericApiResponse.ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }

    fun attempRegistration(email: String, username : String,password: String, confirmPassword : String): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.register(email,username,password,confirmPassword)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is GenericApiResponse.ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = AuthViewState(
                                        authToken = AuthToken(
                                            response.body.pk,
                                            response.body.token
                                        )
                                    ),
                                    response = null
                                )
                            }
                            is GenericApiResponse.ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                            is GenericApiResponse.ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }

}