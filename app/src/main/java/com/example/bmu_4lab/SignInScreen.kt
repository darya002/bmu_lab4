package com.example.bmu_4lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SignInScreen(
    onSignInClick: (String, String) -> Unit,
    onGoogleSignInClick: () -> Unit,
    onBiometricSignInClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Войти", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        RegularSignInForm(onSignInClick)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onGoogleSignInClick) {
            Text("Войти через Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBiometricSignInClick) {
            Text("Войти через Биометрию")
        }
    }
}