package com.cralos.mviarchitecture.ui

interface DataStateChangeListener {
    fun onDataStateChange(dataState: DataState<*>?)
    fun expandAppBar()
}