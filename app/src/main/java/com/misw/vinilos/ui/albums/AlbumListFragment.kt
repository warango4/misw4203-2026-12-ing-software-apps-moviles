package com.misw.vinilos.ui.albums

import android.util.Log
import com.misw.vinilos.data.models.Album
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.data.network.VinilosApiService

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.misw.vinilos.databinding.FragmentAlbumListBinding

class AlbumListFragment : Fragment() {

    private var _binding: FragmentAlbumListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AlbumViewModel by viewModels(
        factoryProducer = {
            AlbumViewModelFactory(AlbumRepository(VinilosApiService.create()))
        }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("AlbumListFragment", "onCreateView called")
        _binding = FragmentAlbumListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AlbumListFragment", "onViewCreated called")
        observeAlbums()
        Log.d("AlbumListFragment", "Calling fetchAlbums on ViewModel")
        viewModel.fetchAlbums()
    }

    private fun observeAlbums() {
        Log.d("AlbumListFragment", "observeAlbums started")
        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            Log.d("AlbumListFragment", "Albums observed, size: ${albums.size}")
            binding.rvAlbums.adapter = AlbumAdapter(albums) { album ->
                Log.d("AlbumListFragment", "Navigate to Detail for album: ${album.name}")
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { message ->
            Log.e("AlbumListFragment", "Error observed: $message")
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("AlbumListFragment", "onDestroyView called")
        _binding = null
    }
}