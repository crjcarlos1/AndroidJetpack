package com.cralos.mviarchitecture.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.cralos.mviarchitecture.R
import com.cralos.mviarchitecture.util.GenericApiResponse

class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "ForgotPasswordFragment: ${viewModel.hashCode()}")

        viewModel.testRegister().observe(viewLifecycleOwner, Observer {response ->
            when(response){
                is GenericApiResponse.ApiSuccessResponse->{
                    Log.e(TAG, "REGISTRATION_RESPONSE: ${response.body}")
                }
                is GenericApiResponse.ApiErrorResponse->{
                    Log.e(TAG, "REGISTRATION_RESPONSE: ${response.errorMessage}")
                }
                is GenericApiResponse.ApiEmptyResponse->{
                    Log.e(TAG, "REGISTRATION Empty Response :(")
                }
            }
        })

    }


}