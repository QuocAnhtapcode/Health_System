package com.example.healthsystem.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

object VoiceHelper {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    private lateinit var recognizerIntent: Intent

    private var callback: VoiceCallback? = null

    fun init(context: Context, voiceCallback: VoiceCallback) {
        if (speechRecognizer != null) return
        callback = voiceCallback

        recognizerIntent = Intent().apply {
            action = RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                isListening = false
                val spoken = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: ""
                Log.d("VoiceHelper", "Kết quả: $spoken")
                callback?.onVoiceResult(spoken)
            }

            override fun onError(error: Int) {
                isListening = false
                Log.e("VoiceHelper", "Lỗi: $error")
                callback?.onVoiceError(error)
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        if (!isListening) {
            speechRecognizer?.startListening(recognizerIntent)
            isListening = true
        }
    }

    fun stopListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
            isListening = false
        }
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
    }

    fun isMicOn(): Boolean = isListening
}

