package com.example.bmu_4lab

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.identity.SignInClient

class MainActivity : ComponentActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val oneTapLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    val name    = credential.displayName
                    val email   = credential.id
                    Log.d("ONE_TAP", "User: $name, email: $email, idToken: $idToken")
                    // TODO: тут переход к биометрии / сохранению токена
                } catch (e: Exception) {
                    Log.e("ONE_TAP", "Error getting credential: ${e.localizedMessage}")
                }
            } else {
                Log.d("ONE_TAP", "One-Tap dialog dismissed")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Инициализируем клиент и запрос
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

        // 2. UI на Compose
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


