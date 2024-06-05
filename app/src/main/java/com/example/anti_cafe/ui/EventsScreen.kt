package com.example.anti_cafe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.anti_cafe.data.AuthViewModel
import com.example.anti_cafe.data.Event
import com.example.anti_cafe.data.EventsViewModel

@Composable
fun Events(eventsViewModel: EventsViewModel, navHostController: NavHostController, authViewModel: AuthViewModel){


    val events = eventsViewModel.eventList
    LaunchedEffect(authViewModel.userAuthInfo?.id) {
       eventsViewModel.loadEvents(authViewModel.userAuthInfo?.id)
    }
    Column {
        Text(text = "События", fontSize = MaterialTheme.typography.headlineLarge.fontSize, textAlign = TextAlign.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp))
        if (authViewModel.hasSession.value == false){
            Text("Присоединение к событию доступно лишь авторизованным пользователям")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(events){
                EventCard(event = it, modifier = Modifier.padding(5.dp), userUid = authViewModel.userAuthInfo?.id,
                    onClick =
                    {if (!it.flag){
                        eventsViewModel.joinEvent(authViewModel.userAuthInfo!!.id, it.id)
                        events.set(events.indexOf(it), it.copy(flag = true, participiants = it.participiants!! + 1))
                    }
                    else{
                        eventsViewModel.leaveEvent(authViewModel.userAuthInfo!!.id, it.id)
                        events.set(events.indexOf(it), it.copy(flag = false, participiants = it.participiants!! - 1))
                    }
                    })
            }
        }
    }
    }







@Composable
fun EventCard(event: Event, userUid: String? = null, onClick: () -> Unit, modifier: Modifier = Modifier){
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }
    Card (shape = RoundedCornerShape(10.dp), modifier = modifier)
    {
        Column(modifier = Modifier.padding(10.dp)){
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)){

                Text(text = event.name,
                    modifier = Modifier
                        .padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
                        .weight(1f),
                    fontSize = 25.sp)
                

                Icon(imageVector = Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.align(Alignment.CenterVertically))
                Text(text = event.date.toString().replace('T', ' '), fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 5.dp)
                        .weight(0.3f), textAlign = TextAlign.Center)
            }




            AsyncImage(model = event.main_image, contentDescription = null, modifier = Modifier
                .background(Color.Gray)
                .height(200.dp)
                .align(Alignment.CenterHorizontally), contentScale = ContentScale.Fit)
            Row(modifier = Modifier.padding(top = 10.dp)){
                if (userUid != null){
                    Button(onClick = onClick, modifier = Modifier.padding(start = 15.dp), enabled = !(!event.flag && event.participiants!! >= event.max_people)) {
                        if (!event.flag && event.participiants!! >= event.max_people) Text(text = "Все места заняты")
                        else if (!event.flag) Text(text = "Участвую")
                        else if (event.flag) Text(text = "Уже участвуете")
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${event.participiants}/${event.max_people}", modifier = Modifier
                    .align(Alignment.CenterVertically))
                Icon(imageVector = Icons.Filled.Person, contentDescription = null, modifier = Modifier
                    .align(Alignment.CenterVertically))
                Text(text = "Описание", modifier = Modifier
                    .align(Alignment.CenterVertically))
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                }
            }
            if (expanded){
                Column {
                    HorizontalDivider()
                    Text(text = event.description,
                        modifier = Modifier.padding(10.dp))
                }

            }


        }




    }
}

