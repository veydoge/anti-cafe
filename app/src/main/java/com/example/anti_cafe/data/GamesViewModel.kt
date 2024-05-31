package com.example.anti_cafe.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anti_cafe.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Game(val id: Int, val name: String, val description: String, @SerialName("age_restrict_id") val age_restrict: AgeRestriction, @SerialName("game_type_id") val game_type: GameType, val rules_link: String, val image_link: String)

data class GameReservation(val id: Int, val name: String, val description: String, val game_type: GameType, val rules_link: String, val image_link: String, var reserved: Boolean){
    constructor(game: Game): this(id = game.id, name = game.name, description = game.description, game_type = game.game_type, rules_link = game.rules_link, image_link = game.image_link, reserved = false)
}
@Serializable
data class AgeRestriction(val name: String)
@Serializable
data class GameType(val name: String)
class GamesViewModel : ViewModel(){

    var gamesList = mutableStateListOf<Game>()

    var gamesListReservation = gamesList.map { GameReservation(it) }.toMutableStateList()


    fun loadGames(){
        if (gamesList.size == 0){
            viewModelScope.launch {
                gamesList = SupabaseClient.client.postgrest.from("games").select(columns = Columns.raw("id, name, description, age_restrict_id(name), game_type_id(name), rules_link, image_link")).decodeList<Game>().toMutableStateList()
                gamesListReservation = gamesList.map { GameReservation(it) }.toMutableStateList()
            }
        }
    }

}