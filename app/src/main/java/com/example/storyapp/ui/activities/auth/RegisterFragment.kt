package com.example.storyapp.ui.activities.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.example.storyapp.databinding.FragmentSignUpBinding
import com.example.storyapp.utils.DataStatus
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = AuthViewModelFactory.getInstance(requireActivity())
        val viewModel: AuthViewModel by viewModels { viewModelFactory }

        lifecycleScope.launch {
            viewModel.response.observe(viewLifecycleOwner) {
                when (it.status) {
                    DataStatus.Status.SUCCESS -> {
                        setLoading(false)
                        Toast.makeText(requireContext(), R.string.registration_success, Toast.LENGTH_SHORT).show()
                        val fragment = LogInFragment()
                        val fragmentManager = requireActivity().supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.authFragmentContainer, fragment)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                    DataStatus.Status.ERROR -> {
                        setLoading(false)
                        Toast.makeText(requireContext(), it.message ,Toast.LENGTH_SHORT).show()
                    }
                    DataStatus.Status.LOADING -> {
                        setLoading(true)
                    }
                }
            }
        }

        binding.apply {
            edSignupName.addTextChangedListener {
                validation()
            }

            edSignupEmail.addTextChangedListener {
                validation()
            }

            edSignupPassword.addTextChangedListener {
                validation()
            }

            edSignupConfirmPassword.addTextChangedListener {
                validation()
            }

            loginBtn.setOnClickListener {
                val fragment = LogInFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.authFragmentContainer, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }

            btnSignup.setOnClickListener {
                val name = binding.edSignupName.text.toString().trim()
                val email = binding.edSignupEmail.text.toString().trim()
                val password = binding.edSignupPassword.text.toString().trim()
                viewModel.register(name, email, password)
            }
        }
        playAnimation()
    }

    private fun validation() {
        val isNameValid = binding.edSignupName.error == null && binding.edSignupName.text.toString().isNotEmpty()
        val isEmailValid = binding.edSignupEmail.error == null && binding.edSignupEmail.text.toString().isNotEmpty()
        val isPasswordValid = binding.edSignupPassword.error == null && binding.edSignupPassword.text.toString().isNotEmpty()
        val isConfirmPasswordValid =
            (binding.edSignupConfirmPassword.error == null) && (binding.edSignupConfirmPassword.text.toString() == binding.edSignupPassword.text.toString())

        binding.btnSignup.isEnabled =
            isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSignup.isEnabled = !isLoading
    }

    private fun playAnimation(){
        val nameAnimation = ObjectAnimator.ofFloat(binding.edSignupName, "alpha", 0f, 1f).setDuration(400)
        val emailAnimation = ObjectAnimator.ofFloat(binding.edSignupEmail, "alpha", 0f, 1f).setDuration(400)
        val passwordAnimation = ObjectAnimator.ofFloat(binding.edSignupPassword, "alpha", 0f, 1f).setDuration(400)
        val confirmPasswordAnimation = ObjectAnimator.ofFloat(binding.edSignupConfirmPassword, "alpha", 0f, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(nameAnimation, emailAnimation, passwordAnimation, confirmPasswordAnimation)
            start()
        }
    }
}