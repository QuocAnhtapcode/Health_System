package com.example.healthsystem.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.healthsystem.R
import com.example.healthsystem.databinding.FragmentLoginBinding
import com.example.healthsystem.ui.main.MainActivity
import com.example.healthsystem.viewmodel.UserViewModel
import com.example.healthsystem.util.PreferenceHelper


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val (savedEmail, savedPassword) = PreferenceHelper.getLoginInfo(requireContext())

        binding.etEmail.setText(savedEmail)
        binding.etPassword.setText(savedPassword)

        binding.tvLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            userViewModel.loginWithEmail(email, password,
                onSuccess = { user ->
                    PreferenceHelper.saveLoginInfo(requireContext(), email, password)
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.putExtra("user_data", user)
                    startActivity(intent)
                    requireActivity().finish()
                },
                onError = { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            )
        }
        setupSignUpLink()
    }

    private fun setupSignUpLink() {
        val fullText = "Don’t have an account? Sign Up"
        val spannable = SpannableString(fullText)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = "#4B258D".toColorInt()
                ds.isUnderlineText = false
            }
        }

        spannable.setSpan(
            clickableSpan,
            fullText.indexOf("Sign Up"),
            fullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvSignUpLink.text = spannable
        binding.tvSignUpLink.movementMethod = LinkMovementMethod.getInstance()
        binding.tvSignUpLink.highlightColor = android.graphics.Color.TRANSPARENT
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
