package com.example.storyapp.ui.activities.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.utils.DataStatus
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.storyapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //viewModel
        val mapsViewModelFactory = MapsViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, mapsViewModelFactory)[MapsViewModel::class.java]

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_json
                )
            )
            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.")
            }
        } catch (e: Exception) {
            Log.e("MapsActivity", e.toString())
        }

        viewModel.stories.observe(this) {
            when (it.status) {
                DataStatus.Status.SUCCESS -> {
                    showLoading(false)
                    showLocationStories(mMap, it.data?.listStory as List<ListStoryItem>)
                }
                DataStatus.Status.ERROR -> {
                    showLoading(false)
                    Log.e("MainActivity", it.message.toString())
                }
                DataStatus.Status.LOADING -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showLocationStories(mMap: GoogleMap ,stories: List<ListStoryItem>) {
        val builder = LatLngBounds.Builder()

        for (story in stories) {
            val lat : Double = story.lat ?: 0.0
            val lon : Double = story.lon ?: 0.0
            val location = LatLng(lat, lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(story.name)
                    .snippet(story.description)
            )
            builder.include(location)
        }
        val bounds = builder.build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarMap.visibility = android.view.View.VISIBLE
        } else {
            binding.progressBarMap.visibility = android.view.View.GONE
        }
    }

}