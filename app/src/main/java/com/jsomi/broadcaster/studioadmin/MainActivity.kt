package com.jsomi.broadcaster.studioadmin

import android.os.Bundle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { JSTVAdminFinalApp() }
    }
}

@Composable
fun JSTVAdminFinalApp() {
    var screen by remember { mutableStateOf("TOP") }
    var channelName by remember { mutableStateOf("JSTV") }
    var ticker by remember { mutableStateOf("Welcome to JSTV") }
    var verse by remember { mutableStateOf("Psalm 23:1 - The LORD is my shepherd, I lack nothing.") }
    var showLogo by remember { mutableStateOf(true) }
    var showVerse by remember { mutableStateOf(true) }
    var showTicker by remember { mutableStateOf(true) }
    var showBible by remember { mutableStateOf(true) }
    var frontCam by remember { mutableStateOf(true) }
    var micOn by remember { mutableStateOf(true) }
    var cameraAllowed by remember { mutableStateOf(false) }
    var youtubeOn by remember { mutableStateOf(true) }
    var facebookOn by remember { mutableStateOf(true) }
    var tiktokOn by remember { mutableStateOf(true) }
    var youtubeKey by remember { mutableStateOf("") }
    var facebookKey by remember { mutableStateOf("") }
    var tiktokKey by remember { mutableStateOf("") }
    var remoteVerse by remember { mutableStateOf("") }
    var remoteTicker by remember { mutableStateOf("") }
    var remoteLogoOn by remember { mutableStateOf(true) }
    var bibleSearch by remember { mutableStateOf("") }
    var selectedBible by remember { mutableStateOf("John 3:16 - For God so loved the world...") }
    val bibleList = listOf(
        "John 3:16 - For God so loved the world that he gave his one and only Son",
        "Psalm 23:1 - The LORD is my shepherd, I lack nothing",
        "Jeremiah 29:11 - For I know the plans I have for you",
        "Philippians 4:13 - I can do all this through him who gives me strength",
        "Romans 10:9 - If you declare with your mouth, Jesus is Lord",
        "Isaiah 41:10 - Do not fear, for I am with you"
    )
    var chatList by remember { mutableStateOf(listOf("Welcome to JSTV!", "God bless you")) }
    var chatInput by remember { mutableStateOf("") }
    var typingText by remember { mutableStateOf("") }
    var subscribed by remember { mutableStateOf(false) }
    var mpesaNumber by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var wirelessConnected by remember { mutableStateOf(false) }
    var wirelessCode by remember { mutableStateOf("JSTV-1234") }
    // ADMIN ONLY - LINKED TO ADMIN DASH
    var adminMsg by remember { mutableStateOf("") }
    var monthlyPrice by remember { mutableStateOf("500") }
    var yearlyPrice by remember { mutableStateOf("5000") }
    var churchPrice by remember { mutableStateOf("10000") }
    var subscribers by remember { mutableStateOf(listOf("John - Monthly - Paid", "Mary - Yearly - Paid", "Peter - Monthly - Pending")) }
    var onlineUsers by remember { mutableStateOf("1 Broadcaster, 1 Remote, 12 Viewers") }

    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            when(screen) {
                "TOP" -> Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(Modifier.height(30.dp))
                    Text("JSTV", color=Color.Yellow, fontSize=48.sp, fontWeight=FontWeight.Bold)
                    Text("JSOMI STUDIO - ADMIN", color=Color.White, fontSize=18.sp)
                    Spacer(Modifier.height(30.dp))
                    Button(onClick={screen="MAIN_STUDIO_SETUP"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth().height(65.dp)) { Text("MAIN STUDIO", fontSize=20.sp, fontWeight=FontWeight.Bold) }
                    Spacer(Modifier.height(14.dp))
                    Button(onClick={screen="REMOTE"}, colors=ButtonDefaults.buttonColors(Color(0xFF4CAF50)), modifier=Modifier.fillMaxWidth().height(65.dp)) { Text("REMOTE CONTROL", fontSize=20.sp, fontWeight=FontWeight.Bold) }
                    Spacer(Modifier.height(14.dp))
                    Button(onClick={screen="WIRELESS"}, colors=ButtonDefaults.buttonColors(Color(0xFF00BCD4)), modifier=Modifier.fillMaxWidth().height(65.dp)) { Text("WIRELESS CAMERA", fontSize=20.sp, fontWeight=FontWeight.Bold) }
                    Spacer(Modifier.height(14.dp))
                    Button(onClick={screen="DASH"}, colors=ButtonDefaults.buttonColors(Color.Red), modifier=Modifier.fillMaxWidth().height(65.dp)) { Text("ADMIN - CONTROL ROOM", fontSize=20.sp, fontWeight=FontWeight.Bold) }
                    Spacer(Modifier.height(14.dp))
                    Button(onClick={screen="TV_BIG"}, colors=ButtonDefaults.buttonColors(Color(0xFFFFC107)), modifier=Modifier.fillMaxWidth().height(60.dp)) { Text("TV / CHURCH BIG SCREEN", color=Color.Black, fontSize=18.sp, fontWeight=FontWeight.Bold) }
                }
                "DASH" -> Column(Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState())) {
                    Text("ADMIN - CONTROL ROOM", color=Color.Yellow, fontSize=26.sp, fontWeight=FontWeight.Bold)
                    Text("Online Now: $onlineUsers", color=Color.White)
                    Text("STREAM DESTINATIONS - ADMIN CONTROLS", color=Color.Yellow, fontSize=20.sp, fontWeight=FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("YouTube Live", color=Color.White); Switch(checked=youtubeOn, onCheckedChange={youtubeOn=it}) }
                    OutlinedTextField(value=youtubeKey, onValueChange={youtubeKey=it}, label={Text("YouTube Stream Key")}, modifier=Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("Facebook Live", color=Color.White); Switch(checked=facebookOn, onCheckedChange={facebookOn=it}) }
                    OutlinedTextField(value=facebookKey, onValueChange={facebookKey=it}, label={Text("Facebook Stream Key")}, modifier=Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("TikTok Live", color=Color.White); Switch(checked=tiktokOn, onCheckedChange={tiktokOn=it}) }
                    OutlinedTextField(value=tiktokKey, onValueChange={tiktokKey=it}, label={Text("TikTok Stream Key")}, modifier=Modifier.fillMaxWidth())
                    Button(onClick={screen="SUBS"}, colors=ButtonDefaults.buttonColors(Color.Yellow), modifier=Modifier.fillMaxWidth()){ Text("SUBSCRIBERS", color=Color.Black) }
                    Button(onClick={screen="PRICES"}, colors=ButtonDefaults.buttonColors(Color.Yellow), modifier=Modifier.fillMaxWidth()){ Text("PRICES", color=Color.Black) }
                    OutlinedTextField(value=adminMsg, onValueChange={adminMsg=it}, label={Text("Message to All Viewers")}, modifier=Modifier.fillMaxWidth())
                    Button(onClick={}, colors=ButtonDefaults.buttonColors(Color.Green), modifier=Modifier.fillMaxWidth()){ Text("SEND MESSAGE") }
                    Button(onClick={}, colors=ButtonDefaults.buttonColors(Color.Green), modifier=Modifier.fillMaxWidth()){ Text("ALLOW USER") }
                    Button(onClick={}, colors=ButtonDefaults.buttonColors(Color.Red), modifier=Modifier.fillMaxWidth()){ Text("BLOCK USER") }
                    Button(onClick={screen="TOP"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK TO TOP") }
                }
                "SUBS" -> Column(Modifier.fillMaxSize().padding(16.dp)){
                    Text("SUBSCRIBERS", color=Color.Yellow, fontSize=26.sp, fontWeight=FontWeight.Bold)
                    subscribers.forEach{ Text(it, color=Color.White, modifier=Modifier.fillMaxWidth().background(Color(0xFF333333)).padding(8.dp)) }
                    Button(onClick={screen="DASH"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK TO DASH") }
                }
                "PRICES" -> Column(Modifier.fillMaxSize().padding(16.dp)){
                    Text("PRICES", color=Color.Yellow, fontSize=26.sp, fontWeight=FontWeight.Bold)
                    OutlinedTextField(value=monthlyPrice, onValueChange={monthlyPrice=it}, label={Text("Monthly Price KSh")}, modifier=Modifier.fillMaxWidth())
                    OutlinedTextField(value=yearlyPrice, onValueChange={yearlyPrice=it}, label={Text("Yearly Price KSh")}, modifier=Modifier.fillMaxWidth())
                    OutlinedTextField(value=churchPrice, onValueChange={churchPrice=it}, label={Text("Church Price KSh")}, modifier=Modifier.fillMaxWidth())
                    Button(onClick={screen="DASH"}, colors=ButtonDefaults.buttonColors(Color.Green), modifier=Modifier.fillMaxWidth()){ Text("SAVE PRICES") }
                    Button(onClick={screen="DASH"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK") }
                }
                "MAIN_STUDIO_SETUP" -> Column(Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState())) {
                    Text("MAIN STUDIO - LIVE SETUP", color=Color.Yellow, fontSize=26.sp, fontWeight=FontWeight.Bold)
                    OutlinedTextField(value=channelName, onValueChange={channelName=it}, label={Text("Channel Name")}, modifier=Modifier.fillMaxWidth())
                    OutlinedTextField(value=ticker, onValueChange={ticker=it}, label={Text("Ticker")}, modifier=Modifier.fillMaxWidth())
                    OutlinedTextField(value=verse, onValueChange={verse=it}, label={Text("Verse")}, modifier=Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("Show Logo", color=Color.White); Switch(checked=showLogo, onCheckedChange={showLogo=it}) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("Show Verse", color=Color.White); Switch(checked=showVerse, onCheckedChange={showVerse=it}) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("Show Ticker", color=Color.White); Switch(checked=showTicker, onCheckedChange={showTicker=it}) }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("Show Bible (from Remote)", color=Color.White); Switch(checked=showBible, onCheckedChange={showBible=it}) }
                    Text("STREAM DESTINATIONS", color=Color.Yellow, fontSize=20.sp, fontWeight=FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("YouTube Live", color=Color.White); Switch(checked=youtubeOn, onCheckedChange={youtubeOn=it}) }
                    OutlinedTextField(value=youtubeKey, onValueChange={youtubeKey=it}, label={Text("YouTube Stream Key")}, modifier=Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("Facebook Live", color=Color.White); Switch(checked=facebookOn, onCheckedChange={facebookOn=it}) }
                    OutlinedTextField(value=facebookKey, onValueChange={facebookKey=it}, label={Text("Facebook Stream Key")}, modifier=Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("TikTok Live", color=Color.White); Switch(checked=tiktokOn, onCheckedChange={tiktokOn=it}) }
                    OutlinedTextField(value=tiktokKey, onValueChange={tiktokKey=it}, label={Text("TikTok Stream Key")}, modifier=Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth()){ Button(onClick={frontCam=true}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.weight(1f).padding(4.dp)){Text("FRONT CAM")} ; Button(onClick={frontCam=false}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.weight(1f).padding(4.dp)){Text("BACK CAM")} }
                    Button(onClick={micOn=!micOn}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text(if(micOn) "MIC ON" else "MIC OFF") }
                    Button(onClick={cameraAllowed=true}, colors=ButtonDefaults.buttonColors(Color.Red), modifier=Modifier.fillMaxWidth()){ Text("ALLOW CAMERA + MIC") }
                    if(cameraAllowed) Text("Camera + Mic Allowed ✓", color=Color.Green)
                    Button(onClick={screen="MAIN_STUDIO_LIVE"}, colors=ButtonDefaults.buttonColors(Color.Green), modifier=Modifier.fillMaxWidth()){ Text("OK - APPLY TO MAIN SCREEN") }
                    Button(onClick={screen="TOP"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK TO TOP") }
                }
                "MAIN_STUDIO_LIVE" -> Column(Modifier.fillMaxSize().padding(12.dp).verticalScroll(rememberScrollState())) {
                    Text(channelName + " - MAIN STUDIO", color=Color.Yellow, fontSize=26.sp, fontWeight=FontWeight.Bold)
                    if(showLogo && remoteLogoOn) Text("JSTV LOGO", color=Color.Red, fontSize=24.sp, fontWeight=FontWeight.Bold)
                    Box(Modifier.fillMaxWidth().height(220.dp).background(Color.DarkGray), contentAlignment=Alignment.Center){ Text(if(wirelessConnected) "WIRELESS CAMERA - LIVE" else if(frontCam) "FRONT CAM - LIVE" else "BACK CAM - LIVE", color=Color.White) }
                    if(showTicker) Text("TICKER: $ticker ${if(remoteTicker.isNotEmpty()) " | $remoteTicker" else ""}", color=Color.Yellow)
                    if(showVerse) Text(verse, color=Color.White, fontSize=18.sp)
                    if(showBible && remoteVerse.isNotEmpty()) Text("BIBLE FROM REMOTE:\n$remoteVerse", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
                    if(showBible && selectedBible.isNotEmpty()) Text("BIBLE: $selectedBible", color=Color.White, fontSize=18.sp)
                    Text("LIVE TO: " + (if(youtubeOn) "YouTube " else "") + (if(facebookOn) "Facebook " else "") + (if(tiktokOn) "TikTok" else ""), color=Color.Green)
                    Text("CHAT", color=Color.Yellow, fontWeight=FontWeight.Bold)
                    chatList.forEach { Text(it, color=Color.White, modifier=Modifier.fillMaxWidth().background(Color(0xFF333333)).padding(6.dp)) }
                    OutlinedTextField(value=chatInput, onValueChange={chatInput=it; typingText=it}, label={Text("Type message - You see as you type")}, modifier=Modifier.fillMaxWidth())
                    Text("Typing: $typingText", color=Color.White)
                    Button(onClick={ if(chatInput.isNotEmpty()){ chatList=chatList+chatInput; chatInput=""; typingText="" } }, colors=ButtonDefaults.buttonColors(Color.Yellow), modifier=Modifier.fillMaxWidth()){ Text("SEND CHAT", color=Color.Black) }
                    Button(onClick={screen="PAYMENT"}, colors=ButtonDefaults.buttonColors(Color.Red), modifier=Modifier.fillMaxWidth()){ Text("🔴 GO LIVE") }
                    Button(onClick={screen="TV_BIG"}, colors=ButtonDefaults.buttonColors(Color(0xFFFFC107)), modifier=Modifier.fillMaxWidth()){ Text("SHOW ON TV / CHURCH BIG SCREEN", color=Color.Black) }
                    Button(onClick={screen="REMOTE"}, colors=ButtonDefaults.buttonColors(Color(0xFF4CAF50)), modifier=Modifier.fillMaxWidth()){ Text("OPEN REMOTE") }
                    Button(onClick={screen="DASH"}, colors=ButtonDefaults.buttonColors(Color.Red), modifier=Modifier.fillMaxWidth()){ Text("ADMIN CONTROL") }
                    Button(onClick={screen="TOP"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK TO TOP") }
                }
                "REMOTE" -> Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text("REMOTE CONTROL", color=Color.Green, fontSize=28.sp, fontWeight=FontWeight.Bold)
                    Text("OPEN BIBLE - SEND TO MAIN STUDIO", color=Color.Yellow, fontSize=20.sp, fontWeight=FontWeight.Bold)
                    OutlinedTextField(value=bibleSearch, onValueChange={bibleSearch=it}, label={Text("Search Bible - e.g. John 3:16")}, modifier=Modifier.fillMaxWidth())
                    bibleList.filter{ it.contains(bibleSearch, ignoreCase=true) }.forEach { v ->
                        Button(onClick={selectedBible=v; remoteVerse=v}, colors=ButtonDefaults.buttonColors(if(selectedBible==v) Color.Yellow else Color(0xFF333333)), modifier=Modifier.fillMaxWidth().padding(2.dp)) { Text(v, color=if(selectedBible==v) Color.Black else Color.White) }
                    }
                    OutlinedTextField(value=remoteVerse, onValueChange={remoteVerse=it}, label={Text("Or Type Verse Manually")}, modifier=Modifier.fillMaxWidth())
                    OutlinedTextField(value=remoteTicker, onValueChange={remoteTicker=it}, label={Text("Send Ticker to Main Studio")}, modifier=Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("Logo ON/OFF", color=Color.White); Switch(checked=remoteLogoOn, onCheckedChange={remoteLogoOn=it}) }
                    Button(onClick={remoteVerse=selectedBible; screen="MAIN_STUDIO_LIVE"}, colors=ButtonDefaults.buttonColors(Color.Green), modifier=Modifier.fillMaxWidth()){ Text("SHOW BIBLE ON MAIN STUDIO") }
                    Button(onClick={screen="TV_BIG"}, colors=ButtonDefaults.buttonColors(Color(0xFFFFC107)), modifier=Modifier.fillMaxWidth()){ Text("SHOW ON TV BIG SCREEN", color=Color.Black) }
                    Button(onClick={screen="TOP"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK TO TOP") }
                }
                "WIRELESS" -> Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment=Alignment.CenterHorizontally) {
                    Text("WIRELESS CAMERA", color=Color(0xFF00BCD4), fontSize=28.sp, fontWeight=FontWeight.Bold)
                    OutlinedTextField(value=wirelessCode, onValueChange={wirelessCode=it}, label={Text("Camera Code")}, modifier=Modifier.fillMaxWidth())
                    Box(Modifier.fillMaxWidth().height(250.dp).background(Color.DarkGray), contentAlignment=Alignment.Center){ Text(if(wirelessConnected) "WIRELESS CAM - CONNECTED" else "WIRELESS CAM - PREVIEW", color=Color.White) }
                    Button(onClick={wirelessConnected=!wirelessConnected}, colors=ButtonDefaults.buttonColors(if(wirelessConnected) Color.Red else Color.Green), modifier=Modifier.fillMaxWidth()){ Text(if(wirelessConnected) "DISCONNECT" else "CONNECT TO MAIN STUDIO") }
                    Button(onClick={screen="MAIN_STUDIO_LIVE"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("GO TO MAIN STUDIO") }
                    Button(onClick={screen="TOP"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK TO TOP") }
                }
                "PAYMENT" -> Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text("PAY TO WATCH LIVE", color=Color.Yellow, fontSize=28.sp, fontWeight=FontWeight.Bold)
                    Text("SYSTEM 1 - M-PESA", color=Color.Green, fontWeight=FontWeight.Bold, fontSize=20.sp)
                    OutlinedTextField(value=mpesaNumber, onValueChange={mpesaNumber=it}, label={Text("M-Pesa Number")}, modifier=Modifier.fillMaxWidth())
                    Button(onClick={subscribed=true; screen="TV_BIG"}, colors=ButtonDefaults.buttonColors(Color.Green), modifier=Modifier.fillMaxWidth()){ Text("PAY WITH M-PESA - KSh 500") }
                    Spacer(Modifier.height(16.dp))
                    Text("SYSTEM 2 - CARD", color=Color.Yellow, fontWeight=FontWeight.Bold, fontSize=20.sp)
                    OutlinedTextField(value=cardNumber, onValueChange={cardNumber=it}, label={Text("Card - Visa / Mastercard")}, modifier=Modifier.fillMaxWidth())
                    Button(onClick={subscribed=true; screen="TV_BIG"}, colors=ButtonDefaults.buttonColors(Color.Yellow), modifier=Modifier.fillMaxWidth()){ Text("PAY WITH CARD - Visa / Mastercard", color=Color.Black) }
                    Button(onClick={screen="MAIN_STUDIO_LIVE"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK") }
                }
                "TV_BIG" -> Column(Modifier.fillMaxSize().background(Color.Black).padding(8.dp).verticalScroll(rememberScrollState()), horizontalAlignment=Alignment.CenterHorizontally) {
                    Text("JSTV - CHURCH BIG SCREEN", color=Color.Yellow, fontSize=26.sp, fontWeight=FontWeight.Bold)
                    Text("For Monitor / TV - For those far to view", color=Color.White, fontSize=14.sp)
                    Box(Modifier.fillMaxWidth().height(320.dp).background(Color.DarkGray), contentAlignment=Alignment.Center){ Text("LIVESTREAM - BIG SCREEN", color=Color.White, fontSize=20.sp) }
                    if(remoteVerse.isNotEmpty()) Text(remoteVerse, color=Color.Yellow, fontSize=30.sp, fontWeight=FontWeight.Bold)
                    else Text(selectedBible, color=Color.Yellow, fontSize=30.sp, fontWeight=FontWeight.Bold)
                    Text(ticker, color=Color.White, fontSize=22.sp)
                    Text(verse, color=Color.White, fontSize=20.sp)
                    if(subscribed) Text("Paid ✓ - Watching Live", color=Color.Green) else Text("Tap PAY to Watch", color=Color.Red)
                    Button(onClick={screen="PAYMENT"}, colors=ButtonDefaults.buttonColors(Color.Yellow), modifier=Modifier.fillMaxWidth()){ Text("PAY TO WATCH - M-PESA / CARD", color=Color.Black) }
                    Button(onClick={screen="TOP"}, colors=ButtonDefaults.buttonColors(Color(0xFF7B61FF)), modifier=Modifier.fillMaxWidth()){ Text("BACK TO TOP") }
                    Text("HOW TO SHOW ON TV: 1. Connect phone to TV with HDMI / Cast / Screen Mirror 2. Open this TV BIG SCREEN 3. Everyone in church sees it", color=Color.White, fontSize=13.sp)
                }
            }
        }
    }
}
