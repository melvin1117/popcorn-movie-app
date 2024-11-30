package com.smfelix.movieapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.smfelix.movieapp.R

@Composable
fun SignupScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo and Name
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_round),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(72.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Popcorn",
            style = MaterialTheme.typography.headlineMedium,
            color = colorResource(id = R.color.white)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Input Fields
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                cursorColor = colorResource(id = R.color.accent),
                focusedTextColor = colorResource(id = R.color.accent),
                focusedLabelColor = colorResource(id = R.color.accent),
                focusedIndicatorColor = colorResource(id = R.color.accent)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                cursorColor = colorResource(id = R.color.accent),
                focusedTextColor = colorResource(id = R.color.accent),
                focusedLabelColor = colorResource(id = R.color.accent),
                focusedIndicatorColor = colorResource(id = R.color.accent)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Signup Button
        Button(
            onClick = {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Signup successful! Please login.", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("signup") { inclusive = true }
                            }
                        } else {
                            errorMessage = task.exception?.localizedMessage ?: "Signup failed"
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.accent))
        ) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navigate to Login
        TextButton(onClick = { navController.navigate("login") }) {
            Text(text = "Already have an account? Login", color = colorResource(id = R.color.accent))
        }
    }
}
