package com.cralos.mviarchitecture.ui.auth.state

sealed class AuthStateEvent {

    data class LoginAttempEvent(val email: String, val password: String) : AuthStateEvent()

    data class RegisterAttempEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirm_password: String
    ) : AuthStateEvent()

    class CheckPreviosAuthEvent() : AuthStateEvent()

    class None() : AuthStateEvent()

}