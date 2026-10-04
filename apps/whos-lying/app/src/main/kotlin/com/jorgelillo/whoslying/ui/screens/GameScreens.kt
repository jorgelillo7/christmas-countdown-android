package com.jorgelillo.whoslying.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jorgelillo.whoslying.R
import com.jorgelillo.whoslying.domain.Card
import com.jorgelillo.whoslying.domain.Game
import com.jorgelillo.whoslying.domain.Role
import com.jorgelillo.whoslying.ui.theme.Neon
import kotlinx.coroutines.delay

/** Dialogs live in their own window: expose their test tags as resource ids too (scripts/tap-text.sh). */
@OptIn(ExperimentalComposeUiApi::class)
internal val DialogTags = Modifier.semantics { testTagsAsResourceId = true }

/** Pass-the-phone reveal: each player taps to see their word, then hides it before passing on. */
@Composable
fun RevealScreen(game: Game, onDone: () -> Unit, onQuit: () -> Unit) {
    var index by rememberSaveable(game) { mutableIntStateOf(0) }
    var shown by rememberSaveable(game) { mutableStateOf(false) }
    val card = game.cards[index]

    Page(
        title = stringResource(R.string.reveal_counter, index + 1, game.cards.size),
        onBack = onQuit,
        background = Neon.RevealBackdrop,
        bottom = {
            if (shown) {
                BigButton(stringResource(R.string.reveal_hide), {
                    shown = false
                    if (index == game.cards.lastIndex) onDone() else index++
                }, color = Neon.Night, modifier = Modifier.testTag("hide"))
            }
        },
    ) {
        AnimatedContent(
            targetState = shown to index,
            transitionSpec = { (fadeIn() + scaleIn(initialScale = 0.92f)) togetherWith fadeOut() },
            modifier = Modifier.weight(1f).fillMaxWidth(),
            label = "reveal",
        ) { (isShown, _) ->
            if (!isShown) {
                Column(
                    Modifier.fillMaxSize().clickable { shown = true }.testTag("tap_to_reveal"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(stringResource(R.string.reveal_pass_to), color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.titleMedium)
                    Text(card.player, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = Color.White, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(40.dp))
                    Emoji("👀", size = 90)
                    Spacer(Modifier.height(24.dp))
                    Text(stringResource(R.string.reveal_tap), color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { SecretCard(card) }
            }
        }
    }
}

@Composable
private fun SecretCard(card: Card) {
    val accent = if (card.knowsRole) Neon.Pink else Neon.Turquoise
    Column(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Neon.Night)
            .border(3.dp, accent, MaterialTheme.shapes.extraLarge)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(card.player, color = Neon.Muted, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(18.dp))
        if (card.knowsRole) {
            Emoji(if (card.role == Role.DRIFTER) "🤷" else "🕵️", size = 64)
            Text(
                stringResource(if (card.role == Role.DRIFTER) R.string.reveal_you_are_drifter else R.string.reveal_you_are_impostor),
                color = accent,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(if (card.role == Role.DRIFTER) R.string.reveal_drifter_desc else R.string.reveal_impostor_desc),
                color = Neon.Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            // CLASSIC impostors also land here: same layout as civilians, so nothing gives them away.
            Text(stringResource(R.string.reveal_your_word), color = Neon.Muted)
            Text(
                card.word.orEmpty(),
                color = accent,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                modifier = Modifier.padding(vertical = 8.dp).testTag("secret_word"),
            )
        }
        card.hint?.let { hint ->
            Text(stringResource(R.string.reveal_hint, hint), color = Neon.Amber, modifier = Modifier.padding(top = 14.dp))
        }
    }
}

/** "X starts. Get ready! 3, 2, 1" before the clue round. */
@Composable
fun CountdownScreen(starter: String, onDone: () -> Unit) {
    var count by rememberSaveable { mutableIntStateOf(3) }
    LaunchedEffect(Unit) {
        while (count > 0) {
            delay(1000)
            count--
        }
        onDone()
    }
    Page(title = null, onBack = null, background = Neon.RevealBackdrop) {
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(R.string.countdown_starts, starter),
            color = Neon.Amber,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            stringResource(R.string.countdown_ready),
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        AnimatedContent(count, transitionSpec = { scaleIn(initialScale = 1.6f) + fadeIn() togetherWith fadeOut() }, modifier = Modifier.fillMaxWidth(), label = "count") {
            Text("${it.coerceAtLeast(1)}", color = Color.White, fontSize = 180.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.weight(1.4f))
    }
}
