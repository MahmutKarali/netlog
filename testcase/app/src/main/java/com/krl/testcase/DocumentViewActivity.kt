package com.krl.testcase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.krl.testcase.databinding.ActivityDocumentBinding
import uk.co.senab.photoview.PhotoViewAttacher

class DocumentViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocumentBinding
    private lateinit var pAttacher: PhotoViewAttacher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pAttacher = PhotoViewAttacher(binding.ivDocument)
        pAttacher.update()

        binding.rlClose.setOnClickListener {
            finish()
        }
    }
}