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
data class ChatUser(val name: String = "", val msg: String = "", val isLocked: Boolean = false)

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
        val map = mapOf(
            "isLive" to isLive, "streamTitle" to streamTitle, "rtmpUrl" to rtmpUrl,
            "streamKey" to streamKey, "quality" to quality, "isPremium" to isPremium,
            "premiumPrice" to premiumPrice, "allowChat" to allowChat, "slowMode" to slowMode,
            "autoRecord" to autoRecord, "bannedUsers" to bannedUsers.toList(),
            "lockedUsers" to lockedUsers.toList(), "viewers" to viewers,
            "premiumViewers" to premiumViewers, "adminWhatsApp" to adminWhatsApp
        )
        controlRef.setValue(map)
    }

    fun generateCode() {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        var code = "JSTV-"
        repeat(4) { code += chars[Random.nextInt(chars.length)] }
        val newCode = PremiumCode(code = code, phone = newCodePhone.ifBlank { "Any User" }, price = newCodePrice, isUsed = false, isLocked = false)
        codes.add(newCode)
        codesRef.child(code).setValue(newCode)
        newCodePhone = ""
        syncControl()
    }

    fun openWhatsApp(code: PremiumCode) {
        val text = "Hello JSTV! I want Premium. My Code: ${code.code} Price: KES ${code.price}. Please unlock me."
        val url = "https://wa.me/${adminWhatsApp}?text=${Uri.encode(text)}"
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun shareCode(code: PremiumCode) {
        val text = "🎬 JSTV PREMIUM ACCESS\nYour Code: ${code.code}\nPrice: KES ${code.price}\n1. Open JSTV Broadcaster App\n2. Enter this code to unlock Premium\n3. Or WhatsApp Admin: https://wa.me/$adminWhatsApp - 0763052911"
        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) }
        context.startActivity(Intent.createChooser(intent, "Share Premium Code"))
    }

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().background(Color
