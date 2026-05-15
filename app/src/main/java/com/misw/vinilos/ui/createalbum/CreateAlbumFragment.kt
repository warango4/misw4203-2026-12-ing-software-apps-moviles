package com.misw.vinilos.ui.createalbum

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.misw.vinilos.R
import com.misw.vinilos.data.network.VinilosServiceAdapter
import com.misw.vinilos.data.repository.AlbumRepository
import com.misw.vinilos.data.session.UserSession
import com.misw.vinilos.databinding.FragmentCreateAlbumBinding
import java.util.Calendar

class CreateAlbumFragment : Fragment() {

    private var _binding: FragmentCreateAlbumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateAlbumViewModel by viewModels {
        CreateAlbumViewModelFactory(
            AlbumRepository(VinilosServiceAdapter.createApiService(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!UserSession.isCollector(requireContext())) {
            findNavController().popBackStack()
            return
        }

        setupGenreDropdown()
        setupRecordLabelDropdown()
        setupDatePicker()
        setupObservers()

        binding.btnSaveAlbum.setOnClickListener {
            if (validateForm()) {
                viewModel.createAlbum(
                    name = binding.etAlbumName.text.toString().trim(),
                    cover = binding.etAlbumCover.text.toString().trim(),
                    releaseDate = "${binding.etAlbumReleaseDate.text.toString().trim()}T00:00:00.000Z",
                    description = binding.etAlbumDescription.text.toString().trim(),
                    genre = binding.actvAlbumGenre.text.toString().trim(),
                    recordLabel = binding.actvAlbumRecordLabel.text.toString().trim()
                )
            }
        }
    }

    private fun setupGenreDropdown() {
        val genres = arrayOf("Classical", "Salsa", "Rock", "Folk")
        binding.actvAlbumGenre.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genres)
        )
    }

    private fun setupRecordLabelDropdown() {
        val labels = arrayOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records")
        binding.actvAlbumRecordLabel.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, labels)
        )
    }

    private fun setupDatePicker() {
        val listener = View.OnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    binding.etAlbumReleaseDate.setText(
                        String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    )
                    binding.tilAlbumReleaseDate.error = null
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.setTitle(getString(R.string.select_album_release_date))
            datePickerDialog.show()
        }
        binding.etAlbumReleaseDate.setOnClickListener(listener)
        binding.tilAlbumReleaseDate.setOnClickListener(listener)
    }

    private fun validateForm(): Boolean {
        var valid = true

        if (binding.etAlbumName.text.isNullOrBlank()) {
            binding.tilAlbumName.error = getString(R.string.create_album_field_required)
            valid = false
        } else {
            binding.tilAlbumName.error = null
        }

        if (binding.etAlbumCover.text.isNullOrBlank()) {
            binding.tilAlbumCover.error = getString(R.string.create_album_field_required)
            valid = false
        } else {
            binding.tilAlbumCover.error = null
        }

        if (binding.etAlbumReleaseDate.text.isNullOrBlank()) {
            binding.tilAlbumReleaseDate.error = getString(R.string.create_album_field_required)
            valid = false
        } else {
            binding.tilAlbumReleaseDate.error = null
        }

        if (binding.actvAlbumGenre.text.isNullOrBlank()) {
            binding.tilAlbumGenre.error = getString(R.string.create_album_field_required)
            valid = false
        } else {
            binding.tilAlbumGenre.error = null
        }

        if (binding.etAlbumDescription.text.isNullOrBlank()) {
            binding.tilAlbumDescription.error = getString(R.string.create_album_field_required)
            valid = false
        } else {
            binding.tilAlbumDescription.error = null
        }

        if (binding.actvAlbumRecordLabel.text.isNullOrBlank()) {
            binding.tilAlbumRecordLabel.error = getString(R.string.create_album_field_required)
            valid = false
        } else {
            binding.tilAlbumRecordLabel.error = null
        }

        return valid
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbCreateAlbum.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSaveAlbum.isEnabled = !isLoading
        }

        viewModel.albumCreated.observe(viewLifecycleOwner) { album ->
            album ?: return@observe
            Toast.makeText(
                requireContext(),
                getString(R.string.create_album_success, album.name),
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
