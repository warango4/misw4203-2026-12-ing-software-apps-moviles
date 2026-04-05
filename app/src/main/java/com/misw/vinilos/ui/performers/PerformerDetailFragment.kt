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
import com.bumptech.glide.Glide
import com.misw.vinilos.R
import com.misw.vinilos.data.network.VinilosApiService
import com.misw.vinilos.data.repository.PerformerRepository
import com.misw.vinilos.databinding.FragmentPerformerDetailBinding
import com.misw.vinilos.ui.albumdetail.TrackAdapter
import com.misw.vinilos.ui.albums.AlbumAdapter
class PerformerDetailFragment : Fragment() {
    private var _binding: FragmentPerformerDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerformerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val performerId = arguments?.getInt("performerId") ?: throw IllegalArgumentException("performerId required")
        val isBand = arguments?.getBoolean("isBand") ?: throw IllegalArgumentException("isBand flag required")
        val apiService = VinilosApiService.create()
        val repository = PerformerRepository(apiService)
        val factory = PerformerDetailViewModelFactory(repository, performerId, isBand)
        val viewModel: PerformerDetailViewModel by viewModels { factory }
        val albumAdapter = AlbumAdapter(emptyList()) { album ->
            try {
                Log.d("PerformerDetailFragment", "Navegando a detalle de Album ${album.name}")
                val bundle = android.os.Bundle().apply { putInt("albumId", album.id) }
                findNavController().navigate(R.id.action_PerformerDetailFragment_to_AlbumDetailFragment, bundle)
            } catch (e: Exception) {
               Log.e("PerformerDetailFragment", "Issue routing to album detail", e)
            }
        }
        binding.rvAlbums.adapter = albumAdapter
        viewModel.performer.observe(viewLifecycleOwner) { performer ->
            Log.d("PerformerDetailFragment", "Cargando detalle de: ${performer.name}")
            binding.performerName.text = performer.name
            binding.performerDescription.text = performer.description
            Glide.with(this)
                .load(performer.image)
                .into(binding.performerImage)
            performer.albums?.let {
                binding.rvAlbums.adapter = AlbumAdapter(it) { album ->
                    val bundle = android.os.Bundle().apply { putInt("albumId", album.id) }
                    findNavController().navigate(R.id.action_PerformerDetailFragment_to_AlbumDetailFragment, bundle)
                }
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Log.e("PerformerDetailFragment", "Error UI observer: $errorMsg")
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
