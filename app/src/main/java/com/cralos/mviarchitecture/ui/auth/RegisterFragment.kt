package com.cralos.mviarchitecture.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import com.cralos.mviarchitecture.R
import com.cralos.mviarchitecture.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : BaseAuthFragment() {

    lateinit var inputEmail: EditText
    lateinit var inputUsername: EditText
    lateinit var inputPassword: EditText
    lateinit var inputConfirmPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRegisterFragment(view)
        Log.e(TAG, "ForgotPasswordFragment: ${viewModel.hashCode()}")
        subscribeObservers()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setRegistrationFields(
            RegistrationFields(
                inputEmail.text.toString(),
                inputUsername.text.toString(),
                inputPassword.text.toString(),
                inputConfirmPassword.text.toString()
            )
        )
    }

    fun initRegisterFragment(view: View) {
        inputEmail = view.findViewById(R.id.input_email)
        inputUsername = view.findViewById(R.id.input_username)
        inputPassword = view.findViewById(R.id.input_password)
        inputConfirmPassword = view.findViewById(R.id.input_password_confirm)
    }

    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            it.registrationFields?.let { registrationFields ->
                registrationFields.registration_email?.let { inputEmail.setText(it) }
                registrationFields.registration_username?.let { inputUsername.setText(it) }
                registrationFields.registration_password?.let { inputPassword.setText(it) }
                registrationFields.registration_confirm_passsword?.let { inputConfirmPassword.setText(it) }
            }
        })
    }


}