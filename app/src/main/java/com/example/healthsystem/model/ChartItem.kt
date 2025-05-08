package com.example.healthsystem.model

import com.github.mikephil.charting.data.Entry

data class ChartItem(
    val title: String,
    val description: String,
    val entries: List<Entry>
)
