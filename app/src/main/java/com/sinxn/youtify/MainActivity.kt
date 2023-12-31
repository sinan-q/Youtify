package com.sinxn.youtify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.sinxn.youtify.navigation.Navigation
import com.sinxn.youtify.tools.Routes
import com.sinxn.youtify.ui.theme.YoutifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                rememberTopAppBarState()
            )
            var mDisplayMenu by remember { mutableStateOf(false) }
            val navController = rememberNavController()
            YoutifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        LargeTopAppBar(
                            scrollBehavior = scrollBehavior,
                            actions = {
                                IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "")
                                }
                                DropdownMenu(
                                    expanded = mDisplayMenu,
                                    onDismissRequest = { mDisplayMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text(text = "Downloads") },
                                        onClick = {
                                            navController.navigate("downloads")
                                        })
                                }

                            },
                            title = {
                                Icon(painter = painterResource(id = R.drawable.ic_app_name_colored),contentDescription = null, modifier = Modifier.size(100.dp))
                            },
                        )
                        Navigation(navController = navController )
                    }
                }
            }
            LaunchedEffect(true) {
                if (Intent.ACTION_VIEW == intent.action && intent.data!=null)
                    navController.navigate(Routes.PLAYLIST_SCREEN+"/?url=${intent.data.toString()}")
            }
        }
    }
}