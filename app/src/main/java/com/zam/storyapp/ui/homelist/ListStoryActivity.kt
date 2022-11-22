package com.zam.storyapp.ui.homelist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.zam.storyapp.R
import com.zam.storyapp.adapter.LoadingStateAdapter
import com.zam.storyapp.adapter.paging.StoryPagingAdapter
import com.zam.storyapp.databinding.ActivityListStoryBinding
import com.zam.storyapp.ui.ViewModelFactory
import com.zam.storyapp.ui.login.LoginActivity
import com.zam.storyapp.ui.login.LoginViewModel
import com.zam.storyapp.ui.maps.GoogleMapsActivity
import com.zam.storyapp.ui.story.AddStoryActivity

class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListStoryBinding
    private lateinit var storyAdapter: StoryPagingAdapter
    private lateinit var factory: ViewModelFactory
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var listStoryViewModel: ListStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Float? = null
    private var lon: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.setHasFixedSize(true)
        setupViewModel()
        storyAdapter = StoryPagingAdapter()

        listStoryViewModel.getUser().observe(this@ListStoryActivity){ user ->
            if (user.isLogin){
                getStory()
            }
            else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)

        listStoryViewModel = ViewModelProvider(this, factory)[ListStoryViewModel::class.java]
        loginViewModel= ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun getStory() {
        binding.rvStory.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        listStoryViewModel.getStory().observe(this@ListStoryActivity) {
            storyAdapter.submitData(lifecycle, it)
            showLoading(false)
        }
    }

    private fun logout() {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_info))
            setMessage(getString(R.string.message_logout))
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                loginViewModel.logout()
                startActivity(Intent(this@ListStoryActivity, LoginActivity::class.java))
                finishAffinity()
            }
            setNegativeButton("tidak") { dialog, _ -> dialog.cancel() }
            create()
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps -> {
                startActivity(Intent(this, GoogleMapsActivity::class.java))
            }
            R.id.bahasa -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.keluar -> {
                logout()
            }
        }
        return super.onOptionsItemSelected(item)
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
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude.toFloat()
                    lon = location.longitude.toFloat()
                    Toast.makeText(
                        this,
                        getString(R.string.location_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION)
            (Manifest.permission.ACCESS_COARSE_LOCATION)

        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading){
            binding.loading.visibility = View.VISIBLE
        }
        else {
            binding.loading.visibility = View.GONE
        }
    }

    companion object {
        const val KEY_STORY = "story"
    }
}