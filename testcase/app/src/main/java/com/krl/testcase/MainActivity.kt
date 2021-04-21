package com.krl.testcase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.krl.testcase.Adapter.ImageAdapter
import com.krl.testcase.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportMapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initView()

        getLocationPermission()
    }

    fun initView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        binding.llDocument.setOnClickListener(this)
        binding.btnUploadDocument.setOnClickListener(this)
    }

    fun getLocationPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
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
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            )
                                return
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location: Location? ->
                                    val currentLatLng =
                                        LatLng(location!!.latitude, location!!.longitude)
                                    mMap.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            currentLatLng,
                                            12f
                                        )
                                    )
                                    mMap.addMarker(
                                        MarkerOptions().position(currentLatLng)
                                            .title("Konumunuz")
                                    )
                                }
                        } catch (e: IOException) {
                            Toast.makeText(
                                this@MainActivity,
                                "Could not create file!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            ).check()
    }

    fun setAdapter() {
        adapter = ImageAdapter(this, DataSource().get().toList())
        binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.llDocument -> {
                startActivity(Intent(this, DocumentViewActivity::class.java))
            }
            binding.btnUploadDocument -> {
                startActivity(Intent(this, DocumentUploadActivity::class.java))
            }
        }
    }
}