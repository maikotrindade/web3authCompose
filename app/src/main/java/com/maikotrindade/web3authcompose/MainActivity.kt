package com.maikotrindade.web3authcompose

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maikotrindade.web3authcompose.ui.theme.Web3AuthComposeTheme
import com.web3auth.core.Web3Auth
import com.web3auth.core.types.*
import java8.util.concurrent.CompletableFuture
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    private lateinit var web3Auth: Web3Auth
    private val userDetailsState = MutableStateFlow("Not Connected")
    private val isLoggedInState = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web3AuthSetup()
        setContent { MainActivityContent() }
    }

    private fun web3AuthSetup() {
        web3Auth = Web3Auth(
            Web3AuthOptions(
                context = this,
                clientId = BuildConfig.WEB3AUTH_PROJECT_ID,
                network = Web3Auth.Network.TESTNET,
                redirectUrl = Uri.parse("com.maikotrindade.web3authcompose://auth"),
                whiteLabel = WhiteLabelData(
                    "Web3Auth Compose Demo", null, null, "en", true,
                    hashMapOf(
                        "primary" to "#eb5424"
                    )
                ),
                loginConfig =
                hashMapOf("jwt" to LoginConfigItem(
                    verifier = "web3auth-auth0-demo",
                    typeOfLogin = TypeOfLogin.GOOGLE,
                    name = "Web3AuthCompose Login",
                    clientId = BuildConfig.WEB3AUTH_PROJECT_ID
                    )
                )
            )
        )

        web3Auth.setResultUrl(intent?.data)

        checkBlockchainSession()
    }

    private fun checkBlockchainSession() {
        val sessionResponse: CompletableFuture<Web3AuthResponse> = web3Auth.sessionResponse()
        sessionResponse.whenComplete { loginResponse, error ->
            if (error == null) {
                reRender(loginResponse, error)
            } else {
                Log.d("MainActivity_Web3Auth", error.message ?: "Something went wrong")
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        web3Auth.setResultUrl(intent?.data)
    }

    @Composable
    private fun MainActivityContent() {
        val userDetails by userDetailsState.collectAsState()
        val isLoggedIn by isLoggedInState.collectAsState()

        Web3AuthComposeTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(60.dp),
                    text = userDetails,
                    fontSize = 36.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )

                AnimatedVisibility(visible = !isLoggedIn) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        onClick = { signIn() }) {
                        Text(text = "Sign In")
                    }
                }

                AnimatedVisibility(visible = isLoggedIn) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        onClick = { signOut() }) {
                        Text(text = "Sign Out")
                    }
                }
            }
        }
    }

    private fun signIn() {
        val selectedLoginProvider = Provider.GOOGLE   // Can be GOOGLE, FACEBOOK, TWITCH etc.
        val loginCompletableFuture: CompletableFuture<Web3AuthResponse> =
            web3Auth.login(LoginParams(selectedLoginProvider))
        loginCompletableFuture.whenComplete { loginResponse, error ->
            reRender(loginResponse, error)
        }
    }

    private fun signOut() {
        val logoutCompletableFuture =  web3Auth.logout()
        logoutCompletableFuture.whenComplete { _, error ->
            reRender(Web3AuthResponse(), error)
        }
        recreate()
    }

    private fun reRender(web3AuthResponse: Web3AuthResponse, error: Throwable?) {
        if (error == null) {
            userDetailsState.value = web3AuthResponse.userInfo?.name ?: kotlin.run { "ERROR" }
            val privateKey = web3AuthResponse.privKey
            isLoggedInState.value = (privateKey is String && privateKey.isNotBlank())
        } else {
            Log.e("MainActivity_Web3Auth", error.message ?: "Something went wrong" )
        }
    }
}