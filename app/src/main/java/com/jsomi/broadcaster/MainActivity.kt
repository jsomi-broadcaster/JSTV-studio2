package com.jsomi.broadcaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AdminStudioApp() }
    }
}

@Composable
fun AdminStudioApp() {
    var selectedTab by remember { mutableStateOf(0) }
    var isLive by remember { mutableStateOf(false) }
    var viewers by remember { mutableStateOf(124) }
    var streamTitle by remember { mutableStateOf("JSTV Live - Evening Show") }
    var streamKey by remember { mutableStateOf("live_123456") }
    var bannedUsers = remember { mutableStateListOf<String>() }
    var chatMessages = remember { mutableStateListOf("John: Hello JSTV!", "Mary: Great show!", "Peter: Love this!") }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F)).padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("📺 JSTV", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Card(colors = CardDefaults.cardColors(containerColor = if (isLive) Color.Red else Color.Gray), shape = RoundedCornerShape(20.dp)) {
                    Text(if (isLive) "● LIVE" else "○ OFFLINE", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
                }
            }
            Text("Admin Studio - Control Room", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Stats Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Viewers", "$viewers", Color(0xFF1E88E5))
                StatCard("Likes", "1.2K", Color(0xFFE91E63))
                StatCard("Duration", if (isLive) "12:45" else "00:00", Color(0xFF43A047))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Stream Control
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Stream Control", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = streamTitle, onValueChange = { streamTitle = it }, label = { Text("Stream Title", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = streamKey, onValueChange = { streamKey = it }, label = { Text("Stream Key", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { isLive = true; viewers += 10 }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), modifier = Modifier.weight(1f)) { Text("GO LIVE") }
                        Button(onClick = { isLive = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray), modifier = Modifier.weight(1f)) { Text("END") }
                        Button(onClick = { viewers += 5 }) { Text("+ Viewers") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(if (isLive) "RTMP: rtmp://a.rtmp.youtube.com/live2" else "Press GO LIVE to start", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Live Chat + Moderation
            Text("Live Chat - Moderation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(chatMessages) { msg ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(msg, color = Color.White, modifier = Modifier.weight(1f), fontSize = 14.sp)
                            Button(onClick = { chatMessages.remove(msg); bannedUsers.add(msg) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))) { Text("Ban", fontSize = 12.sp) }
                        }
                    }
                }
            }
            if (bannedUsers.isNotEmpty()) {
                Text("Banned: ${bannedUsers.size} users", color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("JSTV Studio v1.0 - Admin Only - jsomi-broadcaster", color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), modifier = Modifier.weight(1f)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
