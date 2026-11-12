package com.jsomi.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

// JSTV ADMIN STUDIO - CORRECT PACKAGE FOR JSTV-studio2
// namespace = com.jsomi.admin - Manifest .MainActivity = com.jsomi.admin.MainActivity
// WhatsApp 0763052911 - Paybill 516600 Acc 5600935001

data class Subscriber(
    var username: String, var whatsapp: String, var password: String,
    var plan: String, var start: String, var expiry: String,
    var amount: String, var isLive: Boolean, var code: String
)
data class Payment(var name: String, var amount: String, var method: String, var date: String)
data class Offer(var title: String, var detail: String, var active: Boolean)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AdminApp() } }
    }
}

@Composable
fun AdminApp() {
    val ctx = LocalContext.current
    var screen by remember { mutableStateOf("PERMISSIONS") }
    var tab by remember { mutableStateOf("CONTROL") }

    val subscribers = remember { mutableStateListOf(
        Subscriber("KevinDemo","0763052911","Ab1234","Monthly","12 Nov 2026","12 Dec 2026","1000",true,"JSTV-1000-AB12"),
        Subscriber("MaryChurch","0722000000","Xy9876","Daily","12 Nov 2026","13 Nov 2026","200",false,"JSTV-200-XY98")
    )}
    val payments = remember { mutableStateListOf(
        Payment("KevinDemo","1000","M-PESA","12 Nov 2026 7:00 PM"),
        Payment("MaryChurch","200","M-PESA","12 Nov 2026 6:00 PM")
    )}
    val offers = remember { mutableStateListOf(
        Offer("BLACK FRIDAY - 50% OFF Monthly","Monthly 1000 -> 500",true),
        Offer("CHURCH PACKAGE - 3 Months 2000","Normal 2500, Save 500",true)
    )}
    val codes = remember { mutableStateListOf("JSTV-1000-AB12","JSTV-200-XY98") }

    var roomCode by remember { mutableStateOf("JSTV-2026") }
    var ticker by remember { mutableStateOf("Welcome to JSTV - Your Voice, Your Channel") }
    var verse by remember { mutableStateOf("John 3:16 - For God so loved the world...") }
    var logoText by remember { mutableStateOf("JSTV") }
    var displayText by remember { mutableStateOf("LIVE FROM JSTV STUDIO") }
    var emergency by remember { mutableStateOf("your live ends in 10 minute") }
    var bannerOn by remember { mutableStateOf(true) }
    var tickerOn by remember { mutableStateOf(true) }
    var verseOn by remember { mutableStateOf(true) }
    var logoOn by remember { mutableStateOf(true) }
    var displayOn by remember { mutableStateOf(true) }
    var streamTitle by remember { mutableStateOf("My Live Show") }
    var source by remember { mutableStateOf("REMOTE") }
    var quality by remember { mutableStateOf("HD 720p") }
    var flashOn by remember { mutableStateOf(false) }
    var beautyOn by remember { mutableStateOf(false) }
    var ytKey by remember { mutableStateOf("") }
    var fbKey by remember { mutableStateOf("") }
    var tkKey by remember { mutableStateOf("") }
    var ytOn by remember { mutableStateOf(true) }
    var fbOn by remember { mutableStateOf(true) }
    var tkOn by remember { mutableStateOf(false) }
    var genName by remember { mutableStateOf("") }
    var genWhats by remember { mutableStateOf("") }
    var genPlan by remember { mutableStateOf("Monthly") }
    var genCode by remember { mutableStateOf("") }
    var notifText by remember { mutableStateOf("") }
    var notifTo by remember { mutableStateOf("ALL Subscribers") }
    var accName by remember { mutableStateOf("") }
    var accAmount by remember { mutableStateOf("") }
    var accMethod by remember { mutableStateOf("M-PESA") }
    var smsText by remember { mutableStateOf("") }
    var detected by remember { mutableStateOf("") }
    var offerTitle by remember { mutableStateOf("") }
    var offerDetail by remember { mutableStateOf("") }
    var newUser by remember { mutableStateOf("") }
    var newWhats by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var newPlan by remember { mutableStateOf("Monthly") }

    fun toast(m: String) { Toast.makeText(ctx, m, Toast.LENGTH_SHORT).show() }
    fun openWhatsApp(num: String, msg: String) {
        val clean = num.replace("+", "").replace(" ", "")
        val n = if (clean.startsWith("0")) "254" + clean.substring(1) else clean
        try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$n?text=" + Uri.encode(msg)))) } catch (e: Exception) { toast("WhatsApp not found") }
    }
    fun today(): String { return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()) }
    fun expiryFor(plan: String): String {
        val cal = Calendar.getInstance()
        when (plan) { "Daily" -> cal.add(Calendar.DAY_OF_YEAR, 1); "Monthly" -> cal.add(Calendar.MONTH, 1); "3 Months" -> cal.add(Calendar.MONTH, 3); "Year" -> cal.add(Calendar.YEAR, 1) }
        return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)
    }
    fun priceFor(plan: String): String { return when (plan) { "Daily" -> "200"; "Monthly" -> "1000"; "3 Months" -> "2500"; else -> "8000" } }
    fun makeCode(plan: String): String { return "JSTV-${priceFor(plan)}-${(1000..9999).random()}" }

    if (screen == "PERMISSIONS") {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
            Text("JSTV", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().background(Color.Red).padding(16.dp))
            Text("STUDIO ADMIN", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("RED LOGO = BOSS", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("PERMISSIONS - ADMIN", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Camera: OK\nAudio: OK\nAllow all", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { toast("Permissions Granted") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("ALLOW ALL PERMISSIONS", color = Color.White) }
            Button(onClick = { toast("Open Settings") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF))) { Text("OPEN SETTINGS") }
            Button(onClick = { screen = "DASH" }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("CONTINUE - ADMIN DASHBOARD", color = Color.White) }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("JSTV STUDIO ADMIN", color = Color.Yellow, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("ADMIN PREVIEW - NOT LIVE - Controls Broadcaster - Does NOT Broadcast", color = Color.White, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("CONTROL", "CODES", "SUBS", "MONEY", "OFFERS", "NOTIFY").forEach { t ->
                    Button(onClick = { tab = t }, colors = ButtonDefaults.buttonColors(containerColor = if (tab == t) Color.Yellow else Color.DarkGray), modifier = Modifier.weight(1f).height(36.dp), contentPadding = PaddingValues(2.dp)) { Text(t, fontSize = 9.sp, color = if (tab == t) Color.Black else Color.White) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (tab == "CONTROL") {
                Text("ADMIN PREVIEW - NOT LIVE", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("12 viewers", color = Color.Yellow, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                if (bannerOn) Text("EMERGENCY: $emergency", color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color.Yellow).padding(6.dp))
                Text(logoText, color = Color.Red, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(displayText, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Verse: $verse", color = Color.Yellow, fontSize = 12.sp)
                Text("Ticker: $ticker", color = Color.Cyan, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { toast("BACK Camera") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("BACK") }
                    Button(onClick = { toast("FRONT Camera") }) { Text("FRONT") }
                    Button(onClick = { toast("CAM Toggled") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("CAM ON") }
                    Button(onClick = { toast("MIC Toggled") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("MIC ON") }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { toast("GO LIVE command sent to Broadcaster") }, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("GO LIVE - CONTROL BROADCASTER", fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.height(10.dp))
                Text("CONTROL BROADCASTER - FROM FAR", color = Color.Yellow, fontWeight = FontWeight.Bold)
                Text("ROOM CODE:", color = Color.Yellow, fontSize = 13.sp)
                Row {
                    OutlinedTextField(value = roomCode, onValueChange = { roomCode = it }, label = { Text("Room Code") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { roomCode = "JSTV-${(1000..9999).random()}"; toast("New Room: $roomCode") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("NEW") }
                }
                Button(onClick = { openWhatsApp("254763052911", "Room Code: $roomCode - Join JSTV Live") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("SHARE ROOM CODE ON WHATSAPP") }
                Spacer(modifier = Modifier.height(10.dp))
                Text("SOURCE:", color = Color.Yellow, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { source = "MAIN CAMERA" }) { Text("MAIN CAMERA", fontSize = 10.sp) }
                    Button(onClick = { source = "REMOTE" }, colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("REMOTE", color = Color.Black, fontSize = 10.sp) }
                    Button(onClick = { source = "SCREEN" }) { Text("SCREEN", fontSize = 10.sp) }
                }
                Text("Selected Source: $source", color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = streamTitle, onValueChange = { streamTitle = it }, label = { Text("Stream Title") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("JSTV Main", "JSTV Sports", "JSTV Music", "JSTV News", "JSTV Church").forEach { c -> Button(onClick = { toast("Channel: $c") }, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) { Text(c, fontSize = 9.sp) } }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Studio", "Outdoor", "Church").forEach { s -> Button(onClick = { toast("Scene: $s") }) { Text(s, fontSize = 10.sp) } }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("STREAM KEYS:", color = Color.Yellow, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = ytKey, onValueChange = { ytKey = it }, label = { Text("YouTube Key") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = fbKey, onValueChange = { fbKey = it }, label = { Text("Facebook Key") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tkKey, onValueChange = { tkKey = it }, label = { Text("TikTok Key") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { ytOn = !ytOn }, colors = ButtonDefaults.buttonColors(containerColor = if (ytOn) Color.Red else Color.DarkGray)) { Text("YT ${if (ytOn) "ON" else "OFF"}") }
                    Button(onClick = { fbOn = !fbOn }, colors = ButtonDefaults.buttonColors(containerColor = if (fbOn) Color.Blue else Color.DarkGray)) { Text("FB ${if (fbOn) "ON" else "OFF"}") }
                    Button(onClick = { tkOn = !tkOn }) { Text("TK ${if (tkOn) "ON" else "OFF"}") }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("QUALITY + CAMERA:", color = Color.Yellow, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("SD 480p", "HD 720p", "FHD 1080p").forEach { q -> Button(onClick = { quality = q }, colors = ButtonDefaults.buttonColors(containerColor = if (quality == q) Color.Yellow else Color.DarkGray)) { Text(q, fontSize = 10.sp, color = if (quality == q) Color.Black else Color.White) } }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { flashOn = !flashOn }, modifier = Modifier.weight(1f)) { Text("FLASH ${if (flashOn) "ON" else "OFF"}") }
                    Button(onClick = { beautyOn = !beautyOn }, modifier = Modifier.weight(1f)) { Text("BEAUTY ${if (beautyOn) "ON" else "OFF"}") }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("TICKER / LOGO / VERSE:", color = Color.Yellow, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = ticker, onValueChange = { ticker = it }, label = { Text("Ticker") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { tickerOn = !tickerOn }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("TICKER ${if (tickerOn) "SHOW" else "HIDE"}") }
                OutlinedTextField(value = verse, onValueChange = { verse = it }, label = { Text("Verse") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { verse = "Psalm 23 - The Lord is my shepherd" }) { Text("Psalm 23", fontSize = 10.sp) }
                    Button(onClick = { verse = "John 3:16 - For God so loved the world..." }) { Text("John 3:16", fontSize = 10.sp) }
                    Button(onClick = { verse = "Phil 4:13 - I can do all things" }) { Text("Phil 4:13", fontSize = 10.sp) }
                }
                Button(onClick = { verseOn = !verseOn }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("VERSE ${if (verseOn) "SHOW" else "HIDE"}", color = Color.Black) }
                OutlinedTextField(value = logoText, onValueChange = { logoText = it }, label = { Text("Logo Text") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { logoOn = !logoOn }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("LOGO ${if (logoOn) "SHOW" else "HIDE"}") }
                OutlinedTextField(value = displayText, onValueChange = { displayText = it }, label = { Text("Display Text") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { displayOn = !displayOn }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("DISPLAY ${if (displayOn) "SHOW" else "HIDE"}") }
                Spacer(modifier = Modifier.height(10.dp))
                Text("EMERGENCY BANNER:", color = Color.Yellow, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = emergency, onValueChange = { emergency = it }, label = { Text("Emergency") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { bannerOn = !bannerOn }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("BANNER ${if (bannerOn) "ON" else "OFF"}") }
            }

            if (tab == "CODES") {
                Text("GENERATE CODES", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Rates: Daily 200 | Monthly 1000 | 3 Months 2500 | Year 8000\nPaybill 516600 Acc 5600935001", color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = genName, onValueChange = { genName = it }, label = { Text("Subscriber Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = genWhats, onValueChange = { genWhats = it }, label = { Text("WhatsApp Number") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Daily", "Monthly", "3 Months", "Year").forEach { p -> Button(onClick = { genPlan = p }, colors = ButtonDefaults.buttonColors(containerColor = if (genPlan == p) Color.Yellow else Color.DarkGray)) { Text(p, fontSize = 10.sp, color = if (genPlan == p) Color.Black else Color.White) } }
                }
                Text("Plan: $genPlan - Price: ${priceFor(genPlan)} KES - Expiry: ${expiryFor(genPlan)}", color = Color.Yellow, fontSize = 12.sp)
                Button(onClick = {
                    if (genName.isBlank() || genWhats.isBlank()) { toast("Enter Name + WhatsApp") } else {
                        genCode = makeCode(genPlan); codes.add(genCode)
                        subscribers.add(Subscriber(genName, genWhats, "123456", genPlan, today(), expiryFor(genPlan), priceFor(genPlan), false, genCode))
                        payments.add(Payment(genName, priceFor(genPlan), "M-PESA", today()))
                        toast("Code Generated: $genCode")
                    }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("GENERATE CODE") }
                if (genCode.isNotEmpty()) {
                    Text("New Code: $genCode", color = Color.Green, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Button(onClick = { openWhatsApp(genWhats, "Hi $genName, Your JSTV Code is: $genCode\nPlan: $genPlan - ${priceFor(genPlan)} KES\nPaid via Paybill 516600 Acc 5600935001\nExpiry: ${expiryFor(genPlan)}") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("SEND CODE VIA WHATSAPP") }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("All Codes:", color = Color.White, fontWeight = FontWeight.Bold)
                codes.forEach { c -> Text("• $c", color = Color.White, fontSize = 13.sp) }
            }

            if (tab == "SUBS") {
                Text("SUBSCRIBERS - Who is LIVE?", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("LIVE Now: ${subscribers.count { it.isLive }} | Total: ${subscribers.size}", color = Color.Green, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("ADD SUBSCRIBER LOGIN:", color = Color.Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = newUser, onValueChange = { newUser = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = newWhats, onValueChange = { newWhats = it }, label = { Text("WhatsApp Number") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = newPass, onValueChange = { if (it.length <= 6) newPass = it }, label = { Text("Password - 6 chars") }, modifier = Modifier.fillMaxWidth())
                Text("Password must be 6 chars: e.g. Ab1234", color = Color.Gray, fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Daily", "Monthly", "3 Months", "Year").forEach { p -> Button(onClick = { newPlan = p }, colors = ButtonDefaults.buttonColors(containerColor = if (newPlan == p) Color.Yellow else Color.DarkGray)) { Text(p, fontSize = 9.sp, color = if (newPlan == p) Color.Black else Color.White) } }
                }
                Button(onClick = {
                    if (newUser.isBlank() || newWhats.isBlank() || newPass.length != 6) { toast("Username + WhatsApp + 6-char Password required") }
                    else { val c = makeCode(newPlan); subscribers.add(Subscriber(newUser, newWhats, newPass, newPlan, today(), expiryFor(newPlan), priceFor(newPlan), false, c)); codes.add(c); toast("Subscriber Saved: $newUser"); newUser = ""; newWhats = ""; newPass = "" }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("SAVE SUBSCRIBER LOGIN") }
                Spacer(modifier = Modifier.height(10.dp))
                Text("LIST OF ALL SUBSCRIBERS:", color = Color.White, fontWeight = FontWeight.Bold)
                subscribers.forEach { s ->
                    Column(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)) {
                        Text("${s.username} - ${if (s.isLive) "LIVE NOW" else "OFFLINE"}", color = if (s.isLive) Color.Green else Color.White, fontWeight = FontWeight.Bold)
                        Text("WhatsApp: ${s.whatsapp} | Pass: ${s.password}", color = Color.White, fontSize = 12.sp)
                        Text("Plan: ${s.plan} | ${s.amount} KES | Code: ${s.code}", color = Color.Yellow, fontSize = 12.sp)
                        Text("Start: ${s.start} | Expiry: ${s.expiry}", color = Color.Gray, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(onClick = { s.isLive = !s.isLive }) { Text(if (s.isLive) "STOP LIVE" else "SET LIVE", fontSize = 10.sp) }
                            Button(onClick = { openWhatsApp(s.whatsapp, "Hi ${s.username}, JSTV Admin. Plan ${s.plan} expires ${s.expiry}. Code: ${s.code} - Paybill 516600 Acc 5600935001") }) { Text("WHATSAPP", fontSize = 10.sp) }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            if (tab == "MONEY") {
                Text("ACCOUNTING - Flow of Money", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFF1B5E20)).padding(12.dp)) {
                    Text("PAYBILL: 516600", color = Color.Yellow, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("ACCOUNT: 5600935001", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("WhatsApp: 0763052911", color = Color.White, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                val total = payments.sumOf { it.amount.toIntOrNull() ?: 0 }
                Text("TOTAL COLLECTED: $total KES | Payments: ${payments.size}", color = Color.Green, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("PASTE M-PESA SMS TO DETECT:", color = Color.Yellow, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = smsText, onValueChange = { smsText = it }, label = { Text("Paste M-PESA SMS here...") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Button(onClick = {
                    val hasPaybill = smsText.contains("516600")
                    val hasAccount = smsText.contains("5600935001")
                    val amountRegex = Regex("""(?:KSH|KES)\s*([\d,]+\.?\d*)""")
                    val foundAmount = amountRegex.find(smsText.uppercase())?.groupValues?.get(1)?.replace(",", "")?.split(".")?.get(0) ?: ""
                    if (hasPaybill && hasAccount) {
                        detected = "DETECTED - Paybill 516600 - Account 5600935001 - Amount: $foundAmount KES"
                        if (foundAmount.isNotEmpty()) accAmount = foundAmount
                        toast("Paybill + Account MATCHED")
                    } else if (!hasPaybill) { detected = "WRONG PAYBILL - No 516600"; toast("Wrong Paybill!") }
                    else if (!hasAccount) { detected = "WRONG ACCOUNT - No 5600935001"; toast("Wrong Account!") }
                    else { detected = "Could not detect" }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)) { Text("DETECT PAYBILL + ACCOUNT", color = Color.Black, fontWeight = FontWeight.Bold) }
                if (detected.isNotEmpty()) Text(detected, color = if (detected.startsWith("DETECTED")) Color.Green else Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("ADD PAYMENT:", color = Color.Yellow, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = accName, onValueChange = { accName = it }, label = { Text("Name from SMS") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = accAmount, onValueChange = { accAmount = it }, label = { Text("Amount - e.g. 1000") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("M-PESA", "Airtel", "Cash").forEach { m -> Button(onClick = { accMethod = m }, colors = ButtonDefaults.buttonColors(containerColor = if (accMethod == m) Color.Yellow else Color.DarkGray)) { Text(m, fontSize = 10.sp, color = if (accMethod == m) Color.Black else Color.White) } }
                }
                Button(onClick = { if (accName.isBlank() || accAmount.isBlank()) toast("Enter Name + Amount") else { payments.add(Payment(accName, accAmount, accMethod, today())); toast("Payment Saved"); accName = ""; accAmount = ""; smsText = ""; detected = "" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("SAVE PAYMENT") }
                Spacer(modifier = Modifier.height(10.dp))
                payments.forEach { p -> Text("• ${p.name} - ${p.amount} KES - ${p.method} - ${p.date}", color = Color.White, fontSize = 13.sp) }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { openWhatsApp("254763052911", "JSTV Payment - Paybill 516600 Acc 5600935001 - Total: $total KES") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("SEND REPORT TO WHATSAPP 0763052911") }
            }

            if (tab == "OFFERS") {
                Text("OFFERS / PROMOTION / DISCOUNT", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = offerTitle, onValueChange = { offerTitle = it }, label = { Text("Offer Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = offerDetail, onValueChange = { offerDetail = it }, label = { Text("Detail") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { if (offerTitle.isBlank()) toast("Enter Title") else { offers.add(Offer(offerTitle, offerDetail, true)); toast("Offer Added"); offerTitle = ""; offerDetail = "" } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("ADD OFFER") }
                Spacer(modifier = Modifier.height(10.dp))
                offers.forEach { o ->
                    Column(modifier = Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)) {
                        Text(o.title, color = Color.Yellow, fontWeight = FontWeight.Bold)
                        Text(o.detail, color = Color.White, fontSize = 13.sp)
                        Button(onClick = { o.active = !o.active }) { Text(if (o.active) "ACTIVE - TAP TO OFF" else "OFF - TAP TO ON", fontSize = 10.sp) }
                        Button(onClick = { openWhatsApp("254763052911", "JSTV OFFER: ${o.title} - ${o.detail} - Paybill 516600 Acc 5600935001") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("SEND OFFER VIA WHATSAPP") }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            if (tab == "NOTIFY") {
                Text("SEND NOTIFICATION TO BROADCASTER", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("To: $notifTo", color = Color.White, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { notifTo = "ALL Subscribers" }) { Text("ALL", fontSize = 10.sp) }
                    Button(onClick = { notifTo = "LIVE Only" }) { Text("LIVE", fontSize = 10.sp) }
                }
                OutlinedTextField(value = notifText, onValueChange = { notifText = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Button(onClick = {
                    if (notifText.isBlank()) toast("Write message") else {
                        subscribers.forEach { s -> openWhatsApp(s.whatsapp, "JSTV ADMIN: $notifText - Paybill 516600 Acc 5600935001") }
                        toast("Notification sent via WhatsApp")
                    }
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) { Text("SEND NOTIFICATION VIA WHATSAPP") }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Admin WhatsApp Linked: 0763052911", color = Color.Gray, fontSize = 12.sp)
                Button(onClick = { openWhatsApp("254763052911", "Hi, this is JSTV Admin test - Paybill 516600 Acc 5600935001") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))) { Text("TEST WHATSAPP LINK - 0763052911") }
            }
        }
    }
}
