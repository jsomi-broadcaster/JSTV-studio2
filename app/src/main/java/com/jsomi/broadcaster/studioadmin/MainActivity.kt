package com.jsomi.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

data class Sub(var username:String, var whatsapp:String, var pin:String, var plan:String, var amount:Int, var expiry:String, var isUnlocked:Boolean, var isBanned:Boolean, var premium:String, var receipt:String)
data class PremiumSet(var name:String, var price:Int, var days:Int)
data class Report(var from:String, var reason:String, var keyword:String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AdminApp() } }
    }
}

@Composable
fun RealCamera(modifier: Modifier = Modifier, isFront:Boolean) {
    val ctx = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    AndroidView(factory = { c ->
        val pv = PreviewView(c)
        val fut = ProcessCameraProvider.getInstance(c)
        fut.addListener({
            val provider = fut.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(pv.surfaceProvider) }
            try { provider.unbindAll(); provider.bindToLifecycle(lifecycle, if(isFront) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA, preview) } catch(e:Exception){}
        }, ContextCompat.getMainExecutor(c))
        pv
    }, modifier = modifier)
}

@Composable
fun AdminApp() {
    val ctx = LocalContext.current
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
    val subs = remember { mutableStateListOf(
        Sub("KevinDemo","0763052911","123456","Monthly",1000,"12 Dec 2026",true,false,"Premium", "QK123ABC"),
        Sub("MaryChurch","0722000000","654321","3 Months",2500,"10 Feb 2027",true,false,"Premium","QK456DEF")
    )}
    val premiums = remember { mutableStateListOf(PremiumSet("Daily",200,1), PremiumSet("Monthly",1000,30), PremiumSet("3 Months",2500,90), PremiumSet("Year",8000,365)) }
    val reports = remember { mutableStateListOf(Report("Viewer1","Hate speech","hate"), Report("Viewer2","Illegal stream","illegal")) }
    var newPremiumName by remember { mutableStateOf("") }; var newPremiumPrice by remember { mutableStateOf("") }; var newPremiumDays by remember { mutableStateOf("") }
    var msgToSub by remember { mutableStateOf("") }
    var deleteCode by remember { mutableStateOf("") }; var deleteTarget by remember { mutableStateOf("") }
    var keywordAlert by remember { mutableStateOf("No keyword alert") }
    var monitorMsg by remember { mutableStateOf("") }
    var meetingOn by remember { mutableStateOf(false) }
    var meetingTitle by remember { mutableStateOf("JSTV Studio Meeting") }
    fun toast(m:String){ Toast.makeText(ctx,m,Toast.LENGTH_SHORT).show() }
    fun openWhatsApp(num:String,msg:String){ val n = if(num.startsWith("0")) "254"+num.substring(1) else num.replace("+",""); try{ ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$n?text="+Uri.encode(msg)))) }catch(e:Exception){ toast("WhatsApp not found") } }
    fun checkExpiry(){ val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()); val today = Date(); subs.forEach{ if(fmt.parse(it.expiry)?.before(today)==true){ it.isUnlocked=false } } }

    if(screen=="DASH"){
        Column(modifier=Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("JSTV ADMIN STUDIO", color=Color.Yellow, fontSize=26.sp, fontWeight=FontWeight.Bold)
            Text("Paybill 516600 Acc 5600935001 | Subs: ${subs.size} | Live: ${if(isLive) "ON" else "OFF"}", color=Color.White, fontSize=12.sp)
            Spacer(modifier=Modifier.height(10.dp))
            Button(onClick={ screen="LIVE" }, modifier=Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("📹 GO TO LIVE STUDIO", color=Color.White, fontWeight=FontWeight.Bold) }
            Spacer(modifier=Modifier.height(6.dp))
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ screen="SUBS" }, modifier=Modifier.weight(1f)){ Text("SUBS ${subs.size}") }; Button(onClick={ screen="RATES" }, modifier=Modifier.weight(1f)){ Text("RATES / PREMIUM") }; Button(onClick={ screen="MONITOR" }, modifier=Modifier.weight(1f)){ Text("MONITOR ⚠️") } }
            Spacer(modifier=Modifier.height(6.dp))
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ screen="MEETING" }, modifier=Modifier.weight(1f)){ Text("MEETING") }; Button(onClick={ checkExpiry(); toast("Expiry checked - Expired locked") }, modifier=Modifier.weight(1f)){ Text("CHECK EXPIRY") } }
            Spacer(modifier=Modifier.height(12.dp))
            Text("LIVE MONITOR - Keyword Alert: $keywordAlert", color=Color.Yellow, fontWeight=FontWeight.Bold)
            reports.forEach{ r -> Column(modifier=Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)){ Text("Report from ${r.from}: ${r.reason} - Keyword: ${r.keyword}", color=Color.White, fontSize=12.sp); Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){ Button(onClick={ toast("MUTED + STOPPED + BANNED ${r.from} in 1 tap") }, colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("MUTE/STOP/BAN", fontSize=9.sp) }; Button(onClick={ toast("Warning sent to ${r.from}") }){ Text("WARN", fontSize=9.sp) } } }; Spacer(modifier=Modifier.height(6.dp)) }
            Spacer(modifier=Modifier.height(10.dp))
            Text("SUBSCRIBERS - Auto-saved from Broadcaster Sign Up:", color=Color.Yellow, fontWeight=FontWeight.Bold)
            subs.forEach{ s -> Column(modifier=Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)){ Text("${s.username} | ${s.whatsapp} | ${s.plan} ${s.amount} KES | ${s.premium}", color=Color.White, fontWeight=FontWeight.Bold, fontSize=13.sp); Text("Expiry: ${s.expiry} | ${if(s.isUnlocked) "🔓 UNLOCKED" else "🔒 LOCKED - Expired"} | Receipt: ${s.receipt}", color=if(s.isUnlocked) Color.Green else Color.Red, fontSize=12.sp); Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){ Button(onClick={ screen="SUBS" }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7C4DFF))){ Text("MESSAGE", fontSize=9.sp) }; Button(onClick={ s.isUnlocked=!s.isUnlocked; toast("${s.username} ${if(s.isUnlocked) "unlocked" else "locked"}") }, colors=ButtonDefaults.buttonColors(containerColor=if(s.isUnlocked) Color.Red else Color.Green)){ Text(if(s.isUnlocked) "LOCK" else "UNLOCK", fontSize=9.sp) } } }; Spacer(modifier=Modifier.height(6.dp)) }
        }
    }
    if(screen=="LIVE"){
        Box(modifier=Modifier.fillMaxSize().background(Color.Black)){
            RealCamera(modifier=Modifier.fillMaxSize(), isFront=isFront)
            if(showLogo) Box(modifier=Modifier.offset{ IntOffset(logoX.roundToInt(), logoY.roundToInt()) }.pointerInput(Unit){ detectDragGestures{ c,d -> logoX+=d.x; logoY+=d.y } }.background(Color.Red).padding(8.dp)){ Text("JSTV $channelName", color=Color.White, fontWeight=FontWeight.Bold) }
            if(showVerse) Box(modifier=Modifier.offset{ IntOffset(verseX.roundToInt(), verseY.roundToInt()) }.pointerInput(Unit){ detectDragGestures{ c,d -> verseX+=d.x; verseY+=d.y } }.background(Color.Black).padding(8.dp)){ Text("Verse: $verse", color=Color.Yellow) }
            if(showTicker) Box(modifier=Modifier.offset{ IntOffset(tickerX.roundToInt(), tickerY.roundToInt()) }.pointerInput(Unit){ detectDragGestures{ c,d -> tickerX+=d.x; tickerY+=d.y } }.fillMaxWidth().background(Color.Yellow).padding(8.dp)){ Text("Ticker: $ticker", color=Color.Black) }
            Column(modifier=Modifier.align(androidx.compose.ui.Alignment.TopEnd).padding(10.dp)){ Text(if(isLive) "● LIVE" else "○ NOT LIVE", color=if(isLive) Color.Red else Color.White, fontWeight=FontWeight.Bold); Text("Mic: ${if(isMicOn) "ON" else "OFF"} | Cam: ${if(isFront) "FRONT" else "BACK"}", color=Color.White, fontSize=11.sp) }
            Column(modifier=Modifier.align(androidx.compose.ui.Alignment.BottomCenter).padding(12.dp)){
                if(isLive) Button(onClick={ isLive=false; toast("Live Ended") }, modifier=Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("END LIVE", color=Color.White, fontWeight=FontWeight.Bold) }
                else Button(onClick={ isLive=true; toast("You are LIVE in Studio!") }, modifier=Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("GO LIVE", color=Color.White, fontWeight=FontWeight.Bold) }
                Spacer(modifier=Modifier.height(6.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ screen="SETUP" }, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("LIVE SETUP", color=Color.Black, fontWeight=FontWeight.Bold) }; Button(onClick={ screen="DASH" }, modifier=Modifier.weight(1f)){ Text("BACK") } }
                Text("Drag Logo / Verse / Ticker to move", color=Color.White, fontSize=10.sp)
            }
        }
    }
    if(screen=="SETUP"){
        Column(modifier=Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("LIVE SETUP - All Settings Here", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            Text("Set here > Press OK > Items appear on Main Screen > Drag them", color=Color.Gray, fontSize=12.sp)
            Spacer(modifier=Modifier.height(10.dp))
            OutlinedTextField(value=channelName, onValueChange={ channelName=it }, label={ Text("Channel Name") }, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=ticker, onValueChange={ ticker=it }, label={ Text("Ticker Text") }, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=verse, onValueChange={ verse=it }, label={ Text("Verse / Bible Verse") }, modifier=Modifier.fillMaxWidth())
            Spacer(modifier=Modifier.height(8.dp))
            Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){ Text("Show Logo JSTV", color=Color.White, modifier=Modifier.weight(1f)); Switch(checked=showLogo, onCheckedChange={ showLogo=it }) }
            Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){ Text("Show Verse", color=Color.White, modifier=Modifier.weight(1f)); Switch(checked=showVerse, onCheckedChange={ showVerse=it }) }
            Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){ Text("Show Ticker", color=Color.White, modifier=Modifier.weight(1f)); Switch(checked=showTicker, onCheckedChange={ showTicker=it }) }
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ isFront=true }, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(isFront) Color.Yellow else Color.DarkGray)){ Text("FRONT CAM", color=if(isFront) Color.Black else Color.White) }; Button(onClick={ isFront=false }, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(!isFront) Color.Yellow else Color.DarkGray)){ Text("BACK CAM", color=if(!isFront) Color.Black else Color.White) } }
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ isMicOn=!isMicOn }, modifier=Modifier.weight(1f)){ Text(if(isMicOn) "MIC ON" else "MIC OFF") } }
            Spacer(modifier=Modifier.height(12.dp))
            Button(onClick={ screen="LIVE"; toast("Setup Saved - Items on Main Screen - Drag to move") }, modifier=Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("OK - APPLY TO MAIN SCREEN", color=Color.White, fontWeight=FontWeight.Bold) }
            Button(onClick={ screen="LIVE" }, modifier=Modifier.fillMaxWidth()){ Text("CANCEL - BACK") }
        }
    }
    if(screen=="SUBS"){
        Column(modifier=Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("SUBSCRIBERS + MESSAGE", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            Text("Send Message to Broadcaster Subscriber:", color=Color.White)
            OutlinedTextField(value=msgToSub, onValueChange={ msgToSub=it }, label={ Text("Message to subscriber...") }, modifier=Modifier.fillMaxWidth(), minLines=2)
            Button(onClick={ if(msgToSub.isNotBlank()){ toast("Message sent to all subscribers: $msgToSub"); msgToSub="" } }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7C4DFF))){ Text("SEND MESSAGE TO ALL SUBSCRIBERS") }
            Spacer(modifier=Modifier.height(10.dp))
            Text("DELETE - Admin Authorization Required:", color=Color.Red, fontWeight=FontWeight.Bold)
            Text("To delete Code / Money / Offer, enter Admin PIN + Target. No delete without Admin.", color=Color.Gray, fontSize=11.sp)
            OutlinedTextField(value=deleteTarget, onValueChange={ deleteTarget=it }, label={ Text("What to delete? e.g. Code QK123 / Offer BlackFriday") }, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=deleteCode, onValueChange={ deleteCode=it }, label={ Text("Admin PIN to authorize delete") }, modifier=Modifier.fillMaxWidth())
            Button(onClick={ if(deleteCode=="123456" && deleteTarget.isNotBlank()){ toast("Admin Authorized - Deleted: $deleteTarget"); deleteTarget=""; deleteCode="" } else toast("Wrong Admin PIN - Delete BLOCKED") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("AUTHORIZE DELETE") }
            Spacer(modifier=Modifier.height(10.dp))
            subs.forEach{ s -> Column(modifier=Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)){ Text("${s.username} | ${s.whatsapp} | ${s.plan}", color=Color.White, fontWeight=FontWeight.Bold); Text("Expiry ${s.expiry} | ${if(s.isUnlocked) "UNLOCKED" else "LOCKED"}", color=if(s.isUnlocked) Color.Green else Color.Red); Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){ Button(onClick={ openWhatsApp(s.whatsapp, "Hi ${s.username}, Message from JSTV Admin: $msgToSub") }){ Text("WHATSAPP", fontSize=9.sp) }; Button(onClick={ s.isBanned=!s.isBanned; toast(if(s.isBanned) "Banned ${s.username}" else "Unbanned ${s.username}") }, colors=ButtonDefaults.buttonColors(containerColor=if(s.isBanned) Color.Green else Color.Red)){ Text(if(s.isBanned) "UNBAN" else "BAN", fontSize=9.sp) } } }; Spacer(modifier=Modifier.height(6.dp)) }
            Button(onClick={ screen="DASH" }, modifier=Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
    if(screen=="RATES"){
        Column(modifier=Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("CHANGE RATES + ADD PREMIUM SET", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            premiums.forEach{ p -> Row(modifier=Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)){ Text("${p.name} - ${p.price} KES - ${p.days} Days", color=Color.White, modifier=Modifier.weight(1f), fontWeight=FontWeight.Bold); Button(onClick={ toast("Edit ${p.name} - Change price in Admin") }){ Text("EDIT", fontSize=9.sp) } } ; Spacer(modifier=Modifier.height(6.dp)) }
            Spacer(modifier=Modifier.height(10.dp))
            Text("ADD NEW PREMIUM SET:", color=Color.Yellow, fontWeight=FontWeight.Bold)
            OutlinedTextField(value=newPremiumName, onValueChange={ newPremiumName=it }, label={ Text("Premium Name - e.g. Church Premium") }, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=newPremiumPrice, onValueChange={ newPremiumPrice=it }, label={ Text("Price KES - e.g. 5000") }, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=newPremiumDays, onValueChange={ newPremiumDays=it }, label={ Text("Days - e.g. 60") }, modifier=Modifier.fillMaxWidth())
            Button(onClick={ if(newPremiumName.isNotBlank() && (newPremiumPrice.toIntOrNull()?:0)>0){ premiums.add(PremiumSet(newPremiumName, newPremiumPrice.toInt(), newPremiumDays.toIntOrNull()?:30)); toast("Premium ${newPremiumName} added"); newPremiumName=""; newPremiumPrice=""; newPremiumDays="" } else toast("Enter Name + Price") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("ADD PREMIUM SET") }
            Button(onClick={ screen="DASH" }, modifier=Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
    if(screen=="MONITOR"){
        Column(modifier=Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("LIVE MONITOR - Protect JSTV", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            Text("Hate Speech / Illegal Stream Protection", color=Color.White, fontSize=12.sp)
            OutlinedTextField(value=monitorMsg, onValueChange={ monitorMsg=it; val bad = listOf("hate","kill","sex","illegal","porn","abuse"); if(bad.any{ monitorMsg.lowercase().contains(it) }) keywordAlert="⚠️ KEYWORD ALERT: ${bad.first{ monitorMsg.lowercase().contains(it) }} DETECTED!" else keywordAlert="No keyword alert" }, label={ Text("Monitor Live Chat / Speech - Type to test keyword") }, modifier=Modifier.fillMaxWidth(), minLines=2)
            Text(keywordAlert, color=Color.Red, fontWeight=FontWeight.Bold)
            Spacer(modifier=Modifier.height(8.dp))
            Button(onClick={ toast("MUTE + STOP + BAN in 1 tap - Stream stopped, user banned") }, modifier=Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("🚨 MUTE / STOP / BAN IN ONE TAP", color=Color.White, fontWeight=FontWeight.Bold) }
            Spacer(modifier=Modifier.height(8.dp))
            Text("Viewer Reports:", color=Color.Yellow, fontWeight=FontWeight.Bold)
            reports.forEach{ r -> Column(modifier=Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)){ Text("From ${r.from}: ${r.reason}", color=Color.White); Button(onClick={ toast("Banned ${r.from} + Stopped stream") }, colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("BAN REPORTER TARGET") } }; Spacer(modifier=Modifier.height(6.dp)) }
            Button(onClick={ screen="DASH" }, modifier=Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
    if(screen=="MEETING"){
        Column(modifier=Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("STUDIO MEETING - Admin Host Only", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            Text("Subscribers hear Admin only. Sub to Sub BLOCKED.", color=Color.Gray, fontSize=12.sp)
            OutlinedTextField(value=meetingTitle, onValueChange={ meetingTitle=it }, label={ Text("Meeting Title") }, modifier=Modifier.fillMaxWidth())
            Button(onClick={ meetingOn=!meetingOn; toast(if(meetingOn) "Meeting Live - All subs muted, only you talk" else "Meeting Ended") }, modifier=Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=if(meetingOn) Color.Red else Color.Green)){ Text(if(meetingOn) "END MEETING" else "START MEETING") }
            if(meetingOn) { Spacer(modifier=Modifier.height(10.dp)); RealCamera(modifier=Modifier.fillMaxWidth().height(250.dp), isFront=true); Text("You are LIVE in Meeting - Subs watching", color=Color.Green) }
            Button(onClick={ screen="DASH" }, modifier=Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
}
