//package com.example.fitness
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import com.example.fitness.ui.user.UserViewModel
//import androidx.navigation.compose.rememberNavController
//
//@Composable
//fun RegisterScreen(navController: NavController, viewModel: UserViewModel = viewModel()) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//    var username by remember { mutableStateOf("") }
//
//    val isRegisterEnabled by derivedStateOf {
//        email.isNotBlank() && password.isNotBlank() && password == confirmPassword && username.isNotBlank()
//    }
//
//    var registerError by remember { mutableStateOf<String?>(null) }
//    val registrationState by viewModel.registrationState.collectAsState()
//
//    LaunchedEffect(registrationState) {
//        when (registrationState) {
//            is UserViewModel.RegistrationState.Success -> {
//                registerError = null
//                navController.popBackStack()
//            }
//            is UserViewModel.RegistrationState.Error -> {
//                registerError = (registrationState as UserViewModel.RegistrationState.Error).message
//            }
//            is UserViewModel.RegistrationState.Loading -> {
//                registerError = null
//            }
//            UserViewModel.RegistrationState.Idle -> {
//                registerError = null
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
//    ) {
//        Text(text = "Đăng ký", style = MaterialTheme.typography.headlineMedium)
//
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text("Tên đăng nhập") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Mật khẩu") },
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        OutlinedTextField(
//            value = confirmPassword,
//            onValueChange = { confirmPassword = it },
//            label = { Text("Xác nhận mật khẩu") },
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Button(
//            onClick = {
//                viewModel.register(
//                    email = email,
//                    password = password,
//                    username = username,
//                    age = 0, // Các trường tùy chọn có thể để mặc định hoặc xử lý sau
//                    gender = "",
//                    height = 0.0,
//                    weight = 0.0
//                )
//            },
//            enabled = isRegisterEnabled,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Đăng ký")
//        }
//
//        if (registerError != null) {
//            Text(text = registerError!!, color = MaterialTheme.colorScheme.error)
//        }
//
//        TextButton(onClick = { navController.popBackStack() }) {
//            Text("Đã có tài khoản? Đăng nhập")
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun RegisterScreenPreview() {
//    val navController = rememberNavController()
//    RegisterScreen(navController = navController)
//}