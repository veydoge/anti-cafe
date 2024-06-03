@file:OptIn(SupabaseInternal::class)

package com.example.anti_cafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.anti_cafe.data.AuthViewModel
import com.example.anti_cafe.data.EventsViewModel
import com.example.anti_cafe.data.GamesViewModel
import com.example.anti_cafe.data.RoomsViewModel
import com.example.anti_cafe.ui.Events
import com.example.anti_cafe.ui.GamePagePreload
import com.example.anti_cafe.ui.Games
import com.example.anti_cafe.ui.Main
import com.example.anti_cafe.ui.Profile
import com.example.anti_cafe.ui.ReservationConfirmationPage
import com.example.anti_cafe.ui.RoomPreload
import com.example.anti_cafe.ui.SignInScreen
import com.example.anti_cafe.ui.SignUpScreen
import com.example.anti_cafe.ui.theme.AnticafeTheme
import io.github.jan.supabase.annotations.SupabaseInternal


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
            if (navBackStackEntry?.destination?.route == "" ){

            }
            else {
                BottomAppBar(onNavigateEvents = {navHostController.navigate("events")},
                    onNavigateMain = {navHostController.navigate("main")},
                    onNavigateProfile = {navHostController.navigate("profile")},
                    onNavigateGames = {navHostController.navigate("games")})
            }

        }) {

        NavHost(navController = navHostController, startDestination = "main", modifier = Modifier.padding(it), route = "mainGraph"){


            composable("main"){ navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                val roomsViewModel: RoomsViewModel = viewModel(parentEntry)

                Main(authViewModel, roomsViewModel, {navHostController.navigate("room_page/$it")})
            }
            composable("events"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                val eventsViewModel: EventsViewModel = viewModel(parentEntry)
                Events(eventsViewModel, navHostController, authViewModel)
            }
            composable("games"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val gamesViewModel: GamesViewModel = viewModel(parentEntry)
                Games(gamesViewModel, {navHostController.navigate("games/$it")})
            }
            composable("games/{gameId}"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val gamesViewModel: GamesViewModel = viewModel(parentEntry)
                it.arguments!!.getString("gameId")
                    ?.let{it1 -> GamePagePreload(id = it1, gamesViewModel = gamesViewModel)}
            }
            composable("profile"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val eventsViewModel: EventsViewModel = viewModel(parentEntry)
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                val roomsViewModel: RoomsViewModel = viewModel(parentEntry)
                Profile(onNavigateSignUp = {navHostController.navigate("sign_up")}, onNavigateSignIn = {navHostController.navigate("sign_in")}, authViewModel = authViewModel, eventsViewModel = eventsViewModel, roomsViewModel = roomsViewModel)
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

            composable("room_page/{roomId}"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val authViewModel: AuthViewModel = viewModel(parentEntry)
                val roomsViewModel: RoomsViewModel = viewModel(parentEntry)
                it.arguments!!.getString("roomId")
                    ?.let { it1 -> RoomPreload(it1, authViewModel, roomsViewModel, onReserveConfirmationNavigate = {navHostController.navigate("reserve_confirmation")}) }
            }
            composable("reserve_confirmation"){
                val parentEntry = remember(navBackStackEntry){
                    navHostController.getBackStackEntry("mainGraph")
                }
                val roomsViewModel: RoomsViewModel = viewModel(parentEntry)
                val gamesViewModel: GamesViewModel = viewModel(parentEntry)
                val authViewModel: AuthViewModel = viewModel(parentEntry)

                ReservationConfirmationPage(roomsViewModel = roomsViewModel, gamesViewModel = gamesViewModel, authViewModel = authViewModel, onNavigateMain = {navHostController.navigate("main")})


            }
        }
    }





}


@Preview(showBackground = true)
@Composable
fun ProfilePreview(){
    Profile(authViewModel = viewModel(), eventsViewModel = viewModel(), roomsViewModel = viewModel())
}











@Composable
fun BottomAppBar(onNavigateMain: () -> Unit = {}, onNavigateEvents: () -> Unit = {}, onNavigateProfile: () -> Unit = {}, onNavigateGames: () -> Unit){
    var selected by remember{
        mutableStateOf(Icons.Filled.Home)
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround){
        IconButton(onClick = {
            selected = Icons.Filled.Event
            onNavigateEvents()
        }) {
            Icon(Icons.Filled.Event, contentDescription = null, tint = if (selected == Icons.Filled.Event) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray)
        }
        IconButton(onClick = {
            selected = Icons.Filled.Home
            onNavigateMain()
        }) {
            Icon(Icons.Filled.Home, contentDescription = null, tint = if (selected == Icons.Filled.Home) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray)
            
        }

        IconButton(onClick = {
            selected = Icons.Filled.Casino
            onNavigateGames()
        }) {
            Icon(Icons.Filled.Casino, contentDescription = null, tint = if (selected == Icons.Filled.Casino) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray)
        }
        IconButton(onClick = {
            selected = Icons.Filled.Person
            onNavigateProfile()
        }) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = if (selected == Icons.Filled.Person) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray)
        }

    }
}