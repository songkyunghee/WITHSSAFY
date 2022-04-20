package com.ssafy.withssafy.src.main.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.withssafy.R

class EmployInfoAdapter() : RecyclerView.Adapter<EmployInfoAdapter.EmployInfoHolder>() {

    inner class EmployInfoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployInfoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_employ_item, parent, false)
        return EmployInfoHolder(view)
    }

    override fun onBindViewHolder(holder: EmployInfoHolder, position: Int) {
        holder.apply {
            bind()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

}