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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jorgelillo.whoslying.R
import com.jorgelillo.whoslying.domain.Card
import com.jorgelillo.whoslying.domain.Elimination
import com.jorgelillo.whoslying.domain.Game
import com.jorgelillo.whoslying.domain.Outcome
import com.jorgelillo.whoslying.domain.Role
import com.jorgelillo.whoslying.ui.theme.Neon
import kotlinx.coroutines.delay

/** Dialogs live in their own window: expose their test tags as resource ids too (scripts/tap-text.sh). */
@OptIn(ExperimentalComposeUiApi::class)
private val DialogTags = Modifier.semantics { testTagsAsResourceId = true }

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

@Composable
fun VoteScreen(
    game: Game,
    /** Players still in. Passed in (not read from the mutable [game]) so the list recomposes. */
    alive: Set<String>,
    elimination: Elimination?,
    onVote: (String) -> Unit,
    onGuess: (String) -> Boolean,
    onDismissElimination: () -> Unit,
    onRevealAll: () -> Unit,
    onQuit: () -> Unit,
) {
    var confirm by rememberSaveable { mutableStateOf<String?>(null) }
    var guessFailed by rememberSaveable { mutableStateOf(false) }

    Page(
        title = null,
        onBack = onQuit,
        bottom = {
            TextButton(onClick = onRevealAll, modifier = Modifier.align(Alignment.CenterHorizontally).testTag("reveal_all")) {
                Text(stringResource(R.string.vote_reveal_all), color = Neon.Muted)
            }
        },
    ) {
        Text(stringResource(R.string.vote_title), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
        Text(stringResource(R.string.vote_desc), color = Neon.Muted)
        Text(stringResource(R.string.vote_starter, game.starter), color = Neon.Amber, modifier = Modifier.padding(top = 6.dp, bottom = 16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(game.cards, key = { it.player }) { card ->
                val out = card.player !in alive
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(if (out) Neon.Night else Neon.Card)
                        .border(1.dp, if (out) Neon.Card else Color.Transparent, CircleShape)
                        .clickable(enabled = !out) { confirm = card.player }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(card.player, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = if (out) Neon.Muted else Neon.Ink)
                    if (out) Text(roleLabel(card.role), color = roleColor(card.role), fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    confirm?.let { player ->
        AlertDialog(
            modifier = DialogTags,
            onDismissRequest = { confirm = null },
            title = { Text(stringResource(R.string.vote_confirm, player)) },
            confirmButton = {
                TextButton(onClick = { confirm = null; onVote(player) }, modifier = Modifier.testTag("confirm_vote")) {
                    Text(stringResource(R.string.vote_yes), color = Neon.Pink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { confirm = null }) { Text(stringResource(R.string.vote_cancel)) } },
        )
    }

    if (elimination != null && game.pendingGuess == elimination.player) {
        DrifterGuessDialog(elimination.player) { guessFailed = !onGuess(it) }
    } else if (elimination != null && !game.isOver) {
        EliminationDialog(game.cardOf(elimination.player), extra = if (guessFailed) stringResource(R.string.guess_wrong) else null) {
            guessFailed = false
            onDismissElimination()
        }
    }
}

@Composable
private fun EliminationDialog(card: Card, extra: String?, onDismiss: () -> Unit) {
    val title = when (card.role) {
        Role.CIVILIAN -> stringResource(R.string.out_was_civilian, card.player)
        Role.IMPOSTOR -> stringResource(R.string.out_was_impostor, card.player)
        Role.DRIFTER -> stringResource(R.string.out_was_drifter, card.player)
    }
    AlertDialog(
        modifier = DialogTags,
        onDismissRequest = onDismiss,
        icon = { Text(if (card.role == Role.CIVILIAN) "😇" else "🕵️", fontSize = 40.sp) },
        title = { Text(title, textAlign = TextAlign.Center) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                val word = card.word
                if (card.role == Role.IMPOSTOR && word != null) Text(stringResource(R.string.out_their_word, word))
                if (extra != null) Text(extra, color = Neon.Pink)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss, modifier = Modifier.testTag("keep_playing")) { Text(stringResource(R.string.next_vote)) } },
    )
}

@Composable
private fun DrifterGuessDialog(player: String, onGuess: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        modifier = DialogTags,
        onDismissRequest = {},
        icon = { Text("🤷", fontSize = 40.sp) },
        title = { Text(stringResource(R.string.guess_title, player), textAlign = TextAlign.Center) },
        text = {
            Column {
                Text(stringResource(R.string.guess_desc))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text(stringResource(R.string.guess_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { if (text.isNotBlank()) onGuess(text) }),
                    modifier = Modifier.testTag("guess_text"),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onGuess(text) }, enabled = text.isNotBlank(), modifier = Modifier.testTag("guess")) {
                Text(stringResource(R.string.guess_go), fontWeight = FontWeight.Bold)
            }
        },
    )
}

@Composable
fun ResultScreen(game: Game, onPlayAgain: () -> Unit, onHome: () -> Unit) {
    val (emoji, title) = when (val outcome = game.outcome) {
        Outcome.CiviliansWin -> "🎉" to stringResource(R.string.result_civilians)
        Outcome.DrifterGuessed -> "🤯" to stringResource(R.string.result_drifter_guessed)
        Outcome.NoCivilians -> "🎭" to stringResource(R.string.result_nobody)
        Outcome.EndedEarly -> "🏁" to stringResource(R.string.result_ended)
        is Outcome.InfiltratorsWin -> "🕵️" to when (outcome.roles) {
            setOf(Role.DRIFTER) -> stringResource(R.string.result_drifter)
            setOf(Role.IMPOSTOR) -> stringResource(R.string.result_impostors)
            else -> stringResource(R.string.result_impostors_drifter)
        }
        null -> "🕵️" to stringResource(R.string.vote_title)
    }
    Page(title = null, onBack = null, bottom = {
        BigButton(stringResource(R.string.play_again), onPlayAgain, Modifier.testTag("play_again"))
        TextButton(onClick = onHome, modifier = Modifier.align(Alignment.CenterHorizontally).testTag("home")) {
            Text(stringResource(R.string.home), color = Neon.Muted)
        }
    }) {
        Emoji(emoji, size = 80, modifier = Modifier.fillMaxWidth())
        Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(
            stringResource(R.string.result_word, game.civilianWord),
            color = Neon.Turquoise,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 18.dp),
        )
        Text(stringResource(R.string.result_roles), color = Neon.Muted, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(game.cards, key = { it.player }) { card ->
                Row(
                    Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).background(Neon.Card).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(roleColor(card.role)))
                    Spacer(Modifier.width(12.dp))
                    Text(card.player, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                    Text(card.word ?: "—", color = Neon.Muted)
                    Spacer(Modifier.width(12.dp))
                    Text(roleLabel(card.role), color = roleColor(card.role), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun roleLabel(role: Role): String = stringResource(
    when (role) {
        Role.CIVILIAN -> R.string.role_civilian
        Role.IMPOSTOR -> R.string.role_impostor
        Role.DRIFTER -> R.string.role_drifter
    },
)

private fun roleColor(role: Role): Color = when (role) {
    Role.CIVILIAN -> Neon.Turquoise
    Role.IMPOSTOR -> Neon.Pink
    Role.DRIFTER -> Neon.Amber
}
