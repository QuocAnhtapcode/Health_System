package com.example.healthsystem.model

data class SensorHistory(
    val timestamp: Long = 0L,
    val temperature: Float = 0f,
    val heartRate: Int = 0,
    val humidity: Float = 0f,
    val roomTemperature: Float = 0f
)
