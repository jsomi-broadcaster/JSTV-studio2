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
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

const val DB_URL = "https://jstv-studio-default-rtdb.firebaseio.com"
const val PAYBILL = "516600"
const val ACCOUNT = "5600935001"
val BAD_WORDS = listOf("hate","kill","idiot","stupid","fuck","shit","bitch","tribal","murder","rape","terror")
val BIBLE_LIST = listOf("John 3:16 - For God so loved the world...","Psalm 23:1 - The LORD is my shepherd, I lack nothing.","Jeremiah 29:11 - For I know the plans I have for you...","Philippians 4:13 - I can do all things through Christ...","Romans 8:28 - All things work together for good...","Isaiah 41:10 - Do not fear, for I am with you...","Matthew 11:28 - Come to me, all who are weary...","Psalm 46:1 - God is our refuge and strength...","Proverbs 3:5 - Trust in the LORD with all your heart...","Joshua 1:9 - Be strong and courageous...","2 Timothy 1:7 - God has not given us a spirit of fear...","Psalm 121:1 - I lift my eyes to the mountains...","Matthew 5:14 - You are the light of the world...","Romans 10:9 - If you confess with your mouth...","John 14:6 - I am the way, the truth, and the life...","Psalm 100:4 - Enter His gates with thanksgiving...","Galatians 5:22 - The fruit of the Spirit is love...","Hebrews 11:1 - Faith is confidence in what we hope for...","1 Corinthians 13:4 - Love is patient, love is kind...","Revelation 21:4 - He will wipe every tear...")
fun firebaseGet(path: String, onDone: (String) -> Unit) { Thread { try { val c = URL("$DB_URL/$path.json").openConnection() as HttpURLConnection; c.connectTimeout = 8000; val r = c.inputStream.bufferedReader().readText(); onDone(r) } catch(e: Exception){ onDone("") } }.start() }
fun firebasePut(path: String, json: String) { Thread { try { val c = URL("$DB_URL/$path.json").openConnection() as HttpURLConnection; c.requestMethod="PUT"; c.doOutput=true; c.setRequestProperty("Content-Type","application/json"); c.outputStream.write(json.toByteArray()); c.inputStream.bufferedReader().readText() } catch(e: Exception){} }.start() }
fun firebaseDelete(path: String) { Thread { try { val c = URL("$DB_URL/$path.json").openConnection() as HttpURLConnection; c.requestMethod="DELETE"; c.inputStream.bufferedReader().readText() } catch(e: Exception){} }.start() }
data class Sub(var key: String="", var username: String="", var whatsapp: String="", var pin: String="", var plan: String="", var amount: Int=0, var expiry: String="", var isUnlocked: Boolean=false, var receipt: String="", var banned: Boolean=false, var muted: Boolean=false)
class MainActivity : ComponentActivity() { override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme { AdminApp() } } } }
@Composable fun RealCamera(modifier: Modifier=Modifier, isFront: Boolean) { val lc = LocalLifecycleOwner.current; AndroidView(factory={ c -> val pv=PreviewView(c); try{ val f=ProcessCameraProvider.getInstance(c); f.addListener({ try{ val p=f.get(); val pr=Preview.Builder().build().also{it.setSurfaceProvider(pv.surfaceProvider)}; p.unbindAll(); p.bindToLifecycle(lc, if(isFront) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA, pr)}catch(e:Exception){} }, ContextCompat.getMainExecutor(c))}catch(e:Exception){}; pv }, modifier=modifier) }
// VISIBLE TYPING COLORS - WHITE BOX, BLACK TEXT, YELLOW BORDER
@Composable fun VisibleField(value: String, onChange: (String)->Unit, label: String, modifier: Modifier=Modifier, minLines: Int=1){
    OutlinedTextField(value=value, onValueChange=onChange, label={Text(label, color=Color.Black, fontWeight=FontWeight.Bold)}, placeholder={Text(label, color=Color.DarkGray)}, textStyle=androidx.compose.ui.text.TextStyle(color=Color.Black, fontSize=18.sp, fontWeight=FontWeight.Bold), modifier=modifier.background(Color.White, RoundedCornerShape(8.dp)).border(3.dp, Color.Yellow, RoundedCornerShape(8.dp)), minLines=minLines, colors=OutlinedTextFieldDefaults.colors(focusedContainerColor=Color.White, unfocusedContainerColor=Color.White, focusedTextColor=Color.Black, unfocusedTextColor=Color.Black, cursorColor=Color.Black, focusedBorderColor=Color.Yellow, unfocusedBorderColor=Color.Yellow, focusedLabelColor=Color.Black, unfocusedLabelColor=Color.Black, focusedPlaceholderColor=Color.DarkGray, unfocusedPlaceholderColor=Color.DarkGray))
}

