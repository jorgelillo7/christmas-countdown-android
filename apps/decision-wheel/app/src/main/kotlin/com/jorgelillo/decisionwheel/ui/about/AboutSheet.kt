package com.jorgelillo.decisionwheel.ui.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jorgelillo.decisionwheel.BuildConfig
import com.jorgelillo.decisionwheel.R
import com.jorgelillo.decisionwheel.ui.theme.WheelColors

const val PRIVACY_POLICY_URL = "https://jorgelillo7.github.io/privacy/decision-wheel/"
const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.jorgelillo.decisionwheel"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSheet(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = WheelColors.NightRaised) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(88.dp).clip(MaterialTheme.shapes.large).background(WheelColors.Night),
            )
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
