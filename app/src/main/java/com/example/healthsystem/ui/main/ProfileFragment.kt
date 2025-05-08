package com.example.healthsystem.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthsystem.R
import com.example.healthsystem.databinding.FragmentProfileBinding
import com.example.healthsystem.model.ProfileOption
import com.example.healthsystem.ui.adapter.ProfileAdapter
import com.example.healthsystem.util.PreferenceHelper

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileOptions = listOf(
        ProfileOption(R.drawable.ic_account, "Account"),
        ProfileOption(R.drawable.ic_notification, "Notification"),
        ProfileOption(R.drawable.ic_appearance, "Appearance"),
        ProfileOption(R.drawable.ic_security, "Privacy & Security"),
        ProfileOption(R.drawable.ic_sound, "Sound"),
        ProfileOption(R.drawable.ic_language, "Language")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = ProfileAdapter(profileOptions) { option ->
            Toast.makeText(requireContext(), "Clicked: ${option.title}", Toast.LENGTH_SHORT).show()
        }

        val user = (requireActivity() as MainActivity).currentUser
        binding.tvName.text = user.name
        binding.tvEmail.text = user.email

        binding.profileRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.profileRecycler.adapter = adapter

        binding.btnLogout.setOnClickListener {
            PreferenceHelper.clearLoginInfo(requireActivity())
            (activity as? MainActivity)?.logout()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
