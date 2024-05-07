package com.example.anti_cafe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.anti_cafe.data.AuthViewModel


@Composable
fun Profile(onNavigateSignUp: () -> Unit = {}, onNavigateSignIn: () -> Unit = {}, authViewModel: AuthViewModel){
    Column(
        Modifier
        , verticalArrangement = Arrangement.SpaceAround){

        ProfileCard(onNavigateSignIn = onNavigateSignIn,
            onNavigateSignUp = onNavigateSignUp,
            authViewModel = authViewModel)
    }
}
@Composable
fun ProfileCard(onNavigateSignUp: () -> Unit = {},
                onNavigateSignIn: () -> Unit = {},
                authViewModel: AuthViewModel){
    val context = LocalContext.current

    authViewModel.hasSession.value = null
    authViewModel.isUserLoggedIn(context)
  


    val userInfo = authViewModel.userInfo
    if (authViewModel.hasSession.value == false){
        NoAuthCard("",
            Modifier
            .fillMaxWidth()
            .height(140.dp),
        )
    }
    else if (authViewModel.hasSession.value == true){
        var login: String = userInfo?.email.toString()
        Card {
            Row(modifier = Modifier.height(64.dp)){
                Icon(imageVector = Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(64.dp))
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center){
                    Text(text = "Ваш логин: $login", fontSize = 24.sp, modifier = Modifier.align(
                        Alignment.CenterHorizontally))
                }
            }
        }
    }
    else if (authViewModel.error != ""){
        Text(text = authViewModel.error.toString())
    }
    else if(authViewModel.hasSession.value == null) {
        Text(text = "Загрузка")
    }
}

@Composable
fun NoAuthCard(
    error: String = "",
    modifier: Modifier = Modifier,
    onNavigateSignUp: () -> Unit = {},
    onNavigateSignIn: () -> Unit = {}
){
    Card(modifier = modifier, shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = "Вы не авторизованы", fontSize = 30.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 15.dp))
        Button(onClick = onNavigateSignUp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 20.dp)) {
            Text(text = "Войти",)
        }
        Button(onClick = onNavigateSignIn, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 20.dp)) {
            Text(text = "Зарегистрироваться",)
        }
    }

}