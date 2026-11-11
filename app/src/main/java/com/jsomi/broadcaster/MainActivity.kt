package com.jsomi.studioadmin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class PremiumCode(val code: String, val phone: String, val price: String, val plan: String, var active: Boolean = true)
data class ChatMsg(val name: String, val text: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StudioApp() }
    }
}

@Composable
fun StudioApp() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf(0) }

    // Control
    var rtmpUrl by remember { mutableStateOf("rtmp://a.rtmp.youtube.com/live2") }
    var streamKey by remember { mutableStateOf("") }
    var quality by remember { mutableStateOf("720p") }
    var autoRecord by remember { mutableStateOf(true) }
    var isLive by remember { mutableStateOf(false) }
    var viewers by remember { mutableStateOf(0) }

    // Chat
    val chatList = remember { mutableStateListOf(ChatMsg("System", "Welcome to JSTV Studio Chat")) }
    var chatInput by remember { mutableStateOf("") }

    // Premium - NEW RATES
    var isPremium by remember { mutableStateOf(true) }
    var selectedPlan by remember { mutableStateOf("Daily 200") }
    var selectedPrice by remember { mutableStateOf("200") }
    var premiumViewers by remember { mutableStateOf(0) }
    var revenue by remember { mutableStateOf(0) }

    // Codes - Custom price stays
    val codes = remember { mutableStateListOf<PremiumCode>() }
    var newCodePhone by remember { mutableStateOf("") }
    var newCodePrice by remember { mutableStateOf("200") }

    fun selectPlan(plan: String, price: String) {
        selectedPlan = plan
        selectedPrice = price
        newCodePrice = price
    }

    fun generateCode() {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        var code = "JSTV-"
        repeat(4) { code += chars[Random.nextInt(chars.length)] }
        val phone = if (newCodePhone.isBlank()) "Any User" else newCodePhone
        val price = if (newCodePrice.isBlank()) selectedPrice else newCodePrice
        codes.add(PremiumCode(code = code, phone = phone, price = price, plan = selectedPlan))
        newCodePhone = ""
    }

    fun shareCode(code: PremiumCode) {
        val text = "JSTV PREMIUM ACCESS\nCode: ${code.code}\nPlan: ${code.plan}\nPrice: KES ${code.price}\nPaybill: 516600 Acc 5600935001\nWhatsApp: 0763052911"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Share Code"))
    }

    fun openWhatsApp(code: PremiumCode) {
        val text = "Hello JSTV! I want Premium - ${code.plan} - KES ${code.price}. Code: ${code.code}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Open WhatsApp"))
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Text("JSTV STUDIO", color = Color.Yellow)
            Text(if (isPremium) "PREMIUM KES $selectedPrice - STUDIO" else "FREE - STUDIO", color = Color.Yellow)
            Text("Viewers: $viewers | Premium: $premiumViewers | Revenue: KES $revenue")
            Text("WhatsApp Admin: 0763052911 | Paybill 516600 Acc 5600935001")
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Control", "Chat", "Premium", "Codes", "Settings").forEachIndexed { index, name ->
                    Button(onClick = { tab = index }, colors = ButtonDefaults.buttonColors(containerColor = if (tab == index) Color.Yellow else Color.DarkGray)) {
                        Text(name, color = Color.Black)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                // TAB 0 - CONTROL
                if (tab == 0) {
                    item {
                        Card { Column(modifier = Modifier.padding(12.dp)) {
                            Text("Studio Control", color = Color.Yellow)
                            OutlinedTextField(value = rtmpUrl, onValueChange = { rtmpUrl = it }, label = { Text("RTMP URL") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = streamKey, onValueChange = { streamKey = it }, label = { Text("Stream Key") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Quality: $quality")
                            Row {
                                listOf("480p", "720p", "1080p").forEach { q ->
                                    Button(onClick = { quality = q }, modifier = Modifier.padding(2.dp)) { Text(q) }
                                }
                            }
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Checkbox(checked = autoRecord, onCheckedChange = { autoRecord = it })
                                Text("Auto Record")
                            }
                            Button(onClick = { isLive = !isLive }) { Text(if (isLive) "STOP LIVE" else "GO LIVE") }
                            Text(if (isLive) "● LIVE" else "○ OFFLINE")
                            Text("Paybill 516600 Acc 5600935001")
                        }}
                    }
                }
                // TAB 1 - CHAT
                if (tab == 1) {
                    item {
                        Card { Column(modifier = Modifier.padding(12.dp)) {
                            Text("Live Chat", color = Color.Yellow)
                            OutlinedTextField(value = chatInput, onValueChange = { chatInput = it }, label = { Text("Type message...") }, modifier = Modifier.fillMaxWidth())
                            Button(onClick = { if (chatInput.isNotBlank()) { chatList.add(ChatMsg("Studio", chatInput)); chatInput = "" } }) { Text("Send") }
                        }}
                    }
                    items(chatList) { m ->
                        Card(modifier = Modifier.padding(4.dp)) { Column(modifier = Modifier.padding(8.dp)) { Text(m.name, color = Color.Yellow); Text(m.text) } }
                    }
                }
                // TAB 2 - PREMIUM - NEW RATES
                if (tab == 2) {
                    item {
                        Card { Column(modifier = Modifier.padding(12.dp)) {
                            Text("Premium Control - Studio", color = Color.Yellow)
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Text("Premium ON")
                                Switch(checked = isPremium, onCheckedChange = { isPremium = it })
                            }
                            Text("Select Plan", color = Color.Yellow)
                            Row {
                                Button(onClick = { selectPlan("Daily 200", "200") }, modifier = Modifier.padding(2.dp)) { Text("Daily 200") }
                                Button(onClick = { selectPlan("Monthly 1000", "1000") }, modifier = Modifier.padding(2.dp)) { Text("Monthly 1000") }
                            }
                            Row {
                                Button(onClick = { selectPlan("3 Months 2500", "2500") }, modifier = Modifier.padding(2.dp)) { Text("3 Months 2500") }
                                Button(onClick = { selectPlan("Year 8000", "8000") }, modifier = Modifier.padding(2.dp)) { Text("Year 8000") }
                            }
                            Text("Selected: $selectedPlan", color = Color.Yellow)
                            Text("Price KES: $selectedPrice")
                            Text("Paybill 516600 Acc 5600935001")
                            Text("WhatsApp: 0763052911")
                            Text("Premium Viewers: $premiumViewers")
                            Text("Revenue: KES $revenue")
                            Row {
                                Button(onClick = { premiumViewers++; revenue += (selectedPrice.toIntOrNull() ?: 0) }, modifier = Modifier.padding(4.dp)) { Text("+1 Paid") }
                                Button(onClick = { premiumViewers = 0; revenue = 0 }, modifier = Modifier.padding(4.dp)) { Text("Reset") }
                            }
                        }}
                    }
                }
                // TAB 3 - CODES
                if (tab == 3) {
                    item {
                        Card { Column(modifier = Modifier.padding(12.dp)) {
                            Text("WhatsApp Premium Codes - Studio", color = Color.Yellow)
                            OutlinedTextField(value = newCodePhone, onValueChange = { newCodePhone = it }, label = { Text("Phone / Name for code") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = newCodePrice, onValueChange = { newCodePrice = it }, label = { Text("Price KES - Custom, you can type any") }, modifier = Modifier.fillMaxWidth())
                            Text("Current Plan: $selectedPlan - KES $selectedPrice")
                            Text("Tip: Tap a plan in Premium tab, then come here - price will follow. Or type custom price like 150 here.")
                            Button(onClick = { generateCode() }) { Text("GENERATE NEW CODE") }
                            Text("Paybill 516600 Acc 5600935001")
                        }}
                    }
                    items(codes) { code ->
                        Card(modifier = Modifier.padding(4.dp)) { Column(modifier = Modifier.padding(8.dp)) {
                            Text(code.code, color = Color.Yellow)
                            Text("For: ${code.phone} | ${code.plan} | KES ${code.price} | ${if (code.active) "ACTIVE" else "USED"}")
                            Row {
                                Button(onClick = { shareCode(code) }, modifier = Modifier.padding(2.dp)) { Text("Share") }
                                Button(onClick = { openWhatsApp(code) }, modifier = Modifier.padding(2.dp)) { Text("WhatsApp") }
                                Button(onClick = { codes.remove(code) }, modifier = Modifier.padding(2.dp)) { Text("Delete") }
                            }
                        }}
                    }
                }
                // TAB 4 - SETTINGS
                if (tab == 4) {
                    item {
                        Card { Column(modifier = Modifier.padding(12.dp)) {
                            Text("Studio Settings", color = Color.Yellow)
                            OutlinedTextField(value = rtmpUrl, onValueChange = { rtmpUrl = it }, label = { Text("RTMP URL") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = streamKey, onValueChange = { streamKey = it }, label = { Text("Stream Key") }, modifier = Modifier.fillMaxWidth())
                            Text("Quality: $quality")
                            Row {
                                listOf("480p", "720p", "1080p").forEach { q -> Button(onClick = { quality = q }, modifier = Modifier.padding(2.dp)) { Text(q) } }
                            }
                            Button(onClick = {}) { Text("Save Settings") }
                            Text("Paybill: 516600 Acc 5600935001 - Correct Paybill A")
                            Text("Daily 200 | Monthly 1000 | 3 Months 2500 | Year 8000")
                        }}
                    }
                }
            }
        }
    }
}
