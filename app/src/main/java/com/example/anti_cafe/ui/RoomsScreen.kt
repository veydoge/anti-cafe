package com.example.anti_cafe.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.anti_cafe.data.AuthViewModel
import com.example.anti_cafe.data.DaySchedule
import com.example.anti_cafe.data.ObservableSchedule
import com.example.anti_cafe.data.Room
import com.example.anti_cafe.data.RoomImageLink
import com.example.anti_cafe.data.RoomsViewModel
import com.example.anti_cafe.data.Schedule
import com.example.anti_cafe.data.network.SupabaseClient
import com.example.anti_cafe.utils.getWeekPageTitle
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Preview(showBackground = true)
@Composable
fun RoomPreviewMiniCard(){
    RoomMiniCard(room = Room(1, "Имя", "123", 1, 5, listOf(RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_1221-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_0320-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/IMG_8249-scaled.jpg"))))
}




@Composable
fun Main(authViewModel: AuthViewModel, roomsViewModel: RoomsViewModel, onRoomClicked: (String) -> Unit, modifier: Modifier = Modifier){

    val context = LocalContext.current
    LaunchedEffect(null) {
        authViewModel.isUserLoggedIn(context)
    }

    Column{
        val navHostController: NavHostController = rememberNavController()
        var selectedTabIndex by rememberSaveable {
            mutableStateOf(0)
        }
        Text(text = "Главная", fontSize = MaterialTheme.typography.headlineLarge.fontSize, textAlign = TextAlign.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))

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
                RoomCard(roomsViewModel, onRoomClicked)
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





@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomCard(roomsViewModel: RoomsViewModel, onRoomClicked: (String) -> Unit = {}){

    var rooms = roomsViewModel.rooms

    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(rooms.value){
            RoomMiniCard(room = it, modifier = Modifier
                .padding(8.dp)
                .clickable { onRoomClicked(it.id.toString()) })
        }
        
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewRoomPage(){

    RoomPage(Room(1, "Комната 9", "Длинный стол и удобные стулья. Хороша для настолок, праздников и мозговых штурмов.", 4, 8, listOf(
        RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_1221-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_0320-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/IMG_8249-scaled.jpg")
    )
    ), schedule = listOf(), viewModel(), viewModel(), modifier = Modifier.fillMaxWidth())
}



@Composable
fun RoomPreload(room_id: String, authViewModel: AuthViewModel, roomsViewModel: RoomsViewModel){
    var schedule by roomsViewModel.schedule
    LaunchedEffect(null) {
        roomsViewModel.loadSchedule(room_id.toInt(), LocalDate.now(), 14, 9)
    }
    var room: MutableState<Room?> = remember() {
        mutableStateOf(null)
    }
    LaunchedEffect(null)
    {
        if (room_id != null){
            room.value = SupabaseClient.client.postgrest.from("rooms").select(Columns.raw("id, name, description, minGuest, maxGuest, rooms_images(image_link)"), request = {
                filter{
                    eq("id", room_id.toInt())
                }
            }).decodeSingleOrNull<Room>()
        }
    }
    if (room.value != null){
        RoomPage(room = room.value!!, authViewModel= authViewModel, roomsViewModel = roomsViewModel, schedule = schedule)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RoomPage(room: Room, schedule: List<ObservableSchedule>, authViewModel: AuthViewModel, roomsViewModel: RoomsViewModel, modifier: Modifier = Modifier){

    val pagerState: PagerState = rememberPagerState(pageCount = {room.rooms_images.size})

    Column(modifier = modifier) {
        Text(text = room.name, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(10.dp), fontSize = MaterialTheme.typography.headlineLarge.fontSize)
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)){
            Row(){
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${room.minGuest}-${room.maxGuest}", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.CenterVertically), color = MaterialTheme.colorScheme.onPrimaryContainer)
                Icon(imageVector = Icons.Filled.Person, contentDescription = null, modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(5.dp))
            }

            HorizontalPager(state = pagerState) {
                Box(){
                    AsyncImage(model = room.rooms_images.get(it).image_link, contentDescription = null, modifier = Modifier

                        .aspectRatio(16 / 9f)
                        .padding(4.dp)
                        .align(Alignment.Center))

                }

            }
            Row(modifier = modifier
                .fillMaxWidth()
                .height(20.dp), horizontalArrangement = Arrangement.SpaceEvenly){
                val color = MaterialTheme.colorScheme.onPrimaryContainer
                repeat(room.rooms_images.size){
                    Canvas(Modifier.size(15.dp)){
                        drawCircle(color = color, style = if (!(pagerState.currentPage == it)) Stroke(10f) else Fill)
                    }
                }

            }
            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(text = room.description, textAlign = TextAlign.Justify, modifier = Modifier.padding(5.dp))

        }

        if (authViewModel.hasSession.value == true){
            val currentDay = remember { LocalDate.now() }
            val startWeek = remember { currentDay } // Adjust as needed
            val endWeek = remember { currentDay.plusDays(14) } // Adjust as needed
            val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

            var daySchedule by remember{mutableStateOf(listOf<DaySchedule>())}
            var selectedDay by remember { mutableStateOf<LocalDate?>(null)}
            LaunchedEffect(key1 = selectedDay) {
                if (selectedDay != null){
                    daySchedule = SupabaseClient.client.postgrest.rpc("day_schedule", parameters = buildJsonObject { put("day", selectedDay.toString())
                        put("room_id_", room.id)}).decodeList<DaySchedule>().toMutableStateList()
                }

            }



            val state = rememberWeekCalendarState(
                startDate = startWeek,
                endDate = endWeek,
                firstVisibleWeekDate = LocalDate.now(),
                firstDayOfWeek = DayOfWeek.MONDAY
            )

            val visibleWeek = rememberFirstVisibleWeekAfterScroll(state = state)

            Text(text = getWeekPageTitle(visibleWeek))
            var selectedTime by remember{ mutableStateOf<DaySchedule?>(null)}
            WeekCalendar(
                state = state,
                dayContent = {
                    val day = it
                    Day(day = it, status = schedule.find {it.day.toJavaLocalDate() == day.date }?.status?.value) {
                        selectedDay = it;
                        selectedTime = null
                    }
                },
            )

            var counter = 1
            var sliderState by remember {
                mutableStateOf(SliderState(value = 1f, steps = 3, valueRange = 1f..5f))
            }
            var error by remember{ mutableStateOf("")}
            if (daySchedule != null && selectedTime != null){
                for (i in 1..4){
                    if (daySchedule!!.getOrNull(daySchedule!!.indexOf(selectedTime) + i)?.status == "Не занят" && daySchedule!!.getOrNull(daySchedule!!.indexOf(selectedTime) + i) != null){
                        counter++
                    }
                    else{
                        break
                    }
                }
                if (sliderState.value > counter) error = "Вы пытаетесь забронировать недоступное время"
                else if (selectedTime != null){
                    error = "Дата: ${selectedDay.toString()} Выбранное время ${selectedTime!!.date.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))} - ${selectedTime!!.date.toJavaLocalDateTime().plusHours(sliderState.value.toLong()).format(DateTimeFormatter.ofPattern("HH:mm"))}"
                }
            }
            else{
                error = ""
            }



            LazyRow {
                if (daySchedule != null){
                    items(daySchedule!!.size){
                        val currentTime = daySchedule!!.get(it)
                        val color = when (currentTime!!.status){
                            "Занят" -> Color.Red
                            "Не занят" -> Color.Green
                            else -> Color.Gray
                        }
                        Text(text = currentTime.date.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), modifier = Modifier
                            .background(color)
                            .clickable {
                                if (currentTime.status != "Занят") selectedTime = currentTime
                            }
                        )
                    }
                }
            }

            Slider(state = sliderState, modifier = Modifier.padding(10.dp))
            Text(text = error)
            Button(onClick = {
                val list = daySchedule
                for (i in 0..sliderState.value.toInt() - 1){
                    list?.get(daySchedule!!.indexOf(selectedTime) + i)?.status = "Занят"

                }
                schedule.find {it.day.toJavaLocalDate() == selectedDay }?.status?.value = "Свободен частично"
                daySchedule = list.toMutableStateList()// если тоже использовать toList() то изменения не зафиксируются, я ваще без понятия почему, референс то поменяться должен был

                roomsViewModel.makeReservation(Reservation(room.id, authViewModel.userAuthInfo!!.id, selectedTime!!.date, sliderState!!.value.toInt()))


                             }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Бронируем!")
            }


        }





    }


}

@Composable
fun rememberFirstVisibleWeekAfterScroll(state: WeekCalendarState): Week {
    val visibleWeek = remember(state) { mutableStateOf(state.firstVisibleWeek) }
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { scrolling -> !scrolling }
            .collect { visibleWeek.value = state.firstVisibleWeek }
    }
    return visibleWeek.value
}

@Composable
fun Day(day: WeekDay, status: String?, onClick: (LocalDate) -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(
                when (status) {
                    "Свободен" -> Color.Green
                    "Свободен частично" -> Color.Yellow
                    "Занят" -> Color.Red
                    else -> Color.Gray
                }
            )
            .clickable { onClick(day.date) },
        contentAlignment = Alignment.Center
    ) { // Change the color of in-dates and out-dates, you can also hide them completely!
        Text(
            text = day.date.dayOfMonth.toString(),
        )
    }
}


@Serializable
data class Reservation(val room_id: Int, val user_id: String, val date: LocalDateTime, val hours_reserved: Int)

