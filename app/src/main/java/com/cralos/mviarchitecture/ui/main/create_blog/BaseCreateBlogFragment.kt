package com.cralos.mviarchitecture.ui.main.create_blog

import android.content.Context
import android.util.Log
import com.cralos.mviarchitecture.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment

abstract class BaseCreateBlogFragment : DaggerFragment() {

    val TAG: String = "AppDebug"

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
    }
}