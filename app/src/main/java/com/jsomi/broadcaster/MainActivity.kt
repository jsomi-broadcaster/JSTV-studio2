package com.jsomi.studioadmin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

data class PremiumCode(val code: String = "", val phone: String = "", val price: String = "200", val isUsed: Boolean = false, val isLocked: Boolean = false)
data class ChatUser(val name: String = "", val msg: String = "")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FullStudio() }
    }
}

@Composable
fun FullStudio() {
    val context = LocalContext.current
    val db = FirebaseDatabase.getInstance().getReference("jstv")
    val controlRef = db.child("control")
    val codesRef = db.child("codes")
    var tab by remember { mutableStateOf(0) }
    var isLive by remember { mutableStateOf(false) }
    var viewers by remember { mutableStateOf(124) }
    var streamTitle by remember { mutableStateOf("JSTV Live - Evening Show") }
    var rtmpUrl by remember { mutableStateOf("rtmp://a.rtmp.youtube.com/live2") }
    var streamKey by remember { mutableStateOf("live_123456") }
    var quality by remember { mutableStateOf("720p HD") }
    var allowChat by remember { mutableStateOf(true) }
    var slowMode by remember { mutableStateOf(false) }
    var autoRecord by remember { mutableStateOf(true) }
    var isPremium by remember { mutableStateOf(false) }
    var premiumPrice by remember { mutableStateOf("200") }
    var premiumViewers by remember { mutableStateOf(0) }
    var adminWhatsApp by remember { mutableStateOf("254763052911") }
    var chatMessages = remember { mutableStateListOf(ChatUser("John", "Hello JSTV!"), ChatUser("Mary", "Great show!"), ChatUser("Peter", "Love this!")) }
    var bannedUsers = remember { mutableStateListOf<String>() }
    var lockedUsers = remember { mutableStateListOf<String>() }
    var newAdminMsg by remember { mutableStateOf("") }
    var codes = remember { mutableStateListOf<PremiumCode>() }
    var newCodePhone by remember { mutableStateOf("") }
    var newCodePrice by remember { mutableStateOf("200") }

    fun syncControl() {
        val map = mapOf("isLive" to isLive, "streamTitle" to streamTitle, "rtmpUrl" to rtmpUrl, "streamKey" to streamKey, "quality" to quality, "isPremium" to isPremium, "premiumPrice" to premiumPrice, "allowChat" to allowChat, "slowMode" to slowMode, "autoRecord" to autoRecord, "bannedUsers" to bannedUsers.toList(), "lockedUsers" to lockedUsers.toList(), "viewers" to viewers, "premiumViewers" to premiumViewers, "adminWhatsApp" to adminWhatsApp)
        controlRef.setValue(map)
    }
    fun generateCode() {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        var code = "JSTV-"
        repeat(4) { code += chars[Random.nextInt(chars.length)] }
        val newCode = PremiumCode(code = code, phone = newCodePhone.ifBlank { "Any User" }, price = newCodePrice)
        codes.add(newCode); codesRef.child(code).setValue(newCode); newCodePhone = ""; syncControl()
    }
    fun shareCode(code: PremiumCode) {
        val text = "JSTV PREMIUM ACCESS\nCode: ${code.code}\nPrice: KES ${code.price}\nWhatsApp Admin: https://wa.me/$adminWhatsApp - 0763052911"
        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) }
        context.startActivity(Intent.createChooser(intent, "Share Premium Code"))
    }
    fun openWhatsApp(code: PremiumCode) {
        val text = "Hello JSTV! I want Premium. My Code: ${code.code} Price: KES ${code.price}"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${adminWhatsApp}?text=${Uri.encode(text)}")))
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F)).padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("JSTV STUDIO", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Card(colors = CardDefaults.cardColors(containerColor = if (isLive) Color.Red else Color.Gray), shape = RoundedCornerShape(20.dp)) { Text(if (isLive) "LIVE" else "OFFLINE", color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), fontWeight = FontWeight.Bold) }
            }
            Text(if (isPremium) "PREMIUM KES $premiumPrice - STUDIO CONTROL" else "FREE STREAM - STUDIO CONTROL", color = if (isPremium) Color(0xFFFFC107) else Color.Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Viewers: $viewers | Premium: $premiumViewers | Banned: ${bannedUsers.size} | Locked: ${lockedUsers.size} | Codes: ${codes.size}", color = Color.Gray, fontSize = 11.sp)
            Text("WhatsApp Admin: 0763052911", color = Color(0xFF25D366), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf("Control", "Chat", "Premium", "Codes", "Settings").forEachIndexed { i, t -> Button(onClick = { tab = i }, colors = ButtonDefaults.buttonColors(containerColor = if (tab == i) Color.Red else Color(0xFF333333)), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)) { Text(t, fontSize = 11.sp, color = Color.White) } }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                if (tab == 0) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Studio Control", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                OutlinedTextField(value = streamTitle, onValueChange = { streamTitle = it }, label = { Text("Stream Title") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { isLive = true; syncControl() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), modifier = Modifier.fillMaxWidth()) { Text("GO LIVE") }
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(onClick = { isLive = false; syncControl() }, modifier = Modifier.fillMaxWidth()) { Text("END STREAM") }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Button(onClick = { viewers += 5; syncControl() }) { Text("+ Viewers") }; Button(onClick = { if (viewers > 0) viewers -= 5; syncControl() }) { Text("- Viewers") } }
                            }
                        }
                    }
                }
                if (tab == 1) {
                    item { Text("Live Chat - Studio Moderation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp); Row(verticalAlignment = Alignment.CenterVertically) { Text("Allow Chat", color = Color.White, modifier = Modifier.weight(1f)); Switch(checked = allowChat, onCheckedChange = { allowChat = it; syncControl() }) } }
                    items(chatMessages) { user ->
                        Card(colors = CardDefaults.cardColors(containerColor = if (lockedUsers.contains(user.name)) Color(0xFF4A0000) else Color(0xFF1E1E1E)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("${user.name}: ${user.msg} ${if (lockedUsers.contains(user.name)) "LOCKED" else ""}", color = Color.White)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(onClick = { chatMessages.remove(user); bannedUsers.add(user.name); syncControl() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Ban", fontSize = 11.sp) }
                                    if (lockedUsers.contains(user.name)) Button(onClick = { lockedUsers.remove(user.name); syncControl() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Unlock", fontSize = 11.sp) } else Button(onClick = { lockedUsers.add(user.name); syncControl() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Lock", fontSize = 11.sp) }
                                    Button(onClick = { chatMessages.remove(user) }, contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Delete", fontSize = 11.sp) }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(14.dp)); Text("Banned Users (${bannedUsers.size})", color = Color.Red, fontWeight = FontWeight.Bold) }
                    items(bannedUsers) { name ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3E0000)), modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) { Text(name, color = Color.White, modifier = Modifier.weight(1f)); Button(onClick = { bannedUsers.remove(name); chatMessages.add(ChatUser(name, "Unbanned")); syncControl() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))) { Text("UNBAN", fontSize = 11.sp) } }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(14.dp)); Text("Locked Users (${lockedUsers.size})", color = Color(0xFFFF8F00), fontWeight = FontWeight.Bold) }
                    items(lockedUsers) { name ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF4A2C00)), modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) { Text(name, color = Color.White, modifier = Modifier.weight(1f)); Button(onClick = { lockedUsers.remove(name); syncControl() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))) { Text("UNLOCK", fontSize = 11.sp) } }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(12.dp)); OutlinedTextField(value = newAdminMsg, onValueChange = { newAdminMsg = it }, label = { Text("Send as ADMIN...") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)); Button(onClick = { if (newAdminMsg.isNotBlank()) { chatMessages.add(ChatUser("ADMIN", newAdminMsg)); newAdminMsg = "" } }, modifier = Modifier.fillMaxWidth()) { Text("Send Admin Message") } }
                }
                if (tab == 2) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Premium Control - Studio", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) { Text(if (isPremium) "Premium ON" else "Premium OFF", color = Color.White, modifier = Modifier.weight(1f)); Switch(checked = isPremium, onCheckedChange = { isPremium = it; syncControl() }) }
                                OutlinedTextField(value = premiumPrice, onValueChange = { premiumPrice = it }, label = { Text("Price KES") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                Text("Premium Viewers: $premiumViewers", color = Color.White); Text("Revenue: KES ${premiumViewers * (premiumPrice.toIntOrNull() ?: 0)}", color = Color.Green, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { Button(onClick = { premiumViewers++; syncControl() }) { Text("+1 Paid") }; Button(onClick = { premiumViewers = 0; syncControl() }) { Text("Reset") } }
                                Text("M-Pesa: 522522 - JSTV | WhatsApp: 0763052911", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
                if (tab == 3) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("WhatsApp Premium Codes - Studio", color = Color(0xFF25D366), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text("Generate -> Share via WhatsApp 0763052911", color = Color.Gray, fontSize = 12.sp)
                                OutlinedTextField(value = newCodePhone, onValueChange = { newCodePhone = it }, label = { Text("Phone / Name for code") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                OutlinedTextField(value = newCodePrice, onValueChange = { newCodePrice = it }, label = { Text("Price KES") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                Button(onClick = { generateCode() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), modifier = Modifier.fillMaxWidth()) { Text("GENERATE NEW CODE") }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp)); Text("All Codes (${codes.size})", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    items(codes) { code ->
                        Card(colors = CardDefaults.cardColors(containerColor = if (code.isLocked) Color(0xFF4A0000) else if (code.isUsed) Color(0xFF1B5E20) else Color(0xFF1E1E1E)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(code.code, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text("For: ${code.phone} | KES ${code.price} | ${if (code.isLocked) "LOCKED" else if (code.isUsed) "USED" else "ACTIVE"}", color = Color.Gray, fontSize = 12.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(onClick = { shareCode(code) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("WhatsApp", fontSize = 11.sp) }
                                    Button(onClick = { openWhatsApp(code) }, contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Share", fontSize = 11.sp) }
                                    val idx = codes.indexOf(code)
                                    if (code.isLocked) Button(onClick = { codes[idx] = code.copy(isLocked = false); codesRef.child(code.code).child("isLocked").setValue(false) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Unlock", fontSize = 11.sp) } else Button(onClick = { codes[idx] = code.copy(isLocked = true); codesRef.child(code.code).child("isLocked").setValue(true) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8F00)), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Lock", fontSize = 11.sp) }
                                    Button(onClick = { codes.remove(code); codesRef.child(code.code).removeValue() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), contentPadding = PaddingValues(horizontal = 8.dp)) { Text("Delete", fontSize = 11.sp) }
                                }
                            }
                        }
                    }
                }
                if (tab == 4) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Studio Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                OutlinedTextField(value = rtmpUrl, onValueChange = { rtmpUrl = it }, label = { Text("RTMP URL") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                OutlinedTextField(value = streamKey, onValueChange = { streamKey = it }, label = { Text("Stream Key") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                                Text("Quality: $quality", color = Color.White, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf("480p", "720p HD", "1080p Full HD").forEach { q -> Button(onClick = { quality = q }) { Text(q, fontSize = 11.sp) } } }
                                Row(verticalAlignment = Alignment.CenterVertically) { Text("Auto Record", color = Color.White, modifier = Modifier.weight(1f)); Switch(checked = autoRecord, onCheckedChange = { autoRecord = it }) }
                                Row(verticalAlignment = Alignment.CenterVertically) { Text("Allow Chat", color = Color.White, modifier = Modifier.weight(1f)); Switch(checked = allowChat, onCheckedChange = { allowChat = it }) }
                                Button(onClick = { syncControl() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))) { Text("SAVE STUDIO SETTINGS") }
                            }
                        }
                    }
                }
            }
        }
    }
}
