package com.zam.storyapp.ui.story

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.zam.storyapp.R
import com.zam.storyapp.databinding.ActivityAddStoryBinding
import com.zam.storyapp.ui.ViewModelFactory
import com.zam.storyapp.ui.homelist.ListStoryActivity
import com.zam.storyapp.utils.ResultProcess
import com.zam.storyapp.utils.reduceFileImage
import com.zam.storyapp.utils.rotateBitmap
import com.zam.storyapp.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var getFile: File? = null
    private var result: Bitmap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!cameraPermissionsGranted()) {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.title_permission_denied))
                    setMessage(getString(R.string.message_camera_denied))
                    setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                    create()
                    show()
                }
            }
            if (cameraPermissionsGranted()) {
                startCameraX()
            }
        }
    }

    private fun cameraPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Tambah Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()

        val resultCode = intent.getIntExtra("resultCode", 0)
        if (resultCode == GALLERY_RESULT) {
            startGallery()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.buttonCamera.setOnClickListener { accessCamera() }
        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
        binding.switch2.setOnClickListener { getMyLocation() }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(binding.root.context)
        addStoryViewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]

    }

    private fun accessCamera() {
        if (!cameraPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            startCameraX()
        }
    }


    private fun uploadStory() {
        addStoryViewModel.getUser().observe(this@AddStoryActivity){

            val token = "Bearer " +it.token
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)

                val description = "${binding.edtDescription.text}".toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                addStoryViewModel.getUser().observe(this@AddStoryActivity){
                    addStoryViewModel.addStory(token, imageMultipart, description, lat, lon)
                        .observe(this@AddStoryActivity){ it ->
                            when(it){
                                is ResultProcess.Success -> {
                                    showLoading(false)
                                    println("test consolee=====")
                                    println("token add story$token ")
                                    println("image$imageMultipart")
                                    println("deskripsi$description")
                                    println("lat$lat")
                                    println("lon$lon")
                                    startActivity(Intent(this@AddStoryActivity,ListStoryActivity::class.java))
                                    finish()

                                }
                                is ResultProcess.Loading ->{
                                    showLoading(true)
                                }
                                is ResultProcess.Error ->{
                                    Toast.makeText(this@AddStoryActivity, it.error, Toast.LENGTH_SHORT).show()
                                    errorHandler()
                                }
                            }
                        }

                }
            } else {
                Toast.makeText(this@AddStoryActivity, getString(R.string.input_image_first), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loading.visibility = View.VISIBLE
        } else {
            binding.loading.visibility = View.GONE
        }
    }

    private fun errorHandler() {
        com.zam.storyapp.ui.customview.AlertDialog(
            this,
            R.string.message_eror,
            R.drawable.server_eror
        ).show()
    }


    private fun startCameraX() {
        launcherIntentCameraX.launch(Intent(this, CameraActivity::class.java))
    }

    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            result =
                rotateBitmap(
                    BitmapFactory.decodeFile(getFile?.path),
                    isBackCamera
                )
        }
        binding.ivItemPhoto.setImageBitmap(result)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    @Suppress("DEPRECATION")
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.ivItemPhoto.setImageURI(selectedImg)
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

    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude

                    print("lat muzam : $lat")
                    print("lon muzam : $lon")
                    Toast.makeText(
                        this,
                        "Lokasi tersimpan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Not found. Aktifkan lokasi Terlebih dahulu",
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

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return true
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val GALLERY_RESULT = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}