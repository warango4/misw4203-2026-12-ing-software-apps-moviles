package com.misw.vinilos.ui.collectors

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
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

    private companion object {
        private const val TAG = "CollectorDetailFragment"
        private const val ARG_COLLECTOR_ID = "collectorId"
        private const val ARG_ALBUM_ID = "albumId"
    }

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

        val collectorId = arguments?.getInt(ARG_COLLECTOR_ID) ?: -1
        Log.i(TAG, "onViewCreated collectorId=$collectorId")

        val apiService = VinilosServiceAdapter.createApiService()
        val repository = CollectorRepository(apiService)
        val albumRepository = AlbumRepository(apiService)
        val factory = CollectorDetailViewModelFactory(repository, albumRepository, collectorId)
        val viewModel: CollectorDetailViewModel by viewModels { factory }

        val commentAdapter = CollectorCommentAdapter()
        binding.rvCollectorComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCollectorComments.adapter = commentAdapter

        val albumAdapter = AlbumAdapter { album ->
            try {
                val args = Bundle().apply { putInt(ARG_ALBUM_ID, album.id) }
                findNavController().navigate(R.id.action_CollectorDetailFragment_to_AlbumDetailFragment, args)
            } catch (e: Exception) {
                Log.e(TAG, "navigate to albumDetail failure message=${e.message}", e)
            }
        }
        binding.rvCollectorAlbums.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCollectorAlbums.adapter = albumAdapter

        viewModel.collector.observe(viewLifecycleOwner) { collector ->
            if (collector != null) {
                Log.i(TAG, "render collector loaded id=${collector.id}")
                requireActivity().title = collector.name

                binding.tvCollectorDetailTitle.text = collector.name
                binding.tvCollectorInitial.text = collector.name
                    .trim()
                    .firstOrNull()
                    ?.uppercase()
                    .orEmpty()
                binding.tvCollectorDetailTelephone.text = collector.telephone
                binding.tvCollectorDetailEmail.text = collector.email


                val performers = collector.favoritePerformers.orEmpty()
                val showPerformers = performers.isNotEmpty()
                binding.tvCollectorFavoritePerformersLabel.visibility = if (showPerformers) View.VISIBLE else View.GONE
                binding.cgCollectorFavoritePerformers.visibility = if (showPerformers) View.VISIBLE else View.GONE
                binding.tvCollectorFavoritePerformersEmpty.visibility = if (!showPerformers) View.VISIBLE else View.GONE

                if (showPerformers) {
                    binding.cgCollectorFavoritePerformers.removeAllViews()
                    performers
                        .mapNotNull { it.name?.trim() }
                        .filter { it.isNotBlank() }
                        .forEach { name ->
                            val chip = Chip(requireContext()).apply {
                                text = name
                                isClickable = false
                                isCheckable = false
                                setTextAppearanceResource(R.style.TextAppearance_Vinilos_Genre)
                                setTextColor(resources.getColor(R.color.vin_bg, null))
                                setChipBackgroundColorResource(R.color.vin_secondary)
                            }
                            // Para mantener el estilo de óvalo exacto del género (drawable), forzamos 
                            // el background si el tipo de Chip lo permite.
                            chip.setEnsureMinTouchTargetSize(false)
                            chip.setChipStartPaddingResource(R.dimen.space_s)
                            chip.setChipEndPaddingResource(R.dimen.space_s)
                            binding.cgCollectorFavoritePerformers.addView(chip)
                        }
                }


                val comments = collector.comments.orEmpty()
                val showComments = comments.isNotEmpty()
                binding.tvCollectorCommentsLabel.visibility = if (showComments) View.VISIBLE else View.GONE
                binding.rvCollectorComments.visibility = if (showComments) View.VISIBLE else View.GONE
                binding.tvCollectorCommentsEmpty.visibility = if (!showComments) View.VISIBLE else View.GONE
                if (showComments) {
                    commentAdapter.submitList(comments)
                }
            }
        }

        viewModel.albums.observe(viewLifecycleOwner) { albums ->
            val showAlbums = albums.isNotEmpty()
            binding.tvCollectorAlbumsLabel.visibility = if (showAlbums) View.VISIBLE else View.GONE
            binding.rvCollectorAlbums.visibility = if (showAlbums) View.VISIBLE else View.GONE
            binding.tvCollectorAlbumsEmpty.visibility = if (!showAlbums) View.VISIBLE else View.GONE
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

