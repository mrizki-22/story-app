package com.example.storyapp.ui.activities.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.LoadingStateAdapter
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.activities.add.AddActivity
import com.example.storyapp.ui.activities.auth.AuthActivity
import com.example.storyapp.ui.activities.auth.AuthViewModel
import com.example.storyapp.ui.activities.auth.AuthViewModelFactory
import com.example.storyapp.ui.activities.maps.MapsActivity
import com.example.storyapp.ui.activities.setting.SettingActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // auth view model
        val authViewModelFactory = AuthViewModelFactory.getInstance(this)
        val authViewModel: AuthViewModel by viewModels { authViewModelFactory }

        val mainViewModelFactory = MainViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]

        // check if user is not logged in
        lifecycleScope.launch {
            if (!authViewModel.isLoggedIn()) {
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
            }
        }

        binding.rvStory.layoutManager = LinearLayoutManager(this)
        getData()

        //inflate menu
        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.settings -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                }
                R.id.maps -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                }
            }
            true
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rvStory.adapter = null
    }

    private fun getData(){
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        lifecycleScope.launch {
            mainViewModel.getStories().collectLatest {
                adapter.submitData(it)
            }
        }
    }
}


