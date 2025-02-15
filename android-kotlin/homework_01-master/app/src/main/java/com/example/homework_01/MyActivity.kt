package com.example.homework_01

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MyActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var fab: FloatingActionButton

    private val adapter = MyAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        fab = findViewById(R.id.fab)

        recyclerView.adapter = adapter

        fab.setOnClickListener {
            adapter.addItems(adapter.itemCount + 1)
        }

        if (savedInstanceState != null) {
            for (n in 1..savedInstanceState.getInt("SIZE")) {
                adapter.addItems(n)
            }
        } else {
            adapter.setItems(listOf(1))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SIZE", adapter.itemCount)
    }
}