package com.example.healthsystem.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthsystem.databinding.ItemChartBinding
import com.example.healthsystem.model.ChartItem
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class ChartAdapter(private val items: List<ChartItem>) : RecyclerView.Adapter<ChartAdapter.ChartViewHolder>() {

    inner class ChartViewHolder(val binding: ItemChartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChartBinding.inflate(inflater, parent, false)
        return ChartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChartViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvChartTitle.text = item.title
        holder.binding.tvChartDesc.text = item.description

        val dataSet = LineDataSet(item.entries, item.title)
        dataSet.color = Color.BLUE
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)

        val lineData = LineData(dataSet)
        holder.binding.itemLineChart.data = lineData
        holder.binding.itemLineChart.description = Description().apply {
            text = ""
        }
        holder.binding.itemLineChart.invalidate()
    }

    override fun getItemCount() = items.size
}
