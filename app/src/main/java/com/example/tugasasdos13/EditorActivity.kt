package com.example.tugasasdos13

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EditorActivity : AppCompatActivity() {

    // Deklarasi variabel UI
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var btnsave: Button
    private lateinit var progressBar: ProgressBar

    // Mendapatkan instance Firebase Firestore
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        // Inisialisasi UI
        editName = findViewById(R.id.et_fullname)
        editEmail = findViewById(R.id.et_Email)
        editPhone = findViewById(R.id.et_Phone)
        btnsave = findViewById(R.id.btn_save)
        progressBar = findViewById(R.id.progressBar)

        // Menetapkan nilai EditText jika intent memiliki data ekstra
        if (intent.hasExtra("EXTRA_FULLNAME")) {
            editName.setText(intent.getStringExtra("EXTRA_FULLNAME"))
            editEmail.setText(intent.getStringExtra("EXTRA_EMAIL"))
            editPhone.setText(intent.getStringExtra("EXTRA_PHONE"))
        }

        // Menangani klik tombol Simpan
        btnsave.setOnClickListener { v ->
            if (editName.text.isNotEmpty() && editEmail.text.isNotEmpty() && editPhone.text.isNotEmpty()) {
                showProgressBar(true)
                if (intent.hasExtra("EXTRA_ID")) {
                    // Jika intent memiliki ID ekstra, panggil fungsi updateData
                    updateData(
                        editName.text.toString(),
                        editEmail.text.toString(),
                        editPhone.text.toString(),
                        intent.getStringExtra("EXTRA_ID")!!
                    )
                } else {
                    // Jika tidak ada ID ekstra, panggil fungsi saveData
                    saveData(
                        editName.text.toString(),
                        editEmail.text.toString(),
                        editPhone.text.toString()
                    )
                }
            } else {
                Toast.makeText(applicationContext, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menampilkan atau menyembunyikan ProgressBar
    private fun showProgressBar(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    // Fungsi untuk menyimpan data ke Firestore
    private fun saveData(name: String, email: String, phone: String) {
        // Membuat data pengguna
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "creationTime" to FieldValue.serverTimestamp()
        )

        // Menambahkan data ke koleksi "users" di Firestore
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                // Menampilkan pesan sukses
                Toast.makeText(applicationContext, "Data saved successfully", Toast.LENGTH_SHORT).show()

                // Pindah ke MainActivity setelah data disimpan
                val intent = Intent(this@EditorActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // Menampilkan pesan kesalahan
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                // Menyembunyikan ProgressBar setelah tugas selesai
                showProgressBar(false)
            }
    }

    // Fungsi untuk memperbarui data di Firestore
    private fun updateData(name: String, email: String, phone: String, id: String) {
        // Membuat data pengguna yang akan diperbarui
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "creationTime" to FieldValue.serverTimestamp()
        )

        // Memperbarui data di dokumen dengan ID tertentu di koleksi "users" di Firestore
        db.collection("users")
            .document(id)
            .set(user)
            .addOnSuccessListener {
                // Menampilkan pesan sukses
                Toast.makeText(applicationContext, "Data updated successfully", Toast.LENGTH_SHORT).show()

                // Pindah ke MainActivity setelah data diperbarui
                val intent = Intent(this@EditorActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // Menampilkan pesan kesalahan
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                // Menyembunyikan ProgressBar setelah tugas selesai
                showProgressBar(false)
            }
    }
}
