package com.example.anti_cafe.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anti_cafe.data.network.SupabaseClient
import com.example.anti_cafe.ui.Game
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Game(val game_id: Int, val name: String, val description: String, val age_restrict: AgeRestriction, val game_type: GameType, val rules_link: String, val image_link: String)

@Serializable
data class AgeRestriction(val name: String)
@Serializable
data class GameType(val name: String)
class GamesViewModel : ViewModel(){

    var gamesList = mutableStateListOf<Game>()


    fun loadGames(){
        viewModelScope.launch {
            gamesList = SupabaseClient.client.postgrest.from("games").select(columns = Columns.raw("id, name, description, age_restrict(name), game_type(name), rules_link, image_link")).decodeList<Game>().toMutableStateList()
        }
    }

}