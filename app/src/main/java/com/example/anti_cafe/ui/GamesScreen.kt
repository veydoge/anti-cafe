package com.example.anti_cafe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.anti_cafe.data.AgeRestriction
import com.example.anti_cafe.data.Game
import com.example.anti_cafe.data.GameReservation
import com.example.anti_cafe.data.GameType
import com.example.anti_cafe.data.GamesViewModel


@Composable
fun GameMiniCard(game: Game, modifier: Modifier = Modifier){
    Card(shape = RoundedCornerShape(10.dp), modifier = modifier
        .height(210.dp)
        .width(180.dp)){
        Box(){
            AsyncImage(model = game.image_link , contentDescription = null, modifier = Modifier
                .background(
                    Color.Gray
                )
                .height(140.dp), contentScale = ContentScale.Crop)
        }
        Text(text = game.name, maxLines = 2, textAlign = TextAlign.Center, fontSize = 22.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 10.dp))

    }
}

@Composable
fun GameMiniCardWithReservation(game: GameReservation, reservationChanged: (Boolean) -> Unit, modifier: Modifier = Modifier){
    var reservationFlag = game.reserved

    Card(shape = RoundedCornerShape(10.dp), modifier = modifier
        .height(240.dp)
        .width(180.dp)){
        Box(){

            AsyncImage(model = game.image_link , contentDescription = null, modifier = Modifier
                .background(
                    Color.Gray
                )
                .height(140.dp), contentScale = ContentScale.Crop)
        }
        Text(text = game.name, maxLines = 2, textAlign = TextAlign.Center, fontSize = 22.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 10.dp))
        Spacer(modifier = Modifier.weight(1f))
        Checkbox(checked = reservationFlag, onCheckedChange = {
                                                              reservationChanged(!game.reserved)}, modifier = Modifier.align(Alignment.CenterHorizontally))

    }
}

@Composable
fun GamePagePreload(id: String, gamesViewModel: GamesViewModel){
    var game = gamesViewModel.gamesList.find { it.id == id.toInt() }
    if (game != null){
        GamePage(game = game, gamesViewModel = gamesViewModel)
    }

}

@Preview(showBackground = true)
@Composable
fun previewGamePage(){
    // GameMiniCardWithReservation(game = Game(1, "фу", "фу", AgeRestriction("6+"), GameType("ХАХА"), rules_link = "123", "123"))
}


@Composable
fun GamePage(game: Game, gamesViewModel: GamesViewModel, modifier : Modifier = Modifier){
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        val uriHandler = LocalUriHandler.current
        Text(
            text = game.name, modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize
        )
        Card(
            modifier = Modifier
                .padding(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {

            AsyncImage(
                model = game.image_link, contentDescription = null, modifier = Modifier

                    .aspectRatio(16 / 9f)
                    .padding(4.dp)
            )

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = game.description,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(5.dp)
            )

            Text(text = "Возрастное ограничение: ${game.age_restrict.name}", modifier = Modifier.padding(5.dp))
            Text(text = "Тип игры: ${game.game_type.name}", modifier = Modifier.padding(5.dp))
            Button(onClick = {
                             uriHandler.openUri(game.rules_link)


            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(text = "Посмотреть правила")
                
            }
        }
    }
}

@Composable

fun Games(gamesViewModel: GamesViewModel, onGameClicked: (String) -> Unit) {
    LaunchedEffect(null) {
        gamesViewModel.loadGames()
    }

    Column{
        Text(text = "Настольные игры", fontSize = MaterialTheme.typography.headlineLarge.fontSize, textAlign = TextAlign.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(gamesViewModel.gamesList) {
                GameMiniCard(game = it, modifier = Modifier
                    .padding(8.dp)
                    .clickable { onGameClicked(it.id.toString()) })
            }
        }
    }

}
