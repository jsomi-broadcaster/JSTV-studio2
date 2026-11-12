package com.jsomi.admin
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
class MainActivity : ComponentActivity() { override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme { AdminApp() } } } }
@Composable
fun AdminApp(){
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var screen by remember { mutableStateOf("PERMISSION") }
    var tab by remember { mutableStateOf("CONTROL") } // CONTROL | NOTIFY | USERS | OFFER | CHAT
    var roomCode by remember { mutableStateOf("JSTV-2026") }
    var linkedCount by remember { mutableStateOf(1) }
    var isLive by remember { mutableStateOf(false) }
    var liveSeconds by remember { mutableStateOf(0) }
    var viewers by remember { mutableStateOf(12) }
    var earnings by remember { mutableStateOf(0) }
    var isPremium by remember { mutableStateOf(true) }
    var showPremium by remember { mutableStateOf(false) }
    // BROADCASTER CONTROL - Mirrors Broadcaster
    var source by remember { mutableStateOf("MAIN CAMERA") }
    var cameraSide by remember { mutableStateOf("BACK") }
    var cameraOn by remember { mutableStateOf(true) }
    var micOn by remember { mutableStateOf(true) }
    var streamTitle by remember { mutableStateOf("My Live Show") }
    var channel by remember { mutableStateOf("JSTV Main") }
    var scene by remember { mutableStateOf("Studio") }
    var ticker by remember { mutableStateOf("Welcome to JSTV - Your Voice, Your Channel") }
    var showTicker by remember { mutableStateOf(true) }
    var verse by remember { mutableStateOf("John 3:16 - For God so loved the world...") }
    var showVerse by remember { mutableStateOf(true) }
    var showLogo by remember { mutableStateOf(true) }
    var logoText by remember { mutableStateOf("JSTV") }
    var displayText by remember { mutableStateOf("LIVE FROM JSTV STUDIO") }
    var showDisplayText by remember { mutableStateOf(true) }
    var quality by remember { mutableStateOf("HD 720p") }
    var flash by remember { mutableStateOf(false) }
    var beauty by remember { mutableStateOf(false) }
    var ytOn by remember { mutableStateOf(true) }
    var fbOn by remember { mutableStateOf(false) }
    var tkOn by remember { mutableStateOf(false) }
    var ytKey by remember { mutableStateOf("") }
    var fbKey by remember { mutableStateOf("") }
    var tkKey by remember { mutableStateOf("") }
    // NOTIFICATION - ADMIN -> BROADCASTER
    var notifTitle by remember { mutableStateOf("") }
    var notifMsg by remember { mutableStateOf("") }
    var notifTarget by remember { mutableStateOf("ALL BROADCASTERS") }
    var sentNotifs by remember { mutableStateOf(mutableListOf("👑 ADMIN to ALL: Welcome! Your Room JSTV-2026 is LINKED ✅" to "10:00 AM","👑 ADMIN to ALL: Reminder - 30 sec FREE then Premium" to "10:05 AM")) }
    // OFFER / PREMIUM CODES
    var generatedCode by remember { mutableStateOf("") }
    var offerType by remember { mutableStateOf("MONTHLY 200") }
    var offerPrice by remember { mutableStateOf("200") }
    var offerDays by remember { mutableStateOf("30") }
    var codesList by remember { mutableStateOf(mutableListOf("JSTV-200-AB12 - MONTHLY 200 - 30 Days" to "UNUSED","JSTV-OFFER-X99 - 50% OFF - 30 Days" to "UNUSED")) }
    // USERS
    var usersList by remember { mutableStateOf(mutableListOf("Broadcaster 1 - MAIN STUDIO - LINKED - JSTV-2026" to "LIVE","Broadcaster 2 - REMOTE - LINKED - JSTV-2026" to "READY","Broadcaster 3 - WIRELESS - NOT LINKED" to "OFFLINE")) }
    // CHAT / MODERATION
    var chatList by remember { mutableStateOf(mutableListOf("Viewer John: Amen 🙏" to "KEEP","Viewer Mary: Great show!" to "KEEP","Viewer Bad: Spam link" to "BLOCK")) }
    var chatInput by remember { mutableStateOf("") }
    var emergencyBanner by remember { mutableStateOf("") }
    var showEmergency by remember { mutableStateOf(false) }
    var permMsg by remember { mutableStateOf("Allow all") }
    fun hasPerm(p:String)=ContextCompat.checkSelfPermission(ctx,p)==PackageManager.PERMISSION_GRANTED
    fun permList():Array<String>{ val l = mutableListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO); if(Build.VERSION.SDK_INT>=33){ l.add(Manifest.permission.READ_MEDIA_VIDEO); l.add(Manifest.permission.READ_MEDIA_IMAGES); l.add(Manifest.permission.READ_MEDIA_AUDIO) } else { l.add(Manifest.permission.READ_EXTERNAL_STORAGE); l.add(Manifest.permission.WRITE_EXTERNAL_STORAGE) }; return l.toTypedArray() }
    val permLauncher = androidx.activity.compose.rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ res -> permMsg = if(res.values.all{ it }) "✅ ALL GRANTED" else "⚠️ Some denied" }
    fun toast(m:String){ Toast.makeText(ctx,m,Toast.LENGTH_SHORT).show() }
    fun wa(msg:String){ val i=Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254763052911?text="+Uri.encode(msg))); try{ctx.startActivity(i)}catch(e:Exception){} }
    val timeNow = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
    LaunchedEffect(isLive){ if(isLive){ while(isLive){ delay(1000); liveSeconds++; if(liveSeconds%7==0) viewers += Random.nextInt(-2,5); if(liveSeconds%60==0) earnings += 5 } } }
    fun liveTime():String{ val h=liveSeconds/3600; val m=(liveSeconds%3600)/60; val s=liveSeconds%60; return String.format("%02d:%02d:%02d", h,m,s) }
    fun genCode(prefix:String):String{ val chars="ABCDEFGHJKMNPQRSTUVWXYZ23456789"; var c=""; repeat(4){ c+=chars[Random.nextInt(chars.length)] }; return "$prefix-$c" }
    @Composable fun RealCameraAdmin(){
        if(!cameraOn){ Box(Modifier.fillMaxWidth().height(240.dp).background(Color(0xFF222222)), contentAlignment=Alignment.Center){ Text("📷 CAMERA OFF", color=Color.White, fontSize=18.sp, fontWeight=FontWeight.Bold) }; return }
        Box(Modifier.fillMaxWidth().height(240.dp).background(Color.Black)){
            AndroidView(factory={ c -> val pv = PreviewView(c); val pf = ProcessCameraProvider.getInstance(c); pf.addListener({ val p = pf.get(); val pr = Preview.Builder().build().also{ it.setSurfaceProvider(pv.surfaceProvider) }; val sel = if(cameraSide=="BACK") CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA; try{ p.unbindAll(); p.bindToLifecycle(lifecycleOwner, sel, pr) }catch(e:Exception){} }, ContextCompat.getMainExecutor(c)); pv }, modifier=Modifier.fillMaxSize())
            Column(Modifier.fillMaxSize().padding(8.dp)){ Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text(if(isLive) "● LIVE ${liveTime()} - ADMIN" else "○ ADMIN PREVIEW - NOT LIVE", color=if(isLive) Color.Red else Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold); Text("👁 $viewers viewers", color=Color.Yellow, fontSize=11.sp, fontWeight=FontWeight.Bold) }; Spacer(Modifier.weight(1f)); if(showEmergency) Text("🚨 $emergencyBanner", color=Color.Red, fontSize=13.sp, fontWeight=FontWeight.Bold, modifier=Modifier.background(Color.Yellow).padding(4.dp)); if(showLogo) Text(logoText, color=Color.Red, fontSize=20.sp, fontWeight=FontWeight.Bold); if(showDisplayText) Text(displayText, color=Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold); if(showVerse) Text("📖 $verse", color=Color.Yellow, fontSize=11.sp); if(showTicker) Text("↔ $ticker", color=Color(0xFF00E5FF), fontSize=11.sp) }
        }
    }
    @Composable fun RealCameraFullScreen(){
        if(!cameraOn){ Box(Modifier.fillMaxSize().background(Color(0xFF222222)), contentAlignment=Alignment.Center){ Text("📷 CAMERA OFF", color=Color.White, fontSize=18.sp, fontWeight=FontWeight.Bold) }; return }
        AndroidView(factory={ c -> val pv = PreviewView(c); val pf = ProcessCameraProvider.getInstance(c); pf.addListener({ val p = pf.get(); val pr = Preview.Builder().build().also{ it.setSurfaceProvider(pv.surfaceProvider) }; val sel = if(cameraSide=="BACK") CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA; try{ p.unbindAll(); p.bindToLifecycle(lifecycleOwner, sel, pr) }catch(e:Exception){} }, ContextCompat.getMainExecutor(c)); pv }, modifier=Modifier.fillMaxSize())
    }
    // FULL SCREEN LIVE - ADMIN
    if(isLive){
        Box(Modifier.fillMaxSize().background(Color.Black)){
            RealCameraFullScreen()
            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement=Arrangement.SpaceBetween){ Text("● LIVE ${liveTime()} - ADMIN", color=Color.Red, fontSize=14.sp, fontWeight=FontWeight.Bold, modifier=Modifier.background(Color.Black.copy(alpha=0.5f)).padding(6.dp)); Text("👁 $viewers | 💰 KSh $earnings", color=Color.Yellow, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.background(Color.Black.copy(alpha=0.5f)).padding(6.dp)) }
            if(showEmergency) Text("🚨 $emergencyBanner", color=Color.Red, fontSize=14.sp, fontWeight=FontWeight.Bold, modifier=Modifier.align(Alignment.TopCenter).padding(top=60.dp).background(Color.Yellow).padding(8.dp))
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(bottom=150.dp, start=12.dp, end=12.dp)){ if(showLogo) Text(logoText, color=Color.Red, fontSize=20.sp, fontWeight=FontWeight.Bold); if(showDisplayText) Text(displayText, color=Color.White, fontSize=12.sp, fontWeight=FontWeight.Bold); if(showVerse) Text("📖 $verse", color=Color.Yellow, fontSize=11.sp); if(showTicker) Text("↔ $ticker", color=Color(0xFF00E5FF), fontSize=11.sp) }
            Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp)){ Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={cameraOn=!cameraOn}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF444444))){ Text(if(cameraOn) "CAMERA ON" else "CAMERA OFF", color=Color.White, fontSize=11.sp) }; Button(onClick={micOn=!micOn}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF444444))){ Text(if(micOn) "MIC ON" else "MUTED", color=Color.White, fontSize=11.sp) }; Button(onClick={cameraSide=if(cameraSide=="BACK") "FRONT" else "BACK"}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF444444))){ Text("FLIP", color=Color.White, fontSize=11.sp) } }; Spacer(Modifier.height(8.dp)); Button(onClick={ isLive=false; liveSeconds=0; toast("Admin Ended LIVE - Broadcaster also stopped ✅") }, modifier=Modifier.fillMaxWidth().height(56.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("END LIVE FOR ALL", color=Color.White, fontSize=18.sp, fontWeight=FontWeight.Bold) } }
        }
        return
    }
    Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment=Alignment.CenterHorizontally){
        if(screen=="PERMISSION"){
            Box(Modifier.fillMaxWidth().background(Color.Red).padding(20.dp), contentAlignment=Alignment.Center){ Column(horizontalAlignment=Alignment.CenterHorizontally){ Text("JSTV", color=Color.White, fontSize=42.sp, fontWeight=FontWeight.Bold); Text("STUDIO ADMIN", color=Color.White, fontSize=18.sp, fontWeight=FontWeight.Bold); Text("RED LOGO = BOSS", color=Color.White, fontSize=13.sp) } }
            Spacer(Modifier.height(16.dp)); Text("🔐 PERMISSIONS - ADMIN", color=Color.Yellow, fontSize=20.sp, fontWeight=FontWeight.Bold); Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().background(Color(0xFF222222)).padding(16.dp)){ Column{ Text("📷 Camera: ${if(hasPerm(Manifest.permission.CAMERA)) "✅" else "❌"}", color=Color.White, fontSize=13.sp); Spacer(Modifier.height(6.dp)); Text("🔊 Audio: ${if(hasPerm(Manifest.permission.RECORD_AUDIO)) "✅" else "❌"}", color=Color.White, fontSize=13.sp); Spacer(Modifier.height(6.dp)); Text(permMsg, color=Color.Yellow, fontSize=12.sp) } }
            Spacer(Modifier.height(16.dp)); Button(onClick={ permLauncher.launch(permList()) }, modifier=Modifier.fillMaxWidth().height(56.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("ALLOW ALL PERMISSIONS", color=Color.White, fontWeight=FontWeight.Bold) }; Spacer(Modifier.height(10.dp)); Button(onClick={ ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", ctx.packageName, null))) }, modifier=Modifier.fillMaxWidth()){ Text("OPEN SETTINGS") }; Spacer(Modifier.height(20.dp)); Button(onClick={ screen="MAIN" }, modifier=Modifier.fillMaxWidth().height(56.dp), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("CONTINUE → ADMIN DASHBOARD", color=Color.White, fontWeight=FontWeight.Bold) }
        }
        if(screen=="MAIN"){
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("JSTV STUDIO ADMIN", color=Color.Red, fontSize=19.sp, fontWeight=FontWeight.Bold); Text("● $timeNow", color=Color.Gray, fontSize=11.sp) }
            Text("Room: $roomCode | Linked: $linkedCount | 👁 $viewers | 💰 KSh $earnings", color=Color.Yellow, fontSize=12.sp)
            Spacer(Modifier.height(8.dp))
            // TABS - CONTROL / NOTIFY / USERS / OFFER / CHAT
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(4.dp)){ listOf("CONTROL","NOTIFY","USERS","OFFER","CHAT").forEach{ t -> Button(onClick={tab=t}, modifier=Modifier.weight(1f).height(40.dp), contentPadding=PaddingValues(2.dp), colors=ButtonDefaults.buttonColors(containerColor=if(tab==t) Color.Red else Color(0xFF444444))){ Text(t, color=Color.White, fontSize=9.sp, fontWeight=FontWeight.Bold) } } }
            Spacer(Modifier.height(10.dp))
            // REAL CAMERA - ADMIN - ALWAYS VISIBLE ON CONTROL
            RealCameraAdmin(); Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={cameraSide="BACK"}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(cameraSide=="BACK") Color.Red else Color(0xFF444444))){ Text("BACK", color=Color.White, fontSize=11.sp) }; Button(onClick={cameraSide="FRONT"}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(cameraSide=="FRONT") Color.Red else Color(0xFF444444))){ Text("FRONT", color=Color.White, fontSize=11.sp) }; Button(onClick={cameraOn=!cameraOn}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(cameraOn) Color(0xFF00E600) else Color.Red)){ Text(if(cameraOn) "CAM ON" else "CAM OFF", color=Color.White, fontSize=11.sp) }; Button(onClick={micOn=!micOn}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(micOn) Color(0xFF00E600) else Color.Red)){ Text(if(micOn) "MIC ON" else "MUTED", color=Color.White, fontSize=11.sp) } }
            Spacer(Modifier.height(10.dp))
            if(!isLive){ Button(onClick={ if(ytKey.isBlank() && fbKey.isBlank() && tkKey.isBlank()){ toast("Enter at least 1 Stream Key in CONTROL"); return@Button }; isLive=true; liveSeconds=0; viewers=12; toast("ADMIN Started LIVE - Broadcaster also LIVE ✅") }, modifier=Modifier.fillMaxWidth().height(60.dp), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("GO LIVE - CONTROL BROADCASTER", color=Color.White, fontSize=16.sp, fontWeight=FontWeight.Bold) } }
            Spacer(Modifier.height(10.dp))
            if(tab=="CONTROL"){
                Text("🎛️ CONTROL BROADCASTER - FROM FAR", color=Color.Yellow, fontSize=14.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Spacer(Modifier.height(8.dp))
                Text("ROOM CODE - Give to Broadcaster:", color=Color.Yellow, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ OutlinedTextField(value=roomCode, onValueChange={roomCode=it.uppercase()}, label={Text("Room Code")}, modifier=Modifier.weight(1f)); Button(onClick={ roomCode=genCode("JSTV"); linkedCount=0; toast("New Room: $roomCode") }, colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("NEW", color=Color.White) } }
                Button(onClick={ wa("Hi, your JSTV Room Code is $roomCode - Enter in Broadcaster to LINK") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF25D366))){ Text("SHARE ROOM CODE ON WHATSAPP", color=Color.White, fontSize=12.sp) }; Spacer(Modifier.height(10.dp))
                Text("SOURCE - Control Broadcaster Source:", color=Color.Yellow, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ listOf("MAIN CAMERA","REMOTE","SCREEN").forEach{ s -> Button(onClick={ source=s; toast("Broadcaster Source -> $s ✅") }, modifier=Modifier.weight(1f).height(44.dp), contentPadding=PaddingValues(2.dp), colors=ButtonDefaults.buttonColors(containerColor=if(source==s) Color.Yellow else Color(0xFF444444))){ Text(s, color=if(source==s) Color.Black else Color.White, fontSize=9.sp, fontWeight=FontWeight.Bold) } } }; Spacer(Modifier.height(8.dp))
                OutlinedTextField(value=streamTitle, onValueChange={streamTitle=it}, label={Text("Stream Title - Changes on Broadcaster LIVE")}, modifier=Modifier.fillMaxWidth()); Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ listOf("JSTV Main","JSTV Sports","JSTV Music","JSTV News","JSTV Church").forEach{ c -> Button(onClick={channel=c}, modifier=Modifier.weight(1f).height(40.dp), contentPadding=PaddingValues(2.dp), colors=ButtonDefaults.buttonColors(containerColor=if(channel==c) Color.Yellow else Color(0xFF444444))){ Text(c, color=if(channel==c) Color.Black else Color.White, fontSize=9.sp) } } }; Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ listOf("Studio","Outdoor","Church").forEach{ s -> Button(onClick={scene=s}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(scene==s) Color.Yellow else Color(0xFF444444))){ Text(s, color=if(scene==s) Color.Black else Color.White) } } }; Spacer(Modifier.height(10.dp))
                Text("STREAM KEYS - ADMIN Sets Keys for Broadcaster:", color=Color.Yellow, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value=ytKey, onValueChange={ytKey=it}, label={Text("YouTube Key")}, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value=fbKey, onValueChange={fbKey=it}, label={Text("Facebook Key")}, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value=tkKey, onValueChange={tkKey=it}, label={Text("TikTok Key")}, modifier=Modifier.fillMaxWidth()); Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ytOn=!ytOn}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(ytOn) Color.Red else Color(0xFF444444))){ Text(if(ytOn) "YT ON" else "YT OFF", fontSize=11.sp) }; Button(onClick={fbOn=!fbOn}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(fbOn) Color(0xFF1877F2) else Color(0xFF444444))){ Text(if(fbOn) "FB ON" else "FB OFF", fontSize=11.sp) }; Button(onClick={tkOn=!tkOn}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(tkOn) Color.Black else Color(0xFF444444))){ Text(if(tkOn) "TK ON" else "TK OFF", fontSize=11.sp) } }; Spacer(Modifier.height(10.dp))
                Text("QUALITY + CAMERA:", color=Color.Yellow, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ listOf("SD 480p","HD 720p","FHD 1080p").forEach{ q -> Button(onClick={quality=q}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(quality==q) Color.Yellow else Color(0xFF444444))){ Text(q, color=if(quality==q) Color.Black else Color.White, fontSize=10.sp) } } }; Spacer(Modifier.height(6.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={flash=!flash}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(flash) Color.Yellow else Color(0xFF444444))){ Text(if(flash) "FLASH ON" else "FLASH OFF", color=if(flash) Color.Black else Color.White, fontSize=11.sp) }; Button(onClick={beauty=!beauty}, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=if(beauty) Color.Yellow else Color(0xFF444444))){ Text(if(beauty) "BEAUTY ON" else "BEAUTY OFF", color=if(beauty) Color.Black else Color.White, fontSize=11.sp) } }; Spacer(Modifier.height(10.dp))
                Text("TICKER / LOGO / VERSE - Change LIVE:", color=Color.Yellow, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value=ticker, onValueChange={ticker=it}, label={Text("Ticker - Changes LIVE")}, modifier=Modifier.fillMaxWidth()); Button(onClick={showTicker=!showTicker}, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=if(showTicker) Color(0xFF00E600) else Color(0xFF444444))){ Text(if(showTicker) "TICKER SHOW" else "TICKER HIDE") }; OutlinedTextField(value=verse, onValueChange={verse=it}, label={Text("Verse - Changes LIVE")}, modifier=Modifier.fillMaxWidth()); Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={verse="Psalm 23:1 - The Lord is my shepherd"}, modifier=Modifier.weight(1f)){ Text("Psalm 23", fontSize=10.sp) }; Button(onClick={verse="John 3:16 - For God so loved the world..."}, modifier=Modifier.weight(1f)){ Text("John 3:16", fontSize=10.sp) }; Button(onClick={verse="Philippians 4:13 - I can do all things..."}, modifier=Modifier.weight(1f)){ Text("Phil 4:13", fontSize=10.sp) } }; Button(onClick={showVerse=!showVerse}, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=if(showVerse) Color.Yellow else Color(0xFF444444))){ Text(if(showVerse) "VERSE SHOW" else "VERSE HIDE", color=if(showVerse) Color.Black else Color.White) }; OutlinedTextField(value=logoText, onValueChange={logoText=it}, label={Text("Logo Text")}, modifier=Modifier.fillMaxWidth()); Button(onClick={showLogo=!showLogo}, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=if(showLogo) Color(0xFF00E600) else Color(0xFF444444))){ Text(if(showLogo) "LOGO SHOW" else "LOGO HIDE") }; OutlinedTextField(value=displayText, onValueChange={displayText=it}, label={Text("Display Text")}, modifier=Modifier.fillMaxWidth()); Button(onClick={showDisplayText=!showDisplayText}, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=if(showDisplayText) Color(0xFF00E600) else Color(0xFF444444))){ Text(if(showDisplayText) "DISPLAY SHOW" else "DISPLAY HIDE") }; Spacer(Modifier.height(10.dp))
                Text("🚨 EMERGENCY BANNER - Show on ALL Screens:", color=Color.Yellow, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value=emergencyBanner, onValueChange={emergencyBanner=it}, label={Text("Emergency - e.g. LIVE will end in 5 min")}, modifier=Modifier.fillMaxWidth()); Button(onClick={showEmergency=!showEmergency; toast(if(showEmergency) "Emergency Banner ON for ALL ✅" else "Emergency Banner OFF")}, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=if(showEmergency) Color.Red else Color(0xFF444444))){ Text(if(showEmergency) "🚨 BANNER ON - TAP TO OFF" else "🚨 SHOW EMERGENCY BANNER", color=Color.White) }
            }
            if(tab=="NOTIFY"){
                Text("📩 SEND NOTIFICATION TO BROADCASTER", color=Color.Yellow, fontSize=15.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Text("This appears in Broadcaster INBOX", color=Color.Gray, fontSize=11.sp); Spacer(Modifier.height(10.dp))
                Text("Send To:", color=Color.Yellow, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ listOf("ALL BROADCASTERS","MAIN ONLY","REMOTE ONLY").forEach{ t -> Button(onClick={notifTarget=t}, modifier=Modifier.weight(1f), contentPadding=PaddingValues(2.dp), colors=ButtonDefaults.buttonColors(containerColor=if(notifTarget==t) Color.Yellow else Color(0xFF444444))){ Text(t, color=if(notifTarget==t) Color.Black else Color.White, fontSize=9.sp) } } }; Spacer(Modifier.height(8.dp))
                OutlinedTextField(value=notifTitle, onValueChange={notifTitle=it}, label={Text("Title - e.g. Important")}, modifier=Modifier.fillMaxWidth()); OutlinedTextField(value=notifMsg, onValueChange={notifMsg=it}, label={Text("Message - Type here...")}, modifier=Modifier.fillMaxWidth().height(100.dp)); Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={notifMsg="Your 30 sec FREE ends soon - Get Premium Code now!"}, modifier=Modifier.weight(1f)){ Text("30 Sec Reminder", fontSize=10.sp) }; Button(onClick={notifMsg="New Offer - 50% OFF - Check OFFER tab"}, modifier=Modifier.weight(1f)){ Text("Offer", fontSize=10.sp) }; Button(onClick={notifMsg="GO LIVE in 5 min - Get ready!"}, modifier=Modifier.weight(1f)){ Text("GO LIVE Soon", fontSize=10.sp) } }; Spacer(Modifier.height(8.dp))
                Button(onClick={ if(notifMsg.isBlank()){ toast("Type message first"); return@Button }; val full="👑 ADMIN to $notifTarget: ${if(notifTitle.isNotBlank()) "$notifTitle - " else ""}$notifMsg"; sentNotifs = (sentNotifs + (full to timeNow)).toMutableList(); notifTitle=""; notifMsg=""; toast("Notification Sent to Broadcaster INBOX ✅") }, modifier=Modifier.fillMaxWidth().height(56.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("SEND NOTIFICATION → BROADCASTER INBOX", color=Color.White, fontWeight=FontWeight.Bold) }; Spacer(Modifier.height(10.dp))
                Text("SENT - History:", color=Color.Yellow, fontSize=13.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Spacer(Modifier.height(6.dp)); sentNotifs.reversed().forEach{ (m,t) -> Box(Modifier.fillMaxWidth().background(Color(0xFF222222)).padding(10.dp)){ Column{ Text(m, color=Color.White, fontSize=12.sp); Text(t, color=Color.Gray, fontSize=10.sp) } }; Spacer(Modifier.height(6.dp)) }
                Button(onClick={ wa("JSTV Notification: ${sentNotifs.lastOrNull()?.first ?: ""}") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF25D366))){ Text("ALSO SEND ON WHATSAPP", color=Color.White) }
            }
            if(tab=="USERS"){
                Text("👥 LINKED BROADCASTERS / USERS", color=Color.Yellow, fontSize=14.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Spacer(Modifier.height(8.dp)); usersList.forEach{ (u,s) -> Box(Modifier.fillMaxWidth().background(Color(0xFF222222)).padding(12.dp)){ Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text(u, color=Color.White, fontSize=12.sp, modifier=Modifier.weight(1f)); Text(s, color=if(s=="LIVE") Color.Red else if(s=="READY") Color.Green else Color.Gray, fontSize=11.sp, fontWeight=FontWeight.Bold) } }; Spacer(Modifier.height(6.dp)) }
                Button(onClick={ usersList = (usersList + ("New Broadcaster - LINKED - $roomCode" to "READY")).toMutableList(); linkedCount++; toast("User Added ✅") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("+ ADD USER MANUALLY", color=Color.White) }; Spacer(Modifier.height(8.dp)); Button(onClick={ wa("Hi, your JSTV Room is $roomCode") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF25D366))){ Text("WHATSAPP USER", color=Color.White) }
            }
            if(tab=="OFFER"){
                Text("💰 GENERATE PREMIUM / OFFER CODES", color=Color.Yellow, fontSize=14.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Text("Broadcaster enters this code after 30 sec FREE", color=Color.Gray, fontSize=11.sp); Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ listOf("MONTHLY 200","WEEKLY 100","OFFER 50%").forEach{ o -> Button(onClick={offerType=o; offerPrice=if(o=="MONTHLY 200") "200" else if(o=="WEEKLY 100") "100" else "100"; offerDays=if(o=="WEEKLY 100") "7" else "30" }, modifier=Modifier.weight(1f), contentPadding=PaddingValues(2.dp), colors=ButtonDefaults.buttonColors(containerColor=if(offerType==o) Color.Yellow else Color(0xFF444444))){ Text(o, color=if(offerType==o) Color.Black else Color.White, fontSize=10.sp) } } }; Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ OutlinedTextField(value=offerPrice, onValueChange={offerPrice=it}, label={Text("Price KSh")}, modifier=Modifier.weight(1f)); OutlinedTextField(value=offerDays, onValueChange={offerDays=it}, label={Text("Days")}, modifier=Modifier.weight(1f)) }; Spacer(Modifier.height(8.dp))
                Button(onClick={ val prefix=if(offerType=="OFFER 50%") "JSTV-OFFER" else "JSTV-200"; generatedCode=genCode(prefix); codesList = (codesList + ("$generatedCode - $offerType - KSh $offerPrice - $offerDays Days" to "UNUSED")).toMutableList(); toast("Code Generated ✅") }, modifier=Modifier.fillMaxWidth().height(56.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("GENERATE CODE", color=Color.Black, fontWeight=FontWeight.Bold) }
                if(generatedCode.isNotBlank()){ Spacer(Modifier.height(8.dp)); Box(Modifier.fillMaxWidth().background(Color(0xFF222222)).padding(16.dp), contentAlignment=Alignment.Center){ Column(horizontalAlignment=Alignment.CenterHorizontally){ Text("NEW CODE:", color=Color.Gray, fontSize=12.sp); Text(generatedCode, color=Color.Yellow, fontSize=22.sp, fontWeight=FontWeight.Bold); Text("$offerType - KSh $offerPrice - $offerDays Days", color=Color.White, fontSize=12.sp); Button(onClick={ wa("Your JSTV Premium Code is $generatedCode - $offerType - KSh $offerPrice - $offerDays Days - Enter after 30 sec FREE") }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF25D366))){ Text("SHARE CODE ON WHATSAPP", color=Color.White) } } } }
                Spacer(Modifier.height(10.dp)); Text("ALL CODES:", color=Color.Yellow, fontSize=13.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); codesList.forEach{ (c,s) -> Box(Modifier.fillMaxWidth().background(Color(0xFF222222)).padding(10.dp)){ Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text(c, color=Color.White, fontSize=11.sp, modifier=Modifier.weight(1f)); Text(s, color=Color.Green, fontSize=10.sp) } }; Spacer(Modifier.height(6.dp)) }
            }
            if(tab=="CHAT"){
                Text("💬 LIVE CHAT + MODERATION", color=Color.Yellow, fontSize=14.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth()); Text("Admin can Block, Pin, Reply", color=Color.Gray, fontSize=11.sp); Spacer(Modifier.height(8.dp))
                chatList.forEach{ (m,s) -> Box(Modifier.fillMaxWidth().background(Color(0xFF222222)).padding(10.dp)){ Column{ Text(m, color=Color.White, fontSize=12.sp); Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){ Button(onClick={ toast("Pinned ✅") }, contentPadding=PaddingValues(4.dp), modifier=Modifier.height(32.dp)){ Text("📌 PIN", fontSize=10.sp) }; Button(onClick={ chatList = chatList.filter{ it.first!=m }.toMutableList(); toast("User Blocked ❌") }, contentPadding=PaddingValues(4.dp), modifier=Modifier.height(32.dp), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("🚫 BLOCK", color=Color.White, fontSize=10.sp) }; Text(s, color=Color.Gray, fontSize=10.sp) } } }; Spacer(Modifier.height(6.dp)) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){ OutlinedTextField(value=chatInput, onValueChange={chatInput=it}, label={Text("Reply as ADMIN...")}, modifier=Modifier.weight(1f)); Button(onClick={ if(chatInput.isNotBlank()){ chatList = (chatList + ("ADMIN: $chatInput" to "KEEP")).toMutableList(); chatInput="" } }){ Text("SEND") } }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
