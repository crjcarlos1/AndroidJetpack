package com.cralos.mviarchitecture.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.cralos.mviarchitecture.R
import com.cralos.mviarchitecture.ui.BaseActivity
import com.cralos.mviarchitecture.ui.ResponseType
import com.cralos.mviarchitecture.ui.main.MainActivity
import com.cralos.mviarchitecture.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
class AuthActivity : BaseActivity() ,NavController.OnDestinationChangedListener{

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this, providerFactory).get(AuthViewModel::class.java)
        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)
        subscribeObservers()
    }

    private fun subscribeObservers() {

        viewModel.dataState.observe(this, Observer { dataState ->

            dataState.data?.let { data ->

                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            Log.d(TAG, "AuthActivity: DataState: $it")
                            viewModel.setAuthToken(it)
                        }
                    }
                }

                data.response?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        when (it.responseType) {
                            is ResponseType.Dialog -> {
                                //inflate error handle
                            }
                            is ResponseType.Toast -> {
                                //show toast
                            }
                            is ResponseType.None -> {
                                Log.e(TAG, "AuthActivity, Response: ${it.message}")
                            }
                        }
                    }
                }

            }

        })

        viewModel.viewState.observe(this, Observer {
            it.authToken?.let {
                sessionManager.login(it)
            }
        })

        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.e(TAG, "AuthActivity: subscribeObservers: AuthToken: $authToken")
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navMainActivity()
            }
        })
    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }


}