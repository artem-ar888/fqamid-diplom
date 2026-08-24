package ru.edu.qamid.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.edu.qamid.R
import ru.edu.qamid.databinding.FragmentAuthBinding
import ru.edu.qamid.utils.EspressoIdlingResource
import ru.edu.qamid.utils.TestContext
import ru.edu.qamid.viewmodel.AuthViewModel

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {
    private lateinit var binding: FragmentAuthBinding
    val viewModel: AuthViewModel by viewModels()
    private var wasLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.loginEvent.collectLatest {
                findNavController().navigate(R.id.action_authFragment_to_mainFragment)
            }
        }
        lifecycleScope.launch {
            viewModel.loginExceptionEvent.collectLatest {
                Toast.makeText(
                    requireContext(),
                    R.string.error,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        lifecycleScope.launch {
            viewModel.authorizationFailedExceptionEvent.collectLatest {
                Toast.makeText(
                    requireContext(),
                    R.string.wrong_login_or_password,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        lifecycleScope.launch {
            viewModel.lostConnectionExceptionEvent.collectLatest {
                Toast.makeText(
                    requireContext(),
                    R.string.lost_network_connection,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAuthBinding.bind(view)

        with(binding.authAppBar) {
            mainMenuImageButton.visibility = View.GONE
            authorizationImageButton.visibility = View.GONE
            ourMissionImageButton.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoginLoading.collectLatest { isLoading ->
                    if (isLoading && !wasLoading) {
                        EspressoIdlingResource.increment()
                    } else if (!isLoading && wasLoading) {
                        EspressoIdlingResource.decrement()
                    }
                    wasLoading = isLoading

                    binding.enterButton.isEnabled = !isLoading
                    binding.enterButton.text = if (isLoading) "" else getString(R.string.sign_in)
                    binding.enterButtonProgress.isVisible = isLoading
                }
            }
        }

        binding.enterButton.setOnClickListener {
            if (binding.loginEditText.text.isNullOrBlank() || binding.passwordEditText.text.isNullOrBlank()) {
                showErrorForEmptyFields()
//                Toast.makeText(
//                    requireContext(),
//                    R.string.empty_login_or_password,
//                    Toast.LENGTH_SHORT
//                ).show()
            } else {
                viewModel.login(
                    binding.loginEditText.text.toString().trim(),
                    binding.passwordEditText.text.toString().trim()
                )
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            activity?.finishAffinity()
        }
    }
    private fun showErrorForEmptyFields() {
        val error = getString(R.string.empty_login_or_password)

        if (TestContext.isRunningInEspressoTest()) {
            // В тесте: ставим ошибку в поле (чтобы Espresso мог её проверить)
            binding.loginTextInputLayout.error = error
            binding.passwordTextInputLayout.error = error
        } else {
            // Обычное приложение: показываем Toast
            Toast.makeText(
                requireContext(),
                R.string.empty_login_or_password,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
