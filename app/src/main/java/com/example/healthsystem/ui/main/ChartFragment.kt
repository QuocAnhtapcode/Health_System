package com.example.healthsystem.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthsystem.databinding.FragmentChartBinding
import com.example.healthsystem.model.ChartItem
import com.example.healthsystem.ui.adapter.ChartAdapter
import com.example.healthsystem.viewmodel.SensorViewModel
import com.github.mikephil.charting.data.Entry

class ChartFragment : Fragment() {
    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SensorViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.fetchChartData()
        viewModel.historyData.observe(viewLifecycleOwner) { dataMap ->
            val list = mutableListOf<ChartItem>()
            var index = 0f

            val heartEntries = mutableListOf<Entry>()
            val tempEntries = mutableListOf<Entry>()
            val roomEntries = mutableListOf<Entry>()
            val humidityEntries = mutableListOf<Entry>()

            dataMap.toSortedMap().values.forEach {
                heartEntries.add(Entry(index, it.heartRate.toFloat()))
                tempEntries.add(Entry(index, it.temperature))
                roomEntries.add(Entry(index, it.roomTemperature))
                humidityEntries.add(Entry(index, it.humidity))
                index++
            }

            list.add(ChartItem("Heart Rate", "Nhịp tim theo thời gian", heartEntries))
            list.add(ChartItem("Body Temperature", "Nhiệt độ cơ thể", tempEntries))
            list.add(ChartItem("Room Temperature", "Nhiệt độ phòng", roomEntries))
            list.add(ChartItem("Humidity", "Độ ẩm môi trường", humidityEntries))

            binding.recyclerChart.adapter = ChartAdapter(list)
            binding.recyclerChart.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
