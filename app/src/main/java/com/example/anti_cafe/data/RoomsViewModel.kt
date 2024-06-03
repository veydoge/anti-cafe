package com.example.anti_cafe.data

import android.util.Log
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

@Serializable
data class ReservationId(val id: Int)

class RoomsViewModel : ViewModel() {
    var rooms = mutableStateOf(listOf<Room>())
    var reservations = mutableStateOf(listOf<Reservation>())
    var schedule : MutableState<List<ObservableSchedule>> = mutableStateOf(listOf<ObservableSchedule>())
    var daySchedule: MutableState<List<ObservableDaySchedule>> = mutableStateOf(listOf<ObservableDaySchedule>())
    var selectedRoom: Room? = null
    var selectedTime: LocalDateTime? = null
    var hoursSelected: Int? = null
    var loadedForUser : String? = null


    init {
        loadRooms()
    }

    fun loadRooms(){ // только для первой загрузки
        if (rooms.value.isEmpty()){
            viewModelScope.launch {
                rooms.value = SupabaseClient.client.postgrest.from("rooms").select(Columns.raw("id, name, description, minGuest, maxGuest, rooms_images(image_link), room_type(name)")).decodeList<Room>().toMutableList()
            }
        }

    }

    fun loadReservations(user_id: String){
        if (reservations.value.isEmpty() && loadedForUser != user_id){
            viewModelScope.launch {
                reservations.value = SupabaseClient.client.postgrest.from("rooms_reservations").select {Columns.raw("room_id, user_id, date, hours_reserved")
                filter {eq("user_id", user_id) }}.decodeList()
            }
            loadedForUser = user_id
        }
    }

    fun updateReservations(user_id: String){
        viewModelScope.launch {
            reservations.value = SupabaseClient.client.postgrest.from("rooms_reservations").select {Columns.raw("room_id, user_id, date, hours_reserved")
                filter {eq("user_id", user_id) }}.decodeList()
        }
    }




    fun makeReservation(reservation: Reservation, games: List<GameReservation>){
        viewModelScope.launch {
            val reservationId: ReservationId = SupabaseClient.client.postgrest.from("rooms_reservations").insert(reservation){select(Columns.raw("id"))}.decodeSingle()
            for (gameReservation in games){
                if (gameReservation.reserved){
                    SupabaseClient.client.postgrest.from("games_reservations").insert(GameReservationEntry(reservationId.id, gameReservation.id))
                    gameReservation.reserved = false
                }

            }
            updateReservations(reservation.user_id)
        }

    }

    fun deleteReservation(reservation: Reservation){
        viewModelScope.launch {
            SupabaseClient.client.postgrest.from("rooms_reservations").delete({filter { eq("room_id", reservation.room_id)
            eq("user_id", reservation.user_id)
            eq("date", reservation.date)}})
            updateReservations(reservation.user_id)
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

    fun reserveGame(reservation_id: Int, game_id: Int){
        viewModelScope.launch {
            val gameReservationEntry = GameReservationEntry(reservation_id, game_id)
            SupabaseClient.client.postgrest.from("games_reservations").insert(gameReservationEntry)
        }
    }
}

@Serializable
data class GameReservationEntry(val reservation_id: Int, val game_id: Int)


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