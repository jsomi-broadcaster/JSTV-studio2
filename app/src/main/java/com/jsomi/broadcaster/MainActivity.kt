package com.jsomi.broadcaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BroadcasterFull() }
    }
}

@Composable
fun BroadcasterFull() {
    var isLive by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("Waiting for Studio...") }
    var isPremium by remember { mutableStateOf(false) }
    var price by remember { mutableStateOf("200") }
    var quality by remember { mutableStateOf("720p HD") }
    var allowChat by remember { mutableStateOf(true) }
    var bannedCount by remember { mutableStateOf(0) }
    var myName by remember { mutableStateOf("Viewer") }
    var isLocked by remember { mutableStateOf(false) }
    var isPremiumUnlocked by remember { mutableStateOf(false) }
    var enteredCode by remember { mutableStateOf("") }
    var codeMessage by remember { mutableStateOf("") }
    var adminWhatsApp by remember { mutableStateOf("254763052911") }

    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance().getReference("jstv/control").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                isLive = s.child("isLive").getValue(Boolean::class.java) ?: false
                title = s.child("streamTitle").getValue(String::class.java) ?: title
                isPremium = s.child("isPremium").getValue(Boolean::class.java) ?: false
                price = s.child("premiumPrice").getValue(String::class.java) ?: "200"
                quality = s.child("quality").getValue(String::class.java) ?: "720p HD"
                allowChat = s.child("allowChat").getValue(Boolean::class.java) ?: true
                bannedCount = s.child("bannedUsers").childrenCount.toInt()
                adminWhatsApp = s.child("adminWhatsApp").getValue(String::class.java) ?: "254763052911"
                val locked = mutableListOf<String>()
                s.child("lockedUsers").children.forEach { locked.add(it.getValue(String::class.java) ?: "") }
                isLocked = locked.contains(myName)
            }
            override fun onCancelled(e: DatabaseError) {}
        })
    }

    fun checkCode() {
        if (enteredCode.isBlank()) { codeMessage = "Enter code first"; return }
        FirebaseDatabase.getInstance().getReference("jstv/codes").child(enteredCode.trim().uppercase())
            .get().addOnSuccessListener { snap ->
                val isLockedCode = snap.child("isLocked").getValue(Boolean::class.java) ?: false
                if (!snap.exists()) codeMessage = "Wrong Code - WhatsApp Admin 0763052911"
                else if (isLockedCode) codeMessage = "This Code is LOCKED by Admin"
                else {
                    isPremiumUnlocked = true
                    codeMessage = "UNLOCKED! Premium Access Granted"
                    FirebaseDatabase.getInstance().getReference("jstv/codes").child(enteredCode.trim().uppercase()).child("isUsed").setValue(true)
                }
            }
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
            Text("JSTV BROADCASTER", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(if (isLive) "LIVE - Controlled by Studio" else "OFFLINE - Waiting for Studio GO LIVE", color = if (isLive) Color.Red else Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            if (isLocked) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.Red)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("YOU ARE LOCKED BY ADMIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Contact Admin WhatsApp: 0763052911", color = Color.White, fontSize = 13.sp)
                    }
                }
            } else {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(if (isPremium) "PREMIUM KES $price" else "FREE STREAM", color = if (isPremium) Color(0xFFFFC107) else Color.Green, fontWeight = FontWeight.Bold)
                        Text("Quality: $quality | Chat: ${if (allowChat) "ON" else "OFF"} | Banned: $bannedCount", color = Color.White, fontSize = 13.sp)
                        Text(if (isLive) "You are LIVE!" else "Press GO LIVE in Studio App", color = Color.Gray, fontSize = 13.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (isPremium && !isPremiumUnlocked) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A00))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("PREMIUM LOCKED", color = Color(0xFFFFC107), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("This stream is Premium - KES $price", color = Color.White)
                            Text("1. Pay M-Pesa 522522 JSTV", color = Color.Gray, fontSize = 13.sp)
                            Text("2. Get Code on WhatsApp 0763052911", color = Color.Gray, fontSize = 13.sp)
                            Text("3. Enter Code below", color = Color.Gray, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(value = myName, onValueChange = { myName = it }, label = { Text("Your Name") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = enteredCode, onValueChange = { enteredCode = it.uppercase() }, label = { Text("Enter Code e.g. JSTV-AB12") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White))
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { checkCode() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), modifier = Modifier.fillMaxWidth()) { Text("UNLOCK WITH CODE") }
                            if (codeMessage.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(codeMessage, color = if (codeMessage.contains("UNLOCKED")) Color.Green else Color.Red, fontWeight = FontWeight.Bold)
                            }
                            Text("No Code? WhatsApp Admin: 0763052911", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                } else {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(if (isPremium) "PREMIUM UNLOCKED - Enjoy!" else "FREE ACCESS - Enjoy!", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Stream Area - Controlled by Studio", color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
