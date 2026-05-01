package com.misw.vinilos.ui.collectors

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.misw.vinilos.data.network.VinilosServiceAdapter
import com.misw.vinilos.data.repository.CollectorRepository
import com.misw.vinilos.databinding.FragmentCollectorsBinding

class CollectorsFragment : Fragment() {

    private companion object {
        private const val TAG = "CollectorsFragment"
        private const val ARG_COLLECTOR_ID = "collectorId"
    }

    private var _binding: FragmentCollectorsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CollectorsViewModel
    private lateinit var adapter: CollectorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCollectorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = VinilosServiceAdapter.createApiService(requireContext())
        val repository = CollectorRepository(api)
        val factory = CollectorsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[CollectorsViewModel::class.java]

        adapter = CollectorAdapter(
            onClick = {
                Log.i(TAG, "navigate to collectorDetail collectorId=${it.id}")
                try {
                    val args = Bundle().apply { putInt(ARG_COLLECTOR_ID, it.id) }
                    findNavController().navigate(
                        com.misw.vinilos.R.id.action_CollectorsFragment_to_CollectorDetailFragment,
                        args
                    )
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "No fue posible abrir el detalle", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "navigate to collectorDetail failure message=${e.message}", e)
                }
            }
        )

        binding.rvCollectors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCollectors.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbCollectors.visibility = if (isLoading) View.VISIBLE else View.GONE

            val hasCollectors = !viewModel.collectors.value.isNullOrEmpty()
            binding.rvCollectors.visibility = if (!isLoading && hasCollectors) View.VISIBLE else View.GONE
            binding.tvCollectorsEmpty.visibility = if (!isLoading && !hasCollectors) View.VISIBLE else View.GONE
        }

        viewModel.collectors.observe(viewLifecycleOwner) { collectors ->
            adapter.submitList(collectors)

            val isLoading = viewModel.isLoading.value == true
            val hasCollectors = collectors.isNotEmpty()
            binding.rvCollectors.visibility = if (!isLoading && hasCollectors) View.VISIBLE else View.GONE
            binding.tvCollectorsEmpty.visibility = if (!isLoading && !hasCollectors) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrBlank()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.fetchCollectors()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