@Composable fun AdminApp(){
    val ctx=LocalContext.current
    var hasPerm by remember{ mutableStateOf(ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) }
    val permLauncher=androidx.activity.compose.rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ r-> hasPerm=r.values.all{it} }
    var screen by remember{ mutableStateOf("DASH") }
    var isLive by remember{ mutableStateOf(false) }; var isFront by remember{ mutableStateOf(true) }; var isMicOn by remember{ mutableStateOf(true) }
    var channelName by remember{ mutableStateOf("JSTV") }; var ticker by remember{ mutableStateOf("Welcome to JSTV - Paybill $PAYBILL Acc $ACCOUNT") }; var verse by remember{ mutableStateOf("John 3:16") }
    var showTicker by remember{ mutableStateOf(true) }; var showVerse by remember{ mutableStateOf(true) }; var showLogo by remember{ mutableStateOf(true) }; var showBible by remember{ mutableStateOf(false) }; var bibleText by remember{ mutableStateOf("") }
    var logoX by remember{ mutableStateOf(20f)}; var logoY by remember{ mutableStateOf(20f)}; var tickerX by remember{ mutableStateOf(0f)}; var tickerY by remember{ mutableStateOf(400f)}; var verseX by remember{ mutableStateOf(20f)}; var verseY by remember{ mutableStateOf(300f)}; var bibleX by remember{ mutableStateOf(20f)}; var bibleY by remember{ mutableStateOf(200f)}
    val subs=remember{ mutableStateListOf<Sub>() }
    val premiums=remember{ mutableStateListOf<Triple<String,Int,Int>>(Triple("Daily",200,1), Triple("Monthly",1000,30), Triple("3 Months",2500,90), Triple("Year",8000,365)) }
    var inboxMsg by remember{ mutableStateOf("") }
    var chatInput by remember{ mutableStateOf("") }
    var chatDisplay by remember{ mutableStateOf("No messages yet. Type below to talk to Broadcaster.") }
    var newPremiumName by remember{ mutableStateOf("")}; var newPremiumPrice by remember{ mutableStateOf("")}; var newPremiumDays by remember{ mutableStateOf("")}
    var adminReceipt by remember{ mutableStateOf("")}; var adminUser by remember{ mutableStateOf("")}
    var liveChat by remember{ mutableStateOf("")}; var alertMsg by remember{ mutableStateOf("")}
    var monitorOn by remember{ mutableStateOf(true)}; var searchBible by remember{ mutableStateOf("")}
    fun toast(m:String){ Toast.makeText(ctx,m,Toast.LENGTH_SHORT).show() }
    fun openWhatsApp(num:String,msg:String){ val n=if(num.startsWith("0")) "254"+num.substring(1) else num.replace("+",""); try{ ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$n?text="+Uri.encode(msg)))) }catch(e:Exception){ toast("WhatsApp not found") } }
    fun refreshSubs(){
        firebaseGet("users"){ res-> try{ if(res.isNotBlank() && res!="null"){ val o=JSONObject(res); val list=mutableListOf<Sub>(); o.keys().forEach{ k-> val u=o.getJSONObject(k); val s=Sub(k, u.optString("username"), u.optString("whatsapp"), u.optString("pin"), u.optString("plan"), u.optInt("amount"), u.optString("expiry"), u.optBoolean("isUnlocked"), u.optString("receipt"), u.optBoolean("banned"), u.optBoolean("muted")); try{ val sdf=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); if(s.expiry.isNotBlank() && sdf.parse(s.expiry)?.before(Date())==true && s.isUnlocked){ s.isUnlocked=false; firebasePut("users/${s.key}/isUnlocked","false") } }catch(e:Exception){}; list.add(s) }; subs.clear(); subs.addAll(list) } }catch(e:Exception){} }
        firebaseGet("alerts"){ r-> if(r.isNotBlank() && r!="null") alertMsg=r }
        firebaseGet("chat"){ r-> if(r.isNotBlank() && r!="null"){ liveChat=r.replace("\"",""); chatDisplay=liveChat } }
        firebaseGet("bibleDisplay"){ r-> if(r.isNotBlank() && r!="null" && r!="\"\""){ bibleText=r.replace("\"",""); if(bibleText.isNotBlank()) showBible=true } }
    }
    LaunchedEffect(Unit){ refreshSubs() }
    fun saveSub(s:Sub){ val j=JSONObject(); j.put("username",s.username); j.put("whatsapp",s.whatsapp); j.put("pin",s.pin); j.put("plan",s.plan); j.put("amount",s.amount); j.put("expiry",s.expiry); j.put("isUnlocked",s.isUnlocked); j.put("receipt",s.receipt); j.put("banned",s.banned); j.put("muted",s.muted); firebasePut("users/${s.key}", j.toString()) }
    fun checkHate(text:String): Boolean { val low=text.lowercase(); val found=BAD_WORDS.find{ low.contains(it) }; if(found!=null && monitorOn){ alertMsg="ALERT: '$found' in: $text"; firebasePut("alerts", JSONObject.quote(alertMsg)); toast("ALERT: Hate speech detected!"); return true }; return false }
    @Composable fun TypingBox(){
        Column(Modifier.fillMaxWidth().background(Color.Yellow).padding(10.dp)){
            Text("💬 LIVE CHAT - TYPE HERE - BROADCASTER WILL SEE IT", color=Color.Black, fontWeight=FontWeight.Bold, fontSize=14.sp)
            Box(Modifier.fillMaxWidth().background(Color.White).padding(10.dp)){ Text(chatDisplay, color=Color.Black, fontSize=16.sp, fontWeight=FontWeight.Bold) }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically){
                VisibleField(value=chatInput, onChange={chatInput=it}, label="Type message here...", modifier=Modifier.weight(1f))
                Spacer(Modifier.width(6.dp))
                Button(onClick={ if(chatInput.isBlank()) toast("Type message first") else if(checkHate(chatInput)) toast("Blocked: Hate speech") else { val full="ADMIN: $chatInput"; firebasePut("chat", JSONObject.quote(full)); firebasePut("adminMessage", JSONObject.quote(chatInput)); chatDisplay=full; chatInput=""; toast("Sent to Broadcaster!") } }, colors=ButtonDefaults.buttonColors(containerColor=Color.Green), modifier=Modifier.height(56.dp)){ Text("SEND", color=Color.White, fontWeight=FontWeight.Bold, fontSize=16.sp) }
            }
            Button(onClick={ refreshSubs(); toast("Chat Refreshed") }, Modifier.fillMaxWidth()){ Text("REFRESH CHAT") }
        }
    }
    if(screen=="DASH"){
        Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("JSTV ADMIN STUDIO", color=Color.Yellow, fontSize=26.sp, fontWeight=FontWeight.Bold)
            Text("Paybill $PAYBILL Acc $ACCOUNT | Subs: ${subs.size} | Live: ${if(isLive)"ON" else "OFF"}", color=Color.White, fontSize=12.sp)
            if(alertMsg.isNotBlank()) Text(alertMsg, color=Color.Red, fontWeight=FontWeight.Bold, fontSize=13.sp)
            if(showBible && bibleText.isNotBlank()) Text("BIBLE: $bibleText", color=Color.Yellow, fontWeight=FontWeight.Bold)
            Button(onClick={ refreshSubs(); toast("Refreshed") }, Modifier.fillMaxWidth()){ Text("REFRESH - LOAD NEW SIGNUPS") }
            Spacer(Modifier.height(10.dp))
            Button(onClick={ screen="LIVE" }, Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("GO TO LIVE STUDIO", color=Color.White, fontWeight=FontWeight.Bold) }
            Spacer(Modifier.height(6.dp))
            Button(onClick={ screen="TV" }, Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("TV / CHURCH BIG SCREEN", color=Color.Black, fontWeight=FontWeight.Bold) }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={screen="SUBS"}, Modifier.weight(1f)){ Text("SUBS ${subs.size}") }; Button(onClick={screen="RATES"}, Modifier.weight(1f)){ Text("RATES") }; Button(onClick={screen="INBOX"}, Modifier.weight(1f)){ Text("INBOX") } }
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={screen="MONITOR"}, Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("LIVE MONITOR", color=Color.Black) }; Button(onClick={screen="PAY"}, Modifier.weight(1f)){ Text("PAYMENTS") }; Button(onClick={screen="BIBLE"}, Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("BIBLE") } }
            Spacer(Modifier.height(12.dp))
            TypingBox()
            Spacer(Modifier.height(12.dp))
            Text("SUBSCRIBERS - AUTO SAVED FROM BROADCASTER:", color=Color.Yellow, fontWeight=FontWeight.Bold)
            if(subs.isEmpty()) Text("No subscribers yet.", color=Color.Gray, fontSize=12.sp)
            subs.forEach{ s->
                Column(Modifier.fillMaxWidth().background(if(s.banned) Color.Red else Color.DarkGray).padding(10.dp)){
                    Text("${s.username} | ${s.whatsapp} | ${s.plan} ${s.amount} KES", color=Color.White, fontWeight=FontWeight.Bold, fontSize=13.sp)
                    Text("Expiry: ${s.expiry} | ${if(s.isUnlocked)"UNLOCKED" else "LOCKED"} | Receipt: ${if(s.receipt.isBlank())"NONE" else s.receipt} | ${if(s.muted)"MUTED" else ""} ${if(s.banned)"BANNED" else ""}", color=if(s.isUnlocked) Color.Green else Color.Red, fontSize=12.sp)
                    Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){
                        Button(onClick={ s.isUnlocked=!s.isUnlocked; saveSub(s); toast("${s.username} ${if(s.isUnlocked)"UNLOCKED" else "LOCKED"}") }, colors=ButtonDefaults.buttonColors(containerColor=if(s.isUnlocked) Color.Red else Color.Green)){ Text(if(s.isUnlocked)"LOCK" else "UNLOCK", fontSize=9.sp) }
                        Button(onClick={ openWhatsApp(s.whatsapp, "Hi ${s.username}, JSTV Admin: $inboxMsg") }){ Text("WHATSAPP", fontSize=9.sp) }
                        Button(onClick={ s.muted=!s.muted; saveSub(s); toast(if(s.muted)"Muted" else "Unmuted") }, colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text(if(s.muted)"UNMUTE" else "MUTE", fontSize=9.sp) }
                        Button(onClick={ s.banned=true; saveSub(s); firebasePut("liveControl","{\"action\":\"BAN\",\"user\":\"${s.username}\"}"); toast("${s.username} BANNED") }, colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("BAN", fontSize=9.sp) }
                    }
                }; Spacer(Modifier.height(6.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text("SEND MESSAGE TO BROADCASTER / SUBSCRIBER:", color=Color.Yellow, fontWeight=FontWeight.Bold)
            VisibleField(value=inboxMsg, onChange={inboxMsg=it}, label="Type real message...", modifier=Modifier.fillMaxWidth(), minLines=2)
            Button(onClick={ if(inboxMsg.isBlank()) toast("Type message") else { firebasePut("adminMessage", JSONObject.quote(inboxMsg)); toast("Message sent to ALL Broadcasters!"); inboxMsg="" } }, Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7C4DFF))){ Text("SEND TO BROADCASTERS") }
        }
    }
    if(screen=="LIVE"){
        Box(Modifier.fillMaxSize().background(Color.Black)){
            if(hasPerm) RealCamera(Modifier.fillMaxSize(), isFront) else Box(Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment=Alignment.Center){ Column(horizontalAlignment=Alignment.CenterHorizontally){ Text("Camera Needs Permission", color=Color.White, fontWeight=FontWeight.Bold); Button(onClick={ permLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }){ Text("ALLOW CAMERA + MIC") } } }
            if(hasPerm){
                if(showLogo) Box(Modifier.offset{IntOffset(logoX.roundToInt(), logoY.roundToInt())}.pointerInput(Unit){detectDragGestures{_,d-> logoX+=d.x; logoY+=d.y}}.background(Color.Red).padding(8.dp)){ Text("JSTV $channelName", color=Color.White, fontWeight=FontWeight.Bold) }
                if(showVerse) Box(Modifier.offset{IntOffset(verseX.roundToInt(), verseY.roundToInt())}.pointerInput(Unit){detectDragGestures{_,d-> verseX+=d.x; verseY+=d.y}}.background(Color.Black).padding(8.dp)){ Text("Verse: $verse", color=Color.Yellow) }
                if(showTicker) Box(Modifier.offset{IntOffset(tickerX.roundToInt(), tickerY.roundToInt())}.pointerInput(Unit){detectDragGestures{_,d-> tickerX+=d.x; tickerY+=d.y}}.fillMaxWidth().background(Color.Yellow).padding(8.dp)){ Text("Ticker: $ticker", color=Color.Black) }
                if(showBible && bibleText.isNotBlank()) Box(Modifier.offset{IntOffset(bibleX.roundToInt(), bibleY.roundToInt())}.pointerInput(Unit){detectDragGestures{_,d-> bibleX+=d.x; bibleY+=d.y}}.background(Color.White).padding(12.dp)){ Text("BIBLE: $bibleText", color=Color.Black, fontWeight=FontWeight.Bold) }
            }
            Column(Modifier.align(Alignment.TopEnd).padding(10.dp)){ Text(if(isLive)"LIVE" else "NOT LIVE", color=if(isLive) Color.Red else Color.White, fontWeight=FontWeight.Bold); Text("Mic: ${if(isMicOn)"ON" else "OFF"} | Cam: ${if(isFront)"FRONT" else "BACK"}", color=Color.White, fontSize=11.sp) }
            Column(Modifier.align(Alignment.BottomCenter).padding(12.dp)){
                Box(Modifier.fillMaxWidth().background(Color.Yellow).padding(8.dp)){ Column{ Text("💬 CHAT: $chatDisplay", color=Color.Black, fontSize=14.sp, fontWeight=FontWeight.Bold); Row(verticalAlignment=Alignment.CenterVertically){ VisibleField(value=chatInput, onChange={chatInput=it}, label="Type here...", modifier=Modifier.weight(1f)); Spacer(Modifier.width(6.dp)); Button(onClick={ if(chatInput.isNotBlank()){ val full="ADMIN: $chatInput"; firebasePut("chat", JSONObject.quote(full)); firebasePut("adminMessage", JSONObject.quote(chatInput)); chatDisplay=full; chatInput=""; toast("Sent!") } }){ Text("SEND") } } } }
                if(isLive) Button(onClick={ isLive=false; firebasePut("liveControl","{\"action\":\"STOP\"}"); toast("Live Ended") }, Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("END LIVE", color=Color.White, fontWeight=FontWeight.Bold) }
                else Button(onClick={ if(!hasPerm) permLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) else { isLive=true; firebasePut("liveControl","{\"action\":\"LIVE\"}"); toast("You are LIVE!") } }, Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("GO LIVE", color=Color.White, fontWeight=FontWeight.Bold) }
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={screen="SETUP"}, Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("LIVE SETUP", color=Color.Black, fontWeight=FontWeight.Bold) }; Button(onClick={screen="DASH"}, Modifier.weight(1f)){ Text("BACK") } }
            }
        }
    }
    if(screen=="SETUP"){
        Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("LIVE SETUP", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            VisibleField(value=channelName, onChange={channelName=it}, label="Channel Name", modifier=Modifier.fillMaxWidth())
            VisibleField(value=ticker, onChange={ticker=it}, label="Ticker Text", modifier=Modifier.fillMaxWidth())
            VisibleField(value=verse, onChange={verse=it}, label="Verse", modifier=Modifier.fillMaxWidth())
            Row(verticalAlignment=Alignment.CenterVertically){ Text("Show Logo", color=Color.White, modifier=Modifier.weight(1f)); Switch(checked=showLogo, onCheckedChange={showLogo=it}) }
            Row(verticalAlignment=Alignment.CenterVertically){ Text("Show Verse", color=Color.White, modifier=Modifier.weight(1f)); Switch(checked=showVerse, onCheckedChange={showVerse=it}) }
            Row(verticalAlignment=Alignment.CenterVertically){ Text("Show Ticker", color=Color.White, modifier=Modifier.weight(1f)); Switch(checked=showTicker, onCheckedChange={showTicker=it}) }
            Row(verticalAlignment=Alignment.CenterVertically){ Text("Show Bible", color=Color.White, modifier=Modifier.weight(1f)); Switch(checked=showBible, onCheckedChange={showBible=it}) }
            Row(verticalAlignment=Alignment.CenterVertically){ Text("Live Monitor - Hate Speech Protection", color=Color.White, modifier=Modifier.weight(1f)); Switch(checked=monitorOn, onCheckedChange={monitorOn=it}) }
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={isFront=true}, Modifier.weight(1f)){ Text("FRONT CAM") }; Button(onClick={isFront=false}, Modifier.weight(1f)){ Text("BACK CAM") } }
            Button(onClick={isMicOn=!isMicOn}, Modifier.fillMaxWidth()){ Text(if(isMicOn)"MIC ON - Tap to Mute" else "MIC MUTED - Tap to Unmute") }
            Button(onClick={ permLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }, Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("ALLOW CAMERA + MIC") }
            Button(onClick={ screen="LIVE"; toast("Setup Saved!") }, Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("OK - APPLY TO MAIN SCREEN", color=Color.White, fontWeight=FontWeight.Bold) }
            Button(onClick={ screen="LIVE" }, Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
    if(screen=="TV"){
        Box(Modifier.fillMaxSize().background(Color.Black)){
            if(hasPerm && isLive) RealCamera(Modifier.fillMaxSize(), isFront)
            else Box(Modifier.fillMaxSize(), contentAlignment=Alignment.Center){ Column(horizontalAlignment=Alignment.CenterHorizontally){ Text("JSTV - CHURCH TV", color=Color.Yellow, fontSize=30.sp, fontWeight=FontWeight.Bold); Text("Connect this phone to TV via HDMI / USB-C / Cast", color=Color.White, fontSize=13.sp, textAlign=TextAlign.Center); Button(onClick={ try{ ctx.startActivity(Intent("android.settings.CAST_SETTINGS")) }catch(e:Exception){ toast("Open Settings > Connected Devices > Cast") } }, colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("OPEN CAST TO TV", color=Color.Black, fontWeight=FontWeight.Bold) } } }
            Column(Modifier.align(Alignment.TopCenter).padding(10.dp), horizontalAlignment=Alignment.CenterHorizontally){ Text("JSTV LIVE", color=Color.Red, fontSize=28.sp, fontWeight=FontWeight.Bold) }
            if(showBible && bibleText.isNotBlank()) Box(Modifier.align(Alignment.Center).fillMaxWidth().padding(20.dp).background(Color.Black).padding(20.dp), contentAlignment=Alignment.Center){ Text("BIBLE: $bibleText", color=Color.Yellow, fontSize=28.sp, fontWeight=FontWeight.Bold, textAlign=TextAlign.Center) }
            if(showTicker) Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color.Yellow).padding(12.dp), contentAlignment=Alignment.Center){ Text(ticker, color=Color.Black, fontSize=20.sp, fontWeight=FontWeight.Bold) }
            Button(onClick={ screen="DASH" }, Modifier.align(Alignment.BottomEnd).padding(10.dp)){ Text("EXIT TV") }
        }
    }
    if(screen=="BIBLE"){
        Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("BIBLE - TAP TO SHOW ON MAIN + TV", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            VisibleField(value=searchBible, onChange={searchBible=it}, label="Search Bible e.g. John, Psalm...", modifier=Modifier.fillMaxWidth())
            BIBLE_LIST.filter{ searchBible.isBlank() || it.contains(searchBible, ignoreCase=true) }.forEach{ v->
                Column(Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)){ Text(v, color=Color.White, fontSize=13.sp); Button(onClick={ bibleText=v; showBible=true; firebasePut("bibleDisplay", JSONObject.quote(v)); toast("Bible showing on MAIN + TV!") }, Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("SHOW THIS ON MAIN STUDIO + TV") } }; Spacer(Modifier.height(6.dp))
            }
            Button(onClick={ showBible=false; bibleText=""; firebasePut("bibleDisplay", JSONObject.quote("")); firebasePut("liveControl","{\"action\":\"BIBLE_CLEAR\"}"); toast("Bible cleared") }, Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("CLEAR BIBLE FROM MAIN") }
            Button(onClick={screen="DASH"}, Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
    if(screen=="SUBS"){
        Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("SUBSCRIBERS + VERIFY PAYMENT", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            Text("Only unlock AFTER you check YOUR M-PESA SMS. Paybill $PAYBILL Acc $ACCOUNT", color=Color.Red, fontSize=12.sp, fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            VisibleField(value=adminUser, onChange={adminUser=it}, label="Username to Unlock", modifier=Modifier.fillMaxWidth())
            VisibleField(value=adminReceipt, onChange={adminReceipt=it.uppercase()}, label="Receipt Code from User", modifier=Modifier.fillMaxWidth())
            Button(onClick={ if(adminReceipt.length<6 || adminUser.isBlank()) toast("Enter Username + Receipt") else { val t=subs.find{it.username.equals(adminUser, ignoreCase=true)}; if(t!=null){ t.isUnlocked=true; t.receipt=adminReceipt; val days=premiums.find{it.first.equals(t.plan, ignoreCase=true)}?.third ?: 30; val cal=Calendar.getInstance(); cal.add(Calendar.DAY_OF_YEAR, days); t.expiry=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time); saveSub(t); toast("${t.username} UNLOCKED until ${t.expiry}"); adminReceipt=""; adminUser="" } else toast("User not found") } }, Modifier.fillMaxWidth().height(54.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("VERIFY & UNLOCK USER", color=Color.White, fontWeight=FontWeight.Bold) }
            Spacer(Modifier.height(10.dp))
            Text("STK PUSH: Open M-PESA Business App, send STK Push, or user pays DIRECT to Paybill $PAYBILL Acc $ACCOUNT", color=Color.Gray, fontSize=11.sp)
            Button(onClick={ if(adminUser.isBlank()) toast("Enter Username first") else { val t=subs.find{it.username.equals(adminUser, ignoreCase=true)}; if(t!=null){ openWhatsApp(t.whatsapp, "Hi ${t.username}, STK PUSH for ${t.plan} - ${t.amount} KES. Watch for Safaricom PIN popup. Or pay DIRECT: Paybill $PAYBILL Acc $ACCOUNT"); toast("Now push from M-PESA Business App") } else toast("User not found") } }, Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Cyan)){ Text("SEND STK PUSH TO THIS USER") }
            Button(onClick={ if(adminUser.isBlank()) toast("Enter Username to delete") else { val t=subs.find{it.username.equals(adminUser, ignoreCase=true)}; if(t!=null){ firebaseDelete("users/${t.key}"); subs.remove(t); toast("Deleted ${t.username}") } else toast("User not found") } }, Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("ADMIN AUTHORIZE DELETE SUBSCRIBER") }
            Button(onClick={screen="DASH"}, Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
    if(screen=="RATES"){
        Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("RATES + PREMIUM SETS", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            premiums.forEachIndexed{ idx,p-> Row(Modifier.fillMaxWidth().background(Color.DarkGray).padding(10.dp)){ Text("${p.first} - ${p.second} KES - ${p.third} Days", color=Color.White, modifier=Modifier.weight(1f), fontWeight=FontWeight.Bold); Button(onClick={ premiums.removeAt(idx); firebasePut("rates", JSONObject.quote(premiums.joinToString("|"){ "${it.first}:${it.second}:${it.third}" })); toast("Deleted ${p.first}") }, colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("DEL", fontSize=9.sp) } }; Spacer(Modifier.height(6.dp)) }
            Spacer(Modifier.height(10.dp))
            VisibleField(value=newPremiumName, onChange={newPremiumName=it}, label="Premium Name e.g. Weekly", modifier=Modifier.fillMaxWidth())
            VisibleField(value=newPremiumPrice, onChange={newPremiumPrice=it}, label="Price KES", modifier=Modifier.fillMaxWidth())
            VisibleField(value=newPremiumDays, onChange={newPremiumDays=it}, label="Days", modifier=Modifier.fillMaxWidth())
            Button(onClick={ if(newPremiumName.isNotBlank() && (newPremiumPrice.toIntOrNull()?:0)>0){ val np=Triple(newPremiumName, newPremiumPrice.toInt(), newPremiumDays.toIntOrNull()?:30); premiums.add(np); firebasePut("rates", JSONObject.quote(premiums.joinToString("|"){ "${it.first}:${it.second}:${it.third}" })); toast("Premium added"); newPremiumName=""; newPremiumPrice=""; newPremiumDays="" } else toast("Enter Name + Real Price") }, Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("ADD NEW PREMIUM SET") }
            Button(onClick={screen="DASH"}, Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
    if(screen=="INBOX"){ Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){ Text("INBOX - SEND TO BROADCASTERS", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold); TypingBox(); Button(onClick={screen="DASH"}, Modifier.fillMaxWidth()){ Text("BACK") } } }
    if(screen=="MONITOR"){
        Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("LIVE MONITOR - PROTECT JSTV", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            if(alertMsg.isNotBlank()) Text(alertMsg, color=Color.Red, fontWeight=FontWeight.Bold)
            Text("Live Chat / Reports:", color=Color.Cyan, fontWeight=FontWeight.Bold)
            Text(if(liveChat.isBlank()) "No reports yet." else liveChat, color=Color.White)
            Spacer(Modifier.height(10.dp)); TypingBox()
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ firebasePut("liveControl","{\"action\":\"MUTE\"}"); toast("MUTE sent") }, colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("MUTE LIVE", color=Color.Black) }; Button(onClick={ firebasePut("liveControl","{\"action\":\"STOP\"}"); isLive=false; toast("STOPPED LIVE") }, colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("STOP LIVE") } }
            Button(onClick={ refreshSubs(); toast("Refreshed") }, Modifier.fillMaxWidth()){ Text("REFRESH MONITOR") }
            Button(onClick={screen="DASH"}, Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
    if(screen=="PAY"){
        Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState())){
            Text("PAYMENTS", color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold)
            Text("Paybill: $PAYBILL\nAccount: $ACCOUNT\nSTK Push via M-PESA Business App", color=Color.White, fontSize=13.sp)
            VisibleField(value=inboxMsg, onChange={inboxMsg=it}, label="e.g. 50% OFF Monthly this week!", modifier=Modifier.fillMaxWidth(), minLines=2)
            Button(onClick={ if(inboxMsg.isNotBlank()){ firebasePut("offer", JSONObject.quote(inboxMsg)); toast("Offer sent!"); inboxMsg="" } }, Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("SEND MONEY OFFER TO ALL") }
            Button(onClick={screen="DASH"}, Modifier.fillMaxWidth()){ Text("BACK") }
        }
    }
}
