package com.example.anti_cafe.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.anti_cafe.data.AuthViewModel
import com.example.anti_cafe.data.Event
import com.example.anti_cafe.data.EventsViewModel
import com.example.anti_cafe.data.ReservationWithGames
import com.example.anti_cafe.data.Room
import com.example.anti_cafe.data.RoomsViewModel
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun Profile(onNavigateSignUp: () -> Unit = {}, onNavigateSignIn: () -> Unit = {}, authViewModel: AuthViewModel, eventsViewModel: EventsViewModel, roomsViewModel: RoomsViewModel){
    Column(
        Modifier
        , verticalArrangement = Arrangement.SpaceAround){

        val context = LocalContext.current



        val userInfo = authViewModel.userAuthInfo
        if (authViewModel.hasSession.value == false){
            NoAuthCard(onNavigateSignUp = onNavigateSignUp, onNavigateSignIn = onNavigateSignIn)
        }
        else if (authViewModel.hasSession.value == true){
            WithAuthCard(authViewModel = authViewModel, eventsViewModel = eventsViewModel, roomsViewModel = roomsViewModel, onLogout = {authViewModel.logout(context)})

        }
        else if (authViewModel.error != ""){
            Text(text = authViewModel.error.toString())
        }
        else if(authViewModel.hasSession.value == null) {
            CircularProgressIndicator(modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primaryContainer)
        }
    }
}


@Composable
fun NoAuthCard(onNavigateSignUp: () -> Unit, onNavigateSignIn: () -> Unit, modifier: Modifier = Modifier){
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp), shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = "Вы не авторизованы", fontSize = 30.sp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 15.dp))
        Button(onClick = onNavigateSignIn, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 20.dp)) {
            Text(text = "Войти",)
        }
        Button(onClick = onNavigateSignUp, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 20.dp)) {
            Text(text = "Зарегистрироваться",)
        }
    }
}

@Composable
fun WithAuthCard(onLogout: () -> Unit = {}, authViewModel: AuthViewModel, eventsViewModel: EventsViewModel, roomsViewModel: RoomsViewModel, modifier: Modifier = Modifier){
    Column(modifier = Modifier.padding(8.dp)) {

        Card {
            Row(modifier = Modifier.height(64.dp)) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ваш логин: ${authViewModel.userAuthInfo?.email}", fontSize = 24.sp, modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }
                IconButton(onClick = onLogout, modifier = Modifier.size(64.dp)) {
                    Icon(imageVector = Icons.Filled.Logout, contentDescription = "logout")
                }
            }
        }
        val navContoller = rememberNavController()
        var tabIndex by remember { mutableStateOf(0) }
        TabRow(selectedTabIndex = tabIndex, modifier = Modifier.padding(top = 10.dp)){
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0
            navContoller.navigate("eventsProfile")}) {
                Text(text = "События")
            }
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1
                navContoller.navigate("reservesProfile")}) {
                Text(text = "Брони")
            }
        }

        NavHost(navController = navContoller, startDestination = "eventsProfile") {
            composable("eventsProfile") {
                EventsProfile(authViewModel.userAuthInfo!!.id, eventsViewModel)
            }
            composable("reservesProfile") {
                ReservesProfile(authViewModel.userAuthInfo!!.id, roomsViewModel = roomsViewModel)
            }
        }
    }
}

@Composable
fun EventsProfile(id: String, eventsViewModel: EventsViewModel){
    LaunchedEffect(id) {
        eventsViewModel.loadEvents(id)
    }
    Column{
        Text(text = "Здесь события в которых вы участвуете")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(eventsViewModel.eventList.filter { it.flag == true }){
                ShortEventInfo(event = it, onDelete = {eventsViewModel.leaveEvent(id, it.id)
                eventsViewModel.eventList.set(eventsViewModel.eventList.indexOf(it), it.copy(flag = false))})
            }
        }
    }

}

