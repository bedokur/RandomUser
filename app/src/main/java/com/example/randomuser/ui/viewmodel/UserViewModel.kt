package com.example.randomuser.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomuser.data.NetworkCallResult
import com.example.randomuser.data.models.User
import com.example.randomuser.data.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _userData = MutableLiveData<NetworkCallResult<User>>()
    val userData: LiveData<NetworkCallResult<User>> = _userData

    fun getUserData() {
        _userData.value = NetworkCallResult.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getProfile()
            if (result.isSuccessful) {
                _userData.postValue(NetworkCallResult.Success(result.body()!!))
            } else {
                _userData.postValue(NetworkCallResult.Error("Что-то пошло не так.."))
            }
        }
    }

    init {
        getUserData()
    }

}