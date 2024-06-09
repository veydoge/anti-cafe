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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anti_cafe.data.AuthViewModel

@Preview(showBackground = true)

@Composable
fun SignInScreen(onNavigateProfile: () -> Unit = {}, authViewModel: AuthViewModel = viewModel(), modifier: Modifier = Modifier){
    var email by rememberSaveable{ mutableStateOf("") }
    var password by rememberSaveable{ mutableStateOf("") }

    val isEnabledSignUp = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 6


    val context = LocalContext.current

    LaunchedEffect(null) {
        authViewModel.error = ""
    }
    if (authViewModel.hasSession.value == true){
        onNavigateProfile()
    }


    Column(modifier = modifier.fillMaxSize()){
        Text(text = "Вход", fontSize = 45.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 30.dp))


        EmailField(email = email, onValueChange = {email = it}, Modifier.align(Alignment.CenterHorizontally))

        PasswordField(password = password, onValueChange = {password = it}, Modifier.align(Alignment.CenterHorizontally))

        Button(enabled = isEnabledSignUp, onClick = {
            authViewModel.hasSession.value = null
            authViewModel.signIn(context, email, password)
        },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp),
            colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.primaryContainer)
        )
        {
            Text("Вход")
        }
        Text(text = when (authViewModel.error){
            "" -> ""
            "invalid_grant" -> "Введен неверный логин или пароль"
            else -> "Неизвестная ошибка"
        },
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 14.dp).align(Alignment.CenterHorizontally))


    }
}

@Composable
fun EmailField(email: String = "", onValueChange: (String) -> Unit = {}, modifier: Modifier = Modifier){
    OutlinedTextField(value = email,
        singleLine = true,
        onValueChange = onValueChange,
        supportingText = {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.length > 0){
                Text(text = "Почта не соответствует формату", color = MaterialTheme.colorScheme.error)
            }
        },

        modifier = modifier,
        label = { Text(text = "Почта") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@Composable
fun PasswordField(password: String = "", onValueChange: (String) -> Unit = {}, modifier: Modifier = Modifier){
    var passwordVisible by rememberSaveable{ mutableStateOf(false)}
    OutlinedTextField(value = password,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(text = "Пароль") },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        supportingText = {if (password.length != 0 && password.length <= 6)
            Text(text = "Пароль должен содержать больше 6 символов", color = MaterialTheme.colorScheme.error)},
        trailingIcon ={
            val image = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
            IconButton(onClick = {passwordVisible = !passwordVisible}) {
                Icon(imageVector = image, description)

            }

        })

}