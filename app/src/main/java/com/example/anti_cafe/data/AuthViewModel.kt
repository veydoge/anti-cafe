package com.example.anti_cafe.data

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anti_cafe.data.network.SupabaseClient
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.launch

class AuthViewModel() : ViewModel(){
    var error by mutableStateOf("")
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
                error = e.error.toString()
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
                error = e.error.toString()
            }
        }
    }

    fun isUserLoggedIn(context: Context){
        viewModelScope.launch {
            try{
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
            catch (e: UnknownRestException) {
                error = e.error.toString()

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