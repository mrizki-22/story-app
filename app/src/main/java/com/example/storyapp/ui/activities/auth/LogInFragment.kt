package com.example.storyapp.ui.activities.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentLogInBinding
import com.example.storyapp.ui.activities.main.MainActivity
import com.example.storyapp.utils.DataStatus
import kotlinx.coroutines.launch

class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLogInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = AuthViewModelFactory.getInstance(requireActivity())
        val viewModel: AuthViewModel by viewModels { viewModelFactory }

        binding.apply {
            edLoginEmail.addTextChangedListener {
                validation()
            }

            edLoginPassword.addTextChangedListener {
                validation()
            }

            btnLogin.setOnClickListener {
                viewModel.login(
                    binding.edLoginEmail.text.toString(),
                    binding.edLoginPassword.text.toString()
                )
            }

            registerBtn.setOnClickListener {
                val fragment = RegisterFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.authFragmentContainer, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
        }


        lifecycleScope.launch {
            viewModel.loginResponse.observe(viewLifecycleOwner) {
                when (it.status) {
                    DataStatus.Status.SUCCESS -> {
                        setLoading(false)
                        Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    DataStatus.Status.ERROR -> {
                        setLoading(false)
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    DataStatus.Status.LOADING-> {
                        setLoading(true)
                    }
                }
            }
        }
        playAnimation()
    }


    private fun validation() {
        val isEmailValid = binding.edLoginEmail.error == null && binding.edLoginEmail.text.toString().isNotEmpty()
        val isPasswordValid = binding.edLoginPassword.error == null && binding.edLoginPassword.text.toString().isNotEmpty()

        binding.btnLogin.isEnabled = isEmailValid && isPasswordValid
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun playAnimation(){
        val emailAnimation = ObjectAnimator.ofFloat(binding.edLoginEmail, "alpha", 0f, 1f).setDuration(400)
        val passwordAnimation = ObjectAnimator.ofFloat(binding.edLoginPassword, "alpha", 0f, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(emailAnimation, passwordAnimation)
            start()
        }
    }
}