package com.jsomi.broadcaster

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AdminApp() } }
    }
}

@Composable
fun AdminApp() {
    val ctx = LocalContext.current
    var roomCode by remember { mutableStateOf("JSTV-2026") }
    var isLive by remember { mutableStateOf(false) }
    var isDemo by remember { mutableStateOf(false) }
    var liveSeconds by remember { mutableStateOf(0) }
    var currentTime by remember { mutableStateOf("00:00:00") }
    var showCodes by remember { mutableStateOf(false) }
    var premiumCode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { while (true) { try { currentTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date()) } catch (e: Exception) {}; delay(1000) } }
    LaunchedEffect(isLive, isDemo) { while (isLive || isDemo) { delay(1000); liveSeconds += 1 } }
    fun formatLive(s: Int): String { return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60) }
    fun toast(m: String) { Toast.makeText(ctx, m, Toast.LENGTH_SHORT).show() }
    fun openWhatsApp(msg: String) { val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254763052911?text=" + Uri.encode(msg))); try { ctx.startActivity(i) } catch (e: Exception) { toast("WhatsApp not found") } }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color(0xFF8B0000)).border(3.dp, Color(0xFFFFD700)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👑 JSTV", color = Color(0xFFFFD700), fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Text("STUDIO ADMIN ONLY", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("RED + GOLD = ADMIN", color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Time: $currentTime", color = Color.Gray, fontSize = 12.sp)
        Text("ROOM CODE FOR BROADCASTERS:", color = Color.Yellow, fontWeight = FontWeight.Bold)
        Text(roomCode, color = Color(0xFFFFD700), fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Give this code to CYAN Broadcaster phone to LINK", color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { roomCode = "JSTV-" + (1000..9999).random().toString(); toast("New Code: $roomCode") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))) { Text("GENERATE NEW ROOM CODE", color = Color.White, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(16.dp))
        Text(if (isLive) "● LIVE ${formatLive(liveSeconds)}" else if (isDemo) "● DEMO ${formatLive(liveSeconds)}" else "● OFFLINE", color = if (isLive) Color.Red else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.DarkGray).border(2.dp, Color(0xFFFFD700)), contentAlignment = Alignment.Center) { Text(if (isLive) "ON AIR - ADMIN CONTROLLING" else if (isDemo) "DEMO PREVIEW - ADMIN" else "ADMIN PREVIEW - OFFLINE", color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { isDemo = true; isLive = false; liveSeconds = 0 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))) { Text("START DEMO - FREE", color = Color.Black, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { showCodes = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("START REAL LIVE - GENERATE PREMIUM CODE", color = Color.White, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { isLive = false; isDemo = false; liveSeconds = 0 }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("STOP LIVE") }
        if (showCodes) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("PREMIUM CODES - ADMIN ONLY", color = Color.Yellow, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = premiumCode, onValueChange = { premiumCode = it }, label = { Text("Type Code e.g. JSTV-200-XXXX") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { if (premiumCode.isBlank()) toast("Enter Code") else { isLive = true; isDemo = false; liveSeconds = 0; showCodes = false; toast("LIVE Started with $premiumCode") } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))) { Text("GO LIVE WITH THIS CODE", color = Color.Black, fontWeight = FontWeight.Bold) }
                    Button(onClick = { openWhatsApp("Hi, Premium Code: $premiumCode") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("SEND CODE VIA WHATSAPP") }
                    Button(onClick = { showCodes = false }, modifier = Modifier.fillMaxWidth()) { Text("CANCEL") }
                }
            }
        }
    }
}
