package com.example.randomuser.ui.view

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.randomuser.*
import com.example.randomuser.base.ViewModelFactory
import com.example.randomuser.data.NetworkCallResult
import com.example.randomuser.data.models.User
import com.example.randomuser.databinding.ActivityMainBinding
import com.example.randomuser.ui.viewmodel.UserViewModel
import com.example.randomuser.util.ImagePicker
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private lateinit var menu: Menu

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private var viewModel: UserViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        (applicationContext as RandUserApp).appComponent.inject(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[UserViewModel::class.java]

        val imagePicker = ImagePicker(activityResultRegistry, this) {
            if (it != null) {
                Glide.with(this)
                    .load(it)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding?.userPic!!)
            }
        }

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
            disableEditMode()
            menu.findItem(R.id.action_save).isVisible = false
            menu.findItem(R.id.action_edit).isVisible = true

        }

        binding?.linearLayout?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.hideKeyboard()
            }
        }

        binding?.userPic?.setOnClickListener {
            if (it.isFocusable) {
                imagePicker.pickImage()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        this.menu = menu!!
        return super.onCreateOptionsMenu(menu)
    }

    private fun peekBirthDate() {
        val userDate = binding?.birthLayout?.birthD?.text.toString()
        val calendar = getDateForCalendar(userDate)
        val listener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                binding?.birthLayout?.birthD?.setText(
                    getStringFromPicked(year, month + PLUS_MONTH, dayOfMonth)
                )
            }
        DatePickerDialog(
            this,
            listener,
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        ).show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_edit -> {
            enableEditMode()
            menu.findItem(R.id.action_edit).isVisible = false
            menu.findItem(R.id.action_save).isVisible = true
            true
        }
        R.id.action_save -> {
            disableEditMode()
            binding?.linearLayout?.requestFocus()
            menu.findItem(R.id.action_save).isVisible = false
            menu.findItem(R.id.action_edit).isVisible = true
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setUpUI(user: User) {
        Glide.with(this)
            .load(user.results[0].picture.medium)
            .apply(RequestOptions.circleCropTransform())
            .into(binding?.userPic!!)

        binding?.userName?.setText(
            getString(
                R.string.username_template,
                user.results[0].name.title,
                user.results[0].name.first,
                user.results[0].name.last
            )
        )
        binding?.birthLayout?.birthD?.setText(getFormattedDate(user.results[0].dob.date))
        binding?.phoneLayout?.phoneNumber?.setText(user.results[0].phone)
        binding?.countryLayout?.countryName?.setText(user.results[0].location.country)
        binding?.addressLayout?.cityName?.setText(user.results[0].location.city)
        binding?.addressLayout?.address?.setText(
            getString(
                R.string.address_template,
                user.results[0].location.street.name,
                user.results[0].location.street.number
            )
        )

        binding?.coordLayout?.coordinatesLat?.setText(
            getString(
                R.string.coordinates_template,
                user.results[0].location.coordinates.latitude
            )
        )

        binding?.coordLayout?.coordinatesLong?.setText(
            getString(
                R.string.coordinates_template,
                user.results[0].location.coordinates.longitude
            )
        )

        binding?.coordLayout?.root?.setOnClickListener {
            val latitude = binding?.coordLayout?.coordinatesLat?.text?.toString()
            val longitude = binding?.coordLayout?.coordinatesLong?.text?.toString()
            val uri = Uri.parse("geo:${latitude},${longitude}?q=${latitude},${longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding?.phoneLayout?.root?.setOnClickListener {
            val phoneNumber = binding?.phoneLayout?.phoneNumber?.text?.toString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }
    }

    private fun disableEditMode() {
        binding?.userPic?.isFocusable = false
        binding?.userName?.isFocusableInTouchMode = false
        binding?.birthLayout?.birthD?.setOnClickListener(null)
        binding?.phoneLayout?.phoneNumber?.isFocusableInTouchMode = false
        binding?.countryLayout?.countryName?.isFocusableInTouchMode = false
        binding?.addressLayout?.address?.isFocusableInTouchMode = false
        binding?.addressLayout?.cityName?.isFocusableInTouchMode = false
        binding?.coordLayout?.coordinatesLat?.isFocusableInTouchMode = false
        binding?.coordLayout?.coordinatesLong?.isFocusableInTouchMode = false
    }

    private fun enableEditMode() {
        binding?.userPic?.isFocusable = true
        binding?.userName?.isFocusableInTouchMode = true
        binding?.birthLayout?.birthD?.setOnClickListener { peekBirthDate() }
        binding?.phoneLayout?.phoneNumber?.isFocusableInTouchMode = true
        binding?.countryLayout?.countryName?.isFocusableInTouchMode = true
        binding?.addressLayout?.address?.isFocusableInTouchMode = true
        binding?.addressLayout?.cityName?.isFocusableInTouchMode = true
        binding?.coordLayout?.coordinatesLat?.isFocusableInTouchMode = true
        binding?.coordLayout?.coordinatesLong?.isFocusableInTouchMode = true


    }

    companion object {
        private const val PLUS_MONTH = 1
    }

}