package com.misw.vinilos.ui.collectors

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.misw.vinilos.databinding.FragmentCollectorDetailBinding

class CollectorDetailFragment : Fragment() {

    private var _binding: FragmentCollectorDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectorDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val collectorId = arguments?.getInt("collectorId") ?: -1
        Log.d("CollectorDetailFragment", "render: collectorId=$collectorId")
        binding.tvCollectorDetailTitle.text = "Collector #$collectorId"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

