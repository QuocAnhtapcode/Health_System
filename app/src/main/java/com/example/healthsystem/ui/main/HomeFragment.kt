package com.example.healthsystem.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthsystem.R
import com.example.healthsystem.databinding.FragmentHomeBinding
import com.example.healthsystem.model.Device
import com.example.healthsystem.ui.adapter.DeviceAdapter
import com.example.healthsystem.viewmodel.DeviceViewmodel
import com.example.healthsystem.viewmodel.SensorViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val sensorViewModel: SensorViewModel by activityViewModels()
    private val deviceViewmodel: DeviceViewmodel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorViewModel.fetchSensorData()

        sensorViewModel.sensorData.observe(viewLifecycleOwner, Observer { data ->
            Log.d("HomeFragment", "Dữ liệu nhận được: $data")
            binding.valueHeart.text = "${data.heartRate} BPM"
            binding.valueBody.text = String.format("%.2f °C", data.temperature)
            binding.valueRoom.text = String.format("%.2f °C", data.roomTemperature)
            binding.valueHumidity.text = String.format("%.2f %%", data.humidity)

        })
        deviceViewmodel.acTemperature.observe(viewLifecycleOwner, Observer{data ->
            val devices = listOf(
                Device(R.drawable.ic_ac, "Air Conditioner", "$data °C"),
            )
            val adapter = DeviceAdapter(devices)
            binding.listDevice.layoutManager = LinearLayoutManager(requireContext())
            binding.listDevice.adapter = adapter
        })
        val currentTime = System.currentTimeMillis()
        val formatter = SimpleDateFormat("HH:mm - EEEE, dd/MM/yyyy", Locale.getDefault())
        val formattedTime = formatter.format(Date(currentTime))
        binding.today.text = formattedTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
