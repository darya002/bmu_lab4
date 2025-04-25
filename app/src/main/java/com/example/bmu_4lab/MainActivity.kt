package com.example.bmu_4lab

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.compose.material3.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.identity.SignInClient
import java.util.concurrent.Executor

class MainActivity : FragmentActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    val oneTapLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    val name    = credential.displayName
                    val email   = credential.id
                    Log.d("ONE_TAP", "User: $name, email: $email, idToken: $idToken")

                    if (idToken != null) {
                        SecureStorage.saveToken(this, idToken)
                        authenticateAndShowToken()
                    } else {
                        Toast.makeText(this, "Token is null", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Log.e("ONE_TAP", "Error getting credential: ${e.localizedMessage}")
                }
            } else {
                Log.d("ONE_TAP", "One-Tap dialog dismissed")
            }
        }

    private fun authenticateAndShowToken() {
        val executor: Executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(this@MainActivity as FragmentActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val token = SecureStorage.getToken(this@MainActivity)
                    Toast.makeText(this@MainActivity, "Token: $token", Toast.LENGTH_LONG).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Биометрическая аутентификация")
            .setSubtitle("Подтвердите отпечаток для доступа к токену")
            .setNegativeButtonText("Отмена")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        setContent {
            MaterialTheme {
                SignInScreen(
                    onSignInClick = {
                        oneTapClient.beginSignIn(signInRequest)
                            .addOnSuccessListener { result ->
                                oneTapLauncher.launch(
                                    IntentSenderRequest.Builder(result.pendingIntent).build()
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.e("ONE_TAP", "Sign-in failed: ${e.localizedMessage}")
                            }
                    }
                )
            }
        }
    }
}


