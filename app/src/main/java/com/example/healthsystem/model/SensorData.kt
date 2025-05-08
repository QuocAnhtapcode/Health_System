package com.example.healthsystem.model

data class SensorData(
    val temperature: Float = 0f,
    val heartRate: Int = 0,
    val roomTemperature: Float = 0f,
    val humidity: Float = 0f
)
