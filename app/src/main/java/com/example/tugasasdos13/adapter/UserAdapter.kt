package com.example.tugasasdos13.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasasdos13.R
import com.example.tugasasdos13.model.User
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.example.tugasasdos13.EditorActivity

// Adapter untuk RecyclerView yang menampilkan daftar pengguna
class UserAdapter(
    private val context: Activity,         // Aktivitas yang memakai adapter
    private val list: MutableList<User>    // Daftar pengguna yang akan ditampilkan dalam RecyclerView
) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    // Dialog untuk menangani klik pada item RecyclerView
    private var dialog: Dialog? = null

    // Objek Firebase Firestore untuk berinteraksi dengan database
    private val db = FirebaseFirestore.getInstance()

    // Interface untuk menangani klik pada item RecyclerView
    interface Dialog {
        fun onClick(pos: Int)
    }

    // Mengatur dialog yang akan menangani klik pada item RecyclerView
    fun setDialog(dialog: Dialog) {
        this.dialog = dialog
    }

    // Fungsi ini dipanggil ketika RecyclerView memerlukan ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Membuat dan mengembalikan ViewHolder baru
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_user, parent, false)
        return MyViewHolder(itemView)
    }

    // Fungsi ini dipanggil untuk menetapkan data ke ViewHolder sesuai dengan posisi yang ditentukan
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Mendapatkan item pada posisi tertentu dari daftar
        val currentItem = list[position]

        // Menetapkan nilai teks untuk TextView di dalam ViewHolder
        holder.name.text = currentItem.name
        holder.email.text = currentItem.email
        holder.phone.text = currentItem.phone

        // Menambahkan OnClickListener ke setiap item di RecyclerView
        holder.itemView.setOnClickListener {
            // Membuat dialog konfirmasi dengan opsi tertentu
            AlertDialog.Builder(context).apply {
                setTitle("Pilih opsi untuk '${currentItem.name}'")
                setItems(R.array.items_option) { dialog, which ->
                    when (which) {
                        0 -> {
                            // Opsi: Ubah
                            // Membuat intent untuk memulai EditorActivity dengan data pengguna
                            val intent = Intent(context, EditorActivity::class.java).apply {
                                putExtra("EXTRA_FULLNAME", currentItem.name)
                                putExtra("EXTRA_EMAIL", currentItem.email)
                                // Perlu menambahkan field 'phone' ke model User Anda
                                putExtra("EXTRA_PHONE", currentItem.phone)
                                putExtra("EXTRA_ID", currentItem.id)
                            }
                            // Memulai EditorActivity
                            context.startActivity(intent)
                        }
                        1 -> {
                            // Opsi: Hapus
                            // Mendapatkan ID pengguna
                            val itemId = currentItem.id
                            // Memeriksa apakah ID tidak null
                            if (itemId != null) {
                                // Menghapus dokumen dari koleksi 'users' di Firestore
                                db.collection("users").document(itemId).delete()
                                    .addOnSuccessListener {
                                        // Dokumen berhasil dihapus dari database
                                        // Menghapus item dari daftar dan memberitahu adapter untuk memperbarui UI
                                        list.removeAt(position)
                                        notifyDataSetChanged()
                                    }
                                    .addOnFailureListener {
                                        // Menampilkan pesan kesalahan jika penghapusan gagal
                                        Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                // Menampilkan pesan jika ID tidak ditemukan
                                Toast.makeText(context, "ID tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        }
                        2 -> {
                            // Opsi: Batal
                            // Menutup dialog
                            dialog.dismiss()
                        }
                    }
                }
                // Membuat dialog tidak dapat dibatalkan
                setCancelable(false)
                // Menampilkan dialog
                show()
            }
        }
    }

    // Fungsi ini mengembalikan jumlah item dalam dataset
    override fun getItemCount(): Int {
        return list.size
    }

    // Inner class untuk ViewHolder yang mendeklarasikan tampilan dan metode untuk mengikat data ke tampilan
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Mendeklarasikan TextView untuk setiap item di RecyclerView
        var name: TextView = itemView.findViewById(R.id.name)
        var email: TextView = itemView.findViewById(R.id.email)
        var phone: TextView = itemView.findViewById(R.id.phone)

        init {
            // Menambahkan OnClickListener ke setiap item di RecyclerView
            itemView.setOnClickListener { view ->
                // Menangani klik item dengan memanggil fungsi onClick dari interface Dialog
                dialog?.onClick(layoutPosition)
            }
        }
    }
}
