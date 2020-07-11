package com.cralos.mviarchitecture.util

import androidx.lifecycle.LiveData

/**return empty livedata object*/
class AbsentLiveData<T : Any?> private constructor() : LiveData<T>() {

    init {
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }
    }

}