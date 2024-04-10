package com.example.anti_cafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.anti_cafe.ui.theme.AnticafeTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    val supabaseClient : SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://ektrcyegonyejgbpckqs.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVrdHJjeWVnb255ZWpnYnBja3FzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTI3NzU3MzYsImV4cCI6MjAyODM1MTczNn0.0dy4Zg1AJTqToCZUJkQDySacAO6tZ4PULlKeRKctCyM"
    ){
        install(Postgrest)
    }
    val navHostController: NavHostController = rememberNavController()

    NavHost(navController = navHostController, startDestination = "main"){
        composable("main"){
            Main(supabaseClient)
        }
        composable("events"){
            Events()
        }
        composable("profile"){
            Profile()
        }
    }




}


@Composable
fun Profile(){
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = "Profile")
        Button(onClick = {} ){
            Text("123")
        }
        Button(onClick = {}){
            Text("123")
        }
        Button(onClick = {}){
            Text("123")
        }
    }

}

@Serializable
data class Room(val id: Int, val name: String, val desc: String)
@Composable
fun Main(supabaseClient: SupabaseClient){
    var rooms by remember { mutableStateOf<List<Room>>(listOf())}
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO){
            rooms = supabaseClient.from("Rooms").select().decodeList<Room>()
        }
        
    }
    Column(Modifier.fillMaxSize()){
        Column(
            Modifier
                .weight(1f)
                .fillMaxSize()) {
            LazyColumn() {

                items(rooms, key = {room -> room.id}){
                    room ->
                    Text(text = room.name) // почему то если заключить лямбду в скобки то перестает быть compose

                }
            }


        }
        Column(
            Modifier
                .weight(1f)
                .fillMaxSize(), verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = "Main")
            Button(onClick = {} ){
                Text("123")
            }
            Button(onClick = {}){
                Text("123")
            }
            Button(onClick = {}){
                Text("123")
            }
        }

    }

}

@Composable
fun Events(){
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = "Events")
        Button(onClick = {} ){
            Text("123")
        }
        Button(onClick = {}){
            Text("123")
        }
        Button(onClick = {}){
            Text("123")
        }
    }

}
