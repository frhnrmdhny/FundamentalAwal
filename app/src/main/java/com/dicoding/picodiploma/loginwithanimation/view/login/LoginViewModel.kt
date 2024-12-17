package com.dicoding.picodiploma.loginwithanimation.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    data class LoginResult(
        val isSuccessful: Boolean,
        val message: String,
        val token: String? = null,
    )

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)

                if (response.error) {
                    _loginResult.value = LoginResult(false, response.message)
                } else {
                    val user = UserModel(
                        email = email,
                        token = response.loginResult.token,
                        userId = response.loginResult.userId,
                        name = response.loginResult.name,
                        isLogin = true
                    )
                    repository.saveSession(user)
                    _loginResult.value =
                        LoginResult(true, "Login berhasil", response.loginResult.token)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login gagal: ${e.localizedMessage}"
                _loginResult.value = LoginResult(false, "Error! Harap coba lagi nanti.")
            } finally {
                _isLoading.value = false
            }
        }
    }


}