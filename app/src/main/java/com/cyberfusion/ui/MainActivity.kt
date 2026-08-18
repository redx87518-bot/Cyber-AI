package com.cyberfusion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cyberfusion.core.database.room.CyberFusionDatabase
import com.cyberfusion.ui.compose.LocalViewModelFactory
import com.cyberfusion.ui.compose.ViewModelFactory
import com.cyberfusion.ui.navigation.CyberFusionNavHost
import com.cyberfusion.ui.theme.CyberFusionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = (application as com.cyberfusion.CyberFusionApplication).database
        val factory = ViewModelFactory(database)
        
        setContent {
            CyberFusionTheme {
                androidx.compose.runtime.CompositionLocalProvider(
                    LocalViewModelFactory provides factory
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        CyberFusionNavHost()
                    }
                }
            }
        }
    }
}
