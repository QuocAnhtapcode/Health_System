package com.example.healthsystem.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthsystem.R
import com.example.healthsystem.model.NotificationItem
import com.example.healthsystem.model.SensorData
import com.google.firebase.database.*

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val _sensorData = MutableLiveData<SensorData>()
    val sensorData: LiveData<SensorData> get() = _sensorData

    private val _historyData = MutableLiveData<Map<String, SensorData>>()
    val historyData: LiveData<Map<String, SensorData>> get() = _historyData

    private val _notifications = MutableLiveData<MutableList<NotificationItem>>()
    val notifications: LiveData<MutableList<NotificationItem>> get() = _notifications

    private val firebaseUrl =
        "https://iot-ptit-fc1d1-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val dbRef = FirebaseDatabase.getInstance(firebaseUrl).getReference("sensorData")

    private var previousData: SensorData? = null
    private var notificationId = 0

    init {
        createNotificationChannel()
        observeSensorData()
    }

    private fun observeSensorData() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val newData = SensorData(
                        heartRate = snapshot.child("heartRate").getValue(Int::class.java) ?: 0,
                        temperature = snapshot.child("temperature").getValue(Float::class.java) ?: 0f,
                        roomTemperature = snapshot.child("roomTemperature").getValue(Float::class.java) ?: 0f,
                        humidity = snapshot.child("humidity").getValue(Float::class.java) ?: 0f,
                    )

                    if (newData != previousData) {
                        _sensorData.value = newData
                        checkThresholds(newData)
                        previousData = newData
                    } else {
                        Log.d("SensorViewModel", "Dữ liệu không thay đổi, không kiểm tra lại.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorViewModel", "Lỗi khi lắng nghe sensorData: ${error.message}")
            }
        })
    }

    fun fetchChartData() {
        val historyRef = dbRef.child("history")

        historyRef.limitToLast(30).get()
            .addOnSuccessListener { snapshot ->
                val result = mutableMapOf<String, SensorData>()
                for (child in snapshot.children) {
                    val record = child.getValue(SensorData::class.java)
                    if (record != null) {
                        result[child.key ?: ""] = record
                    }
                }
                _historyData.value = result.toSortedMap()
            }
            .addOnFailureListener {
                Log.e("SensorViewModel", "Lỗi khi lấy history: ${it.message}")
            }
    }

    fun checkThresholds(data: SensorData) {
        val list = _notifications.value ?: mutableListOf()

        fun addNoti(title: String, content: String, color: Int) {
            list.add(NotificationItem(title, content, color))
            showSystemNotification(title, content)
        }

        if (data.heartRate > 95 && (previousData?.heartRate ?: 0) != data.heartRate) {
            addNoti("Nhịp tim tăng cao", "Nhịp tim hiện tại là ${data.heartRate} bpm", R.color.red_dot)
        }
        if (data.temperature > 37.5  && (previousData?.temperature ?: 0) != data.temperature) {
            addNoti("Sốt nhẹ", "Nhiệt độ cơ thể là ${data.temperature}°C", R.color.purple_dot)
        }
        if (data.roomTemperature > 28  && (previousData?.roomTemperature ?: 0) != data.roomTemperature) {
            addNoti("Nhiệt độ phòng cao", "Phòng hiện tại ${data.roomTemperature}°C", R.color.yellow_dot)
        }
        if (data.humidity < 45  && (previousData?.humidity ?: 0) != data.humidity) {
            addNoti("Độ ẩm thấp", "Độ ẩm hiện tại chỉ ${data.humidity}%", R.color.blue_dot)
        }

        _notifications.postValue(list)
    }

    private fun showSystemNotification(title: String, content: String) {
        val context = getApplication<Application>()
        val builder = NotificationCompat.Builder(context, "health_alerts")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId++, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Health Alerts"
            val desc = "Thông báo khi các chỉ số vượt ngưỡng"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("health_alerts", name, importance).apply {
                description = desc
            }
            val manager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
