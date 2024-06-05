package com.example.anti_cafe.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anti_cafe.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Game(val id: Int, val name: String, val description: String, @SerialName("age_restrict_id") val age_restrict: AgeRestriction, @SerialName("game_type_id") val game_type: GameType, val rules_link: String, val image_link: String)

data class GameReservation(val id: Int, val name: String, val description: String, val game_type: GameType, val rules_link: String, val image_link: String, var reserved: Boolean, var available: String = ""){
    constructor(game: Game): this(id = game.id, name = game.name, description = game.description, game_type = game.game_type, rules_link = game.rules_link, image_link = game.image_link, reserved = false)
}
@Serializable
data class GameAvailable(val available: String)
@Serializable
data class AgeRestriction(val name: String)
@Serializable
data class GameType(val name: String)
class GamesViewModel : ViewModel(){

    var ageRestrictList = mutableStateListOf<AgeRestriction>()
    var gamesList = mutableStateListOf<Game>()
    var gamesTypesList = mutableStateListOf<GameType>()

    var gamesListReservation = gamesList.map { GameReservation(it) }.toMutableStateList()




    fun loadGames(){
        if (gamesList.size == 0){
            viewModelScope.launch {
                gamesList = SupabaseClient.client.postgrest.from("games").select(columns = Columns.raw("id, name, description, age_restrict_id(name), game_type_id(name), rules_link, image_link")).decodeList<Game>().toMutableStateList()
            }
        }
    }

    fun loadNewReservationGameList(date: LocalDateTime, hours: Int){
        viewModelScope.launch {
            gamesListReservation = gamesList.map { GameReservation(it) }.toMutableStateList()
            for (i in 0 until gamesListReservation.size){
                val available : String = SupabaseClient.client.postgrest.rpc("is_available_to_reserve_game", parameters = buildJsonObject { put("game_id", gamesListReservation[i].id.toString())
                put("datetime", date.toString())
                put("hours_reserved", hours.toString())}).decodeAs()
                gamesListReservation[i] = gamesListReservation[i].copy(available = available)

            }
        }


    }

    fun loadGameTypes() {
        viewModelScope.launch {
            gamesTypesList = SupabaseClient.client.postgrest.from("game_types").select(columns = Columns.raw("id, name")).decodeList<GameType>().toMutableStateList()
        }
    }
    fun loadAgeRestrictions(){
        viewModelScope.launch {
            ageRestrictList = SupabaseClient.client.postgrest.from("age_restrict").select(columns = Columns.raw("id, name")).decodeList<AgeRestriction>().toMutableStateList()
        }
    }

}