package com.example.anti_cafe.ui

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anti_cafe.data.AuthViewModel


@Composable
fun SignUpScreen(onNavigateProfile: () -> Unit = {}, authViewModel: AuthViewModel, modifier: Modifier = Modifier){
    val client = com.example.anti_cafe.data.network.SupabaseClient.client
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("")}
    var phoneNumber by remember { mutableStateOf("")}
    val isEnabledSignUp = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 6

    LaunchedEffect(null) {
        authViewModel.error = ""
    }

    if (authViewModel.hasSession.value == true){
        onNavigateProfile()
    }


    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize()){
        Text(text = "Регистрация", fontSize = 45.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 30.dp))
        EmailField(email = email, onValueChange = {email = it}, Modifier.align(Alignment.CenterHorizontally))

        PasswordField(password = password, onValueChange = {password = it}, Modifier.align(Alignment.CenterHorizontally))

        OutlinedTextField(value = name,
            onValueChange = {name = it},
            label = {Text(text = "Имя")},
            supportingText = { if (name.length != 0 && name.length < 3) Text(text = "Имя должно содержать больше трех символов", color = MaterialTheme.colorScheme.error) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(value = phoneNumber,
            onValueChange = {phoneNumber = it},
            label = {Text(text = "Номер ")},
            supportingText = {if (!Patterns.PHONE.matcher(phoneNumber).matches() && phoneNumber != "") Text(text = "Телефон не соответствует формату", color = MaterialTheme.colorScheme.error)},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        Button(enabled = isEnabledSignUp, onClick = {
            authViewModel.hasSession.value = null
            authViewModel.signUp(context, email, password, name, phoneNumber)
        },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp),
            colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.primaryContainer)
        )
        {
            Text("Зарегистрироваться")
        }

        Text(text = when (authViewModel.error){
            "" -> ""
            "User already registered" -> "Пользователь с такой почтой уже зарегистрирован"
            else -> "Неизвестная ошибка"
        },
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 14.dp).align(Alignment.CenterHorizontally))


    }

}