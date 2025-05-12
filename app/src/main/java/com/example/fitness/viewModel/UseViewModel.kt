package com.example.fitness.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitness.repository.UserRepository // Đã sửa import
import com.example.fitness.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // StateFlow để theo dõi trạng thái đăng ký
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    // StateFlow để theo dõi trạng thái đăng nhập
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // LiveData để giữ thông tin người dùng hiện tại
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    // Hàm xử lý đăng ký
    fun register(email: String, password: String, username: String, age: Int, gender: String, height: Double, weight: Double) {
        _registrationState.value = RegistrationState.Loading
        viewModelScope.launch {
            userRepository.getUserByEmail(email).collectLatest { existingUser ->
                if (existingUser != null) {
                    _registrationState.value = RegistrationState.Error("Email đã tồn tại")
                } else {
                    val newUser = User(
                        email = email,
                        password = password,
                        username = username, // Sử dụng username
                        age = age,
                        gender = gender,
                        height = height,
                        weight = weight
                    )
                    userRepository.insertUser(newUser)
                    _registrationState.value = RegistrationState.Success
                }
            }
            // Bạn có thể thêm logic kiểm tra khác nếu cần
        }
    }

    // Hàm xử lý đăng nhập
    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val isLoggedIn = userRepository.checkLogin(email, password)
            if (isLoggedIn) {
                userRepository.getUserByEmail(email).collectLatest { user ->
                    _currentUser.value = user
                    _loginState.value = LoginState.Success(user)
                }
            } else {
                _loginState.value = LoginState.Error("Email hoặc mật khẩu không đúng")
            }
        }
    }

    // Hàm để lưu thông tin cá nhân (cập nhật user hiện tại)
    fun savePersonalInfo(age: Int, gender: String, height: Double, weight: Double) {
        // Lấy thông tin người dùng hiện tại (nếu đã đăng nhập)
        _currentUser.value?.let { currentUser ->
            val updatedUser = currentUser.copy(
                age = age,
                gender = gender,
                height = height,
                weight = weight
            )
            viewModelScope.launch {
                userRepository.updateUser(updatedUser)
                _currentUser.value = updatedUser // Cập nhật currentUser sau khi lưu
            }
        }
    }

    // Hàm để lấy thông tin người dùng theo ID (có thể dùng sau khi đăng nhập)
    fun getUserById(userId: Int) {
        viewModelScope.launch {
            userRepository.getUserById(userId).collectLatest { user ->
                _currentUser.value = user
            }
        }
    }

    // Các trạng thái cho đăng ký
    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    // Các trạng thái cho đăng nhập
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: User?) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}