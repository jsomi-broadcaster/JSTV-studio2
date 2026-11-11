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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AdminApp() } }
    }
}
@Composable
fun AdminApp() {
    val ctx = LocalContext.current
    var roomCode by remember { mutableStateOf("JSTV-6638") }
    var isLive by remember { mutableStateOf(false) }
    var totalMoney by remember { mutableStateOf(0) }
    var codes by remember { mutableStateOf(mutableListOf("JSTV-200-1234 - Daily - 200", "JSTV-1000-5678 - Monthly - 1000")) }
    var newCodeType by remember { mutableStateOf("Daily 200") }
    var newCode by remember { mutableStateOf("") }
    var importText by remember { mutableStateOf("") }
    var broadcasters by remember { mutableStateOf(listOf("Broadcaster 1 - LINKED - JSTV-6638")) }
    fun toast(m:String){ Toast.makeText(ctx,m,Toast.LENGTH_SHORT).show() }
    fun wa(msg:String){ val i=Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254763052911?text="+Uri.encode(msg))); try{ctx.startActivity(i)}catch(e:Exception){} }

    Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment=Alignment.CenterHorizontally){
        Text("👑 JSTV", color=Color.Yellow, fontSize=42.sp, fontWeight=FontWeight.Bold)
        Text("STUDIO ADMIN ONLY", color=Color.White, fontSize=16.sp, fontWeight=FontWeight.Bold)
        Text("RED + GOLD = ADMIN", color=Color.Yellow, fontSize=11.sp)
        Spacer(Modifier.height(12.dp))
        Text("ROOM CODE FOR BROADCASTERS:", color=Color.Yellow, fontSize=14.sp, fontWeight=FontWeight.Bold)
        Text(roomCode, color=Color.Yellow, fontSize=32.sp, fontWeight=FontWeight.Bold)
        Text("Give this code to CYAN Broadcaster phone to LINK", color=Color.White, fontSize=12.sp)
        Spacer(Modifier.height(8.dp))
        Button(onClick={ roomCode="JSTV-"+(1000..9999).random(); toast("New Room: $roomCode") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF8B0000))){ Text("GENERATE NEW ROOM CODE", color=Color.White) }
        Spacer(Modifier.height(8.dp))
        Text(if(isLive) "● LIVE" else "● OFFLINE", color=if(isLive) Color.Green else Color.Gray, fontSize=16.sp, fontWeight=FontWeight.Bold)
        Box(Modifier.fillMaxWidth().height(140.dp).background(Color.DarkGray), contentAlignment=Alignment.Center){ Text(if(isLive) "ADMIN PREVIEW - LIVE" else "ADMIN PREVIEW - OFFLINE", color=Color.White) }
        Spacer(Modifier.height(10.dp))
        Button(onClick={ isLive=true; toast("Demo Started - FREE") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("START DEMO - FREE", color=Color.Black) }
        Button(onClick={ isLive=true; toast("Real Live Started") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("START REAL LIVE - GENERATE PREMIUM CODE", color=Color.White) }
        Button(onClick={ isLive=false; toast("Live Stopped") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("STOP LIVE", color=Color.White) }
        Spacer(Modifier.height(14.dp))
        Text("💰 MONEY RECORD - TOTAL: KSH $totalMoney", color=Color.Yellow, fontSize=16.sp, fontWeight=FontWeight.Bold)
        Text("Daily 200 | Monthly 1000 | 3 Months 2500 | Year 8000", color=Color.White, fontSize=12.sp)
        Text("Paybill 516600 Acc 5600935001", color=Color.White, fontSize=12.sp)
        Spacer(Modifier.height(8.dp))
        Text("GENERATE PREMIUM CODE", color=Color.Yellow, fontSize=14.sp, fontWeight=FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceEvenly){
            listOf("Daily 200","Monthly 1000","3 Months 2500","Year 8000").forEach{ t -> Button(onClick={newCodeType=t}, colors=ButtonDefaults.buttonColors(containerColor=if(newCodeType==t) Color.Yellow else Color.DarkGray)){ Text(t, color=if(newCodeType==t) Color.Black else Color.White, fontSize=10.sp) } }
        }
        Button(onClick={
            val price = when(newCodeType){ "Daily 200"->200; "Monthly 1000"->1000; "3 Months 2500"->2500; else->8000 }
            newCode="JSTV-$price-"+(1000..9999).random()
            codes.add("$newCode - $newCodeType - $price")
            totalMoney+=price
            toast("Code: $newCode")
            wa("Your JSTV Premium Code: $newCode - $newCodeType - Paybill 516600 Acc 5600935001")
        }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Green)){ Text("GENERATE + WHATSAPP CODE") }
        if(newCode.isNotBlank()) Text("Last Code: $newCode", color=Color.Green, fontSize=13.sp)
        Spacer(Modifier.height(10.dp))
        Text("ALL CODES:", color=Color.White, fontSize=13.sp)
        codes.forEach{ Text(it, color=Color.Gray, fontSize=11.sp) }
        Spacer(Modifier.height(10.dp))
        Text("LINKED BROADCASTERS:", color=Color.White, fontSize=13.sp)
        broadcasters.forEach{ Text(it, color=Color.Green, fontSize=12.sp) }
        Spacer(Modifier.height(10.dp))
        Text("IMPORT / EXPORT CODES", color=Color.Yellow, fontSize=13.sp)
        OutlinedTextField(value=importText, onValueChange={importText=it}, label={Text("Paste codes to import")}, modifier=Modifier.fillMaxWidth())
        Button(onClick={ if(importText.isNotBlank()){ codes.add(importText); toast("Imported") } }, modifier=Modifier.fillMaxWidth()){ Text("IMPORT CODES TO THIS PHONE") }
        Button(onClick={ wa("JSTV Codes:\n"+codes.joinToString("\n")) }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF25D366))){ Text("EXPORT CODES VIA WHATSAPP") }
    }
}
