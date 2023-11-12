package com.example.storyapp.ui.activities.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.utils.DataStatus
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get intent
        val id = intent.getStringExtra(EXTRA_ID) ?: ""

        //viewModel
        val detailViewModelFactory = DetailViewModelFactory.getInstance(this)
        val detailViewModel : DetailViewModel by viewModels {
            detailViewModelFactory
        }

        //get detail story
        detailViewModel.getDetailStory(id)

        //observe detail story
        lifecycleScope.launch {
            detailViewModel.story.observe(this@DetailActivity) {
                when (it.status) {
                    DataStatus.Status.SUCCESS -> {
                        binding.progressBarDetail.visibility = android.view.View.GONE
                        binding.tvDetailName.text = it.data?.story?.name
                        binding.tvDetailDesc.text = it.data?.story?.description
                        Glide.with(this@DetailActivity)
                            .load(it.data?.story?.photoUrl)
                            .into(binding.ivDetailPhoto)

                    }
                    DataStatus.Status.ERROR -> {
                        binding.progressBarDetail.visibility = android.view.View.GONE
                        Log.e("DetailActivity", it.message.toString())
                    }
                    DataStatus.Status.LOADING -> {
                        binding.progressBarDetail.visibility = android.view.View.VISIBLE
                    }
                }
            }
        }


    }



    companion object {
        const val EXTRA_ID: String = "extra_id"
    }
}