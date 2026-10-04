package com.jorgelillo.whoslying.ui.screens

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
