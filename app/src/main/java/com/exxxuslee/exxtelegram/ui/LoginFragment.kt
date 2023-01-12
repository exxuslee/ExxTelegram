package com.exxxuslee.exxtelegram.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.exxxuslee.exxtelegram.MainViewModel
import com.exxxuslee.exxtelegram.R
import com.exxxuslee.exxtelegram.databinding.FragmentLoginBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnTelephone.setOnClickListener {
            viewModel.initClient(
                requireContext().filesDir.absolutePath,
                binding.editTextPhone.text.toString()
            )
        }
        binding.btnCode.setOnClickListener {
            viewModel.sendCode(binding.editTextCode.text.toString())
        }
        binding.btnLogin.setOnClickListener {
            viewModel.sendPassword(binding.editTextPassword.text.toString())
        }
        viewModel.onAuthorizedClickListener = {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        viewModel.observeStatus(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}