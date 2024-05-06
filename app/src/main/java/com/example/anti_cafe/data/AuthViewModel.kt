package com.example.anti_cafe.data

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anti_cafe.data.network.SupabaseClient
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel() : ViewModel(){
    var _error by mutableStateOf("1234666")
    var hasSession: MutableState<Boolean?> = mutableStateOf(null)
    var userInfo: UserInfo? = null
    fun signUp(context: Context, _email: String, _password: String){
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    email = _email
                    password = _password
                }
                saveToken(context)
            }
            catch(e: BadRequestRestException){
                _error = e.error.toString()
            }

        }
    }

    fun signIn(context: Context, _email: String, _password: String){
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    email = _email
                    password = _password
                }
                saveToken(context)
            }
            catch(e: BadRequestRestException){
                _error = e.error.toString()
            }
        }
    }

    fun isUserLoggedIn(context: Context){
        viewModelScope.launch {
            val token = getToken(context)
            if (token.isNullOrEmpty()){
                hasSession.value = false
            } else{
                userInfo = SupabaseClient.client.auth.retrieveUser(token)
                SupabaseClient.client.auth.refreshCurrentSession()
                saveToken(context)
                hasSession.value = true
            }

        }
    }

    fun saveToken(context: Context){
        viewModelScope.launch {
            val accessToken = SupabaseClient.client.auth.currentAccessTokenOrNull()
            val sharedPrefs = SharedPrefsHelper(context)
            sharedPrefs.saveStringData("accessToken", accessToken)
        }
    }
    fun getToken(context: Context): String? {
        val sharedPref = SharedPrefsHelper(context)
        return sharedPref.getStringData("accessToken")
    }
}