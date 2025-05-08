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
import com.example.healthsystem.databinding.FragmentRegisterBinding
import com.example.healthsystem.ui.main.MainActivity
import com.example.healthsystem.viewmodel.UserViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvSignUp.setOnClickListener {
            val name = binding.etFullName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            userViewModel.registerWithEmail(
                name,
                email,
                password,
                onSuccess = { user ->
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.putExtra("user_data", user)
                    startActivity(intent)
                    requireActivity().finish()
                },
                onError = { message ->
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
                }
            )

        }

        setupLoginLink()
    }

    private fun setupLoginLink() {
        val fullText = "Already have an account? Login"
        val spannable = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = "#4B258D".toColorInt()
                ds.isUnderlineText = false
            }
        }

        spannable.setSpan(
            clickableSpan,
            fullText.indexOf("Login"),
            fullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvLoginLink.text = spannable
        binding.tvLoginLink.movementMethod = LinkMovementMethod.getInstance()
        binding.tvLoginLink.highlightColor = android.graphics.Color.TRANSPARENT
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
