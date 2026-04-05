package com.misw.vinilos.ui.performers
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.PerformerRepository
import com.misw.vinilos.databinding.FragmentPerformerListBinding
class PerformerListFragment : Fragment() {
    private var _binding: FragmentPerformerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PerformerViewModel by viewModels(
        factoryProducer = {
            PerformerViewModelFactory(PerformerRepository(VinilosApiService.create()))
        }
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("PerformerListFragment", "onCreateView called")
        _binding = FragmentPerformerListBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PerformerListFragment", "onViewCreated called")

        val adapter = PerformerAdapter { performer ->
            try {
                Log.d("PerformerListFragment", "Navigate to Detail for performer: ${performer.name}")
                val isBand = performer.creationDate != null
                val bundle = android.os.Bundle().apply {
                    putInt("performerId", performer.id)
                    putBoolean("isBand", isBand)
                }
                findNavController().navigate(com.misw.vinilos.R.id.action_PerformerListFragment_to_PerformerDetailFragment, bundle)
            } catch (e: Exception) {
                Log.e("PerformerListFragment", "Navigation error to performer detail ${e.message}", e)
            }
        }
        binding.rvPerformers.adapter = adapter

        viewModel.performers.observe(viewLifecycleOwner) { performers ->
            Log.d("PerformerListFragment", "Performers observed: ${performers.size}")
            adapter.submitList(performers)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Log.e("PerformerListFragment", "Error observed: $errorMsg")
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
        Log.d("PerformerListFragment", "Calling fetchPerformers on ViewModel")
        viewModel.fetchPerformers()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("PerformerListFragment", "onDestroyView called")
        _binding = null
    }
}
