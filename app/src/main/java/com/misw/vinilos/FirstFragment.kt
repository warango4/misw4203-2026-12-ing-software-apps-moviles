package com.misw.vinilos
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.misw.vinilos.databinding.FragmentFirstBinding
class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("FirstFragment", "onCreateView called")
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FirstFragment", "onViewCreated called")
        binding.buttonFirst.setOnClickListener {
            Log.d("FirstFragment", "Button to AlbumList clicked")
            findNavController().navigate(R.id.action_FirstFragment_to_AlbumListFragment)
        }
        binding.buttonPerformers.setOnClickListener {
            Log.d("FirstFragment", "Button to PerformerList clicked")
            findNavController().navigate(R.id.action_FirstFragment_to_PerformerListFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("FirstFragment", "onDestroyView called")
        _binding = null
    }
}
