package com.misw.vinilos.ui.albumdetail

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
import com.misw.vinilos.data.models.Track
import com.misw.vinilos.data.network.VinilosServiceAdapter
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.data.session.UserSession
import com.misw.vinilos.databinding.FragmentAlbumDetailBinding

class AlbumDetailFragment : Fragment() {

    private var _binding: FragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!

    private val albumId by lazy {
        arguments?.getInt("albumId") ?: throw IllegalArgumentException("albumId required")
    }

    private val viewModel: AlbumDetailViewModel by viewModels {
        AlbumDetailViewModelFactory(
            AlbumRepository(VinilosServiceAdapter.createApiService(requireContext())),
            albumId
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().title = ""

        val trackAdapter = TrackAdapter()
        binding.tracksRecyclerView.adapter = trackAdapter

        viewModel.album.observe(viewLifecycleOwner) { album ->
            Log.d("AlbumDetailFragment", "render: albumId=${album.id}, name=${album.name}")
            requireActivity().title = album.name
            binding.albumName.text = album.name
            binding.albumGenre.text = album.genre
            binding.albumDescription.text = album.description

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
            Glide.with(this).load(album.cover).into(binding.albumCover)
            trackAdapter.submitList(album.tracks)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Log.e("AlbumDetailFragment", "render: error message=$errorMsg")
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.let { handle ->
            handle.getLiveData<Int>("new_track_id").observe(viewLifecycleOwner) { trackId ->
                trackId ?: return@observe
                val name = handle.get<String>("new_track_name") ?: return@observe
                val duration = handle.get<String>("new_track_duration") ?: return@observe
                
                // Actualizamos localmente para feedback inmediato
                viewModel.appendTrack(Track(trackId, name, duration))
                
                // Sincronizamos con el servidor FORZANDO el refresco (bypass cache)
                viewModel.fetchAlbum(refresh = true)

                handle.remove<Int>("new_track_id")
                handle.remove<String>("new_track_name")
                handle.remove<String>("new_track_duration")
            }
        }

        updateFabVisibility()
    }

    fun updateFabVisibility() {
        if (_binding == null) return
        val isCollector = UserSession.isCollector(requireContext())
        binding.fabAddTrack.visibility = if (isCollector) View.VISIBLE else View.GONE
        binding.fabAddTrack.setOnClickListener {
            val bundle = android.os.Bundle().apply { putInt("albumId", albumId) }
            findNavController().navigate(R.id.action_AlbumDetailFragment_to_AddTrackFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
