package com.capstone.fresco.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.fresco.core.adapter.HistoryAdapter
import com.capstone.fresco.core.model.History
import com.capstone.fresco.databinding.ActivityHistoryBinding
import com.google.firebase.firestore.*
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private lateinit var db: FirebaseFirestore
    private var list: ArrayList<History> = arrayListOf()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        showRecyclerView()
        changeListener()
        toolbarSetup()
    }

    private fun toolbarSetup() {
        setSupportActionBar(binding.tbHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.tbHistory.setNavigationOnClickListener {
            finish()
        }
    }

    private fun changeListener() {
        db.collection("scanhistory").orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            list.add(dc.document.toObject(History::class.java))
                        }
                    }
                    historyAdapter.notifyDataSetChanged()
                }
            })
    }

    private fun showRecyclerView() {
        historyAdapter = HistoryAdapter(list)
        binding.rvHistory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }
    }
}