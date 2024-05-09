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
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthViewModel() : ViewModel(){
    var error by mutableStateOf("")
    var hasSession: MutableState<Boolean?> = mutableStateOf(null)
    var userAuthInfo: UserInfo? = null
    fun signUp(context: Context, _email: String, _password: String, _name: String, _phone: String){
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    email = _email
                    password = _password
                    data = buildJsonObject {
                        put("name", _name)
                        put("phone", _phone)
                    }
                }
                saveToken(context)
                isUserLoggedIn(context)
            }
            catch(e: BadRequestRestException){
                error = e.error.toString()
                hasSession.value = false
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
                isUserLoggedIn(context)
            }
            catch(e: BadRequestRestException){
                error = e.error.toString()
                hasSession.value = false
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
                    userAuthInfo = SupabaseClient.client.auth.retrieveUser(token)
                    SupabaseClient.client.auth.refreshCurrentSession()
                    saveToken(context)
                    hasSession.value = true
                }
            }
            catch (e: UnknownRestException) {
                error = e.error.toString()
                hasSession.value = false
            }
        }
    }

    fun logout(context: Context){
        viewModelScope.launch{

            SupabaseClient.client.auth.clearSession()
            clearToken(context)
            userAuthInfo = null
            hasSession.value = false
        }
    }

    fun clearToken(context: Context){
        viewModelScope.launch {
            val sharedPrefs = SharedPrefsHelper(context)
            sharedPrefs.saveStringData("accessToken", "")
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