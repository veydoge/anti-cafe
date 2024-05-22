package com.example.anti_cafe.data

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anti_cafe.data.network.SupabaseClient
import com.example.anti_cafe.ui.Reservation
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate


@Serializable
data class Room(val id: Int, val name: String, val description: String, val minGuest: Int, val maxGuest: Int, val rooms_images: List<RoomImageLink>)

@Serializable
data class RoomImageLink(val image_link: String)

class RoomsViewModel : ViewModel() {
    var rooms = mutableStateOf(mutableListOf<Room>())
    var schedule : MutableState<List<ObservableSchedule>> = mutableStateOf(listOf<ObservableSchedule>())
    init {
        viewModelScope.launch {
            rooms.value = SupabaseClient.client.postgrest.from("rooms").select(Columns.raw("id, name, description, minGuest, maxGuest, rooms_images(image_link)")).decodeList<Room>().toMutableList()

        }
    }


    public fun makeReservation(reservation: Reservation){
        viewModelScope.launch {
            SupabaseClient.client.postgrest.from("rooms_reservations").insert(reservation)
        }

    }

    public fun loadSchedule(room_id: Int, start_date: LocalDate, days: Int, available_hours_per_day: Int){
        viewModelScope.launch {
            schedule.value = SupabaseClient.client.postgrest.rpc("schedule", parameters = buildJsonObject { put("room_id", room_id)
                put("start_date", start_date.toString())
                put("days", days,)
                put("available_hours_per_day", available_hours_per_day)}).decodeList<Schedule>().map { ObservableSchedule(it) }
        }

    }
}



@Serializable
data class Schedule(val room_id: Int, val day: kotlinx.datetime.LocalDate, val status: String)

@Serializable data class ObservableSchedule(val room_id: Int, val day: kotlinx.datetime.LocalDate, val status: MutableState<String>){
    constructor(schedule: Schedule) : this(schedule.room_id, schedule.day, mutableStateOf(schedule.status))
}

@Serializable
data class DaySchedule(val room_id: Int, val date: LocalDateTime, var status: String, val user_id: String?)