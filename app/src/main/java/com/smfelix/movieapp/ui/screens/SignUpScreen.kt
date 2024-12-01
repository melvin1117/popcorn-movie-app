package com.smfelix.movieapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.smfelix.movieapp.R
import com.smfelix.movieapp.data.User
import com.smfelix.movieapp.service.AuthService

@Composable
fun SignupScreen(navController: NavController, authService: AuthService, onUserCreated: (User) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_round),
            contentDescription = "App Logo",
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Popcorn",
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.white)
        )
        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = { Text("Email") },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                cursorColor = colorResource(id = R.color.accent),
                focusedTextColor = colorResource(id = R.color.accent),
                focusedLabelColor = colorResource(id = R.color.accent),
                focusedIndicatorColor = colorResource(id = R.color.accent)
            )
        )
        if (emailError != null) {
            Text(text = emailError!!, color = colorResource(id = R.color.red), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))

        
        TextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = { Text("Password") },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                cursorColor = colorResource(id = R.color.accent),
                focusedTextColor = colorResource(id = R.color.accent),
                focusedLabelColor = colorResource(id = R.color.accent),
                focusedIndicatorColor = colorResource(id = R.color.accent)
            )
        )
        if (passwordError != null) {
            Text(text = passwordError!!, color = colorResource(id = R.color.red), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        generalError?.let {
            Text(text = it, color = colorResource(id = R.color.red), fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        Button(
            onClick = {
                emailError = if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    "Invalid email address"
                } else null

                passwordError = if (password.length < 6) {
                    "Password must be at least 6 characters"
                } else null

                if (emailError == null && passwordError == null) {
                    authService.signupUser(email, password) { success, userId, error ->
                        if (success && userId != null) {
                            val newUser = User(
                                userId = userId,
                                name = "",
                                email = email,
                                profilePhotoUrl = null,
                                dateOfBirth = "",
                                gender = "",
                                mobileNumber = "",
                                shortBio = "",
                                favorites = emptyList()
                            )
                            authService.updateUserProfile(userId, newUser) { updateSuccess, _ ->
                                if (updateSuccess) {
                                    Toast.makeText(context, "Signup successful!", Toast.LENGTH_SHORT).show()
                                    onUserCreated(newUser)
                                    navController.navigate("user_profile") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                } else {
                                    generalError = "Failed to save user data."
                                }
                            }
                        } else {
                            generalError = error
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.accent))
        ) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text(text = "Already have an account? Login", color = colorResource(id = R.color.accent))
        }
    }
}
