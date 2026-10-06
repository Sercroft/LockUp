package com.sercroft.lockup.ui.apps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.materialswitch.MaterialSwitch
import androidx.recyclerview.widget.ListAdapter
import com.sercroft.lockup.R
import com.sercroft.lockup.data.repository.BlockedAppsRepository

class AppsAdapter : ListAdapter<AppItem, AppsAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imgIcon)
        val name: TextView = view.findViewById(R.id.tvName)
        val block: MaterialSwitch = view.findViewById(R.id.switchBlock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = getItem(position)

        holder.icon.setImageDrawable(app.icon)
        holder.name.text = app.name

        holder.block.setOnCheckedChangeListener(null)
        holder.block.isChecked = app.isBlocked

        holder.block.setOnCheckedChangeListener { _, checked ->
            app.isBlocked = checked

            if (checked) {
                BlockedAppsRepository.blockApp(app.pkgName)
            } else {
                BlockedAppsRepository.unblockApp(app.pkgName)
            }
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AppItem>() {

            override fun areItemsTheSame(
                oldItem: AppItem,
                newItem: AppItem
            ): Boolean {
                return oldItem.pkgName == newItem.pkgName
            }

            override fun areContentsTheSame(
                oldItem: AppItem,
                newItem: AppItem
            ): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.isBlocked == newItem.isBlocked
            }
        }
    }
}