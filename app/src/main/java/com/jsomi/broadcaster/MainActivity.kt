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
    var isLive by remember { mutableStateOf(false) }
    var viewers by remember { mutableStateOf(124) }
    var streamTitle by remember { mutableStateOf("JSTV Live - Evening Show") }
    var streamKey by remember { mutableStateOf("live_123456") }
    var bannedUsers = remember { mutableStateListOf<String>() }
    var chatMessages = remember { mutableStateListOf("John: Hello JSTV!", "Mary: Great show!", "Peter: Love this!") }

    MaterialTheme {
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F)).padding(16.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("📺 JSTV", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Card(colors = CardDefaults.cardColors(containerColor = if (isLive) Color.Red else Color.Gray), shape = RoundedCornerShape(20.dp)) {
                        Text(if (isLive) "● LIVE" else "○ OFFLINE", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontWeight = FontWeight.Bold)
                    }
                }
                Text("Admin Studio - Control Room", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Viewers", "$viewers", Color(0xFF1E88E5))
                    StatCard("Likes", "1.2K", Color(0xFFE91E63))
                    StatCard("Duration", if (isLive) "12:45" else "00:00", Color(0xFF43A047))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Stream Control", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = streamTitle, onValueChange = { streamTitle = it }, label = { Text("Stream Title", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = streamKey, onValueChange = { streamKey = it }, label = { Text("Stream Key", color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { isLive = true; viewers += 10 }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), modifier = Modifier.fillMaxWidth()) { Text("GO LIVE") }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { isLive = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) { Text("END") }
                            Button(onClick = { viewers += 5 }) { Text("+ Viewers") }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(if (isLive) "RTMP: rtmp://a.rtmp.youtube.com/live2" else "Press GO LIVE to start", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Live Chat - Moderation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(chatMessages) { msg ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(msg, color = Color.White, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { chatMessages.remove(msg); bannedUsers.add(msg) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))) { Text("Ban User", fontSize = 12.sp) }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (bannedUsers.isNotEmpty()) {
                    Text("Banned: ${bannedUsers.size} users", color = Color.Red, fontSize = 12.sp)
                }
                Text("JSTV Studio v1.0 - Admin Only", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
