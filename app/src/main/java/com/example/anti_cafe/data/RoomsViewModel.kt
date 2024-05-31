package com.example.anti_cafe.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
data class Room(val id: Int, val name: String, val description: String, val minGuest: Int, val maxGuest: Int, val rooms_images: List<RoomImageLink>, val room_type: RoomType    )
@Serializable
data class RoomType(val name: String)

@Serializable
data class RoomImageLink(val image_link: String)

class RoomsViewModel : ViewModel() {
    var rooms = mutableStateOf(listOf<Room>())
    var schedule : MutableState<List<ObservableSchedule>> = mutableStateOf(listOf<ObservableSchedule>())
    var daySchedule: MutableState<List<ObservableDaySchedule>> = mutableStateOf(listOf<ObservableDaySchedule>())
    var selectedRoom: Room? = null
    var selectedTime: LocalDateTime? = null
    var hoursSelected: Int? = null

    init {
        viewModelScope.launch {
            rooms.value = SupabaseClient.client.postgrest.from("rooms").select(Columns.raw("id, name, description, minGuest, maxGuest, rooms_images(image_link), room_type(name)")).decodeList<Room>().toMutableList()

        }
    }


    fun makeReservation(reservation: Reservation){
        viewModelScope.launch {
            SupabaseClient.client.postgrest.from("rooms_reservations").insert(reservation)
        }

    }

    fun loadSchedule(room_id: Int, start_date: LocalDate, days: Int, available_hours_per_day: Int){
        viewModelScope.launch {
            daySchedule = mutableStateOf(listOf<ObservableDaySchedule>())
            schedule.value = SupabaseClient.client.postgrest.rpc("schedule", parameters = buildJsonObject { put("room_id", room_id)
                put("start_date", start_date.toString())
                put("days", days,)
                put("available_hours_per_day", available_hours_per_day)}).decodeList<Schedule>().map { ObservableSchedule(it) }
        }
    }

    fun loadDaySchedule(day: LocalDate, room_id: Int){
        viewModelScope.launch {
            daySchedule.value = SupabaseClient.client.postgrest.rpc("day_schedule", parameters = buildJsonObject { put("day", day.toString())
                put("room_id_", room_id)}).decodeList<DaySchedule>().map { ObservableDaySchedule(it) }

        }
    }
}



@Serializable
data class Schedule(val room_id: Int, val day: kotlinx.datetime.LocalDate, val status: String)

data class ObservableSchedule(val room_id: Int, val day: kotlinx.datetime.LocalDate, val status: MutableState<String>){
    constructor(schedule: Schedule) : this(schedule.room_id, schedule.day, mutableStateOf(schedule.status))
}

@Serializable
data class DaySchedule(val room_id: Int, val date: LocalDateTime, var status: String, val user_id: String?)

@Serializable
data class ObservableDaySchedule(val room_id: Int, val date: LocalDateTime, var status: MutableState<String>, val user_id: MutableState<String?>){
    constructor(daySchedule: DaySchedule) : this(daySchedule.room_id, daySchedule.date, mutableStateOf(daySchedule.status), mutableStateOf(daySchedule.user_id))
}