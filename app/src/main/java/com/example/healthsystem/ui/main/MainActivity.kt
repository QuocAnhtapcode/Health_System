package com.example.healthsystem.ui.main

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.healthsystem.R
import com.example.healthsystem.databinding.ActivityMainBinding
import com.example.healthsystem.model.User
import com.example.healthsystem.util.VoiceCallback
import com.example.healthsystem.util.VoiceHelper
import com.example.healthsystem.viewmodel.DeviceViewmodel
import java.util.Locale
import android.app.AlertDialog
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.example.healthsystem.ui.login.LoginActivity

class MainActivity : AppCompatActivity(), VoiceCallback {

    lateinit var currentUser: User
    private lateinit var binding: ActivityMainBinding
    private lateinit var textToSpeech: TextToSpeech
    private val deviceViewmodel: DeviceViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)

        currentUser = intent.getSerializableExtra("user_data") as? User
            ?: User(name = "Guest", email = "guest@example.com")

        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.US
            }
        }

        VoiceHelper.init(this, this)

        binding.btnVoice.setOnClickListener {
            if (VoiceHelper.isMicOn()) {
                VoiceHelper.stopListening()
                binding.btnVoice.setImageResource(R.drawable.ic_listening)
            } else {
                VoiceHelper.startListening()
                binding.btnVoice.setImageResource(R.drawable.ic_mic)
                Toast.makeText(this, "🎤 Đang lắng nghe...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onVoiceResult(result: String) {
        VoiceHelper.stopListening()
        binding.btnVoice.setImageResource(R.drawable.ic_listening)

        when (parseVoiceCommand(result)) {
            ActionType.INCREASE -> raiseTemperature()
            ActionType.DECREASE -> lowerTemperature()
            else -> {
                Toast.makeText(this, "❌ Không nhận diện được lệnh!", Toast.LENGTH_SHORT).show()
                textToSpeech.speak("Sorry, I didn't understand your command", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

    }

    override fun onVoiceError(errorCode: Int) {
        Toast.makeText(this, "Lỗi mic: $errorCode", Toast.LENGTH_SHORT).show()
        VoiceHelper.stopListening()
        binding.btnVoice.setImageResource(R.drawable.ic_listening)
    }

    override fun onDestroy() {
        VoiceHelper.destroy()
        super.onDestroy()
    }

    private fun parseVoiceCommand(command: String): ActionType {
        val lower = command.lowercase()
        return when {
            listOf("cold", "raise", "increase", "up").any { it in lower } -> ActionType.INCREASE
            listOf("hot", "lower", "decrease", "down").any { it in lower } -> ActionType.DECREASE
            else -> ActionType.NONE
        }
    }

    private fun raiseTemperature() {
        Toast.makeText(this, "🔺 Tăng nhiệt độ!", Toast.LENGTH_SHORT).show()
        textToSpeech.speak("Increasing temperature", TextToSpeech.QUEUE_FLUSH, null, null)
        deviceViewmodel.increaseAC()
    }

    private fun lowerTemperature() {
        Toast.makeText(this, "🔻 Giảm nhiệt độ!", Toast.LENGTH_SHORT).show()
        textToSpeech.speak("Decreasing temperature", TextToSpeech.QUEUE_FLUSH, null, null)
        deviceViewmodel.decreaseAC()
    }

    enum class ActionType {
        INCREASE,
        DECREASE,
        NONE
    }

    fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Huỷ", null)
            .show()
    }

}
