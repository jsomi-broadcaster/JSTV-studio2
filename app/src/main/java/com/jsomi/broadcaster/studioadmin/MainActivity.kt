package com.jsomi.studioadmin
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

data class CodeItem(val code:String, val type:String, val price:Int, val customer:String, var active:Boolean=true)
data class BroadcasterItem(val name:String, val room:String, var live:Boolean, var muted:Boolean=false, var blocked:Boolean=false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme { AdminApp() } } }
}
@Composable
fun AdminApp(){
    val ctx = LocalContext.current
    var tab by remember { mutableStateOf("DASHBOARD") }
    var roomCode by remember { mutableStateOf("JSTV-6638") }
    var isLive by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("Daily") }
    var customer by remember { mutableStateOf("") }
    var lastCode by remember { mutableStateOf("") }
    var importText by remember { mutableStateOf("") }
    // OFFER - ADMIN GIVING OFFER
    var offerType by remember { mutableStateOf("Free Trial 1 Day") }
    var offerPrice by remember { mutableStateOf("0") }
    var offerCustomer by remember { mutableStateOf("") }
    var lastOffer by remember { mutableStateOf("") }
    // SETTINGS
    var defaultRtmp by remember { mutableStateOf("rtmp://a.rtmp.youtube.com/live2") }
    var defaultQuality by remember { mutableStateOf("HD 720p") }
    var defaultTicker by remember { mutableStateOf("Welcome to JSTV - Your Voice, Your Channel") }
    var paybill by remember { mutableStateOf("516600") }
    var payAcc by remember { mutableStateOf("5600935001") }
    var waNumber by remember { mutableStateOf("0763052911") }
    var priceDaily by remember { mutableStateOf("200") }
    var priceMonthly by remember { mutableStateOf("1000") }
    var price3M by remember { mutableStateOf("2500") }
    var priceYear by remember { mutableStateOf("8000") }
    var codes by remember { mutableStateOf(mutableListOf<CodeItem>()) }
    var offers by remember { mutableStateOf(mutableListOf<CodeItem>()) }
    var broadcasters by remember { mutableStateOf(mutableListOf(BroadcasterItem("Broadcaster 1","JSTV-6638",false), BroadcasterItem("Broadcaster 2","JSTV-6638",false))) }
    fun toast(m:String){ Toast.makeText(ctx,m,Toast.LENGTH_SHORT).show() }
    fun wa(msg:String){ val i=Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254763052911?text="+Uri.encode(msg))); try{ctx.startActivity(i)}catch(e:Exception){} }
    fun suffix():String{ val c="ABCDEFGHJKLMNPQRSTUVWXYZ0123456789"; return (1..4).map{ c[Random.nextInt(c.length)] }.joinToString("") }
    fun priceOf(s:String)=when(s){ "Daily"->200; "Monthly"->1000; "3M"->2500; else->8000 }
    fun prefixOf(s:String)=when(s){ "Daily"->"200"; "Monthly"->"1K"; "3M"->"3M"; else->"YR" }
    fun labelOf(s:String)=when(s){ "Daily"->"Daily 200"; "Monthly"->"Monthly 1000"; "3M"->"3 Months 2500"; else->"Year 8000" }
    val total = codes.sumOf{ it.price } + offers.sumOf{ it.price }

    Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment=Alignment.CenterHorizontally){
        Text("👑 JSTV", color=Color.Yellow, fontSize=42.sp, fontWeight=FontWeight.Bold)
        Text("STUDIO ADMIN ONLY - FULL CONTROL", color=Color.White, fontSize=14.sp, fontWeight=FontWeight.Bold)
        Text("NO DEMO - PREMIUM ONLY | ADMIN GIVES OFFER", color=Color.Yellow, fontSize=11.sp)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(4.dp)){
            listOf("DASHBOARD","CODES","OFFER","CONTROL","SETTINGS").forEach{ t -> Button(onClick={tab=t}, modifier=Modifier.weight(1f).height(44.dp), contentPadding=PaddingValues(0.dp), colors=ButtonDefaults.buttonColors(containerColor=if(tab==t) Color.Yellow else Color(0xFF444444))){ Text(t, color=if(tab==t) Color.Black else Color.White, fontSize=9.sp, fontWeight=FontWeight.Bold) } }
        }
        Spacer(Modifier.height(12.dp))
        if(tab=="DASHBOARD"){
            Text("ROOM CODE FOR BROADCASTERS:", color=Color.Yellow, fontSize=14.sp, fontWeight=FontWeight.Bold)
            Text(roomCode, color=Color.Yellow, fontSize=34.sp, fontWeight=FontWeight.Bold)
            Button(onClick={ roomCode="JSTV-"+(1000..9999).random() }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF8B0000))){ Text("GENERATE NEW ROOM CODE", color=Color.White) }
            Spacer(Modifier.height(10.dp))
            Text(if(isLive) "● LIVE" else "● OFFLINE", color=if(isLive) Color.Green else Color.Gray, fontWeight=FontWeight.Bold)
            Box(Modifier.fillMaxWidth().height(140.dp).background(Color(0xFF333333)), contentAlignment=Alignment.Center){ Text(if(isLive) "ADMIN PREVIEW - LIVE" else "ADMIN PREVIEW - OFFLINE", color=Color.White) }
            Spacer(Modifier.height(10.dp))
            Button(onClick={ isLive=true; toast("Real Live Started - Premium Only") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("START REAL LIVE - PREMIUM ONLY", color=Color.White) }
            Button(onClick={ isLive=false; broadcasters.forEach{ it.live=false }; toast("ALL LIVE STOPPED") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("STOP ALL LIVE - ADMIN CONTROL", color=Color.White) }
            Spacer(Modifier.height(14.dp))
            Text("💰 MONEY RECORD - TOTAL: KSH $total", color=Color.Yellow, fontSize=16.sp, fontWeight=FontWeight.Bold)
            Text("Daily $priceDaily | Monthly $priceMonthly | 3 Months $price3M | Year $priceYear", color=Color.White, fontSize=12.sp)
            Text("Paybill $paybill Acc $payAcc | WhatsApp $waNumber", color=Color.White, fontSize=12.sp)
            Text("Codes: ${codes.size} | Offers: ${offers.size} | Broadcasters: ${broadcasters.size}", color=Color.Gray, fontSize=12.sp)
            Spacer(Modifier.height(10.dp))
            Text("ABOUT APP & BROADCASTING - ADMIN INFO:", color=Color.Yellow, fontSize=13.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth())
            Text("Broadcaster: Main Camera / Remote / Screen. Permissions: Camera + Mic. Streaming: YouTube / Facebook / TikTok / Custom RTMP. Premium ONLY on GO LIVE - No Demo Live. Admin controls Room, Codes, Offers, Money, Broadcasters, Settings.", color=Color.Gray, fontSize=12.sp)
        }
        if(tab=="CODES"){
            Text("Customer Name", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=customer, onValueChange={customer=it}, label={Text("e.g. Customer Name")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ listOf("Daily","Monthly","3M","Year").forEach{ t -> Button(onClick={selected=t}, modifier=Modifier.weight(1f).height(44.dp), contentPadding=PaddingValues(0.dp), colors=ButtonDefaults.buttonColors(containerColor=if(selected==t) Color.Yellow else Color(0xFF444444))){ Text(t, color=if(selected==t) Color.Black else Color.White, fontWeight=FontWeight.Bold) } } }
            Spacer(Modifier.height(10.dp))
            Button(onClick={ val c="JSTV-${prefixOf(selected)}-${suffix()}"; lastCode=c; codes.add(0, CodeItem(c, labelOf(selected), priceOf(selected), if(customer.isBlank()) "Customer" else customer)); customer=""; toast("Generated $c") }, modifier=Modifier.fillMaxWidth().height(52.dp), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("GENERATE CODE FOR ${labelOf(selected)}", color=Color.White, fontWeight=FontWeight.Bold) }
            Spacer(Modifier.height(10.dp))
            if(lastCode.isNotBlank()){ Box(Modifier.fillMaxWidth().background(Color(0xFF444444)).padding(12.dp)){ Column{ Text("Last Code:", color=Color.White); Text(lastCode, color=Color.Yellow, fontSize=24.sp, fontWeight=FontWeight.Bold) } }; Spacer(Modifier.height(10.dp)); Button(onClick={ wa("Hi! Your JSTV Code: $lastCode - ${labelOf(selected)} - Paybill $paybill Acc $payAcc") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text("WHATSAPP CODE TO CUSTOMER", color=Color.White, fontWeight=FontWeight.Bold) } }
            Spacer(Modifier.height(14.dp))
            Text("👥 ALL CODES", color=Color.Yellow, fontSize=18.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth())
            if(codes.isEmpty()) Text("No codes yet - Generate one above", color=Color.Gray, fontSize=12.sp)
            codes.forEachIndexed{ idx, item -> Spacer(Modifier.height(8.dp)); Box(Modifier.fillMaxWidth().background(Color(0xFF444444)).padding(12.dp)){ Column{ Text("${item.code} - ${item.type}", color=Color.Yellow, fontSize=16.sp, fontWeight=FontWeight.Bold); Text("Customer: ${item.customer} | ${if(item.active) "✅ ACTIVE" else "🚫 BLOCKED"}", color=Color.White, fontSize=13.sp); Spacer(Modifier.height(8.dp)); Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ Button(onClick={ val nl=codes.toMutableList(); nl[idx]=item.copy(active=!item.active); codes=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text(if(item.active) "BLOCK" else "UNBLOCK", color=Color.White) }; Button(onClick={ val nl=codes.toMutableList(); nl.removeAt(idx); codes=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text("DELETE", color=Color.White) } } } } }
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value=importText, onValueChange={importText=it}, label={Text("Paste codes to import")}, modifier=Modifier.fillMaxWidth())
            Button(onClick={ if(importText.isNotBlank()){ codes.add(0, CodeItem(importText.take(20),"Imported",0,"Imported")) } }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text("IMPORT CODES TO THIS PHONE", color=Color.White) }
            Button(onClick={ wa("JSTV Codes:\n"+codes.joinToString("\n"){ "${it.code} - ${it.type} - ${it.customer}" }) }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("EXPORT CODES VIA WHATSAPP", color=Color.White) }
        }
        if(tab=="OFFER"){
            Text("🎁 ADMIN GIVING OFFER", color=Color.Yellow, fontSize=18.sp, fontWeight=FontWeight.Bold)
            Text("Give special offer to win customer - Free Trial, Discount, or Custom Price", color=Color.White, fontSize=12.sp)
            Spacer(Modifier.height(10.dp))
            Text("Customer for Offer", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=offerCustomer, onValueChange={offerCustomer=it}, label={Text("Customer Name")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Text("Offer Type", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){
                listOf("Free Trial 1 Day","50% Off","Custom Offer").forEach{ o -> Button(onClick={ offerType=o; offerPrice=when(o){ "Free Trial 1 Day"->"0"; "50% Off"->"100"; else->offerPrice } }, modifier=Modifier.weight(1f).height(44.dp), contentPadding=PaddingValues(0.dp), colors=ButtonDefaults.buttonColors(containerColor=if(offerType==o) Color.Yellow else Color(0xFF444444))){ Text(o, color=if(offerType==o) Color.Black else Color.White, fontSize=10.sp, fontWeight=FontWeight.Bold) } }
            }
            Spacer(Modifier.height(10.dp))
            Text("Offer Price (KSH) - 0 = Free", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=offerPrice, onValueChange={offerPrice=it}, label={Text("e.g. 0, 100, 500")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Button(onClick={
                val c="JSTV-OFFER-${suffix()}"; lastOffer=c
                val p = offerPrice.toIntOrNull() ?: 0
                offers.add(0, CodeItem(c, "$offerType - KSH $p", p, if(offerCustomer.isBlank()) "Offer Customer" else offerCustomer))
                toast("Offer Created: $c")
            }, modifier=Modifier.fillMaxWidth().height(52.dp), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFFF8800))){ Text("GENERATE OFFER CODE", color=Color.White, fontWeight=FontWeight.Bold) }
            Spacer(Modifier.height(10.dp))
            if(lastOffer.isNotBlank()){
                Box(Modifier.fillMaxWidth().background(Color(0xFF444444)).padding(12.dp)){ Column{ Text("Last Offer:", color=Color.White); Text(lastOffer, color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold); Text("$offerType - KSH $offerPrice - For: $offerCustomer", color=Color.White, fontSize=12.sp) } }
                Spacer(Modifier.height(10.dp))
                Button(onClick={ wa("🎁 Special JSTV OFFER for you! Code: $lastOffer - $offerType - Price: KSH $offerPrice - Paybill $paybill Acc $payAcc - Use this code to GO LIVE!") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text("WHATSAPP OFFER TO CUSTOMER", color=Color.White, fontWeight=FontWeight.Bold) }
            }
            Spacer(Modifier.height(14.dp))
            Text("ALL OFFERS", color=Color.Yellow, fontSize=16.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth())
            if(offers.isEmpty()) Text("No offers yet", color=Color.Gray, fontSize=12.sp)
            offers.forEachIndexed{ idx, item -> Spacer(Modifier.height(8.dp)); Box(Modifier.fillMaxWidth().background(Color(0xFF444444)).padding(12.dp)){ Column{ Text(item.code, color=Color.Yellow, fontSize=15.sp, fontWeight=FontWeight.Bold); Text("${item.type} | Customer: ${item.customer} | ${if(item.active) "✅ ACTIVE" else "🚫 BLOCKED"}", color=Color.White, fontSize=12.sp); Spacer(Modifier.height(6.dp)); Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ Button(onClick={ val nl=offers.toMutableList(); nl[idx]=item.copy(active=!item.active); offers=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text(if(item.active) "BLOCK" else "UNBLOCK", color=Color.White) }; Button(onClick={ val nl=offers.toMutableList(); nl.removeAt(idx); offers=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text("DELETE", color=Color.White) } } } } }
        }
        if(tab=="CONTROL"){
            Text("FULL CONTROL - BROADCASTERS", color=Color.Yellow, fontSize=17.sp, fontWeight=FontWeight.Bold)
            Text("STOP, MUTE, KICK, BLOCK any broadcaster", color=Color.Gray, fontSize=12.sp)
            broadcasters.forEachIndexed{ idx, b -> Spacer(Modifier.height(10.dp)); Box(Modifier.fillMaxWidth().background(Color(0xFF444444)).padding(12.dp)){ Column{ Text("${b.name} - ${b.room}", color=Color.Yellow, fontSize=15.sp, fontWeight=FontWeight.Bold); Text("Live: ${if(b.live) "● LIVE" else "OFFLINE"} | Muted: ${if(b.muted) "YES" else "NO"} | Blocked: ${if(b.blocked) "YES" else "NO"}", color=Color.White, fontSize=12.sp); Spacer(Modifier.height(8.dp)); Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ val nl=broadcasters.toMutableList(); nl[idx]=b.copy(live=!b.live); broadcasters=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text(if(b.live) "STOP" else "START", color=Color.White, fontSize=11.sp) }; Button(onClick={ val nl=broadcasters.toMutableList(); nl[idx]=b.copy(muted=!b.muted); broadcasters=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text(if(b.muted) "UNMUTE" else "MUTE", color=Color.White, fontSize=11.sp) }; Button(onClick={ val nl=broadcasters.toMutableList(); nl[idx]=b.copy(blocked=!b.blocked, live=false); broadcasters=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text(if(b.blocked) "UNBLOCK" else "BLOCK/KICK", color=Color.White, fontSize=11.sp) } } } } }
            Spacer(Modifier.height(10.dp))
            Button(onClick={ isLive=false; val nl=broadcasters.toMutableList(); nl.forEachIndexed{ i, x -> nl[i]=x.copy(live=false) }; broadcasters=nl }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("STOP ALL BROADCASTERS", color=Color.White) }
        }
        if(tab=="SETTINGS"){
            Text("FULL SETTINGS - ADMIN ONLY", color=Color.Yellow, fontSize=17.sp, fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Default RTMP URL", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=defaultRtmp, onValueChange={defaultRtmp=it}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text("Default Quality", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ listOf("SD 480p","HD 720p","FHD 1080p").forEach{ q -> Button(onClick={defaultQuality=q}, colors=ButtonDefaults.buttonColors(containerColor=if(defaultQuality==q) Color.Yellow else Color(0xFF444444))){ Text(q, color=if(defaultQuality==q) Color.Black else Color.White, fontSize=11.sp) } } }
            Spacer(Modifier.height(8.dp))
            Text("Default Ticker", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=defaultTicker, onValueChange={defaultTicker=it}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text("Paybill", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=paybill, onValueChange={paybill=it}, modifier=Modifier.fillMaxWidth())
            Text("Paybill Acc", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=payAcc, onValueChange={payAcc=it}, modifier=Modifier.fillMaxWidth())
            Text("WhatsApp Number", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            OutlinedTextField(value=waNumber, onValueChange={waNumber=it}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text("Prices - Daily / Monthly / 3M / Year", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ OutlinedTextField(value=priceDaily, onValueChange={priceDaily=it}, label={Text("Daily")}, modifier=Modifier.weight(1f)); OutlinedTextField(value=priceMonthly, onValueChange={priceMonthly=it}, label={Text("Monthly")}, modifier=Modifier.weight(1f)) }
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ OutlinedTextField(value=price3M, onValueChange={price3M=it}, label={Text("3M")}, modifier=Modifier.weight(1f)); OutlinedTextField(value=priceYear, onValueChange={priceYear=it}, label={Text("Year")}, modifier=Modifier.weight(1f)) }
            Spacer(Modifier.height(10.dp))
            Text("Streaming URLs - 3 URL + Custom:", color=Color.Yellow, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
            Text("YouTube: rtmp://a.rtmp.youtube.com/live2", color=Color.Gray, fontSize=11.sp, modifier=Modifier.fillMaxWidth())
            Text("Facebook: rtmps://live-api-s.facebook.com:443/rtmp/", color=Color.Gray, fontSize=11.sp, modifier=Modifier.fillMaxWidth())
            Text("TikTok: rtmp://push.tiktok.com/live/", color=Color.Gray, fontSize=11.sp, modifier=Modifier.fillMaxWidth())
            Text("Custom: Any RTMP in Broadcaster SETUP", color=Color.Gray, fontSize=11.sp, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Button(onClick={ toast("Settings Saved") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("SAVE ALL SETTINGS", color=Color.Black, fontWeight=FontWeight.Bold) }
        }
        Spacer(Modifier.height(20.dp))
    }
}
