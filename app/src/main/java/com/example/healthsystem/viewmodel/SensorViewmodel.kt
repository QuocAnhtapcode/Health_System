package com.example.healthsystem.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthsystem.R
import com.example.healthsystem.model.NotificationItem
import com.example.healthsystem.model.SensorData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SensorViewModel : ViewModel() {

    private val _sensorData = MutableLiveData<SensorData>()
    val sensorData: LiveData<SensorData> get() = _sensorData

    private val _historyData = MutableLiveData<Map<String, SensorData>>()
    val historyData: LiveData<Map<String, SensorData>> get() = _historyData

    private val _notifications = MutableLiveData<MutableList<NotificationItem>>(mutableListOf())
    val notifications: LiveData<MutableList<NotificationItem>> get() = _notifications

    private val firebaseUrl = "https://iot-ptit-fc1d1-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val dbRef = FirebaseDatabase.getInstance(firebaseUrl).getReference("sensorData")

    private var previousData: SensorData? = null

    init {
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

        if (data.heartRate > 95 && (previousData?.heartRate ?: 0) != data.heartRate) {
            list.add(NotificationItem("Nhịp tim tăng cao", "Nhịp tim hiện tại là ${data.heartRate} bpm", R.color.red_dot))
        }
        if (data.temperature > 37.5 && (previousData?.temperature ?: 0f) != data.temperature) {
            list.add(NotificationItem("Sốt nhẹ", "Nhiệt độ cơ thể là ${data.temperature}°C", R.color.purple_dot))
        }
        if (data.roomTemperature > 28 && (previousData?.roomTemperature ?: 0f) != data.roomTemperature) {
            list.add(NotificationItem("Nhiệt độ phòng cao", "Phòng hiện tại ${data.roomTemperature}°C", R.color.yellow_dot))
        }
        if (data.humidity < 45 && (previousData?.humidity ?: 100f) != data.humidity) {
            list.add(NotificationItem("Độ ẩm thấp", "Độ ẩm hiện tại chỉ ${data.humidity}%", R.color.blue_dot))
        }

        _notifications.postValue(list)
    }
}