@Composable
fun ReservesProfile(id: String, roomsViewModel: RoomsViewModel){
    LaunchedEffect(null) {
        roomsViewModel.loadRooms()
        roomsViewModel.loadReservations(id)

    }
    Column(){
        Text(text = "Здесь ваши бронирования")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(roomsViewModel.reservations.value){
                ShortReservationInfo(reservation = it, room = roomsViewModel.rooms.value.find({room ->  room.id == it.room_id})!!, {reservation: ReservationWithGames -> roomsViewModel.deleteReservation(reservation)})
            }
        }
    }
}

@Composable
fun ShortEventInfo(event: Event, onDelete: (Event) -> Unit){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val openAlertDialog = remember { mutableStateOf(false) }
    Card(modifier = Modifier, shape = RoundedCornerShape(topStart = 15.dp, bottomEnd = 15.dp)
        ){
        Box (modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)) {
            Row(){
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 10.dp), verticalArrangement = Arrangement.Center){
                    Text(text = event.name, fontSize = 20.sp, modifier = Modifier.padding(top = 10.dp))
                    Text(text = event.date.toJavaLocalDateTime().format(formatter))
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .padding(5.dp), verticalArrangement = Arrangement.Center){
                    Button(onClick = {openAlertDialog.value = true}) {
                        Text(text = "Отменить участие")
                        
                    }
                }
            }
            var context = LocalContext.current
            if (openAlertDialog.value == true){
                AlertDialogExample(dialogTitle = "Отменить регистрацию", dialogText = "Вы уверены, что хотите отменить участие в событии?", onConfirmation = {openAlertDialog.value = false
                    onDelete(event)
                    Toast.makeText(context,"Участие успешно отменено", Toast.LENGTH_LONG).show()
                                                                                                                                                             }, onDismissRequest = {openAlertDialog.value = false})
            }
        }
    }
}

@Composable
fun ShortReservationInfo(reservation: ReservationWithGames, room: Room, onDelete: (ReservationWithGames) -> Unit){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val openAlertDialog = remember { mutableStateOf(false) }
    Card(modifier = Modifier, shape = RoundedCornerShape(topStart = 15.dp, bottomEnd = 15.dp)
    ){
        Box (modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)) {
            Column{
                Row(){
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 10.dp), verticalArrangement = Arrangement.Center){
                        Text(text = room.name, fontSize = 20.sp, modifier = Modifier.padding(top = 10.dp))
                        Text(text = reservation.date.toJavaLocalDateTime().format(formatter))
                        Text(text = "Часов забронировано: ${reservation.hours_reserved}")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(modifier = Modifier
                        .fillMaxHeight()
                        .padding(5.dp), verticalArrangement = Arrangement.Center){
                        Button(onClick = {openAlertDialog.value = true}) {
                            Text(text = "Отменить бронирование")
                        }
                    }
                }
                var context = LocalContext.current
                if (openAlertDialog.value == true){
                    AlertDialogExample(dialogTitle = "Отменить бронирование", dialogText = "Вы уверены, что хотите отменить свое бронирование?", onConfirmation = {openAlertDialog.value = false
                        onDelete(reservation)
                        Toast.makeText(context,"Бронирование успешно отменено", Toast.LENGTH_LONG).show()

                    }, onDismissRequest = {openAlertDialog.value = false})
                }


                if (reservation.games_reservations.isNotEmpty()){
                    var mes = ""
                    for (i in reservation.games_reservations.indices){
                        if (i == reservation.games_reservations.lastIndex){
                            mes += reservation.games_reservations[i].games.name
                        }
                        else{
                            mes += reservation.games_reservations[i].games.name + ", "
                        }

                    }
                    Text(text = "Забронированные игры: ${mes}", modifier = Modifier.padding(start = 10.dp))
                }

            }


        }

    }
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,

) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Отклонить")
            }
        }
    )
}