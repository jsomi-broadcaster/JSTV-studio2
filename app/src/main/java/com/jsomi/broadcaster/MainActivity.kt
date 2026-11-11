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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// JSTV STUDIO - ADMIN - LOGO: RED + GOLD - NEVER SAME AS VIEWERS
// FOR KEVIN ONLY - NEVER GIVE TO VIEWERS
// WhatsApp: 0763052911

data class PremiumCode(val code: String, val plan: String, val amount: Int, val name: String, val phone: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AdminStudioApp() } }
    }
}

@Composable
fun AdminStudioApp() {
    val ctx = LocalContext.current
    var screen by remember { mutableStateOf("STUDIO") }
    var roomCode by remember { mutableStateOf("JSTV-2026") }
    var isLinked by remember { mutableStateOf(false) }
    var channel by remember { mutableStateOf("JSTV Main") }
    var scene by remember { mutableStateOf("Studio") }
    var ticker by remember { mutableStateOf("Welcome to JSTV - Jehovah Shalom TV - God Bless You") }
    var tickerOn by remember { mutableStateOf(true) }
    var tickerPos by remember { mutableStateOf("Bottom") }
    var tickerSpeed by remember { mutableStateOf("Medium") }
    var cameraOn by remember { mutableStateOf(true) }
    var micOn by remember { mutableStateOf(true) }
    var isLive by remember { mutableStateOf(false) }
    var isDemo by remember { mutableStateOf(false) }
    var quality by remember { mutableStateOf("HD 720p") }
    var streamTitle by remember { mutableStateOf("My Live Show") }
    var liveSeconds by remember { mutableStateOf(0) }
    var currentTime by remember { mutableStateOf("") }
    var blink by remember { mutableStateOf(true) }
    var fullScreen by remember { mutableStateOf(false) }
    var isLandscape by remember { mutableStateOf(false) }
    var ytOn by remember { mutableStateOf(true) }
    var fbOn by remember { mutableStateOf(false) }
    var tkOn by remember { mutableStateOf(false) }
    var ytKey by remember { mutableStateOf("") }
    var fbKey by remember { mutableStateOf("") }
    var tkKey by remember { mutableStateOf("") }
    var logoOn by remember { mutableStateOf(true) }
    var logoText by remember { mutableStateOf("JSTV") }
    var logoPos by remember { mutableStateOf("Top Right") }
    var lsName by remember { mutableStateOf("Pastor Kevin") }
    var lsTitle by remember { mutableStateOf("Host - Jehovah Shalom TV") }
    var lsOn by remember { mutableStateOf(false) }
    var lsPos by remember { mutableStateOf("Bottom Left") }
    var titlePos by remember { mutableStateOf("Center") }
    var loopOn by remember { mutableStateOf(false) }
    var loopVideo by remember { mutableStateOf("Worship Loop 1") }
    var introOn by remember { mutableStateOf(true) }
    var introVideo by remember { mutableStateOf("JSTV Intro 1 - 10s") }
    var introDuration by remember { mutableStateOf("10s") }
    var introPlaying by remember { mutableStateOf(false) }
    var introCountdown by remember { mutableStateOf(10) }
    var custName by remember { mutableStateOf("") }
    var custPhone by remember { mutableStateOf("") }
    var plan by remember { mutableStateOf("Daily 200") }
    var codes by remember { mutableStateOf(listOf<PremiumCode>()) }
    var lastCode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { while (true) { val f = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()); currentTime = f.format(Date()); delay(1000) } }
    LaunchedEffect(isLive, isDemo) { while (isLive || isDemo) { delay(1000); liveSeconds += 1 } }
    LaunchedEffect(Unit) { while (true) { delay(500); blink = !blink } }
    LaunchedEffect(introPlaying) { if (introPlaying) { var c = if (introDuration == "5s") 5 else if (introDuration == "15s") 15 else if (introDuration == "30s") 30 else 10; introCountdown = c; while (c > 0 && introPlaying) { delay(1000); c -= 1; introCountdown = c }; if (c <= 0) introPlaying = false } }

    fun formatLive(s: Int): String { return String.format("%02d:%02d:%02d", s/3600, (s%3600)/60, s%60) }
    fun toast(m: String) { Toast.makeText(ctx, m, Toast.LENGTH_SHORT).show() }
    fun shareWhatsApp(msg: String) { val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254763052911?text=" + Uri.encode(msg))); try { ctx.startActivity(i) } catch (e: Exception) { toast("WhatsApp not found") } }
    fun getDests(): String { val l = mutableListOf<String>(); if (ytOn) l.add("YouTube"); if (fbOn) l.add("Facebook"); if (tkOn) l.add("TikTok"); return if (l.isEmpty()) "YouTube (Default)" else l.joinToString(" + ") }
    fun stopAll() { isLive = false; isDemo = false; introPlaying = false; fullScreen = false; liveSeconds = 0; toast("Stopped - Timer Reset") }

    if (fullScreen && (isLive || isDemo || introPlaying)) {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(8.dp)) {
            // STUDIO FULL SCREEN - RED LOGO
            Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(6.dp)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("JSTV STUDIO", color = Color.Yellow, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text(if (isLive) "● LIVE ${formatLive(liveSeconds)}" else "● DEMO ${formatLive(liveSeconds)}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold) } }
            Text("Room: $roomCode | ${getDests()} | $currentTime", color = Color.Gray, fontSize = 11.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text(streamTitle, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.weight(1f))
            if (lsOn) { Box(modifier = Modifier.fillMaxWidth().background(Color.Yellow).padding(10.dp)) { Column { Text(lsName, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text(lsTitle, color = Color.Black, fontSize = 12.sp) } }; Spacer(modifier = Modifier.height(8.dp)) }
            if (tickerOn) { Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(8.dp)) { Text(ticker, color = Color.White, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) } }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { fullScreen = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("EXIT FULL SCREEN", color = Color.Black) }; Button(onClick = { stopAll() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("STOP LIVE", color = Color.White) } }
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        // ===== STUDIO LOGO - RED + GOLD - ADMIN ONLY =====
        Box(modifier = Modifier.fillMaxWidth().background(Color.Red).border(3.dp, Color.Yellow).padding(12.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("JSTV", color = Color.Yellow, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                Text("STUDIO - ADMIN ONLY", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.background(Color.Yellow).padding(horizontal = 10.dp, vertical = 3.dp)) { Text("● ADMIN - KEVIN ONLY - DO NOT SHARE", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("If Top Is RED + GOLD = You Are In STUDIO (Admin)", color = Color.Yellow, fontSize = 11.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { screen = "STUDIO" }, colors = ButtonDefaults.buttonColors(containerColor = if (screen == "STUDIO") Color.Red else Color.DarkGray)) { Text("STUDIO", color = Color.White) }
            Button(onClick = { screen = "CODES" }, colors = ButtonDefaults.buttonColors(containerColor = if (screen == "CODES") Color.Red else Color.DarkGray)) { Text("CODES", color = Color.White) }
            Button(onClick = { screen = "MONEY" }, colors = ButtonDefaults.buttonColors(containerColor = if (screen == "MONEY") Color.Red else Color.DarkGray)) { Text("MONEY", color = Color.White) }
            Button(onClick = { screen = "LINK" }, colors = ButtonDefaults.buttonColors(containerColor = if (screen == "LINK") Color.Red else Color.DarkGray)) { Text("LINK", color = Color.White) }
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (screen == "STUDIO") {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Room: $roomCode", color = Color.Yellow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(if (isLive) "● LIVE ${formatLive(liveSeconds)}" else if (isDemo) "● DEMO ${formatLive(liveSeconds)}" else "● OFFLINE", color = if (isLive) Color.Red else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text("Time: $currentTime | Live: ${formatLive(liveSeconds)}", color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(Color.DarkGray).border(2.dp, Color.Red).padding(8.dp)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(if (logoOn) logoText else "", color = Color.Yellow, fontSize = 16.sp, fontWeight = FontWeight.Bold); Text(if (isLive) "● LIVE" else "PREVIEW - STUDIO", color = if (isLive) Color.Red else Color.White, fontSize = 12.sp) }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(streamTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text("$channel | $scene | $quality | ${getDests()}", color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.weight(1f))
                    if (lsOn) { Box(modifier = Modifier.fillMaxWidth().background(Color.Yellow).padding(6.dp)) { Column { Text(lsName, color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold); Text(lsTitle, color = Color.Black, fontSize = 11.sp) } } }
                    if (tickerOn) { Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(6.dp)) { Text(ticker, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) } }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { fullScreen = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("FULL SCREEN PREVIEW - STUDIO", color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Stream Title", color = Color.White, fontSize = 12.sp)
            OutlinedTextField(value = streamTitle, onValueChange = { streamTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Text("MULTISTREAMING - ADMIN ONLY - Keys Here", color = Color.Yellow, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("URLs Already Set - Only Paste Keys", color = Color.Gray, fontSize = 11.sp)
            Text("1. YOUTUBE - rtmp://a.rtmp.youtube.com/live2", color = Color.White, fontSize = 11.sp)
            OutlinedTextField(value = ytKey, onValueChange = { ytKey = it }, label = { Text("YouTube Key") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { ytOn = true }, colors = ButtonDefaults.buttonColors(containerColor = if (ytOn) Color.Red else Color.DarkGray)) { Text("YT ON") }; Button(onClick = { ytOn = false }) { Text("YT OFF") } }
            Spacer(modifier = Modifier.height(6.dp))
            Text("2. FACEBOOK - rtmps://live-api-s.facebook.com:443/rtmp/", color = Color.White, fontSize = 11.sp)
            OutlinedTextField(value = fbKey, onValueChange = { fbKey = it }, label = { Text("Facebook Key") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { fbOn = true }, colors = ButtonDefaults.buttonColors(containerColor = if (fbOn) Color(0xFF1877F2) else Color.DarkGray)) { Text("FB ON", color = Color.White) }; Button(onClick = { fbOn = false }) { Text("FB OFF") } }
            Spacer(modifier = Modifier.height(6.dp))
            Text("3. TIKTOK - rtmp://push-rtmp-l1.tiktok.com/live/", color = Color.White, fontSize = 11.sp)
            OutlinedTextField(value = tkKey, onValueChange = { tkKey = it }, label = { Text("TikTok Key") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { tkOn = true }, colors = ButtonDefaults.buttonColors(containerColor = if (tkOn) Color.White else Color.DarkGray)) { Text("TK ON", color = if (tkOn) Color.Black else Color.White) }; Button(onClick = { tkOn = false }) { Text("TK OFF") } }
            Text("Active: ${getDests()}", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text("LOGO / TITLE / TICKER / LOOPS / INTRO - ALL IN STUDIO", color = Color.Yellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = logoText, onValueChange = { logoText = it }, label = { Text("Logo Text") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { logoPos = "Top Left" }, colors = ButtonDefaults.buttonColors(containerColor = if (logoPos == "Top Left") Color.Yellow else Color.DarkGray)) { Text("Logo Top Left", color = if (logoPos == "Top Left") Color.Black else Color.White) }; Button(onClick = { logoPos = "Top Right" }, colors = ButtonDefaults.buttonColors(containerColor = if (logoPos == "Top Right") Color.Yellow else Color.DarkGray)) { Text("Logo Top Right", color = if (logoPos == "Top Right") Color.Black else Color.White) }; Button(onClick = { logoPos = "Bottom" }, colors = ButtonDefaults.buttonColors(containerColor = if (logoPos == "Bottom") Color.Yellow else Color.DarkGray)) { Text("Logo Bottom", color = if (logoPos == "Bottom") Color.Black else Color.White) } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { logoOn = true }, colors = ButtonDefaults.buttonColors(containerColor = if (logoOn) Color.Green else Color.DarkGray)) { Text("LOGO ON") }; Button(onClick = { logoOn = false }, colors = ButtonDefaults.buttonColors(containerColor = if (!logoOn) Color.Red else Color.DarkGray)) { Text("LOGO OFF") } }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = lsName, onValueChange = { lsName = it }, label = { Text("LS Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = lsTitle, onValueChange = { lsTitle = it }, label = { Text("LS Title") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { lsOn = true }, colors = ButtonDefaults.buttonColors(containerColor = if (lsOn) Color.Yellow else Color.DarkGray)) { Text("SHOW LS", color = if (lsOn) Color.Black else Color.White) }; Button(onClick = { lsOn = false }, colors = ButtonDefaults.buttonColors(containerColor = if (!lsOn) Color.Red else Color.DarkGray)) { Text("HIDE LS") } }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Ticker - Single Line - Movable", color = Color.White, fontSize = 12.sp)
            OutlinedTextField(value = ticker, onValueChange = { ticker = it }, label = { Text("Scrolling Text") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(6.dp)) { Text(ticker, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { tickerPos = "Top" }, colors = ButtonDefaults.buttonColors(containerColor = if (tickerPos == "Top") Color.Yellow else Color.DarkGray)) { Text("TOP", color = if (tickerPos == "Top") Color.Black else Color.White) }; Button(onClick = { tickerPos = "Middle" }, colors = ButtonDefaults.buttonColors(containerColor = if (tickerPos == "Middle") Color.Yellow else Color.DarkGray)) { Text("MIDDLE", color = if (tickerPos == "Middle") Color.Black else Color.White) }; Button(onClick = { tickerPos = "Bottom" }, colors = ButtonDefaults.buttonColors(containerColor = if (tickerPos == "Bottom") Color.Yellow else Color.DarkGray)) { Text("BOTTOM", color = if (tickerPos == "Bottom") Color.Black else Color.White) } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { tickerOn = true }, colors = ButtonDefaults.buttonColors(containerColor = if (tickerOn) Color.Green else Color.DarkGray)) { Text("TICKER ON") }; Button(onClick = { tickerOn = false }, colors = ButtonDefaults.buttonColors(containerColor = if (!tickerOn) Color.Red else Color.DarkGray)) { Text("TICKER OFF") } }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Intro: $introVideo - $introDuration", color = Color.Yellow, fontSize = 12.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { introVideo = "JSTV Intro 1 - 10s"; introDuration = "10s" }, colors = ButtonDefaults.buttonColors(containerColor = if (introVideo == "JSTV Intro 1 - 10s") Color.Yellow else Color.DarkGray)) { Text("Intro 10s", color = if (introVideo == "JSTV Intro 1 - 10s") Color.Black else Color.White) }; Button(onClick = { introVideo = "Worship Intro - 15s"; introDuration = "15s" }, colors = ButtonDefaults.buttonColors(containerColor = if (introVideo == "Worship Intro - 15s") Color.Yellow else Color.DarkGray)) { Text("Worship 15s", color = if (introVideo == "Worship Intro - 15s") Color.Black else Color.White) } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { introOn = true }, colors = ButtonDefaults.buttonColors(containerColor = if (introOn) Color.Green else Color.DarkGray)) { Text("INTRO ON") }; Button(onClick = { introOn = false }, colors = ButtonDefaults.buttonColors(containerColor = if (!introOn) Color.Red else Color.DarkGray)) { Text("INTRO OFF") } }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Loop: $loopVideo", color = Color.Cyan, fontSize = 12.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { loopOn = true }, colors = ButtonDefaults.buttonColors(containerColor = if (loopOn) Color.Green else Color.DarkGray)) { Text("LOOP ON") }; Button(onClick = { loopOn = false }, colors = ButtonDefaults.buttonColors(containerColor = if (!loopOn) Color.Red else Color.DarkGray)) { Text("LOOP OFF") } }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { channel = "JSTV Main" }) { Text("Main") }; Button(onClick = { channel = "JSTV Church" }) { Text("Church") }; Button(onClick = { scene = if (scene == "Studio") "Outdoor" else "Studio" }, colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("Scene: $scene", color = Color.Black) } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { cameraOn = !cameraOn }, colors = ButtonDefaults.buttonColors(containerColor = if (cameraOn) Color.Green else Color.DarkGray)) { Text(if (cameraOn) "CAMERA ON" else "CAMERA OFF") }; Button(onClick = { micOn = !micOn }, colors = ButtonDefaults.buttonColors(containerColor = if (micOn) Color.Green else Color.DarkGray)) { Text(if (micOn) "MIC ON" else "MIC OFF") } }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { isDemo = true; isLive = false; liveSeconds = 0; fullScreen = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("DEMO - FREE - FULL SCREEN", color = Color.Black, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { if (isLive) stopAll() else { if (introOn && !introPlaying) { introPlaying = true } else { isLive = true; liveSeconds = 0; fullScreen = true } } }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isLive) Color.Red else Color.Green)) { Text(if (isLive) "STOP REAL LIVE - ${formatLive(liveSeconds)}" else "GO REAL LIVE - ${getDests()}", color = Color.White, fontWeight = FontWeight.Bold) }
        }
        if (screen == "CODES") {
            Text("GENERATE PREMIUM CODE", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = custName, onValueChange = { custName = it }, label = { Text("Viewer Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = custPhone, onValueChange = { custPhone = it }, label = { Text("Viewer Phone") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { plan = "Daily 200" }, colors = ButtonDefaults.buttonColors(containerColor = if (plan == "Daily 200") Color.Red else Color.DarkGray)) { Text("Daily 200", color = Color.White) }; Button(onClick = { plan = "Monthly 1000" }, colors = ButtonDefaults.buttonColors(containerColor = if (plan == "Monthly 1000") Color.Red else Color.DarkGray)) { Text("Monthly 1000", color = Color.White) } }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { Button(onClick = { plan = "3 Months 2500" }, colors = ButtonDefaults.buttonColors(containerColor = if (plan == "3 Months 2500") Color.Red else Color.DarkGray)) { Text("3M 2500", color = Color.White) }; Button(onClick = { plan = "Year 8000" }, colors = ButtonDefaults.buttonColors(containerColor = if (plan == "Year 8000") Color.Red else Color.DarkGray)) { Text("Year 8000", color = Color.White) } }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { if (custName.isBlank()) toast("Enter Name") else { val amt = when (plan) { "Daily 200" -> 200; "Monthly 1000" -> 1000; "3 Months 2500" -> 2500; else -> 8000 }; val nc = "JSTV-$amt-${Random.nextInt(1000,9999)}"; lastCode = nc; codes = codes + PremiumCode(nc, plan, amt, custName, custPhone); toast("Code: $nc"); custName = ""; custPhone = "" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("GENERATE CODE - $plan", color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(10.dp))
            if (lastCode.isNotBlank()) { Text("Last Code: $lastCode", color = Color.Yellow, fontSize = 15.sp, fontWeight = FontWeight.Bold); Button(onClick = { shareWhatsApp("Hi, your JSTV Premium Code is: $lastCode. Plan: $plan. Enter it in BROADCASTER to Go Real Live. Paybill 516600 Acc 5600935001") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("WHATSAPP CODE TO VIEWER") } }
            Spacer(modifier = Modifier.height(12.dp))
            Text("ALL CODES - ${codes.size}", color = Color.Yellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            codes.forEach { c -> Box(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(8.dp)) { Column { Text(c.code, color = Color.Yellow, fontWeight = FontWeight.Bold); Text("${c.name} - ${c.phone} - ${c.plan} - KSH ${c.amount}", color = Color.White, fontSize = 12.sp) } }; Spacer(modifier = Modifier.height(4.dp)) }
        }
        if (screen == "MONEY") {
            Text("MONEY RECORD - STUDIO", color = Color.Yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Total: KSH ${codes.sumOf { it.amount }}", color = Color.Green, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Codes Sold: ${codes.size}", color = Color.White, fontSize = 14.sp)
            Text("Paybill 516600 Acc 5600935001", color = Color.White, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            codes.forEach { c -> Box(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(8.dp)) { Column { Text("${c.name} - KSH ${c.amount}", color = Color.White, fontWeight = FontWeight.Bold); Text("${c.plan} - ${c.code}", color = Color.Gray, fontSize = 12.sp) } }; Spacer(modifier = Modifier.height(4.dp)) }
            Button(onClick = { shareWhatsApp("JSTV MONEY - Total KSH ${codes.sumOf { it.amount }} - Codes ${codes.size}") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("SHARE MONEY RECORD") }
        }
        if (screen == "LINK") {
            Text("LINK STUDIO TO BROADCASTER", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Your Room Code - Give this to Broadcaster phone", color = Color.White, fontSize = 13.sp, textAlign = TextAlign.Center)
            Box(modifier = Modifier.fillMaxWidth().background(Color.Red).padding(12.dp), contentAlignment = Alignment.Center) { Text(roomCode, color = Color.Yellow, fontSize = 32.sp, fontWeight = FontWeight.Bold) }
            OutlinedTextField(value = roomCode, onValueChange = { roomCode = it.uppercase() }, label = { Text("Room Code") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            Text("1. Studio phone: Keep this code", color = Color.White, fontSize = 12.sp)
            Text("2. Broadcaster phone: Open BROADCASTER > Type SAME code > LINK", color = Color.White, fontSize = 12.sp)
            Text("3. Both phones on SAME WiFi", color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { isLinked = true; toast("Studio Ready - Waiting for Broadcaster $roomCode") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("STUDIO READY - WAIT FOR BROADCASTER", color = Color.White) }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { shareWhatsApp("Hi, open JSTV BROADCASTER (CYAN logo), enter Room Code: $roomCode, press LINK. Same WiFi. I will control from STUDIO (RED logo).") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("WHATSAPP ROOM CODE") }
        }
    }
}
