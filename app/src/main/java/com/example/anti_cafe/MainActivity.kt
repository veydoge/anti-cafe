@file:OptIn(SupabaseInternal::class)

package com.example.anti_cafe

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.anti_cafe.data.AuthViewModel
import com.example.anti_cafe.data.network.SupabaseClient
import com.example.anti_cafe.ui.Profile
import com.example.anti_cafe.ui.SignUpScreen
import com.example.anti_cafe.ui.theme.AnticafeTheme
import com.example.anti_cafe.ui.Events
import com.example.anti_cafe.ui.SignInScreen
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import io.ktor.util.Identity.decode
import kotlinx.coroutines.selects.select
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.sql.Timestamp
import java.util.Date


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

@Serializable
data class Event(val name: String, val description: String, val main_image: String?, val date: LocalDateTime,  val flag: Boolean)

@Preview(showBackground = true)
@Composable
fun AntiCafeApp(modifier: Modifier = Modifier.fillMaxSize()){
    val navHostController: NavHostController = rememberNavController()
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    LaunchedEffect(null) {



    }

    Scaffold(
        bottomBar = {
            if (navBackStackEntry?.destination?.route == "" ){

            }
            else {
                BottomAppBar(onNavigateEvents = {navHostController.navigate("events")},
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

                Main(authViewModel)
            }
            composable("events"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                Events(navHostController, authViewModel)
            }
            composable("profile"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                Profile(onNavigateSignUp = {navHostController.navigate("sign_up")}, onNavigateSignIn = {navHostController.navigate("sign_in")}, authViewModel = authViewModel)
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
fun Main(authViewModel: AuthViewModel){



    val context = LocalContext.current
    LaunchedEffect(null) {
        if (authViewModel.hasSession.value == null){
            authViewModel.isUserLoggedIn(context)
        }
    }



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
fun BottomAppBar(onNavigateMain: () -> Unit = {}, onNavigateEvents: () -> Unit = {}, onNavigateProfile: () -> Unit = {}){
    var selected by remember{
        mutableStateOf(Icons.Filled.Home)
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
        IconButton(onClick = {
            selected = Icons.Filled.Event
            onNavigateEvents()
        }) {
            Icon(Icons.Filled.Event, contentDescription = "null", tint = if (selected == Icons.Filled.Event) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray)
        }
        IconButton(onClick = {
            selected = Icons.Filled.Home
            onNavigateMain()
        }) {
            Icon(Icons.Filled.Home, contentDescription = "null", tint = if (selected == Icons.Filled.Home) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray)
            
        }
        IconButton(onClick = {
            selected = Icons.Filled.Person
            onNavigateProfile()
        }) {
            Icon(Icons.Filled.Person, contentDescription = "null", tint = if (selected == Icons.Filled.Person) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray)

        }

    }
}