package com.example.anti_cafe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anti_cafe.data.AuthViewModel


@Composable
fun Profile(onNavigateSignUp: () -> Unit = {}, onNavigateSignIn: () -> Unit = {}, authViewModel: AuthViewModel){
    Column(
        Modifier
        , verticalArrangement = Arrangement.SpaceAround){

        ProfileCard(onNavigateSignIn = onNavigateSignIn, onNavigateSignUp = onNavigateSignUp, authViewModel = authViewModel)
    }
}

@Composable
fun ProfileCard(onNavigateSignUp: () -> Unit = {}, onNavigateSignIn: () -> Unit = {}, authViewModel: AuthViewModel = viewModel()){
    val context = LocalContext.current
  


    val userInfo = authViewModel.userAuthInfo
    if (authViewModel.hasSession.value == false){
        NoAuthCard(onNavigateSignUp = onNavigateSignUp, onNavigateSignIn = onNavigateSignIn)
    }
    else if (authViewModel.hasSession.value == true){
        var login: String = userInfo?.email.toString()
        WithAuthCard(login = login, onLogout = {authViewModel.logout(context)})

    }
    else if (authViewModel.error != ""){
        Text(text = authViewModel.error.toString())
    }
    else if(authViewModel.hasSession.value == null) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primaryContainer)
    }
}

@Composable
fun NoAuthCard(onNavigateSignUp: () -> Unit, onNavigateSignIn: () -> Unit, modifier: Modifier = Modifier){
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp), shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = "Вы не авторизованы", fontSize = 30.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 15.dp))
        Button(onClick = onNavigateSignIn, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 20.dp)) {
            Text(text = "Войти",)
        }
        Button(onClick = onNavigateSignUp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 20.dp)) {
            Text(text = "Зарегистрироваться",)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WithAuthCard(onLogout: () -> Unit = {}, login: String = "", modifier: Modifier = Modifier){
    Card {
        Row(modifier = Modifier.height(64.dp)){
            Icon(imageVector = Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(64.dp))
            Column(modifier = Modifier
                .weight(1f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center){
                Text(text = "Ваш логин: $login", fontSize = 24.sp, modifier = Modifier.align(
                    Alignment.CenterHorizontally))
            }
            IconButton(onClick = onLogout, modifier = Modifier.size(64.dp)) {
                Icon(imageVector = Icons.Filled.Logout, contentDescription = "logout")
            }
        }
    }
}