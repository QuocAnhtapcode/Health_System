package com.example.healthsystem.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthsystem.model.SensorData
import com.google.firebase.database.FirebaseDatabase

class SensorViewModel : ViewModel() {

    private val _sensorData = MutableLiveData<SensorData>()
    val sensorData: LiveData<SensorData> get() = _sensorData

    fun fetchSensorData() {
        val firebaseUrl = "https://iot-ptit-fc1d1-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val dbRef = FirebaseDatabase
            .getInstance(firebaseUrl)
            .getReference("sensorData")

        Log.d("SensorViewModel", "Đang truy cập vào: $firebaseUrl/sensorData")

        dbRef.get()
            .addOnSuccessListener { snapshot ->
                Log.d("SensorViewModel", "snapshot.exists = ${snapshot.exists()}")
                Log.d("SensorViewModel", "snapshot = ${snapshot.value}")
                if (snapshot.exists()) {
                    val data = SensorData(
                        heartRate = snapshot.child("heartRate").getValue(Int::class.java) ?: 0,
                        temperature = snapshot.child("temperature").getValue(Float::class.java) ?: 0f,
                        roomTemperature = snapshot.child("roomTemperature").getValue(Float::class.java) ?: 0f,
                        humidity = snapshot.child("humidity").getValue(Float::class.java) ?: 0f,
                    )
                    _sensorData.value = data
                }
            }
            .addOnFailureListener {
                Log.e("SensorViewModel", "Lỗi khi lấy dữ liệu: ${it.message}")
            }
    }

    private val _historyData = MutableLiveData<Map<String, SensorData>>()
    val historyData: LiveData<Map<String, SensorData>> get() = _historyData

    fun fetchChartData() {
        val firebaseUrl = "https://iot-ptit-fc1d1-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val dbRef = FirebaseDatabase
            .getInstance(firebaseUrl)
            .getReference("sensorData").child("history")

        dbRef.limitToLast(30).get()
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
}
