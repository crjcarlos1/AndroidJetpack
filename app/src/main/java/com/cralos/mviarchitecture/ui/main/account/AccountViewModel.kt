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
import javax.inject.Inject

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
                return AbsentLiveData.create()
            }
            is AccountStateEvent.UpdateAccountPropertiesEvent -> {
                return AbsentLiveData.create()
            }
            is AccountStateEvent.ChangePasswordEvent -> {
                return AbsentLiveData.create()
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