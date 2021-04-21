package com.krl.testcase

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.krl.testcase.Adapter.ImageUploadAdapter
import com.krl.testcase.databinding.ActivityDocumentUploadBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.IOException

class DocumentUploadActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityDocumentUploadBinding
    private lateinit var adapter: ImageUploadAdapter
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setAdapter()
    }

    fun initView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        binding.rlRetake.setOnClickListener(this)
        binding.rlClose.setOnClickListener(this)
        binding.rlSend.setOnClickListener(this)
        binding.rlTakePhoto.setOnClickListener(this)
    }

    private fun setAdapter() {
        adapter = ImageUploadAdapter(this, DataSource().get().toList())
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun takePhoto() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted()!!) {

                        try {
                            if (ActivityCompat.checkSelfPermission(
                                    this@DocumentUploadActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this@DocumentUploadActivity,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            )
                                return
                            openCamera()
                        } catch (e: IOException) {
                        }
                    }
                }
            }
            ).check()
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1920, 1080)
                .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
                .start(this)
        } else if (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                DataSource().push(result.uri!!)
                setAdapter()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(
                    this@DocumentUploadActivity,
                    "Crop sırasında bir hata ile karşılaşıldı!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.rlRetake -> {
                if (DataSource().pop()) {
                    Toast.makeText(
                        this@DocumentUploadActivity,
                        "Son yüklenen fotograf silindi!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setAdapter()
            }
            binding.rlClose -> {
                finish()
            }
            binding.rlTakePhoto -> {
                takePhoto()
            }
            binding.rlSend -> {
                if (DataSource().isEmpty()) {
                    Toast.makeText(
                        this@DocumentUploadActivity,
                        "Lütfen fotoğraf çekiniz!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                finish()
            }
        }
    }
}