package com.cralos.mviarchitecture.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.cralos.mviarchitecture.R
import com.cralos.mviarchitecture.ui.DataState
import com.cralos.mviarchitecture.ui.DataStateChangeListener
import com.cralos.mviarchitecture.ui.Response
import com.cralos.mviarchitecture.ui.ResponseType
import com.cralos.mviarchitecture.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class ForgotPasswordFragment : BaseAuthFragment() {

    lateinit var webView: WebView
    lateinit var stateChangeListener: DataStateChangeListener
    val webInteractionCallback: WebAppInterface.OnWebInteractionCallback =
        object : WebAppInterface.OnWebInteractionCallback {
            override fun onSuccess(message: String) {
                Log.d(TAG, "onSuccess: a reset link will be sent to $message")
                onPasswordResetLink()
            }

            override fun oError(errorMessage: String) {
                Log.e(TAG, "oError: $errorMessage")
                val dataState = DataState.error<Any>(
                    response = Response(errorMessage, ResponseType.Dialog())
                )
                stateChangeListener.onDataStateChange(dataState = dataState)
            }

            override fun onLoading(isLoading: Boolean) {
                Log.d(TAG, "onLoading...")
                GlobalScope.launch(Dispatchers.Main) {
                    stateChangeListener.onDataStateChange(
                        DataState.loading(
                            isLoading = isLoading,
                            cachedData = null
                        )
                    )
                }
            }
        }

    private fun onPasswordResetLink() {
        GlobalScope.launch(Dispatchers.Main) {
            parent_view.removeView(webView)
            webView.destroy()

            val animation = TranslateAnimation(
                password_reset_done_container.width.toFloat(), 0f, 0f, 0f
            )
            animation.duration=500
            password_reset_done_container.startAnimation(animation)
            password_reset_done_container.visibility=View.VISIBLE

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "ForgotPasswordFragment: ${viewModel.hashCode()}")
        webView=view.findViewById(R.id.webview)
        loadPasswordResetWebView()
        return_to_launcher_fragment.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DatStateChangeListener")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun loadPasswordResetWebView() {
        /**Mostramos progressBar*/
        stateChangeListener.onDataStateChange(
            DataState.loading(
                isLoading = true,
                cachedData = null
            )
        )

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stateChangeListener.onDataStateChange(
                    DataState.loading(
                        isLoading = false,
                        cachedData = null
                    )
                )
            }
        }

        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(
            WebAppInterface(webInteractionCallback),
            "AndroidTextListener"
        )

    }

    class WebAppInterface constructor(private val callBack: OnWebInteractionCallback) {
        private val TAG = "AppDebug"

        @JavascriptInterface
        fun onSuccess(email: String) {
            callBack.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(error: String) {
            callBack.onSuccess(error)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callBack.onLoading(isLoading)
        }

        interface OnWebInteractionCallback {
            fun onSuccess(message: String)
            fun oError(errorMessage: String)
            fun onLoading(isLoading: Boolean)
        }

    }

}