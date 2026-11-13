package com.jsomi.broadcaster.studioadmin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

data class Sub(var username: String, var whatsapp: String, var pin: String, var plan: String, var amount: Int, var expiry: String, var isUnlocked: Boolean, var receipt: String)
data class PremiumSet(var name: String, var price: Int, var days: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try { setContent { MaterialTheme { AdminApp() } } } catch (e: Exception) { Toast.makeText(this, "Start error: " + e.message, Toast.LENGTH_LONG).show() }
    }
}

@Composable
fun RealCamera(modifier: Modifier = Modifier, isFront: Boolean) {
    val lifecycle = LocalLifecycleOwner.current
    AndroidView(factory = { c ->
        val pv = PreviewView(c)
        try {
            val fut = ProcessCameraProvider.getInstance(c)
            fut.addListener({
                try {
                    val provider = fut.get()
                    val preview = Preview.Builder().build().also { it.setSurfaceProvider(pv.surfaceProvider) }
                    provider.unbindAll()
                    provider.bindToLifecycle(lifecycle, if (isFront) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA, preview)
                } catch (e: Exception) {}
            }, ContextCompat.getMainExecutor(c))
        } catch (e: Exception) {}
        pv
    }, modifier = modifier)
}

@Composable
fun AdminApp() {
    val ctx = LocalContext.current
    var hasPerm by remember { mutableStateOf(ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    val permLauncher = androidx.activity.compose.rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { res -> hasPerm = res.values.all { it } }
    var screen by remember { mutableStateOf("DASH") }
    var isLive by remember { mutableStateOf(false) }
    var isFront by remember { mutableStateOf(true) }
    var isMicOn by remember { mutableStateOf(true) }
    var channelName by remember { mutableStateOf("JSTV") }
    var ticker by remember { mutableStateOf("Welcome to JSTV") }
    var verse by remember { mutableStateOf("John 3:16") }
    var showTicker by remember { mutableStateOf(true) }
    var showVerse by remember { mutableStateOf(true) }
    var showLogo by remember { mutableStateOf(true) }
    var logoX by remember { mutableStateOf(20f) }; var logoY by remember { mutableStateOf(20f) }
    var tickerX by remember { mutableStateOf(0f) }; var tickerY by remember { mutableStateOf(400f) }
    var verseX by remember { mutableStateOf(20f) }; var verseY by remember { mutableStateOf(300f) }
    val subs = remember { mutableStateListOf<Sub>() }
    val premiums = remember { mutableStateListOf(PremiumSet("Daily", 200, 1), PremiumSet("Monthly", 1000, 30), PremiumSet("3 Months", 2500, 90), PremiumSet("Year", 8000, 365)) }
    var inboxMsg by remember { mutableStateOf("") }
    var msgToSub by remember { mutableStateOf("") }
    var newPremiumName by remember { mutableStateOf("") }; var newPremiumPrice by remember { mutableStateOf("") }; var newPremiumDays by remember { mutableStateOf("") }
    var adminReceipt by remember { mutableStateOf("") }; var adminUser by remember { mutableStateOf("") }
    fun toast(m: String) { Toast.makeText(ctx, m, Toast.LENGTH_SHORT).show() }
    fun openWhatsApp(num: String, msg: String) {
        val n = if (num.startsWith("0")) "254" + num.substring(1) else num.replace("+", "")
        try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$n?text=" + Uri.encode(msg)))) } catch (e: Exception) { toast("WhatsApp not found") }
    }
    if (screen == "DASH") {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("JSTV ADMIN STUDIO", color = Color.Yellow, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Paybill 516600 Acc 5600935001 | Subs: ${subs.size} | Live: ${if (isLive) "ON" else "OFF"}", color = Color.White, fontSize = 12.sp)
            Text("Time: ${SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())}", color = Color.Gray, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { screen = "LIVE" }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("📹 GO TO LIVE STUDIO", color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { Button(onClick = { screen = "SUBS" }, modifier = Modifier.weight(1f)) { Text("SUBS ${subs.size}") }; Button(onClick = { screen = "RATES" }, modifier = Modifier.weight(1f)) { Text("RATES") }; Button(onClick = { screen = "INBOX" }, modifier = Modifier.weight(1f)) { Text("INBOX") } }
            Spacer(modifier = Modifier.height(12.dp))
            Text("SUBSCRIBERS:", color = Color.Yellow, fontWeight = FontWeight.Bold)
            if (subs.isEmpty()) Text("No subscribers yet.", color = Color.Gray, fontSize = 12.sp)
            subs.forEach { s ->
                Column(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)) {
                    Text("${s.username} | ${s.whatsapp} | ${s.plan} ${s.amount} KES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Expiry: ${s.expiry} | ${if (s.isUnlocked) "UNLOCKED" else "LOCKED"} | Receipt: ${if (s.receipt.isBlank()) "NONE YET" else s.receipt}", color = if (s.isUnlocked) Color.Green else Color.Red, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(onClick = { s.isUnlocked = !s.isUnlocked; toast("${s.username} ${if (s.isUnlocked) "unlocked" else "locked"}") }, colors = ButtonDefaults.buttonColors(containerColor = if (s.isUnlocked) Color.Red else Color.Green)) { Text(if (s.isUnlocked) "LOCK" else "UNLOCK", fontSize = 9.sp) }
                        Button(onClick = { openWhatsApp(s.whatsapp, "Hi ${s.username}, Message from JSTV Admin: $msgToSub") }) { Text("WHATSAPP", fontSize = 9.sp) }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("SEND INBOX NOTIFICATION:", color = Color.Yellow, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = inboxMsg, onValueChange = { inboxMsg = it }, label = { Text("Type real notification...") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Button(onClick = { if (inboxMsg.isBlank()) toast("Type a message first") else { toast("Notification ready: $inboxMsg"); inboxMsg = "" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))) { Text("SEND NOTIFICATION TO BROADCASTERS") }
        }
    }
    if (screen == "LIVE") {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            if (hasPerm) RealCamera(modifier = Modifier.fillMaxSize(), isFront = isFront)
            else Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Camera Needs Permission", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = { permLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }) { Text("ALLOW CAMERA + MIC") }
                }
            }
            if (hasPerm) {
                if (showLogo) Box(modifier = Modifier.offset { IntOffset(logoX.roundToInt(), logoY.roundToInt()) }.pointerInput(Unit) { detectDragGestures { _, d -> logoX += d.x; logoY += d.y } }.background(Color.Red).padding(8.dp)) { Text("JSTV $channelName", color = Color.White, fontWeight = FontWeight.Bold) }
                if (showVerse) Box(modifier = Modifier.offset { IntOffset(verseX.roundToInt(), verseY.roundToInt()) }.pointerInput(Unit) { detectDragGestures { _, d -> verseX += d.x; verseY += d.y } }.background(Color.Black).padding(8.dp)) { Text("Verse: $verse", color = Color.Yellow) }
                if (showTicker) Box(modifier = Modifier.offset { IntOffset(tickerX.roundToInt(), tickerY.roundToInt()) }.pointerInput(Unit) { detectDragGestures { _, d -> tickerX += d.x; tickerY += d.y } }.fillMaxWidth().background(Color.Yellow).padding(8.dp)) { Text("Ticker: $ticker", color = Color.Black) }
            }
            Column(modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)) { Text(if (isLive) "● LIVE" else "○ NOT LIVE", color = if (isLive) Color.Red else Color.White, fontWeight = FontWeight.Bold); Text("Mic: ${if (isMicOn) "ON" else "OFF"} | Cam: ${if (isFront) "FRONT" else "BACK"}", color = Color.White, fontSize = 11.sp) }
            Column(modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)) {
                if (isLive) Button(onClick = { isLive = false; toast("Live Ended") }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("END LIVE", color = Color.White, fontWeight = FontWeight.Bold) }
                else Button(onClick = { if (!hasPerm) permLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) else { isLive = true; toast("You are LIVE!") } }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("GO LIVE", color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { Button(onClick = { screen = "SETUP" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("LIVE SETUP", color = Color.Black, fontWeight = FontWeight.Bold) }; Button(onClick = { screen = "DASH" }, modifier = Modifier.weight(1f)) { Text("BACK") } }
            }
        }
    }
    if (screen == "SETUP") {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("LIVE SETUP", color = Color.Yellow, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = channelName, onValueChange = { channelName = it }, label = { Text("Channel Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = ticker, onValueChange = { ticker = it }, label = { Text("Ticker Text") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = verse, onValueChange = { verse = it }, label = { Text("Verse") }, modifier = Modifier.fillMaxWidth())
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Show Logo", color = Color.White, modifier = Modifier.weight(1f)); Switch(checked = showLogo, onCheckedChange = { showLogo = it }) }
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Show Verse", color = Color.White, modifier = Modifier.weight(1f)); Switch(checked = showVerse, onCheckedChange = { showVerse = it }) }
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Show Ticker", color = Color.White, modifier = Modifier.weight(1f)); Switch(checked = showTicker, onCheckedChange = { showTicker = it }) }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { Button(onClick = { isFront = true }, modifier = Modifier.weight(1f)) { Text("FRONT CAM") }; Button(onClick = { isFront = false }, modifier = Modifier.weight(1f)) { Text("BACK CAM") } }
            Button(onClick = { isMicOn = !isMicOn }, modifier = Modifier.fillMaxWidth()) { Text(if (isMicOn) "MIC ON" else "MIC OFF") }
            Button(onClick = { permLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("ALLOW CAMERA + MIC") }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { screen = "LIVE"; toast("Setup Saved") }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("OK - APPLY TO MAIN SCREEN", color = Color.White, fontWeight = FontWeight.Bold) }
            Button(onClick = { screen = "LIVE" }, modifier = Modifier.fillMaxWidth()) { Text("BACK") }
        }
    }
    if (screen == "SUBS") {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("SUBSCRIBERS + VERIFY PAYMENT", color = Color.Yellow, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Only unlock AFTER you check YOUR M-PESA SMS.", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = adminUser, onValueChange = { adminUser = it }, label = { Text("Username to Unlock") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = adminReceipt, onValueChange = { adminReceipt = it.uppercase() }, label = { Text("Receipt Code from User") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                if (adminReceipt.length < 6 || adminUser.isBlank()) toast("Enter Username + Receipt")
                else {
                    val target = subs.find { it.username.equals(adminUser, ignoreCase = true) }
                    if (target != null) { target.isUnlocked = true; target.receipt = adminReceipt; toast("${target.username} UNLOCKED - Receipt $adminReceipt"); adminReceipt = ""; adminUser = "" }
                    else toast("User $adminUser not found.")
                }
            }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("VERIFY & UNLOCK USER", color = Color.White, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(10.dp))
            Text("STK PUSH:", color = Color.Cyan, fontWeight = FontWeight.Bold)
            Text("1. Open M-PESA Business App\n2. Send STK Push to user number\n3. User gets REAL Safaricom PIN popup", color = Color.Gray, fontSize = 11.sp)
            Button(onClick = {
                if (adminUser.isBlank()) toast("Enter Username first")
                else {
                    val t = subs.find { it.username.equals(adminUser, ignoreCase = true) }
                    if (t != null) { openWhatsApp(t.whatsapp, "Hi ${t.username}, I am sending STK PUSH now for ${t.plan} - ${t.amount} KES. Watch for Safaricom PIN popup."); toast("Now push from M-PESA Business App") }
                    else toast("User not found")
                }
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)) { Text("SEND STK PUSH TO THIS USER") }
            Spacer(modifier = Modifier.height(10.dp))
            var newUser by remember { mutableStateOf("") }; var newWhats by remember { mutableStateOf("") }
            OutlinedTextField(value = newUser, onValueChange = { newUser = it }, label = { Text("Real Username") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = newWhats, onValueChange = { newWhats = it }, label = { Text("Real WhatsApp") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { if (newUser.isBlank() || newWhats.isBlank()) toast("Enter Username + WhatsApp") else { subs.add(Sub(newUser, newWhats, "", "Monthly", 1000, "Not set yet", false, "")); toast("Added $newUser"); newUser = ""; newWhats = "" } }, modifier = Modifier.fillMaxWidth()) { Text("ADD REAL SUBSCRIBER") }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = msgToSub, onValueChange = { msgToSub = it }, label = { Text("Message...") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Button(onClick = { if (msgToSub.isBlank()) toast("Type message first") else toast("Message ready") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))) { Text("PREPARE MESSAGE") }
            Spacer(modifier = Modifier.height(10.dp))
            if (subs.isEmpty()) Text("No subscribers yet.", color = Color.Gray)
            subs.forEach { s ->
                Column(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)) {
                    Text("${s.username} | ${s.whatsapp} | ${s.plan}", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Expiry ${s.expiry} | ${if (s.isUnlocked) "UNLOCKED" else "LOCKED"} | Receipt: ${if (s.receipt.isBlank()) "NONE" else s.receipt}", color = if (s.isUnlocked) Color.Green else Color.Red)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(onClick = { openWhatsApp(s.whatsapp, "Hi ${s.username}, Message from JSTV Admin: $msgToSub") }) { Text("WHATSAPP", fontSize = 9.sp) }
                        Button(onClick = { s.isUnlocked = !s.isUnlocked }, colors = ButtonDefaults.buttonColors(containerColor = if (s.isUnlocked) Color.Red else Color.Green)) { Text(if (s.isUnlocked) "LOCK" else "UNLOCK", fontSize = 9.sp) }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            Button(onClick = { screen = "DASH" }, modifier = Modifier.fillMaxWidth()) { Text("BACK") }
        }
    }
    if (screen == "RATES") {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("RATES + PREMIUM SETS", color = Color.Yellow, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            premiums.forEach { p -> Row(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)) { Text("${p.name} - ${p.price} KES - ${p.days} Days", color = Color.White, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold) }; Spacer(modifier = Modifier.height(6.dp)) }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = newPremiumName, onValueChange = { newPremiumName = it }, label = { Text("Premium Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = newPremiumPrice, onValueChange = { newPremiumPrice = it }, label = { Text("Price KES") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = newPremiumDays, onValueChange = { newPremiumDays = it }, label = { Text("Days") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { if (newPremiumName.isNotBlank() && (newPremiumPrice.toIntOrNull() ?: 0) > 0) { premiums.add(PremiumSet(newPremiumName, newPremiumPrice.toInt(), newPremiumDays.toIntOrNull() ?: 30)); toast("Premium ${newPremiumName} added"); newPremiumName = ""; newPremiumPrice = ""; newPremiumDays = "" } else toast("Enter Name + Real Price") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("ADD PREMIUM SET") }
            Button(onClick = { screen = "DASH" }, modifier = Modifier.fillMaxWidth()) { Text("BACK") }
        }
    }
    if (screen == "INBOX") {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("INBOX - SEND TO BROADCASTERS", color = Color.Yellow, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = inboxMsg, onValueChange = { inboxMsg = it }, label = { Text("Real notification...") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Button(onClick = { if (inboxMsg.isBlank()) toast("Type a real message first") else { toast("Notification sent: $inboxMsg"); inboxMsg = "" } }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))) { Text("SEND NOTIFICATION") }
            Button(onClick = { screen = "DASH" }, modifier = Modifier.fillMaxWidth()) { Text("BACK") }
        }
    }
}
