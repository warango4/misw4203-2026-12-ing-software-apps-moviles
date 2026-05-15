package com.misw.vinilos.ui.addtrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.misw.vinilos.R
import com.misw.vinilos.data.network.VinilosServiceAdapter
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.data.session.UserSession
import com.misw.vinilos.databinding.FragmentAddTrackBinding

class AddTrackFragment : Fragment() {

    private var _binding: FragmentAddTrackBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTrackViewModel by viewModels {
        AddTrackViewModelFactory(
            AlbumRepository(VinilosServiceAdapter.createApiService(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!UserSession.isCollector(requireContext())) {
            findNavController().popBackStack()
            return
        }

        val albumId = arguments?.getInt("albumId") ?: throw IllegalArgumentException("albumId required")
        setupObservers(albumId)

        binding.btnSaveTrack.setOnClickListener {
            if (validateForm()) {
                viewModel.addTrack(
                    albumId = albumId,
                    name = binding.etTrackName.text.toString().trim(),
                    duration = binding.etTrackDuration.text.toString().trim()
                )
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        if (binding.etTrackName.text.isNullOrBlank()) {
            binding.tilTrackName.error = getString(R.string.create_album_field_required)
            valid = false
        } else {
            binding.tilTrackName.error = null
        }

        if (binding.etTrackDuration.text.isNullOrBlank()) {
            binding.tilTrackDuration.error = getString(R.string.create_album_field_required)
            valid = false
        } else {
            val duration = binding.etTrackDuration.text.toString().trim()
            val durationRegex = Regex("^\\d+:[0-5]\\d$")
            if (!durationRegex.matches(duration)) {
                binding.tilTrackDuration.error = getString(R.string.add_track_duration_invalid)
                valid = false
            } else {
                binding.tilTrackDuration.error = null
            }
        }

        return valid
    }

    private fun setupObservers(albumId: Int) {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbAddTrack.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSaveTrack.isEnabled = !isLoading
        }

        viewModel.trackAdded.observe(viewLifecycleOwner) { track ->
            track ?: return@observe
            findNavController().previousBackStackEntry?.savedStateHandle?.apply {
                set("new_track_id", track.id)
                set("new_track_name", track.name)
                set("new_track_duration", track.duration)
            }
            Toast.makeText(
                requireContext(),
                getString(R.string.add_track_success, track.name),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().popBackStack()
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            message ?: return@observe
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
