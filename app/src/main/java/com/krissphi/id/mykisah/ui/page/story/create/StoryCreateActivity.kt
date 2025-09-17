package com.krissphi.id.mykisah.ui.page.story.create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.krissphi.id.mykisah.R
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.krissphi.id.mykisah.data.repository.ViewModelFactory
import com.krissphi.id.mykisah.databinding.ActivityStoryCreateBinding
import com.krissphi.id.mykisah.utils.getImageUri
import com.krissphi.id.mykisah.utils.reduceFileImage
import com.krissphi.id.mykisah.utils.uriToFile

class StoryCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryCreateBinding
    private var currentImageUri: Uri? = null
    private val viewModel: StoryCreateViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentLat: Double? = null
    private var currentLon: Double? = null

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, getString(R.string.permission_camera), Toast.LENGTH_LONG)
                    .show()
            }
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }

                else -> {
                    Toast.makeText(
                        this,
                        R.string.location_permission_denied,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.switchLocation.isChecked = false
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupToolbar()
        setupAction()
        setupObserver()
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener {
            if (!isCameraPermissionGranted()) {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                openCamera()
            }
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.btnUpload.setOnClickListener {
            uploadStory()
        }

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLastLocation()
            } else {
                currentLat = null
                currentLon = null
            }
        }
    }

    private fun openCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let { uri -> cameraLauncher.launch(uri) }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentImageUri?.let { updatePreviewImage(it) }
        }
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            updatePreviewImage(uri)
        }
    }

    private fun updatePreviewImage(uri: Uri) {
        currentImageUri = uri
        binding.imgPreview.setImageURI(uri)
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLat = location.latitude
                    currentLon = location.longitude
                } else {
                    Toast.makeText(
                        this,
                        R.string.location_off,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadStory() {
        val description = binding.edtDescription.text.toString().trim()

        if (currentImageUri == null) {
            Toast.makeText(this, getString(R.string.choose_image), Toast.LENGTH_SHORT).show()
            return
        }
        if (description.isBlank()) {
            Toast.makeText(this, getString(R.string.form_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val imageFile = uriToFile(currentImageUri!!, this).reduceFileImage()
        viewModel.uploadStory(
            imageFile,
            description,
            currentLat,
            currentLon
        )
    }

    private fun setupObserver() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.uploadResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            result.onFailure { error ->
                Toast.makeText(
                    this,
                    getString(R.string.error_message, error.message),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}