package com.example.anti_cafe.ui

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anti_cafe.data.AuthViewModel


@Composable
fun SignUpScreen(onNavigateProfile: () -> Unit = {}, authViewModel: AuthViewModel, modifier: Modifier = Modifier){
    val client = com.example.anti_cafe.data.network.SupabaseClient.client
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isEnabledSignUp = android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches() && password.length > 6


    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()){
        Text(text = "Регистрация", fontSize = 45.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 30.dp))
        OutlinedTextField(value = login,
            onValueChange = {
                login = it
            },
            supportingText = {
                if (!Patterns.EMAIL_ADDRESS.matcher(login).matches() && login.length > 0){
                    Text(text = "Почта не соответствует формату")
                }

            },

            modifier = Modifier.align(Alignment.CenterHorizontally),
            label = { Text(text = "Логин") })
        OutlinedTextField(value = password,
            onValueChange = {password = it},
            modifier = Modifier.align(Alignment.CenterHorizontally),
            label = { Text(text = "Пароль") })
        Button(enabled = isEnabledSignUp, onClick = {
            authViewModel.signUp(context, login, password)
            onNavigateProfile
        },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp),
            colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.primaryContainer)
        )
        {
            Text("Зарегистрироваться")
        }
        Text(text = authViewModel.error)
    }

}