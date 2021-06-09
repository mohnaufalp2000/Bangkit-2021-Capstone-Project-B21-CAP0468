package com.capstone.fresco.core.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fresco.core.model.History
import com.capstone.fresco.databinding.ListHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class HistoryAdapter(private val historyList : ArrayList<History>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userId: String? = null
    private lateinit var docRef: DocumentReference

    class ViewHolder(val binding: ListHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userId = auth.currentUser?.uid


        docRef = db.collection("scanhistory").document(userId.toString())
        docRef.get().addOnSuccessListener {
            if (it != null) {
                Log.d(ContentValues.TAG, "Success on $userId")
                holder.binding.txtNameHistory.text =
                    it.getString("name").also { historyList[position].name }
                holder.binding.txtDateHistory.text =
                    it.getString("time").also { historyList[position].date }
            }
        }
    }

    override fun getItemCount(): Int = historyList.size
}