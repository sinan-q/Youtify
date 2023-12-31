package com.sinxn.youtify.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.youtify.R
import com.sinxn.youtify.domain.model.Playlists
import com.sinxn.youtify.tools.Routes
import com.sinxn.youtify.ui.home.components.AddPlaylistDialog
import com.sinxn.youtify.ui.setup.MyButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val snackBarHostState = SnackbarHostState()

    var playlistDialogState by remember { mutableStateOf(false) }
    Scaffold(snackbarHost =  { SnackbarHost(
        hostState = snackBarHostState
    ) },
        floatingActionButton = {
        FloatingActionButton(modifier = Modifier.padding(20.dp), onClick = { playlistDialogState = true}) {
            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                if (uiState.playlists.isEmpty()) Text(text = stringResource(R.string.add_playlist))
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_playlist))
            }

        }
    }) {padding->
        if (uiState.playlists.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "No Playlists")
                MyButton(onClick = { /*TODO*/ }, text = "Browse Top Songs")
            }
        }
        else {  
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(uiState.playlists) { playlist ->
                    PlaylistCard(playlist, onOptionClick = {
                        viewModel.onEvent(it)
                    }, onClick = {
                        viewModel.onEvent(PlaylistEvent.OnSelect(playlist))
                        navController.navigate(Routes.PLAYLIST_SCREEN)
                    })
                }
            }
        }
    }
    if (playlistDialogState) AddPlaylistDialog(onDismiss = { playlistDialogState = false }, onConfirm = {
        viewModel.onEvent(PlaylistEvent.OnConvert(it))
        navController.navigate(Routes.PLAYLIST_SCREEN)
    })
    LaunchedEffect(true) {
        viewModel.init()
        if (!viewModel.isLogged) navController.navigate(Routes.SETUP_SCREEN)
    }
    LaunchedEffect(uiState.error) {
        if (uiState.error!=null) {
            snackBarHostState.showSnackbar(uiState.error)
            viewModel.onEvent(PlaylistEvent.ErrorDisplayed(null))
        }
    }
}

@Composable
fun PlaylistCard(
    playlist: Playlists,
    onClick: () -> Unit,
    onOptionClick: (PlaylistEvent) -> Unit
) {
    var optionsState by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier) {
        Card(modifier = Modifier
            .align(Alignment.Center)
            .fillMaxWidth()
            .padding(20.dp, 5.dp)
            .clickable { onClick() }
        ) {
            Box(Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconToggleButton(checked = optionsState, onCheckedChange = {optionsState = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.delete_selected_playlist))
                        DropdownMenu(
                            expanded = optionsState,
                            onDismissRequest = { optionsState = false }) {
                            DropdownMenuItem(
                                text = { Text(text = "Delete") },
                                onClick = {
                                    onOptionClick(PlaylistEvent.OnDelete(playlist))
                                })
                        }
                    }
                }
                Column(modifier = Modifier.padding(50.dp,20.dp)) {
                    Text(
                        text = formatDate(playlist.date),
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = playlist.name, fontWeight = FontWeight.Bold, fontSize = 24.sp , maxLines = 1)
                    Spacer(modifier = Modifier.height(10.dp))
                    playlist.description?.let { Text(text = it, fontWeight = FontWeight.Light, fontSize = 14.sp, lineHeight = 15.sp, maxLines = 2) }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        Text(
                            text = "${playlist.count} Songs",
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        playlist.followers?.let { Text(
                            text = "$it Followers",
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp,
                        ) }
                    }
                }
            }


        }
    }

}
fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val date = Date(timestamp)
    return dateFormat.format(date)
}