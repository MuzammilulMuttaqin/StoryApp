package com.zam.storyapp.ui.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.zam.storyapp.R
import com.zam.storyapp.data.source.model.StoryResponse
import com.zam.storyapp.databinding.ActivityGoogleMapsBinding
import com.zam.storyapp.ui.ViewModelFactory
import com.zam.storyapp.ui.detaillist.DetailStoryActivity
import com.zam.storyapp.utils.ResultProcess

class GoogleMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGoogleMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private val boundBuilder = LatLngBounds.Builder()
    private var iteratorPage: Int = 0
    private var isFirstStory: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGoogleMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iteratorPage = 1
        supportActionBar?.title = "Maps Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        print("EROROOOO BROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setupStory()
        getMyLocation()
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        mapsViewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]
    }

    private fun setupStory() {
        mapsViewModel.getUser().observe(this){ it ->
            val token = "Bearer " +it.token
            mapsViewModel.getStoryLocation(token).observe(this){
                when(it){
                    is ResultProcess.Loading -> {}
                    is ResultProcess.Success -> showMarker(it.data.listStory)
                    is ResultProcess.Error -> Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showMarker(data: List<StoryResponse.Story>) {
        data.forEach {
            val latLng = it.lon?.let { it1 -> it.lat?.let { it2 -> LatLng(it2, it1) } }
            val marker = latLng?.let { it1 ->
                MarkerOptions()
                    .position(it1)
                    .title(getString(R.string.story_from)+it.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                    .alpha(0.7f)
                    .snippet(it.description)
            }?.let { it2 ->
                mMap.addMarker(
                    it2
                )
            }
            latLng?.let { it1 -> CameraUpdateFactory.newLatLng(it1) }
                ?.let { it2 -> mMap.moveCamera(it2) }
            if (latLng != null) {
                boundBuilder.include(latLng)
            }
            marker?.tag = it
            mMap.setOnInfoWindowClickListener {
                val intent = Intent(this, DetailStoryActivity::class.java).apply {
                    putExtra(DetailStoryActivity.EXTRA_DETAIL, it.tag as StoryResponse.Story)
                }
                startActivity(intent)
            }
            if (!isFirstStory) {
                latLng?.let { it1 -> CameraUpdateFactory.newLatLng(it1) }
                    ?.let { it2 -> mMap.moveCamera(it2) }
                isFirstStory = true
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    @Suppress("DEPRECATION")
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}