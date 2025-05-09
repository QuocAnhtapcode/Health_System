package com.example.healthsystem.model

import androidx.annotation.ColorRes

data class NotificationItem(
    val title: String,
    val content: String,
    @ColorRes val colorResId: Int
)

