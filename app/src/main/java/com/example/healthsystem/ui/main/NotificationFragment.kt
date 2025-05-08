package com.example.healthsystem.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthsystem.R
import com.example.healthsystem.databinding.FragmentNotificationBinding
import com.example.healthsystem.model.NotificationItem
import com.example.healthsystem.ui.adapter.NotificationAdapter

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notifications = listOf(
            NotificationItem(
                title = "Nhịp tim tăng cao",
                content = "Chỉ số nhịp tim vừa vượt ngưỡng an toàn (98 bpm). Hãy nghỉ ngơi trong vài phút.",
                color = ContextCompat.getColor(requireContext(), R.color.red_dot)
            ),
            NotificationItem(
                title = "Nhiệt độ cơ thể bình thường",
                content = "Cơ thể bạn đang duy trì nhiệt độ ổn định ở mức 36.7°C. Tiếp tục theo dõi.",
                color = ContextCompat.getColor(requireContext(), R.color.purple_dot)
            ),
            NotificationItem(
                title = "Nhiệt độ phòng cao",
                content = "Phòng của bạn đang ở mức 29.4°C. Cân nhắc sử dụng điều hòa hoặc quạt.",
                color = ContextCompat.getColor(requireContext(), R.color.yellow_dot)
            ),
            NotificationItem(
                title = "Độ ẩm không khí thấp",
                content = "Độ ẩm hiện tại là 42%. Nên sử dụng máy tạo ẩm để tránh khô da.",
                color = ContextCompat.getColor(requireContext(), R.color.blue_dot)
            )
        )

        binding.notificationList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = NotificationAdapter(notifications)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
