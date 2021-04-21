package com.krl.testcase.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krl.testcase.databinding.DocumentItemViewBinding

class ImageAdapter(val context: Context, val imageList: List<Uri>) :
    RecyclerView.Adapter<ImageAdapter.CustomViewHolder>() {

    private lateinit var binding: DocumentItemViewBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        binding = DocumentItemViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return CustomViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bindItems(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(uri: Uri) {
            binding.ivImage.setImageURI(uri)
        }
    }
}