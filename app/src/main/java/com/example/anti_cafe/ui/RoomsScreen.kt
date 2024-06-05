package com.example.anti_cafe.ui

import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import coil.compose.AsyncImage
import com.example.anti_cafe.data.AuthViewModel
import com.example.anti_cafe.data.GamesViewModel
import com.example.anti_cafe.data.ObservableDaySchedule
import com.example.anti_cafe.data.ObservableSchedule
import com.example.anti_cafe.data.Room
import com.example.anti_cafe.data.RoomImageLink
import com.example.anti_cafe.data.RoomType
import com.example.anti_cafe.data.RoomsViewModel
import com.example.anti_cafe.utils.getWeekPageTitle
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.Week
import com.kizitonwose.calendar.core.WeekDay
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter


const val DAYS_SCHEDULE_READY = 15 // на сколько дней вперед можно бронировать

@Preview(showBackground = true)
@Composable
fun RoomPreviewMiniCard(){
    RoomMiniCard(room = Room(1, "Имя", "123", 1, 5, listOf(RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_1221-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_0320-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/IMG_8249-scaled.jpg")), RoomType("Комната")))
}




@Composable
fun Main(authViewModel: AuthViewModel, roomsViewModel: RoomsViewModel, onRoomClicked: (String) -> Unit, modifier: Modifier = Modifier){

    val context = LocalContext.current
    LaunchedEffect(null) {
        if (authViewModel.hasSession.value == null){
            authViewModel.isUserLoggedIn(context)
        }
    }

    Column{

        Text(text = "Главная", fontSize = MaterialTheme.typography.headlineLarge.fontSize, textAlign = TextAlign.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        if (authViewModel.hasSession.value == false){
            Text("Бронирование доступно лишь авторизованным пользователям")
        }

        RoomCard(roomsViewModel, onRoomClicked)
    }

}


@Composable
fun RoomMiniCard(room: Room, modifier: Modifier = Modifier){
    Card(shape = RoundedCornerShape(10.dp), modifier = modifier
        .height(210.dp)
        .width(180.dp)){
        Box(){
                AsyncImage(
                    model = if (room.rooms_images.size != 0) room.rooms_images.get(0).image_link else null,
                    contentDescription = null,
                    modifier = Modifier
                        .background(
                            Color.Gray
                        )
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )

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
    val rooms = roomsViewModel.rooms
    var filterOption by remember {
        mutableStateOf("Все")
    }
    var roomsFiltered = rooms.value
    if (filterOption == "Комнаты") roomsFiltered = roomsFiltered.filter { it.room_type.name == "Комната" }
    if (filterOption == "Столы") roomsFiltered = roomsFiltered.filter { it.room_type.name == "Стол" }
    Column {
        ChoiceRoomFilter(onAllSelected = {filterOption = "Все"}, onRoomsSelected = {filterOption = "Комнаты"}, onTablesSelected = {filterOption = "Столы"})


        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(roomsFiltered){
                RoomMiniCard(room = it, modifier = Modifier
                    .padding(8.dp)
                    .clickable { onRoomClicked(it.id.toString()) })
            }

        }
    }


}

@Preview(showBackground = true)
@Composable
fun PreviewRoomPage(){

    RoomPage(Room(1, "Комната 9", "Длинный стол и удобные стулья. Хороша для настолок, праздников и мозговых штурмов.", 4, 8, listOf(
        RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_1221-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/img_0320-scaled.jpg"), RoomImageLink("https://12komnat.com/wp-content/uploads/2022/09/IMG_8249-scaled.jpg")
    ),
        RoomType("Комната")
    ), schedule = listOf(), viewModel(), viewModel(), onReserveConfirmationNavigate = {}, modifier = Modifier.fillMaxWidth())
}



@Composable
fun RoomPreload(room_id: String, authViewModel: AuthViewModel, roomsViewModel: RoomsViewModel, onReserveConfirmationNavigate: () -> Unit){
    val schedule by roomsViewModel.schedule
    val room: Room? = roomsViewModel.rooms.value.find { it.id == room_id.toInt() }
    LaunchedEffect(null)
    {
        roomsViewModel.loadSchedule(room_id.toInt(), LocalDate.now(), DAYS_SCHEDULE_READY, 9)
    }
    if (room != null){
        RoomPage(room = room, authViewModel= authViewModel, roomsViewModel = roomsViewModel, onReserveConfirmationNavigate = onReserveConfirmationNavigate, schedule = schedule)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RoomPage(room: Room, schedule: List<ObservableSchedule>, authViewModel: AuthViewModel, roomsViewModel: RoomsViewModel, onReserveConfirmationNavigate: () -> Unit, modifier: Modifier = Modifier){

    LaunchedEffect(null) {
        roomsViewModel.selectedRoom = room
    }
    val pagerState: PagerState = rememberPagerState(pageCount = {room.rooms_images.size})

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
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
            Row(modifier = Modifier
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
            val endWeek = remember { currentDay.plusDays(DAYS_SCHEDULE_READY.toLong()) } // Adjust as needed
            val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

            val daySchedule = roomsViewModel.daySchedule
            var selectedDay by remember { mutableStateOf<LocalDate?>(null)}
            LaunchedEffect(key1 = selectedDay) {
                if (selectedDay != null){
                    roomsViewModel.loadDaySchedule(selectedDay!!, room.id)
                }
            }



            val state = rememberWeekCalendarState(
                startDate = startWeek,
                endDate = endWeek,
                firstVisibleWeekDate = LocalDate.now(),
                firstDayOfWeek = DayOfWeek.MONDAY
            )

            val visibleWeek = rememberFirstVisibleWeekAfterScroll(state = state)

            Text(text = getWeekPageTitle(visibleWeek), modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            var selectedTime by remember{ mutableStateOf<ObservableDaySchedule?>(null)}
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
            var message = ""
            var isReservationEnabled = false
            if (daySchedule.value.isNotEmpty() && selectedTime != null){
                for (i in 1..sliderState.value.toInt() - 1){
                    if (daySchedule.value.getOrNull(daySchedule.value.indexOf(selectedTime) + i) == null){
                        message = "Вы пытаетесь забронировать нерабочее время"
                        break
                    }
                    if (daySchedule.value.getOrNull(daySchedule.value.indexOf(selectedTime) + i)?.status?.value == "Не занят"){
                        counter++
                    }
                    else{
                        break
                    }
                }
                if (message == ""){
                    if (sliderState.value > counter) message = "Вы пытаетесь забронировать недоступное время"
                    else {
                        message = "Дата: ${selectedDay.toString()} Выбранное время ${selectedTime!!.date.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))} - ${selectedTime!!.date.toJavaLocalDateTime().plusHours(sliderState.value.toLong()).format(DateTimeFormatter.ofPattern("HH:mm"))}"
                        isReservationEnabled = true
                    }
                }

            }



            LazyRow {
                if (daySchedule.value.isNotEmpty()){
                    items(daySchedule.value.size){
                        val currentTime = daySchedule.value.get(it)
                        val color = when (currentTime.status.value){
                            "Занят" -> Color.Red
                            "Не занят" -> Color.Green
                            else -> Color.Gray
                        }
                        Text(text = currentTime.date.toJavaLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")), modifier = Modifier
                            .background(color)
                            .clickable {
                                if (currentTime.status.value != "Занят") selectedTime = currentTime
                            }
                            .padding(4.dp)
                        )
                    }
                }
            }

            Slider(state = sliderState, modifier = Modifier.padding(10.dp))
            Text(text = message)
            Button(onClick = {
                for (i in 0..sliderState.value.toInt() - 1){
                    daySchedule.value.get(daySchedule.value.indexOf(selectedTime) + i).status.value = "Занят"
                }

                schedule.find {it.day.toJavaLocalDate() == selectedDay }?.let {
                    if (daySchedule.value.all { it.status.value == "Занят" }) it.status.value = "Занят"
                    else it.status.value = "Свободен частично"
                }

                roomsViewModel.selectedTime = selectedTime!!.date
                roomsViewModel.hoursSelected = sliderState.value.toInt()
                onReserveConfirmationNavigate()

                // roomsViewModel.makeReservation(Reservation(room.id, authViewModel.userAuthInfo!!.id, selectedTime!!.date, sliderState!!.value.toInt()))
                selectedTime = null

                             }, enabled = isReservationEnabled, modifier = Modifier.align(Alignment.CenterHorizontally)) {
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
    if (day.position == WeekDayPosition.RangeDate){
        Card(
            modifier = Modifier
                .aspectRatio(1f)

                .clickable { onClick(day.date) }
                .padding(8.dp),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)


        )
        {
            Box(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
                .background(
                    when (status) {
                        "Свободен" -> Color.Green
                        "Свободен частично" -> Color.Yellow
                        "Занят" -> Color.Red
                        else -> Color.Gray
                    }
                ), contentAlignment = Alignment.Center){
                Text(
                    text = day.date.dayOfMonth.toString(),

                    )

            }

        }
    }
    else{
        Card(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(8.dp),

            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)


        )
        {
            Box(modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
                .background(
                    Color.Gray
                ), contentAlignment = Alignment.Center){
                Text(
                    text = day.date.dayOfMonth.toString(),

                    )

            }

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceRoomFilter(onAllSelected: () -> Unit = {}, onRoomsSelected: () -> Unit = {}, onTablesSelected: () -> Unit = {}){
    var selectedIndex by remember {
        mutableStateOf(0)
    }
    SingleChoiceSegmentedButtonRow(modifier = Modifier
        .padding(5.dp)
        .fillMaxWidth()) {
        SegmentedButton(selected = (selectedIndex == 0), onClick = { onAllSelected()
                                                                   selectedIndex = 0}, shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp), modifier = Modifier.weight(0.2f)) {
            Text(text = "Все")
        }
        SegmentedButton(selected = (selectedIndex == 1), onClick = { onRoomsSelected()
                                                                   selectedIndex = 1}, shape = RoundedCornerShape(1.dp), modifier = Modifier.weight(0.3f)) {
            Text(text = "Только комнаты")
        }
        SegmentedButton(selected = (selectedIndex == 2), onClick = { onTablesSelected()
                                                                   selectedIndex = 2}, shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp), modifier = Modifier.weight(0.3f)) {
            Text(text = "Только столы")
        }
    }

}

@Composable
fun ReservationConfirmationPage(roomsViewModel: RoomsViewModel, gamesViewModel: GamesViewModel, authViewModel: AuthViewModel, onNavigateMain: () -> Unit){
    LaunchedEffect(gamesViewModel.gamesList) {
        gamesViewModel.loadGames()
        gamesViewModel.loadNewReservationGameList(roomsViewModel.selectedTime!!.toJavaLocalDateTime(), roomsViewModel.hoursSelected!!)
    }
    val gamesListReservation = gamesViewModel.gamesListReservation
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    Column{
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)){
            Text(text = "Ваше бронирование", fontWeight = FontWeight.Bold, fontSize = 30.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "Комната: ${roomsViewModel.selectedRoom!!.name}")
                Text(text = "Дата: ${roomsViewModel.selectedTime!!.toJavaLocalDateTime().format(formatter)}")
            }

        }
        Column(modifier = Modifier
            .padding(10.dp).fillMaxWidth()){
            Text(text = "Возьмем игру?", fontSize = 20.sp, modifier = Modifier.align(Alignment.CenterHorizontally))


            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f)) {

                items(gamesListReservation.size) {
                    val index = it
                    GameMiniCardWithReservation(game = gamesListReservation.get(index), reservationChanged = {gamesListReservation[index] = gamesListReservation[index].copy(reserved = it)},  modifier = Modifier
                        .padding(8.dp))
                }
            }
            val context = LocalContext.current
            Button(onClick = {
                roomsViewModel.makeReservation(Reservation(roomsViewModel.selectedRoom!!.id, authViewModel.userAuthInfo!!.id, roomsViewModel.selectedTime!!, roomsViewModel.hoursSelected!!), gamesListReservation.toList())
                Toast.makeText(context, "Успешно забронировано", Toast.LENGTH_LONG).show()
                onNavigateMain()

            }, modifier = Modifier.align(Alignment.CenterHorizontally)){
                Text(text = "Бронируем!")
            }

        }


    }


}


@Serializable
data class Reservation(val room_id: Int, val user_id: String, val date: LocalDateTime, val hours_reserved: Int)

