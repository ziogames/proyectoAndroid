package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sigefiv.app.data.SessionManager

class LoginViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                LoginViewModel::class.java
            )
        ) {

            return LoginViewModel(
                sessionManager =
                    SessionManager(context),
                context =
                    context
            ) as T
        }

        throw IllegalArgumentException(
            "Clase ViewModel desconocida"
        )
    }
}