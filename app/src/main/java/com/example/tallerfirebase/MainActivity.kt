package com.example.tallerfirebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.tallerfirebase.navigation.NavigationStack
import com.example.tallerfirebase.ui.theme.TallerFirebaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var esModoOscuro by remember { mutableStateOf<Boolean?>(null) }
            val modoOscuroActual = esModoOscuro ?: isSystemInDarkTheme()

            TallerFirebaseTheme(darkTheme = modoOscuroActual) {
                val navController = rememberNavController()
                NavigationStack(controladorNav = navController)
            }
        }
    }
}
