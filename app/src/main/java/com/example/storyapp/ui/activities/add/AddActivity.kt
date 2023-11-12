package com.example.storyapp.ui.activities.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAddBinding
import com.example.storyapp.utils.DataStatus
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var addViewModel: AddViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location

    private var currentImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //viewModel
        val addViewModelFactory = AddViewModelFactory.getInstance(this)
        addViewModel = ViewModelProvider(this, addViewModelFactory)[AddViewModel::class.java]


        binding.apply {
            //disable button add
            buttonAdd.isEnabled = false

            galleryButton.setOnClickListener {
                startGallery()
            }
            cameraButton.setOnClickListener {
                startCamera()
            }

            edAddDescription.addTextChangedListener {
                buttonAdd.isEnabled = it.toString().isNotEmpty()
            }

            buttonAdd.setOnClickListener {
                val description = edAddDescription.text.toString()
                if (checkBox.isChecked) {
                    uploadStory(description, currentLocation.latitude, currentLocation.longitude)
                } else {
                    uploadStory(description, null, null)
                }
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLastLocation()
                } else {
                    tvLocation.text = ""
                }
            }

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        addViewModel.response.observe(this) {
            when (it.status) {
                DataStatus.Status.SUCCESS -> {
                    showLoading(false)
                    Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }

                DataStatus.Status.ERROR -> {
                    showLoading(false)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                DataStatus.Status.LOADING -> {
                    showLoading(true)
                }
            }
        }

    }

    private fun uploadStory(description: String, lat: Double?, lon: Double?) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            //request body include description, lat, lon
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
            val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            addViewModel.uploadStory(
                multipartBody,
                descriptionRequestBody,
                latRequestBody,
                lonRequestBody
            )
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private var launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
            } else {
                Log.d("Photo Picker", "No Media Selected")
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                setCurrentLocation(location)
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setCurrentLocation(location: Location?) {
        if (location != null) {
            currentLocation = location
            @SuppressLint("SetTextI18n")
            binding.tvLocation.text = "${currentLocation.latitude}, ${currentLocation.longitude}"
        } else {
            Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
        }
    }


    private fun showImage() {
        currentImageUri?.let {
            binding.ivPreviewImage.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                progressBarUpload.visibility = android.view.View.VISIBLE
                galleryButton.isEnabled = false
                cameraButton.isEnabled = false
                buttonAdd.isEnabled = false
            } else {
                progressBarUpload.visibility = android.view.View.GONE
                galleryButton.isEnabled = true
                cameraButton.isEnabled = true
                buttonAdd.isEnabled = true
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}