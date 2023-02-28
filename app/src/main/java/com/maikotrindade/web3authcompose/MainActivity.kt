package com.maikotrindade.web3authcompose

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.maikotrindade.web3authcompose.ui.theme.Web3AuthComposeTheme
import com.web3auth.core.Web3Auth
import com.web3auth.core.types.Web3AuthOptions

class MainActivity : ComponentActivity() {

    private lateinit var web3Auth: Web3Auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        web3AuthSetup()

        setContent {
            Web3AuthComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                }
            }
        }
    }

    private fun web3AuthSetup() {
    }
}