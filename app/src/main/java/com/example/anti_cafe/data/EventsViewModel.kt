package com.example.anti_cafe.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anti_cafe.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


@Serializable
data class EventEntry(@SerialName("user_id") val userid: String, @SerialName("event_id") val eventid: Int)

@Serializable
data class Event(val id: Int, val name: String, val description: String, val main_image: String?, val date: LocalDateTime, val flag: Boolean)

data class EventObservable(val id: Int, val name: String, val description: String, val main_image: String?, val date: LocalDateTime, var flag: MutableState<Boolean>){
    constructor(event: Event) : this(event.id, event.name, event.description, event.main_image, event.date, mutableStateOf(event.flag))
}
class EventsViewModel: ViewModel(){
    var eventList: List<EventObservable> = mutableStateListOf()

    fun loadEvents(userid: String?){
        viewModelScope.launch {
            var eventListLoaded = SupabaseClient.client.postgrest.rpc("user_events", parameters = buildJsonObject { put("useruid", userid) }).decodeList<Event>().toMutableStateList()
            eventList = eventListLoaded.map { EventObservable(it) }
        }

    }


    fun joinEvent(userid: String, eventid: Int){
        viewModelScope.launch {
            val eventEntry = EventEntry(userid, eventid)
            SupabaseClient.client.postgrest.from("events_users").insert(eventEntry)
        }
    }

    fun leaveEvent(userid: String, eventid: Int){
        viewModelScope.launch(){
            SupabaseClient.client.postgrest.from("events_users").delete(){
                filter{
                    eq("event_id", eventid)
                    eq("user_id", userid)
                }
            }

        }

    }





}