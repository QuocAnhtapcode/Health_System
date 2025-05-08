package com.example.healthsystem.util

interface VoiceCallback {
    fun onVoiceResult(result: String)
    fun onVoiceError(errorCode: Int)
}