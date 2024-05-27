package com.example.anti_cafe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.anti_cafe.data.Room
import kotlinx.serialization.Serializable

@Serializable
data class Game(val game_id: Int, val name: String, val description: String, val age_restrict: String, val game_type: String, val rules_link: String, val image_link: String)
@Composable
fun GameMiniCard(room: Room, modifier: Modifier = Modifier){
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