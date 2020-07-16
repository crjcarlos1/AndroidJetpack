package com.cralos.mviarchitecture.ui.main.account

import androidx.lifecycle.LiveData
import com.cralos.mviarchitecture.models.AccountProperties
import com.cralos.mviarchitecture.repository.main.AccountRepository
import com.cralos.mviarchitecture.session.SessionManager
import com.cralos.mviarchitecture.ui.BaseViewModel
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.main.account.state.AccountStateEvent
import com.cralos.mviarchitecture.ui.main.account.state.AccountViewState
import com.cralos.mviarchitecture.util.AbsentLiveData
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {
            is AccountStateEvent.GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                } ?: AbsentLiveData.create()
            }
            is AccountStateEvent.UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        accountRepository.saveAccountProperties(
                            authToken, AccountProperties(
                                pk,
                                stateEvent.email,
                                stateEvent.username
                            )
                        )
                    }
                } ?: AbsentLiveData.create()
            }
            is AccountStateEvent.ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmNewPasword
                    )
                } ?: AbsentLiveData.create()
            }
            is AccountStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accoutnProperties == accountProperties) {
            return
        }
        update.accoutnProperties = accountProperties
        _viewState.value = update
    }

    fun logout() {
        sessionManager.logout()
    }

}