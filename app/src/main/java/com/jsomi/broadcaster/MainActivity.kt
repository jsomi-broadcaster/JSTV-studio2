package com.jsomi.jstvstudioadmin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// JSTV STUDIO ADMIN - RED + GOLD - LOGO FILE: jstv_studio_admin.jpg
// ADMIN ONLY - Keys, Codes, Money, Multistream, Ticker, LS, Loop, Intro

data class PremiumCode(val code: String, val plan: String, val amount: Int, val name: String, val phone: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { StudioAdminApp() } }
    }
}

@Composable
fun StudioAdminApp() {
    val ctx = LocalContext.current
    var screen by remember { mutableStateOf("STUDIO") }
    var roomCode by remember { mutableStateOf("JSTV-2026") }
    var ticker by remember { mutableStateOf("Welcome to JSTV - Jehovah Shalom TV - God Bless You") }
    var tickerOn by remember { mutableStateOf(true) }
    var tickerPos by remember { mutableStateOf("Bottom") }
    var cameraOn by remember { mutableStateOf(true) }
    var micOn by remember { mutableStateOf(true) }
    var isLive by remember { mutableStateOf(false) }
    var isDemo by remember { mutableStateOf(false) }
    var streamTitle by remember { mutableStateOf("My Live Show") }
    var liveSeconds by remember { mutableStateOf(0) }
    var currentTime by remember { mutableStateOf("00:00:00") }
    var fullScreen by remember { mutableStateOf(false) }
    var ytOn by remember { mutableStateOf(true) }
    var fbOn by remember { mutableStateOf(false) }
    var tkOn by remember { mutableStateOf(false) }
    var ytKey by remember { mutableStateOf("") }
    var fbKey by remember { mutableStateOf("") }
    var tkKey by remember { mutableStateOf("") }
    var logoOn by remember { mutableStateOf(true) }
    var logoText by remember { mutableStateOf("JSTV") }
    var lsName by remember { mutableStateOf("Pastor Kevin") }
    var lsTitle by remember { mutableStateOf("Host - Jehovah Shalom TV") }
    var lsOn by remember { mutableStateOf(false) }
    var loopOn by remember { mutableStateOf(false) }
    var introOn by remember { mutableStateOf(true) }
    var introVideo by remember { mutableStateOf("JSTV Intro 1 - 10s") }
    var introPlaying by remember { mutableStateOf(false) }
    var custName by remember { mutableStateOf("") }
    var custPhone by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("Daily 200") }
    var codes by remember { mutableStateOf(listOf<PremiumCode>()) }
    var lastCode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { while (true) { try { currentTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date()) } catch (e: Exception) {}; delay(1000) } }
    LaunchedEffect(isLive, isDemo) { while (isLive || isDemo) { delay(1000); liveSeconds += 1 } }
    fun formatLive(s: Int): String { return String.format("%02d:%02d:%02d", s/3600, (s%3600)/60, s%60) }
    fun toast(m: String) { Toast.makeText(ctx, m, Toast.LENGTH_SHORT).show() }
    fun shareWhatsApp(msg: String) { val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254763052911?text=" + Uri.encode(msg))); try { ctx.startActivity(i) } catch (e: Exception) { toast("WhatsApp not found") } }
    fun getDests(): String { val l = mutableListOf<String>(); if (ytOn) l.add("YouTube"); if (fbOn) l.add("Facebook"); if (tkOn) l.add("TikTok"); return if (l.isEmpty()) "YouTube (Default)" else l.joinToString(" + ") }
    fun stopAll() { isLive = false; isDemo = false; introPlaying = false; fullScreen = false; liveSeconds = 0; toast("Stopped") }

    if (fullScreen && (isLive || isDemo)) {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(8.dp)) {
            Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(6.dp)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("JSTV STUDIO ADMIN", color = Color.Yellow, fontSize = 15.sp, fontWeight = FontWeight.Bold); Text(if (isLive) "● LIVE ${formatLive(liveSeconds)}" else "● DEMO", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold) } }
            Spacer(modifier = Modifier.weight(1f)); Text(streamTitle, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()); Spacer(modifier = Modifier.weight(1f))
            if (lsOn) { Box(modifier = Modifier.fillMaxWidth().background(Color.Yellow).padding(10.dp)) { Column { Text(lsName, color = Color.Black, fontWeight = FontWeight.Bold); Text(lsTitle, color = Color.Black, fontSize = 12.sp) } }; Spacer(modifier = Modifier.height(8.dp)) }
            if (tickerOn) { Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(8.dp)) { Text(ticker, color = Color.White, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { fullScreen = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("EXIT", color = Color.Black) }; Button(onClick = { stopAll() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("STOP LIVE", color = Color.White) } }
        }; return
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        // LOGO FROM YOUR FILE: JSTV STUDIO ADMIN.jpg
        Image(painter = painterResource(id = R.drawable.jstv_studio_admin), contentDescription = "JSTV STUDIO ADMIN", modifier = Modifier.fillMaxWidth().height(160.dp).border(3.dp, Color.Yellow), contentScale = ContentScale.FillWidth)
        Spacer(modifier = Modifier.height(4.dp)); Text("RED + GOLD = STUDIO ADMIN - KEVIN ONLY", color = Color.Yellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { screen = "STUDIO" }, colors = ButtonDefaults.buttonColors(containerColor = if (screen == "STUDIO") Color.Red else Color.DarkGray)) { Text("STUDIO", color = Color.White) }
            Button(onClick = { screen = "CODES" }, colors = ButtonDefaults.buttonColors(containerColor = if (screen == "CODES") Color.Red else Color.DarkGray)) { Text("CODES", color = Color.White) }
            Button(onClick = { screen = "MONEY" }, colors = ButtonDefaults.buttonColors(containerColor = if (screen == "MONEY") Color.Red else Color.DarkGray)) { Text("MONEY", color = Color.White) }
            Button(onClick = { screen = "LINK" }, colors = ButtonDefaults.buttonColors(containerColor = if (screen == "LINK") Color.Red else Color.DarkGray)) { Text("LINK", color = Color.White) }
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (screen == "STUDIO") {
            Text("Room: $roomCode | ${if (isLive) "● LIVE ${formatLive(liveSeconds)}" else if (isDemo) "● DEMO ${formatLive(liveSeconds)}" else "● OFFLINE"} | $currentTime", color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.DarkGray).border(2.dp, Color.Red).padding(8.dp)) { Column { Text(if (logoOn) logoText else "", color = Color.Yellow, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.weight(1f)); Text(streamTitle, color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()); Text(getDests(), color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()); Spacer(modifier = Modifier.weight(1f)); if (lsOn) { Box(modifier = Modifier.fillMaxWidth().background(Color.Yellow).padding(6.dp)) { Column { Text(lsName, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp); Text(lsTitle, color = Color.Black, fontSize = 11.sp) } } }; if (tickerOn) { Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(6.dp)) { Text(ticker, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) } } } }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = streamTitle, onValueChange = { streamTitle = it }, label = { Text("Stream Title") }, modifier = Modifier.fillMaxWidth())
            Text("MULTISTREAM - ADMIN ONLY", color = Color.Yellow, fontWeight = FontWeight.Bold)
            Text("1. YOUTUBE - rtmp://a.rtmp.youtube.com/live2", color = Color.White, fontSize = 11.sp); OutlinedTextField(value = ytKey, onValueChange = { ytKey = it }, label = { Text("YouTube Key") }, modifier = Modifier.fillMaxWidth()); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { ytOn = true }) { Text("YT ON") }; Button(onClick = { ytOn = false }) { Text("YT OFF") } }
            Text("2. FACEBOOK - rtmps://live-api-s.facebook.com:443/rtmp/", color = Color.White, fontSize = 11.sp); OutlinedTextField(value = fbKey, onValueChange = { fbKey = it }, label = { Text("Facebook Key") }, modifier = Modifier.fillMaxWidth()); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { fbOn = true }) { Text("FB ON") }; Button(onClick = { fbOn = false }) { Text("FB OFF") } }
            Text("3. TIKTOK - rtmp://push-rtmp-l1.tiktok.com/live/", color = Color.White, fontSize = 11.sp); OutlinedTextField(value = tkKey, onValueChange = { tkKey = it }, label = { Text("TikTok Key") }, modifier = Modifier.fillMaxWidth()); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { tkOn = true }) { Text("TK ON") }; Button(onClick = { tkOn = false }) { Text("TK OFF") } }
            Text("Active: ${getDests()}", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = logoText, onValueChange = { logoText = it }, label = { Text("Logo Text") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = lsName, onValueChange = { lsName = it }, label = { Text("LS Name") }, modifier = Modifier.fillMaxWidth()); OutlinedTextField(value = lsTitle, onValueChange = { lsTitle = it }, label = { Text("LS Title") }, modifier = Modifier.fillMaxWidth()); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { lsOn = true }) { Text("SHOW LS") }; Button(onClick = { lsOn = false }) { Text("HIDE LS") } }
            OutlinedTextField(value = ticker, onValueChange = { ticker = it }, label = { Text("Ticker - Single Line") }, modifier = Modifier.fillMaxWidth(), singleLine = true); Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(6.dp)) { Text(ticker, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }; Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { tickerPos = "Top" }) { Text("TOP") }; Button(onClick = { tickerPos = "Middle" }) { Text("MIDDLE") }; Button(onClick = { tickerPos = "Bottom" }) { Text("BOTTOM") } }; Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { tickerOn = true }) { Text("TICKER ON") }; Button(onClick = { tickerOn = false }) { Text("TICKER OFF") } }
            Text("Intro: $introVideo", color = Color.Yellow, fontSize = 12.sp); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { introVideo = "JSTV Intro 1 - 10s" }) { Text("Intro 10s") }; Button(onClick = { introVideo = "Worship Intro - 15s" }) { Text("Worship 15s") } }; Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { loopOn = true }) { Text("LOOP ON") }; Button(onClick = { loopOn = false }) { Text("LOOP OFF") } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { cameraOn = !cameraOn }) { Text(if (cameraOn) "CAMERA ON" else "CAMERA OFF") }; Button(onClick = { micOn = !micOn }) { Text(if (micOn) "MIC ON" else "MIC OFF") } }
            Spacer(modifier = Modifier.height(12.dp)); Button(onClick = { isDemo = true; isLive = false; liveSeconds = 0; fullScreen = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("DEMO - FREE - FULL SCREEN", color = Color.Black, fontWeight = FontWeight.Bold) }; Spacer(modifier = Modifier.height(8.dp)); Button(onClick = { if (isLive) stopAll() else { isLive = true; liveSeconds = 0; fullScreen = true } }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isLive) Color.Red else Color.Green)) { Text(if (isLive) "STOP REAL LIVE" else "GO REAL LIVE - ${getDests()}", color = Color.White, fontWeight = FontWeight.Bold) }
        }
        if (screen == "CODES") {
            Text("GENERATE PREMIUM CODE", color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 18.sp); OutlinedTextField(value = custName, onValueChange = { custName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth()); OutlinedTextField(value = custPhone, onValueChange = { custPhone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth()); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { plan = "Daily 200" }, colors = ButtonDefaults.buttonColors(containerColor = if (plan == "Daily 200") Color.Red else Color.DarkGray)) { Text("Daily 200", color = Color.White) }; Button(onClick = { plan = "Monthly 1000" }, colors = ButtonDefaults.buttonColors(containerColor = if (plan == "Monthly 1000") Color.Red else Color.DarkGray)) { Text("Monthly 1000", color = Color.White) } }; Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { plan = "3 Months 2500" }, colors = ButtonDefaults.buttonColors(containerColor = if (plan == "3 Months 2500") Color.Red else Color.DarkGray)) { Text("3M 2500", color = Color.White) }; Button(onClick = { plan = "Year 8000" }, colors = ButtonDefaults.buttonColors(containerColor = if (plan == "Year 8000") Color.Red else Color.DarkGray)) { Text("Year 8000", color = Color.White) } }; Button(onClick = { if (custName.isBlank()) toast("Enter Name") else { val amt = when (plan) { "Daily 200" -> 200; "Monthly 1000" -> 1000; "3 Months 2500" -> 2500; else -> 8000 }; val nc = "JSTV-$amt-${Random.nextInt(1000,9999)}"; lastCode = nc; codes = codes + PremiumCode(nc, plan, amt, custName, custPhone); toast("Code: $nc"); custName = ""; custPhone = "" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("GENERATE CODE - $plan", color = Color.White) }; if (lastCode.isNotBlank()) { Text("Last Code: $lastCode", color = Color.Yellow, fontWeight = FontWeight.Bold); Button(onClick = { shareWhatsApp("Hi, your JSTV Premium Code is: $lastCode. Plan: $plan. Paybill 516600 Acc 5600935001") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("WHATSAPP CODE") } }; codes.forEach { c -> Box(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(8.dp)) { Column { Text(c.code, color = Color.Yellow, fontWeight = FontWeight.Bold); Text("${c.name} - ${c.plan} - KSH ${c.amount}", color = Color.White, fontSize = 12.sp) } }; Spacer(modifier = Modifier.height(4.dp)) }
        }
        if (screen == "MONEY") { Text("MONEY RECORD - STUDIO ADMIN", color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 20.sp); Text("Total: KSH ${codes.sumOf { it.amount }}", color = Color.Green, fontSize = 26.sp, fontWeight = FontWeight.Bold); Text("Codes Sold: ${codes.size}", color = Color.White); Text("Paybill 516600 Acc 5600935001", color = Color.White); codes.forEach { c -> Box(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(8.dp)) { Column { Text("${c.name} - KSH ${c.amount}", color = Color.White, fontWeight = FontWeight.Bold); Text("${c.plan} - ${c.code}", color = Color.Gray, fontSize = 12.sp) } }; Spacer(modifier = Modifier.height(4.dp)) } }
        if (screen == "LINK") { Text("LINK STUDIO ADMIN TO BROADCASTER STUDIO", color = Color.Yellow, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center); Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(12.dp), contentAlignment = Alignment.Center) { Text(roomCode, color = Color.Yellow, fontSize = 32.sp, fontWeight = FontWeight.Bold) }; OutlinedTextField(value = roomCode, onValueChange = { roomCode = it.uppercase() }, label = { Text("Room Code") }, modifier = Modifier.fillMaxWidth()); Text("1. ADMIN keeps this code\n2. BROADCASTER STUDIO types SAME code > LINK\n3. Same WiFi", color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center); Button(onClick = { shareWhatsApp("Hi, open JSTV BROADCASTER STUDIO (CYAN logo), enter Room Code: $roomCode, press LINK. Same WiFi. I control from JSTV STUDIO ADMIN (RED logo).") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("WHATSAPP ROOM CODE") } }
    }
}
