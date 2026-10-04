package com.jorgelillo.whoslying.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jorgelillo.whoslying.BuildConfig
import com.jorgelillo.whoslying.R
import com.jorgelillo.whoslying.ui.theme.Neon

const val PRIVACY_POLICY_URL = "https://jorgelillo7.github.io/privacy/whos-lying/"
const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.jorgelillo.whoslying"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Neon.Card) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            listOf(
                Triple("🤫", R.string.how_1_title, R.string.how_1),
                Triple("💬", R.string.how_2_title, R.string.how_2),
                Triple("🗳️", R.string.how_3_title, R.string.how_3),
                Triple("🏆", R.string.how_4_title, R.string.how_4),
            ).forEach { (emoji, title, body) ->
                Column(Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(Neon.Night).padding(18.dp)) {
                    Text("$emoji  " + stringResource(title), color = Neon.Amber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(stringResource(body), modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}

/** Detailed guide to each game mode and the options that change a game. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModesSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Neon.Card) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 32.dp).testTag("modes_sheet"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.modes_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(stringResource(R.string.modes_intro), color = Neon.Muted)
            listOf(
                ModeGuide("🎭", R.string.mode_classic, R.string.modes_classic_who, R.string.modes_classic_win, R.string.modes_classic_tip),
                ModeGuide("🙈", R.string.mode_blind, R.string.modes_blind_who, R.string.modes_blind_win, R.string.modes_blind_tip),
                ModeGuide("🤷", R.string.mode_drifter, R.string.modes_drifter_who, R.string.modes_drifter_win, R.string.modes_drifter_tip),
            ).forEach { mode ->
                Column(Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(Neon.Night).padding(18.dp)) {
                    Text("${mode.emoji}  " + stringResource(mode.title), color = Neon.Amber, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    listOf(R.string.modes_who_gets to mode.who, R.string.modes_how_win to mode.win, R.string.modes_tip to mode.tip).forEach { (label, body) ->
                        Text(stringResource(label), color = Neon.Turquoise, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 12.dp))
                        Text(stringResource(body), modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
            listOf(
                Triple("🌀", R.string.modes_chaos_title, R.string.modes_chaos),
                Triple("💡", R.string.modes_hint_title, R.string.modes_hint),
                Triple("⏱️", R.string.modes_timer_title, R.string.modes_timer),
            ).forEach { (emoji, title, body) ->
                Column(Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(Neon.Night).padding(18.dp)) {
                    Text("$emoji  " + stringResource(title), color = Neon.Amber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(stringResource(body), modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
    }
}

private class ModeGuide(
    val emoji: String,
    @param:StringRes val title: Int,
    @param:StringRes val who: Int,
    @param:StringRes val win: Int,
    @param:StringRes val tip: Int,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSheet(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Neon.Card) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Emoji("🕵️", size = 56)
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME), style = MaterialTheme.typography.bodySmall, color = Neon.Muted)
            Text(stringResource(R.string.about_body), textAlign = TextAlign.Center)
            OutlinedButton(onClick = { uriHandler.openUri(PLAY_STORE_URL) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.about_rate))
            }
            OutlinedButton(onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.about_privacy))
            }
        }
    }
}
