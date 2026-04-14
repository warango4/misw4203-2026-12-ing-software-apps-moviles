package com.misw.vinilos.ui.albumdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.misw.vinilos.databinding.FragmentAlbumDetailBinding
import com.misw.vinilos.data.network.VinilosServiceAdapter
import com.misw.vinilos.data.repository.AlbumRepository

class AlbumDetailFragment : Fragment() {
    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Evita que el toolbar quede sin título mientras carga la data
        requireActivity().title = ""

        val albumId = arguments?.getInt("albumId") ?: throw IllegalArgumentException("albumId required")
        val apiService = VinilosServiceAdapter.createApiService()
        val repository = AlbumRepository(apiService)
        val factory = AlbumDetailViewModelFactory(repository, albumId)
        val viewModel: AlbumDetailViewModel by viewModels { factory }
        val trackAdapter = TrackAdapter()
        binding.tracksRecyclerView.adapter = trackAdapter
        viewModel.album.observe(viewLifecycleOwner) { album ->
            Log.d("AlbumDetailFragment", "Displaying album: ${album.name}")

            // Título dinámico en el toolbar (reemplaza el label fijo del nav_graph)
            requireActivity().title = album.name

            binding.albumName.text = album.name
            binding.albumGenre.text = album.genre
            binding.albumDescription.text = album.description

            // UI requirement: show "Artist - Year" (same row). If artist not available, fall back to year/date.
            val artistName = album.performers?.firstOrNull()?.name
            val year = album.releaseDate
                ?.trim()
                ?.takeIf { it.length >= 4 }
                ?.substring(0, 4)

            binding.albumReleaseDate.text = when {
                !artistName.isNullOrBlank() && !year.isNullOrBlank() -> "$artistName - $year"
                !artistName.isNullOrBlank() -> artistName
                !year.isNullOrBlank() -> year
                else -> album.releaseDate.orEmpty()
            }

            binding.albumRecordLabel.text = album.recordLabel
            Glide.with(this)
                .load(album.cover)
                .into(binding.albumCover)
            trackAdapter.submitList(album.tracks)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Log.e("AlbumDetailFragment", "Error observed from ViewModel: $errorMsg")
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
