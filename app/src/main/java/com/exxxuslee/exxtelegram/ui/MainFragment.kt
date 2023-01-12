package com.exxxuslee.exxtelegram.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.exxxuslee.exxtelegram.MainViewModel
import com.exxxuslee.exxtelegram.databinding.FragmentMainBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            viewModel.chatList()
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        binding.buttonSendMessage.setOnClickListener {
            viewModel.sendMessage(
                binding.editChatId.text.toString().toLong(),
                binding.editMessage.text.toString()
            )
        }

        val settingAdapter = ChatsAdapter()
        binding.recyclerChats.adapter = settingAdapter

        viewModel.observe(viewLifecycleOwner) { listChats ->
            if (listChats != null) settingAdapter.updateAdapter(listChats)
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