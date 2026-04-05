package com.misw.vinilos.ui.performers
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.misw.vinilos.data.models.Performer
import com.misw.vinilos.databinding.ItemPerformerBinding
class PerformerAdapter(private val onClick: (Performer) -> Unit) :
    ListAdapter<Performer, PerformerAdapter.PerformerViewHolder>(PerformerDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerformerViewHolder {
        val binding = ItemPerformerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PerformerViewHolder(binding)
    }
    override fun onBindViewHolder(holder: PerformerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class PerformerViewHolder(private val binding: ItemPerformerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(performer: Performer) {
            binding.tvPerformerName.text = performer.name
            val count = performer.albums?.size ?: 0
            binding.tvPerformerAlbumsCount.text = "$count Albums"
            Glide.with(binding.root.context)
                .load(performer.image)
                .into(binding.ivPerformerImage)
            binding.root.setOnClickListener {
                Log.d("PerformerAdapter", "Performer clicked: ${performer.name}")
                onClick(performer)
            }
        }
    }
}
class PerformerDiffCallback : DiffUtil.ItemCallback<Performer>() {
    override fun areItemsTheSame(oldItem: Performer, newItem: Performer): Boolean =
        oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Performer, newItem: Performer): Boolean =
        oldItem == newItem
}
