package com.smfelix.movieapp.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.smfelix.movieapp.R
import com.smfelix.movieapp.data.User
import com.smfelix.movieapp.service.UserService
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    userService: UserService,
    user: User,
    onUserUpdated: (User) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var dateOfBirth by remember { mutableStateOf(user.dateOfBirth ?: "") }
    var gender by remember { mutableStateOf(user.gender ?: "Male") }
    var mobileNumber by remember { mutableStateOf(user.mobileNumber ?: "") }
    var shortBio by remember { mutableStateOf(user.shortBio ?: "") }
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Validation state
    var nameError by remember { mutableStateOf(false) }
    var mobileError by remember { mutableStateOf(false) }
    var dobError by remember { mutableStateOf(false) }

    // DatePicker dialog
    var showDatePicker by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        profilePhotoUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colorResource(id = R.color.bgGray)),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Photo Section
        if (profilePhotoUri != null) {
            Image(
                painter = rememberAsyncImagePainter(profilePhotoUri),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else if (user.profilePhotoUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(user.profilePhotoUrl),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            val initials = if (user.name.isNullOrEmpty()) "🍿" else user.name
                .split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .joinToString("")
                .uppercase()
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                colorResource(id = R.color.bgStar),
                                colorResource(id = R.color.star)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorResource(id = R.color.black),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = { launcher.launch("image/*") },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.accent))
        ) {
            Text("Upload Profile Photo")
        }

        // Form Fields
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Name Field
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                isError = nameError,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    cursorColor = colorResource(id = R.color.accent),
                    focusedTextColor = colorResource(id = R.color.accent),
                    focusedLabelColor = colorResource(id = R.color.accent),
                    focusedIndicatorColor = colorResource(id = R.color.accent)
                )
            )
            if (nameError) {
                Text("Name is required", color = colorResource(id = R.color.red), fontSize = 12.sp)
            }

            // Date of Birth Field
            TextField(
                value = dateOfBirth,
                onValueChange = { },
                label = { Text("Date of Birth") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Select Date")
                    }
                },
                readOnly = true,
                isError = dobError,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    cursorColor = colorResource(id = R.color.accent),
                    focusedTextColor = colorResource(id = R.color.accent),
                    focusedLabelColor = colorResource(id = R.color.accent),
                    focusedIndicatorColor = colorResource(id = R.color.accent)
                )
            )
            if (dobError) {
                Text("Date of birth is required", color = colorResource(id = R.color.red), fontSize = 12.sp)
            }
            if (showDatePicker) {
                DatePickerDialog(
                    initialDate = dateOfBirth,
                    onDateSelected = { dateOfBirth = it; showDatePicker = false },
                    onDismiss = { showDatePicker = false }
                )
            }

            // Gender Field
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Gender:", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = gender == "Male",
                        onClick = { gender = "Male" },
                        colors = RadioButtonDefaults.colors(selectedColor = colorResource(id = R.color.accent))
                    )
                    Text("Male", color = Color.White)
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = gender == "Female",
                        onClick = { gender = "Female" },
                        colors = RadioButtonDefaults.colors(selectedColor = colorResource(id = R.color.accent))
                    )
                    Text("Female", color = Color.White)
                }
            }

            // Mobile Number Field
            TextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                label = { Text("Mobile Number") },
                isError = mobileError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = TextFieldDefaults.colors(
                    cursorColor = colorResource(id = R.color.accent),
                    focusedTextColor = colorResource(id = R.color.accent),
                    focusedLabelColor = colorResource(id = R.color.accent),
                    focusedIndicatorColor = colorResource(id = R.color.accent)
                )
            )
            if (mobileError) {
                Text("Mobile number is invalid", color = colorResource(id = R.color.red), fontSize = 12.sp)
            }

            // Short Bio Field
            TextField(
                value = shortBio,
                onValueChange = { shortBio = it },
                label = { Text("Short Bio") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    cursorColor = colorResource(id = R.color.accent),
                    focusedTextColor = colorResource(id = R.color.accent),
                    focusedLabelColor = colorResource(id = R.color.accent),
                    focusedIndicatorColor = colorResource(id = R.color.accent)
                )
            )
        }

        // Save Button
        Button(
            onClick = {
                nameError = name.isBlank()
                mobileError = mobileNumber.isBlank() || !mobileNumber.matches(Regex("\\d{10}"))
                dobError = dateOfBirth.isBlank()

                if (!nameError && !mobileError && !dobError) {
                    val updatedUser = user.copy(
                        name = name,
                        dateOfBirth = dateOfBirth,
                        gender = gender,
                        mobileNumber = mobileNumber,
                        shortBio = shortBio
                    )
                    coroutineScope.launch {
                        userService.updateUserProfile(updatedUser, profilePhotoUri) { success, message ->
                            if (success) {
                                onUserUpdated(updatedUser)
                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                navController.navigateUp()
                            } else {
                                Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.accent))
        ) {
            Text("Save Profile")
        }
    }
}

@Composable
fun DatePickerDialog(
    initialDate: String?,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current // Get context from Compose
    val calendar = Calendar.getInstance()

    // Parse the initialDate if provided, otherwise use the current date
    if (!initialDate.isNullOrEmpty()) {
        val parts = initialDate.split("/")
        if (parts.size == 3) {
            val day = parts[0].toIntOrNull()
            val month = parts[1].toIntOrNull()
            val year = parts[2].toIntOrNull()
            if (day != null && month != null && year != null) {
                calendar.set(year, month - 1, day)
            }
        }
    }

    // Show DatePickerDialog using Android's native API
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val date = "$dayOfMonth/${month + 1}/$year"
            onDateSelected(date)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.setOnDismissListener { onDismiss() }
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // Restrict to past dates
    datePickerDialog.show()
}

