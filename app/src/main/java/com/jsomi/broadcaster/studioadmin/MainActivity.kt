package com.jsomi.admin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val prefs = getSharedPreferences("JSTV_KEYS", MODE_PRIVATE)

        val etYT: EditText = findViewById(R.id.etAdminYT)
        val etFB: EditText = findViewById(R.id.etAdminFB)
        val etTikTok: EditText = findViewById(R.id.etAdminTikTok)
        val etCustomUrl: EditText = findViewById(R.id.etAdminCustomUrl)
        val etCustomKey: EditText = findViewById(R.id.etAdminCustomKey)
        val btnSave: Button = findViewById(R.id.btnSaveAllKeys)
        val tvStatus: TextView = findViewById(R.id.tvAdminStatus)
        val btnYTStudio: Button = findViewById(R.id.btnYTStudio)
        val btnFBStudio: Button = findViewById(R.id.btnFBStudio)
        val btnTikTokStudio: Button = findViewById(R.id.btnTikTokStudio)

        etYT.setText(prefs.getString("YT", ""))
        etFB.setText(prefs.getString("FB", ""))
        etTikTok.setText(prefs.getString("TIKTOK", ""))
        etCustomUrl.setText(prefs.getString("CUSTOM_URL", ""))
        etCustomKey.setText(prefs.getString("CUSTOM_KEY", ""))

        btnSave.setOnClickListener {
            prefs.edit().putString("YT", etYT.text.toString().trim())
                .putString("FB", etFB.text.toString().trim())
                .putString("TIKTOK", etTikTok.text.toString().trim())
                .putString("CUSTOM_URL", etCustomUrl.text.toString().trim())
                .putString("CUSTOM_KEY", etCustomKey.text.toString().trim()).apply()
            tvStatus.text = "✅ All 4 Keys Saved - Broadcaster will use same keys"
            Toast.makeText(this, "All Keys Saved!", Toast.LENGTH_SHORT).show()
        }
        btnYTStudio.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://studio.youtube.com"))) }
        btnFBStudio.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/live/producer"))) }
        btnTikTokStudio.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/live"))) }
    }
}
