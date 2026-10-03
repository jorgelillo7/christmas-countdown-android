package com.jorgelillo.christmascountdown.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jorgelillo.christmascountdown.ChristmasCountdownApplication
import com.jorgelillo.christmascountdown.R
import com.jorgelillo.christmascountdown.domain.CountdownState
import com.jorgelillo.christmascountdown.ui.about.AboutSheet
import com.jorgelillo.christmascountdown.ui.about.PLAY_STORE_URL
import com.jorgelillo.christmascountdown.ui.advent.AdventScreen
import com.jorgelillo.christmascountdown.ui.advent.AdventViewModel
import com.jorgelillo.christmascountdown.ui.components.Snowfall
import com.jorgelillo.christmascountdown.ui.countdown.CountdownScreen
import com.jorgelillo.christmascountdown.ui.countdown.CountdownViewModel
import com.jorgelillo.christmascountdown.ui.theme.ChristmasColors
import com.jorgelillo.core.designsystem.GradientBackground

private enum class Tab(val label: Int) { Countdown(R.string.tab_countdown), Advent(R.string.tab_advent) }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChristmasApp(app: ChristmasCountdownApplication) {
    val countdownViewModel: CountdownViewModel = viewModel(factory = CountdownViewModel.factory(app))
    val adventViewModel: AdventViewModel = viewModel(factory = AdventViewModel.factory(app.settings))
    val countdown by countdownViewModel.uiState.collectAsStateWithLifecycle()
    val advent by adventViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var tab by rememberSaveable { mutableIntStateOf(Tab.Countdown.ordinal) }
    var showAbout by rememberSaveable { mutableStateOf(false) }

    // Carols only play while the app is visible.
    LifecycleStartEffect(countdown.musicEnabled) {
        if (countdown.musicEnabled) app.musicPlayer.play()
        onStopOrDispose { app.musicPlayer.stop() }
    }

    GradientBackground(ChristmasColors.Background) {
        Snowfall(Modifier.fillMaxSize())
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleMedium) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = { countdownViewModel.setMusicEnabled(!countdown.musicEnabled) }) {
                            Icon(
                                painter = painterResource(if (countdown.musicEnabled) R.drawable.ic_music_note else R.drawable.ic_music_off),
                                contentDescription = stringResource(
                                    if (countdown.musicEnabled) R.string.action_music_off else R.string.action_music_on,
                                ),
                                tint = if (countdown.musicEnabled) ChristmasColors.Gold else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { context.shareCountdown(countdown.countdown) }) {
                            Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.action_share))
                        }
                        IconButton(onClick = { showAbout = true }) {
                            Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.action_about))
                        }
                    },
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color.Black.copy(alpha = 0.25f)) {
                    Tab.entries.forEach { item ->
                        NavigationBarItem(
                            selected = tab == item.ordinal,
                            onClick = { tab = item.ordinal },
                            icon = {
                                Icon(
                                    imageVector = if (item == Tab.Countdown) Icons.Filled.Star else Icons.Filled.DateRange,
                                    contentDescription = null,
                                )
                            },
                            label = { Text(stringResource(item.label)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = ChristmasColors.Gold,
                                indicatorColor = ChristmasColors.Gold,
                            ),
                        )
                    }
                }
            },
        ) { padding ->
            when (Tab.entries[tab]) {
                Tab.Countdown -> CountdownScreen(
                    state = countdown,
                    onSleepsModeChange = countdownViewModel::setSleepsMode,
                    modifier = Modifier.padding(padding),
                )
                Tab.Advent -> AdventScreen(
                    state = advent,
                    canOpen = adventViewModel::canOpen,
                    onOpen = adventViewModel::open,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }

    if (showAbout) AboutSheet(onDismiss = { showAbout = false })
}

private fun Context.shareCountdown(state: CountdownState) {
    val message = when (state) {
        CountdownState.ChristmasDay -> getString(R.string.share_text_christmas)
        is CountdownState.Counting -> resources.getQuantityString(R.plurals.share_text, state.sleeps.toInt(), state.sleeps.toInt())
    }
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "$message\n$PLAY_STORE_URL")
    }
    startActivity(Intent.createChooser(send, getString(R.string.share_chooser)))
}
