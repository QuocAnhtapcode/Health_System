package com.example.healthsystem.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class DeviceViewmodel : ViewModel() {

    private val firebaseUrl = "https://iot-ptit-fc1d1-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val acRef: DatabaseReference = FirebaseDatabase.getInstance(firebaseUrl).getReference("device/AC")

    private val _acTemperature = MutableLiveData<Int>()
    val acTemperature: LiveData<Int> get() = _acTemperature

    private val acListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val currentTemp = snapshot.getValue(Int::class.java) ?: 0
            _acTemperature.value = currentTemp
            Log.d("DeviceViewModel", "AC updated: $currentTemp")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("DeviceViewModel", "Lỗi khi theo dõi AC: ${error.message}")
        }
    }

    init {
        acRef.addValueEventListener(acListener)
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
                Log.d("DeviceViewModel", "Đã cập nhật AC: $value")
            }
            .addOnFailureListener {
                Log.e("DeviceViewModel", "Lỗi cập nhật AC: ${it.message}")
            }
    }

    override fun onCleared() {
        super.onCleared()
        acRef.removeEventListener(acListener)
    }
}
