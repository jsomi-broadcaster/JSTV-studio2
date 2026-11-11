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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AdminApp() } }
    }
}
@Composable
fun AdminApp(){
    val ctx = LocalContext.current
    var roomCode by remember { mutableStateOf("JSTV-6638") }
    var isLive by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("Daily") }
    var customer by remember { mutableStateOf("") }
    var lastCode by remember { mutableStateOf("JSTV-200-NBF9") }
    var importText by remember { mutableStateOf("") }
    var codes by remember { mutableStateOf(mutableListOf(
        CodeItem("JSTV-200-1A2B","Daily 200",200,"Demo User 1"),
        CodeItem("JSTV-1K-3C4D","Monthly 1000",1000,"Demo User 2"),
        CodeItem("JSTV-3M-5E6F","3 Months 2500",2500,"Demo User 3"),
        CodeItem("JSTV-YR-7G8H","Year 8000",8000,"Demo User 4"),
        CodeItem("JSTV-200-84C7","Daily 200",200,"Demo Customer")
    ))}
    fun toast(m:String){ Toast.makeText(ctx,m,Toast.LENGTH_SHORT).show() }
    fun wa(msg:String){ val i=Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/254763052911?text="+Uri.encode(msg))); try{ctx.startActivity(i)}catch(e:Exception){} }
    fun suffix():String{ val c="ABCDEFGHJKLMNPQRSTUVWXYZ0123456789"; return (1..4).map{ c[Random.nextInt(c.length)] }.joinToString("") }
    fun priceOf(s:String)=when(s){ "Daily"->200; "Monthly"->1000; "3M"->2500; else->8000 }
    fun prefixOf(s:String)=when(s){ "Daily"->"200"; "Monthly"->"1K"; "3M"->"3M"; else->"YR" }
    fun labelOf(s:String)=when(s){ "Daily"->"Daily 200"; "Monthly"->"Monthly 1000"; "3M"->"3 Months 2500"; else->"Year 8000" }
    val total = codes.sumOf{ it.price }

    Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment=Alignment.CenterHorizontally){
        Text("👑 JSTV", color=Color.Yellow, fontSize=42.sp, fontWeight=FontWeight.Bold)
        Text("STUDIO ADMIN ONLY", color=Color.White, fontSize=16.sp, fontWeight=FontWeight.Bold)
        Text("RED + GOLD = ADMIN", color=Color.Yellow, fontSize=11.sp)
        Spacer(Modifier.height(10.dp))
        Text("ROOM CODE FOR BROADCASTERS:", color=Color.Yellow, fontSize=14.sp, fontWeight=FontWeight.Bold)
        Text(roomCode, color=Color.Yellow, fontSize=34.sp, fontWeight=FontWeight.Bold)
        Text("Give this code to CYAN Broadcaster phone to LINK", color=Color.White, fontSize=12.sp)
        Button(onClick={ roomCode="JSTV-"+(1000..9999).random(); toast("New Room: $roomCode") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF8B0000))){ Text("GENERATE NEW ROOM CODE", color=Color.White) }
        Spacer(Modifier.height(10.dp))
        Text(if(isLive) "● LIVE" else "● OFFLINE", color=if(isLive) Color.Green else Color.Gray, fontWeight=FontWeight.Bold)
        Box(Modifier.fillMaxWidth().height(140.dp).background(Color(0xFF333333)), contentAlignment=Alignment.Center){ Text(if(isLive) "ADMIN PREVIEW - LIVE" else "ADMIN PREVIEW - OFFLINE", color=Color.White) }
        Spacer(Modifier.height(10.dp))
        Button(onClick={ isLive=true }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Yellow)){ Text("START DEMO - FREE", color=Color.Black) }
        Button(onClick={ isLive=true }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("START REAL LIVE - GENERATE PREMIUM CODE", color=Color.White, fontSize=13.sp) }
        Button(onClick={ isLive=false; toast("Live Stopped") }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color.Red)){ Text("STOP LIVE", color=Color.White) }
        Spacer(Modifier.height(16.dp))
        Text("💰 MONEY RECORD - TOTAL: KSH $total", color=Color.Yellow, fontSize=16.sp, fontWeight=FontWeight.Bold)
        Text("Daily 200 | Monthly 1000 | 3 Months 2500 | Year 8000", color=Color.White, fontSize=12.sp)
        Text("Paybill 516600 Acc 5600935001", color=Color.White, fontSize=12.sp)
        Spacer(Modifier.height(14.dp))
        Text("Customer Name", color=Color.White, fontSize=13.sp, modifier=Modifier.fillMaxWidth())
        OutlinedTextField(value=customer, onValueChange={customer=it}, label={Text("e.g. Demo User 1")}, modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(6.dp)){
            listOf("Daily","Monthly","3M","Year").forEach{ t ->
                Button(onClick={selected=t}, modifier=Modifier.weight(1f).height(44.dp), contentPadding=PaddingValues(0.dp), colors=ButtonDefaults.buttonColors(containerColor=if(selected==t) Color.Yellow else Color(0xFF444444))){ Text(t, color=if(selected==t) Color.Black else Color.White, fontSize=13.sp, fontWeight=FontWeight.Bold) }
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick={
            val c="JSTV-${prefixOf(selected)}-${suffix()}"; lastCode=c
            val cust=if(customer.isBlank()) "Customer" else customer
            codes.add(0, CodeItem(c, labelOf(selected), priceOf(selected), cust)); toast("Generated: $c"); customer=""
        }, modifier=Modifier.fillMaxWidth().height(52.dp), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("GENERATE CODE FOR ${labelOf(selected)}", color=Color.White, fontSize=15.sp, fontWeight=FontWeight.Bold) }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.fillMaxWidth().background(Color(0xFF444444)).padding(12.dp)){ Column{ Text("Last Code:", color=Color.White, fontSize=14.sp); Text(lastCode, color=Color.Yellow, fontSize=24.sp, fontWeight=FontWeight.Bold) } }
        Spacer(Modifier.height(10.dp))
        Button(onClick={ wa("Hi! Your JSTV Premium Code is: $lastCode - ${labelOf(selected)}\nPaybill 516600 Acc 5600935001") }, modifier=Modifier.fillMaxWidth().height(48.dp), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text("WHATSAPP CODE TO CUSTOMER", color=Color.White, fontWeight=FontWeight.Bold, fontSize=13.sp) }
        Spacer(Modifier.height(16.dp))
        Text("👥 5) ALL CODES", color=Color.Yellow, fontSize=18.sp, fontWeight=FontWeight.Bold, modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        codes.forEachIndexed{ idx, item ->
            Box(Modifier.fillMaxWidth().background(Color(0xFF444444)).padding(12.dp)){
                Column{
                    Text("${item.code} - ${item.type}", color=Color.Yellow, fontSize=16.sp, fontWeight=FontWeight.Bold)
                    Text("Customer: ${item.customer} | ${if(item.active) "✅ ACTIVE" else "🚫 BLOCKED"}", color=Color.White, fontSize=13.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){
                        Button(onClick={ val nl=codes.toMutableList(); nl[idx]=item.copy(active=!item.active); codes=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text(if(item.active) "BLOCK" else "UNBLOCK", color=Color.White) }
                        Button(onClick={ val nl=codes.toMutableList(); nl.removeAt(idx); codes=nl }, colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text("DELETE", color=Color.White) }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
        Text("LINKED BROADCASTERS:", color=Color.White, fontSize=13.sp)
        Text("Broadcaster 1 - LINKED - $roomCode", color=Color.Green, fontSize=12.sp)
        Spacer(Modifier.height(10.dp))
        Text("IMPORT / EXPORT CODES", color=Color.Yellow, fontSize=13.sp, fontWeight=FontWeight.Bold)
        OutlinedTextField(value=importText, onValueChange={importText=it}, label={Text("Paste codes to import")}, modifier=Modifier.fillMaxWidth())
        Button(onClick={ if(importText.isNotBlank()){ codes.add(0, CodeItem(importText.take(20),"Imported",0,"Imported")); toast("Imported") } }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF7B61FF))){ Text("IMPORT CODES TO THIS PHONE", color=Color.White) }
        Button(onClick={ wa("JSTV Codes:\n"+codes.joinToString("\n"){ "${it.code} - ${it.type} - ${it.customer}" }) }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF00E600))){ Text("EXPORT CODES VIA WHATSAPP", color=Color.White) }
        Spacer(Modifier.height(20.dp))
    }
}
