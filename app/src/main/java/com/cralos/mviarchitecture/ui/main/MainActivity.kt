package com.cralos.mviarchitecture.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.cralos.mviarchitecture.R
import com.cralos.mviarchitecture.ui.BaseActivity
import com.cralos.mviarchitecture.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeObservers()
    }

    fun subscribeObservers(){
        sessionManager.cachedToken.observe(this, Observer {authToken ->
            Log.e(TAG, "MainActivity: subscribeObservers: AuthToken: $authToken")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null){
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this,AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

}