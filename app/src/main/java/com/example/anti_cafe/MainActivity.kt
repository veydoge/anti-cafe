@file:OptIn(SupabaseInternal::class)

package com.example.anti_cafe

import android.os.Bundle
import android.view.RoundedCorner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.anti_cafe.data.AuthViewModel
import com.example.anti_cafe.ui.theme.AnticafeTheme
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            AnticafeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AntiCafeApp()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AntiCafeApp(modifier: Modifier = Modifier.fillMaxSize()){
    val supabaseClient = com.example.anti_cafe.data.network.SupabaseClient.client
    val navHostController: NavHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            if (navBackStackEntry?.destination?.route == "sign_up" ){

            }
            else {
                BottomAppBar(navHostContoller = navHostController)
            }
        }) {
        NavHost(navController = navHostController, startDestination = "main", modifier = Modifier.padding(it), route = "mainGraph"){


            composable("main"){ navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                Main(navHostController)
            }
            composable("events"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                Events(navHostController)
            }
            composable("profile"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                Profile(navHostController, authViewModel)
            }
            composable("sign_up"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                SignUpScreen(navHostController, authViewModel)
            }
        }
    }





}


@Preview(showBackground = true)
@Composable
fun ProfilePreview(){
    Profile(rememberNavController(), viewModel())
}

@Composable
fun Profile(navHostController: NavHostController, authViewModel: AuthViewModel){
    Column(
        Modifier
            , verticalArrangement = Arrangement.SpaceAround){

        ProfileCard({navHostController.navigate("sign_up")}, authViewModel)
    }
}


@Composable
fun ProfileCard(onClick: () -> Unit, authViewModel: AuthViewModel){
    authViewModel.isUserLoggedIn(LocalContext.current)
    val userInfo = authViewModel.userInfo
    if (authViewModel.hasSession.value == false){
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(140.dp), shape = RoundedCornerShape(10.dp)) {
            Text(text = "Вы не зарегистрированы", fontSize = 30.sp, modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 15.dp))
            Button(onClick = onClick, modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp)) {
                Text(text = "Зарегистрироваться",)
            }
        }

    }
    else if (authViewModel.hasSession.value == true){
        var login: String = userInfo?.email.toString()
        Card {
            Row(modifier = Modifier.height(64.dp)){
                Icon(imageVector = Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(64.dp))
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center){
                    Text(text = "Ваш логин: $login", fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
    else if(authViewModel.hasSession.value == null){
        Text(text = "Загрузка")
    }



}

@Serializable
data class Room(val id: Int, val name: String, val desc: String)
@Composable
fun Main(navHostController: NavHostController){
    var rooms by remember { mutableStateOf<List<Room>>(listOf())}
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO){


        }
        
    }

        Column(
            Modifier
                .fillMaxSize()
                ) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                LazyColumn() {

                    items(rooms, key = { room -> room.id }) { room ->
                        Text(text = room.name) // почему то если заключить лямбду в скобки то перестает быть compose

                    }
                }


            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Main")

            }

        }


}

@Composable
fun Events(navHostController: NavHostController){

        Column(
            Modifier
                .fillMaxSize()
                , verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = "Events")

        }



}

@Composable
fun SignUpScreen(navHostController: NavHostController, authViewModel: AuthViewModel, modifier: Modifier = Modifier){
    val client = com.example.anti_cafe.data.network.SupabaseClient.client
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}
    val isEnabledSignUp = android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches() && password.length > 6
    var authViewModel: AuthViewModel = viewModel<AuthViewModel>()


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
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches() && login.length > 0){
                    Text(text = "Почта не соответствует формату")
                }

            },

            modifier = Modifier.align(Alignment.CenterHorizontally),
            label = { Text(text = "Логин")})
        OutlinedTextField(value = password,
            onValueChange = {password = it},
            modifier = Modifier.align(Alignment.CenterHorizontally),
            label = { Text(text = "Пароль")})
        Button(enabled = isEnabledSignUp, onClick = {
            authViewModel.signUp(context, login, password)
            navHostController.navigate("profile")
        },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp),
            colors = buttonColors(contentColor = MaterialTheme.colorScheme.primaryContainer)
        )
        {
            Text("Зарегистрироваться")
        }
        Text(text = authViewModel._error)
    }

}


@Composable
fun BottomAppBar(navHostContoller: NavHostController){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
        IconButton(onClick = { navHostContoller.navigate("events") }) {
            Icon(Icons.Outlined.Event, contentDescription = "null")
        }
        IconButton(onClick = {  navHostContoller.navigate("main") }) {
            Icon(Icons.Filled.Home, contentDescription = "null")
            
        }
        IconButton(onClick = { navHostContoller.navigate("profile") }) {
            Icon(Icons.Filled.Person, contentDescription = "null")

        }

    }
}