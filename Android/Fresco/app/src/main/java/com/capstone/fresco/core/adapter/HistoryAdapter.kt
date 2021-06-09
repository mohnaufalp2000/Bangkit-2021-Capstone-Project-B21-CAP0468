package com.capstone.fresco.core.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fresco.core.model.History
import com.capstone.fresco.databinding.ListHistoryBinding

class HistoryAdapter(private val historyList : ArrayList<History>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding : ListHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtNameHistory.text = historyList[position].name
        holder.binding.txtDateHistory.text = historyList[position].date
    }

    override fun getItemCount(): Int = historyList.size
}