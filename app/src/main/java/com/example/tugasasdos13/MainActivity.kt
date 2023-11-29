package com.example.tugasasdos13

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasasdos13.adapter.UserAdapter
import com.example.tugasasdos13.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var btnAdd: FloatingActionButton

    // Inisialisasi objek Firebase Firestore
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Inisialisasi objek RecyclerView untuk menampilkan daftar data
    private lateinit var recyclerView: RecyclerView

    // Inisialisasi objek MutableList<User> untuk menyimpan daftar data pengguna
    private val list: MutableList<User> = mutableListOf()

    // Inisialisasi objek UserAdapter untuk menghubungkan data dengan RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var progressBar: ProgressBar

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi UI
        recyclerView = findViewById(R.id.recycler_view)
        btnAdd = findViewById(R.id.btn_add)
        progressBar = findViewById(R.id.progress_bar)

        // Inisialisasi adapter dan konfigurasi RecyclerView
        userAdapter = UserAdapter(this, list)
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        val decoration = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)

        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(decoration)
        recyclerView.adapter = userAdapter

        // Mengambil data dari Firestore dan memasukkannya ke dalam adapter
        db.collection("users")
            //mengurutkan data dari yang terbaru
            .orderBy("creationTime", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                // Membersihkan daftar sebelum menambahkan data baru
                list.clear()

                // Memeriksa apakah pengambilan data berhasil
                if (task.isSuccessful) {
                    // Iterasi melalui setiap dokumen hasil pengambilan data
                    for (document in task.result!!) {
                        // Membuat objek User dari data dokumen
                        val user = User(
                            id = document.id,
                            name = document.getString("name"),
                            email = document.getString("email"),
                            phone = document.getString("phone")
                        )
                        // Menambahkan objek User ke dalam daftar
                        list.add(user)
                    }
                    // Memberi tahu adapter bahwa data telah berubah
                    userAdapter.notifyDataSetChanged()
                } else {
                    // Menampilkan pesan kesalahan jika pengambilan data gagal
                    Toast.makeText(applicationContext, "Data gagal diambil!", Toast.LENGTH_SHORT).show()
                }
                // Menghilangkan indikator loading setelah pengambilan data selesai
                progressBar.visibility = View.GONE
            }

        // Menangani klik tombol tambah
        btnAdd.setOnClickListener { v ->
            // Berpindah ke EditorActivity untuk menambahkan data baru
            startActivity(Intent(applicationContext, EditorActivity::class.java))
        }
    }
}