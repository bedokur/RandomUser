package com.example.randomuser.ui.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.randomuser.RandUserApp
import com.example.randomuser.data.NetworkCallResult
import com.example.randomuser.databinding.ActivityMainBinding
import com.example.randomuser.di.ViewModelFactory
import com.example.randomuser.ui.viewmodel.UserViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var viewModel: UserViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        (applicationContext as RandUserApp).appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        Log.wtf("onCreate", "$viewModel")
        viewModel?.userData?.observe(this, {
            when (it) {
                is NetworkCallResult.Loading -> binding?.swipeRefreshLayout?.isRefreshing = true
                is NetworkCallResult.Success -> {
                    binding?.textView?.text = it.data?.results?.get(0)?.name?.first
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                is NetworkCallResult.Error ->
                    binding?.textView?.text = it.message
            }
        })

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel?.getUserData()
        }
    }
}