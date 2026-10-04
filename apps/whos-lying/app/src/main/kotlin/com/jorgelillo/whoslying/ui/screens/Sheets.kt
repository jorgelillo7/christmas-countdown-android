package com.jorgelillo.whoslying.ui.screens

import android.app.LocaleManager
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 32.dp).then(DialogTags).testTag("modes_sheet"),
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
                Triple("🎨", R.string.modes_drawing_title, R.string.modes_drawing),
                Triple("🏆", R.string.modes_scores_title, R.string.modes_scores),
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

/** Settings and about: language, rules, sharing, rating and privacy. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(onHowTo: () -> Unit, onModes: () -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var pickLanguage by rememberSaveable { mutableStateOf(false) }
    val shareText = stringResource(R.string.share_text, PLAY_STORE_URL)
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Neon.Card) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 32.dp).then(DialogTags).testTag("settings_sheet"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val current = LocalConfiguration.current.locales[0].displayLanguage.replaceFirstChar { it.uppercase() }
                SettingsRow("🌐", stringResource(R.string.settings_language), current, "language") { pickLanguage = true }
            }
            SettingsRow("📖", stringResource(R.string.how_to_play), null, "settings_how_to") { onDismiss(); onHowTo() }
            SettingsRow("🎭", stringResource(R.string.modes_title), null, "settings_modes") { onDismiss(); onModes() }
            SettingsRow("💌", stringResource(R.string.settings_share), stringResource(R.string.settings_share_desc), "share") {
                val send = Intent(Intent.ACTION_SEND).setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, shareText)
                context.startActivity(Intent.createChooser(send, null))
            }
            SettingsRow("⭐", stringResource(R.string.about_rate), null, "rate") { uriHandler.openUri(PLAY_STORE_URL) }
            SettingsRow("🔒", stringResource(R.string.about_privacy), null, "privacy") { uriHandler.openUri(PRIVACY_POLICY_URL) }
            Text(
                stringResource(R.string.about_body) + "\n" + stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodySmall,
                color = Neon.Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
    }
    if (pickLanguage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        LanguageDialog(onDismiss = { pickLanguage = false })
    }
}

@Composable
private fun SettingsRow(emoji: String, title: String, subtitle: String?, tag: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Neon.Night)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, style = MaterialTheme.typography.titleLarge)
        Column(Modifier.weight(1f).padding(start = 14.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Neon.Muted)
        }
        Text("›", style = MaterialTheme.typography.headlineSmall, color = Neon.Muted)
    }
}

/** Per-app language (Android 13+). An empty list follows the phone's language. */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun LanguageDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    fun choose(tags: String) {
        context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList.forLanguageTags(tags)
        onDismiss()
    }
    AlertDialog(
        modifier = DialogTags,
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            Column {
                listOf("" to stringResource(R.string.language_system), "es" to "Español", "en" to "English").forEach { (tag, label) ->
                    TextButton(onClick = { choose(tag) }, modifier = Modifier.fillMaxWidth().testTag("language_${tag.ifEmpty { "system" }}")) {
                        Text(label, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.vote_cancel)) } },
    )
}
