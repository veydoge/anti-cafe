@file:OptIn(SupabaseInternal::class)

package com.example.anti_cafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.anti_cafe.data.AuthViewModel
import com.example.anti_cafe.ui.Profile
import com.example.anti_cafe.ui.SignUpScreen
import com.example.anti_cafe.ui.theme.AnticafeTheme
import com.example.anti_cafe.ui.Events
import com.example.anti_cafe.ui.SignInScreen
import io.github.jan.supabase.annotations.SupabaseInternal
import kotlinx.serialization.Serializable


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
    val navHostController: NavHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            if (navBackStackEntry?.destination?.route == "sign_up" ){

            }
            else {
                BottomAppBar(onEventsNavigate = {navHostController.navigate("events")},
                    onNavigateMain = {navHostController.navigate("main")},
                    onNavigateProfile = {navHostController.navigate("profile")})
            }
        }) {
        NavHost(navController = navHostController, startDestination = "main", modifier = Modifier.padding(it), route = "mainGraph"){


            composable("main"){ navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                Main()
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
                Profile(onSignUpNavigate = {navHostController.navigate("sign_up")},
                    onSignInNavigate = {navHostController.navigate("sign_in")},
                    authViewModel = authViewModel)
            }
            composable("sign_up"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                SignUpScreen({ navHostController.navigate("profile") }, authViewModel)
            }
            composable("sign_in"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                SignInScreen({ navHostController.navigate("profile") }, authViewModel)
            }
        }
    }





}


@Preview(showBackground = true)
@Composable
fun ProfilePreview(){
    Profile(authViewModel = viewModel())
}





@Serializable
data class Room(val id: Int, val name: String, val desc: String)


@Composable
fun Main(){


        Column(
            Modifier
                .fillMaxSize()
                ) {
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
fun BottomAppBar(onNavigateMain: () -> Unit = {}, onEventsNavigate: () -> Unit = {}, onNavigateProfile: () -> Unit = {}){
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
        IconButton(onClick = onEventsNavigate) {
            Icon(Icons.Outlined.Event, contentDescription = "null")
        }
        IconButton(onClick = onNavigateMain) {
            Icon(Icons.Filled.Home, contentDescription = "null")
            
        }
        IconButton(onClick = onNavigateProfile) {
            Icon(Icons.Filled.Person, contentDescription = "null")

        }

    }
}