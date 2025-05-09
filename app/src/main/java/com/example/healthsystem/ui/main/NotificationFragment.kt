package com.example.healthsystem.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthsystem.databinding.FragmentNotificationBinding
import com.example.healthsystem.ui.adapter.NotificationAdapter
import com.example.healthsystem.viewmodel.SensorViewModel
import androidx.lifecycle.Observer
import kotlin.getValue

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val sensorViewModel: SensorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sensorViewModel.notifications.observe(viewLifecycleOwner, Observer{ notifications ->
            binding.notificationList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = NotificationAdapter(notifications)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
