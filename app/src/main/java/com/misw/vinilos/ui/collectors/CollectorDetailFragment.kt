package com.misw.vinilos.ui.collectors

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.data.network.VinilosServiceAdapter
import com.misw.vinilos.data.repository.CollectorRepository
import com.misw.vinilos.databinding.FragmentCollectorDetailBinding
import com.misw.vinilos.ui.albums.AlbumAdapter
import com.misw.vinilos.R

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

        requireActivity().title = ""

        val collectorId = arguments?.getInt("collectorId") ?: -1
        Log.d("CollectorDetailFragment", "onViewCreated: collectorId=$collectorId")

        binding.btnCollectorBack.setOnClickListener {
            try {
                findNavController().navigateUp()
            } catch (e: Exception) {
                Log.e("CollectorDetailFragment", "navigateUp: failure message=${e.message}", e)
            }
        }

        val apiService = VinilosServiceAdapter.createApiService()
        val repository = CollectorRepository(apiService)
        val albumRepository = AlbumRepository(apiService)
        val factory = CollectorDetailViewModelFactory(repository, albumRepository, collectorId)
        val viewModel: CollectorDetailViewModel by viewModels { factory }

        val favoriteAdapter = com.misw.vinilos.ui.performers.PerformerAdapter { _ ->
        }
        binding.rvFavoritePerformers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoritePerformers.adapter = favoriteAdapter
        binding.rvFavoritePerformers.isEnabled = false

        val commentAdapter = CollectorCommentAdapter()
        binding.rvCollectorComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCollectorComments.adapter = commentAdapter

        val albumAdapter = AlbumAdapter { album ->
            try {
                val args = Bundle().apply { putInt("albumId", album.id) }
                findNavController().navigate(R.id.action_CollectorDetailFragment_to_AlbumDetailFragment, args)
            } catch (e: Exception) {
                Log.e("CollectorDetailFragment", "navigate: albumDetail failure message=${e.message}", e)
            }
        }
        binding.rvCollectorAlbums.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCollectorAlbums.adapter = albumAdapter

        viewModel.collector.observe(viewLifecycleOwner) { collector ->
            if (collector != null) {
                Log.d("CollectorDetailFragment", "render: id=${collector.id} name=${collector.name}")
                requireActivity().title = collector.name

                binding.tvCollectorDetailTitle.text = collector.name
                binding.tvCollectorDetailTelephone.text = collector.telephone
                binding.tvCollectorDetailEmail.text = collector.email

                val favorites = collector.favoritePerformers.orEmpty()
                val showFavorites = favorites.isNotEmpty()
                binding.tvFavoritePerformersLabel.visibility = if (showFavorites) View.VISIBLE else View.GONE
                binding.rvFavoritePerformers.visibility = if (showFavorites) View.VISIBLE else View.GONE
                if (showFavorites) {
                    favoriteAdapter.submitList(favorites)
                }

                val comments = collector.comments.orEmpty()
                val showComments = comments.isNotEmpty()
                binding.tvCollectorCommentsLabel.visibility = if (showComments) View.VISIBLE else View.GONE
                binding.rvCollectorComments.visibility = if (showComments) View.VISIBLE else View.GONE
                if (showComments) {
                    commentAdapter.submitList(comments)
                }

                val albums = collector.collectorAlbums.orEmpty()
                val showAlbums = albums.isNotEmpty()
                binding.tvCollectorAlbumsLabel.visibility = if (showAlbums) View.VISIBLE else View.GONE
                binding.rvCollectorAlbums.visibility = if (showAlbums) View.VISIBLE else View.GONE
            }
        }

        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            val showAlbums = albums.isNotEmpty()
            binding.tvCollectorAlbumsLabel.visibility = if (showAlbums) View.VISIBLE else View.GONE
            binding.rvCollectorAlbums.visibility = if (showAlbums) View.VISIBLE else View.GONE
            albumAdapter.submitList(albums)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbCollectorDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            val showError = !message.isNullOrBlank()
            binding.tvCollectorDetailError.visibility = if (showError) View.VISIBLE else View.GONE
            binding.tvCollectorDetailError.text = message.orEmpty()
        }

        viewModel.fetchCollector()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

