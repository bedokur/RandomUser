package com.example.randomuser.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.randomuser.R
import com.example.randomuser.RandUserApp
import com.example.randomuser.base.ViewModelFactory
import com.example.randomuser.data.NetworkCallResult
import com.example.randomuser.data.models.User
import com.example.randomuser.databinding.ActivityMainBinding
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

        viewModel?.userData?.observe(this, {
            when (it) {
                is NetworkCallResult.Loading -> binding?.swipeRefreshLayout?.isRefreshing = true
                is NetworkCallResult.Success -> {
                    it.data?.let { user -> setUpUI(user) }
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                is NetworkCallResult.Error ->
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        })

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel?.getUserData()
        }


    }

    private fun setUpUI(user: User) {
        Glide.with(this)
            .load(user.results[0].picture.medium)
            .apply(RequestOptions.circleCropTransform())
            .into(binding?.userPic!!)

        binding?.userName?.text = getString(
            R.string.username_template,
            user.results[0].name.title,
            user.results[0].name.first,
            user.results[0].name.last
        )
        binding?.birthLayout?.birthD?.text = user.results[0].dob.date
        binding?.phoneLayout?.phoneNumber?.text = user.results[0].phone
        binding?.countryLayout?.countryName?.text = user.results[0].location.country
        binding?.addressLayout?.cityName?.text = user.results[0].location.city
        binding?.addressLayout?.address?.text = getString(
            R.string.address_template,
            user.results[0].location.street.name,
            user.results[0].location.street.number
        )

        binding?.coordLayout?.coordinates?.text = getString(
            R.string.coordinates_template,
            user.results[0].location.coordinates.latitude,
            user.results[0].location.coordinates.longitude
        )
        binding?.coordLayout?.root?.setOnClickListener {
            val latitude = user.results[0].location.coordinates.latitude
            val longitude = user.results[0].location.coordinates.longitude
            val uri = Uri.parse("geo:${latitude},${longitude}?q=${latitude},${longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }
    }
}