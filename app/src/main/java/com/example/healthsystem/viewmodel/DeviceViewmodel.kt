package com.example.healthsystem.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase

class DeviceViewmodel : ViewModel() {

    private val firebaseUrl = "https://iot-ptit-fc1d1-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val acRef = FirebaseDatabase.getInstance(firebaseUrl).getReference("device/AC")

    private val _acTemperature = MutableLiveData<Int>()
    val acTemperature: LiveData<Int> get() = _acTemperature

    init {
        fetchACTemperature()
    }

    fun fetchACTemperature() {
        acRef.get().addOnSuccessListener { snapshot ->
            val currentTemp = snapshot.getValue(Int::class.java) ?: 0
            _acTemperature.value = currentTemp
            Log.d("DeviceViewModel", "Fetched AC temp: $currentTemp")
        }.addOnFailureListener {
            Log.e("DeviceViewModel", "Lỗi lấy AC: ${it.message}")
        }
    }

    fun increaseAC() {
        val current = _acTemperature.value ?: return
        val newTemp = current + 1
        updateACTemperature(newTemp)
    }

    fun decreaseAC() {
        val current = _acTemperature.value ?: return
        val newTemp = current - 1
        updateACTemperature(newTemp)
    }

    private fun updateACTemperature(value: Int) {
        acRef.setValue(value)
            .addOnSuccessListener {
                Log.d("DeviceViewModel", "Updated AC to $value")
                _acTemperature.value = value
            }
            .addOnFailureListener {
                Log.e("DeviceViewModel", "Lỗi cập nhật AC: ${it.message}")
            }
    }
}
