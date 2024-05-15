package com.example.anti_cafe.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.anti_cafe.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.util.Identity.decode
import kotlinx.serialization.Serializable


@Preview(showBackground = true)
@Composable
fun RoomPreviewMiniCard(){
    RoomMiniCard(room = Room("Имя", "123", 1, 5, listOf(RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_1221-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_0320-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/IMG_8249-scaled.jpg"))))
}



@Preview(showBackground = true)

@Composable
fun Main(modifier: Modifier = Modifier){
    Column{
        val navHostController: NavHostController = rememberNavController()
        var selectedTabIndex by rememberSaveable {
            mutableStateOf(0)
        }
        Text(text = "Главная", fontSize = 34.sp, textAlign = TextAlign.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp))

        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(true, {selectedTabIndex = 0
            navHostController.navigate("rooms")}){
                Text(text = "Комнаты", fontSize = 20.sp)
            }
            Tab(false, {selectedTabIndex = 1
                navHostController.navigate("tables")}){
                Text(text = "Стол", fontSize = 20.sp)
            }
        }

        NavHost(navController = navHostController, startDestination = "rooms"){
            composable("rooms"){
                RoomCard()
            }
            composable("tables"){

            }
        }
    }

}


@Composable
fun RoomMiniCard(room: Room, modifier: Modifier = Modifier){
    Card(shape = RoundedCornerShape(10.dp), modifier = modifier
        .height(210.dp)
        .width(180.dp)){
        Box(){
            AsyncImage(model = room.rooms_images[0].image_link , contentDescription = null, modifier = Modifier
                .background(
                    Color.Gray
                )
                .height(140.dp), contentScale = ContentScale.Crop)
            Text(text = "${room.minGuest}-${room.maxGuest}", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 10.dp, top = 10.dp), color = MaterialTheme.colorScheme.tertiaryContainer)
        }
        Text(text = room.name, maxLines = 2, textAlign = TextAlign.Center, fontSize = 22.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 10.dp))

    }
}



@Composable
fun GridTest(){
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomCard(){
    var rooms by remember { mutableStateOf(mutableStateListOf<Room>())}


    LaunchedEffect(null) {
        rooms = SupabaseClient.client.postgrest.from("rooms").select(Columns.raw("name, description, minGuest, maxGuest, rooms_images(image_link)")).decodeList<Room>().toMutableStateList()
    }

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(rooms){
            RoomMiniCard(room = it, modifier = Modifier.padding(8.dp))
        }
        
    }

}


@Composable
fun PreviewRoomPage(){

}
@Composable
fun RoomPage(){

}
@Serializable
data class Room(val name: String, val description: String, val minGuest: Int, val maxGuest: Int, val rooms_images: List<RoomImageLink>)

@Serializable
data class RoomImageLink(val image_link: String)