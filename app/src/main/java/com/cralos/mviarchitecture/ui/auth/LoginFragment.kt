package com.cralos.mviarchitecture.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import com.cralos.mviarchitecture.R
import com.cralos.mviarchitecture.ui.auth.state.LoginFields

class LoginFragment : BaseAuthFragment() {

    lateinit var inputEmail: EditText
    lateinit var inputPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoginFragment(view)
        Log.e(TAG, "ForgotPasswordFragment: ${viewModel.hashCode()}")
        subscribeObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        val email: String = inputEmail.text.toString()
        val password: String = inputPassword.text.toString()
        viewModel.setLoginFields(LoginFields(email, password))
    }

    fun initLoginFragment(view: View) {
        inputEmail = view.findViewById(R.id.input_email)
        inputPassword = view.findViewById(R.id.input_password)
    }

    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.loginFields?.let { loginFields ->
                loginFields.login_email?.let { inputEmail.setText(it) }
                loginFields.login_password?.let { inputPassword.setText(it) }
            }
        })
    }


}