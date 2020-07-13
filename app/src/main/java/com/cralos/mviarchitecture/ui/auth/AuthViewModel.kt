package com.cralos.mviarchitecture.ui.auth

import androidx.lifecycle.LiveData
import com.cralos.mviarchitecture.models.AuthToken
import com.cralos.mviarchitecture.repository.auth.AuthRepository
import com.cralos.mviarchitecture.ui.BaseViewModel
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.auth.state.AuthStateEvent
import com.cralos.mviarchitecture.ui.auth.state.AuthViewState
import com.cralos.mviarchitecture.ui.auth.state.LoginFields
import com.cralos.mviarchitecture.ui.auth.state.RegistrationFields
import com.cralos.mviarchitecture.util.AbsentLiveData
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class AuthViewModel
@InternalCoroutinesApi
@Inject
constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when (stateEvent) {
            is AuthStateEvent.LoginAttempEvent -> {
                return authRepository.attempLogin(
                    stateEvent.email, stateEvent.password
                )
            }
            is AuthStateEvent.RegisterAttempEvent -> {
                return authRepository.attempRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }
            is AuthStateEvent.CheckPreviosAuthEvent -> {
                return AbsentLiveData.create()
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields) {
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    fun cancelActiveJobs(){
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}