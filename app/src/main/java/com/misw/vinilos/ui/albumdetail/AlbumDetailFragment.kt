package com.misw.vinilos.ui.albumdetail
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.misw.vinilos.databinding.FragmentAlbumDetailBinding
import com.misw.vinilos.data.network.VinilosApiService
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
        val albumId = arguments?.getInt("albumId") ?: throw IllegalArgumentException("albumId required")
        val apiService = VinilosApiService.create()
        val repository = AlbumRepository(apiService)
        val factory = AlbumDetailViewModelFactory(repository, albumId)
        val viewModel: AlbumDetailViewModel by viewModels { factory }
        val trackAdapter = TrackAdapter()
        binding.tracksRecyclerView.adapter = trackAdapter
        viewModel.album.observe(viewLifecycleOwner) { album ->
            Log.d("AlbumDetailFragment", "Displaying album: ${album.name}")
            binding.albumName.text = album.name
            binding.albumGenre.text = album.genre
            binding.albumDescription.text = album.description

            val artistName = album.performers?.firstOrNull()?.name ?: "Artista Desconocido"
            val releaseYear = album.releaseDate?.take(4) ?: "Año Desconocido"
            binding.albumReleaseDate.text = "$artistName - $releaseYear"
            binding.albumRecordLabel.text = album.recordLabel
            binding.albumRecordLabel.visibility = View.GONE
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
