package com.jorgelillo.whoslying.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
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
import com.jorgelillo.whoslying.ui.DrawStroke
import com.jorgelillo.whoslying.ui.theme.Neon
import kotlinx.coroutines.delay
import kotlin.random.Random

private val GoGreen = Color(0xFF00D26A)

/** Clue round: who starts, the optional countdown and the way to the vote. */
@Composable
fun DebateScreen(
    game: Game,
    /** Players still in. Passed in (not read from the mutable [game]) so the screen recomposes. */
    alive: Set<String>,
    discussionSeconds: Int,
    onVote: () -> Unit,
    onRevealAll: () -> Unit,
    onQuit: () -> Unit,
) {
    Page(
        title = null,
        onBack = onQuit,
        background = Neon.RevealBackdrop,
        bottom = {
            BigButton(stringResource(R.string.vote_button), onVote, Modifier.testTag("go_vote"), color = GoGreen, contentColor = Neon.Night)
            TextButton(onClick = onRevealAll, modifier = Modifier.align(Alignment.CenterHorizontally).testTag("reveal_all")) {
                Text(stringResource(R.string.vote_reveal_all), color = Color.White.copy(alpha = 0.75f))
            }
        },
    ) {
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Emoji("💬", size = 72)
            Text(stringResource(R.string.debate_title), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(
                stringResource(R.string.debate_desc),
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp),
            )
            // After the first vote, the next round starts with whoever is still in.
            val starter = game.starter.takeIf { it in alive } ?: game.cards.first { it.player in alive && it.role != Role.DRIFTER }.player
            Text(
                stringResource(R.string.countdown_starts, starter),
                color = Neon.Amber,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
            )
            Spacer(Modifier.height(24.dp))
            if (discussionSeconds > 0) {
                // Keyed by the players left, so every round of discussion gets the full time.
                key(alive.size) { CircularTimer(discussionSeconds) }
            } else {
                Emoji("🗣️", size = 110)
                Text(stringResource(R.string.debate_no_timer), color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 12.dp))
            }
            val out = game.cards.filter { it.player !in alive }
            if (out.isNotEmpty()) {
                val labels = Role.entries.associateWith { roleLabel(it).lowercase() }
                Text(
                    stringResource(R.string.debate_out, out.joinToString { "${it.player} (${labels.getValue(it.role)})" }),
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp).testTag("debate_out"),
                )
            }
        }
    }
}

/** Big round countdown. Tap to pause or resume; beeps once at zero. */
@Composable
private fun CircularTimer(seconds: Int) {
    var left by rememberSaveable { mutableIntStateOf(seconds) }
    var paused by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(paused) {
        while (!paused && left > 0) {
            delay(1_000)
            left--
            if (left == 0) beep()
        }
    }
    val color = when {
        left == 0 -> Neon.Pink
        left <= 10 -> Neon.Amber
        else -> Color.White
    }
    Box(
        Modifier
            .widthIn(max = 260.dp)
            .fillMaxWidth(0.75f)
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable(enabled = left > 0) { paused = !paused }
            .testTag("timer"),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize().padding(8.dp)) {
            val stroke = 12.dp.toPx()
            drawCircle(Color.Black.copy(alpha = 0.18f), radius = size.minDimension / 2 - stroke / 2)
            drawCircle(Color.White.copy(alpha = 0.2f), radius = size.minDimension / 2 - stroke / 2, style = Stroke(stroke))
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * left / seconds,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(size.width - stroke, size.height - stroke),
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("%d:%02d".format(left / 60, left % 60), color = color, fontSize = 64.sp, fontWeight = FontWeight.Black)
            Text(
                stringResource(
                    when {
                        left == 0 -> R.string.timer_up
                        paused -> R.string.timer_paused
                        else -> R.string.timer_pause
                    },
                ),
                color = if (left == 0) Neon.Pink else Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (left == 0) FontWeight.Bold else null,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
    }
}

internal fun beep() {
    runCatching {
        val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME)
        tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 600)
        Handler(Looper.getMainLooper()).postDelayed({ tone.release() }, 1_000)
    }
}

/** The group picks who to vote out, then confirms. */
@Composable
fun VotingScreen(game: Game, alive: Set<String>, onConfirm: (String) -> Unit, onBack: () -> Unit) {
    var chosen by rememberSaveable { mutableStateOf<String?>(null) }
    Page(
        title = null,
        onBack = onBack,
        background = Neon.RevealBackdrop,
        bottom = {
            BigButton(
                stringResource(R.string.voting_confirm),
                { chosen?.let(onConfirm) },
                Modifier.testTag("confirm_vote"),
                enabled = chosen != null,
                color = GoGreen,
                contentColor = Neon.Night,
            )
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally).testTag("back_to_debate")) {
                Text(stringResource(R.string.voting_back), color = Color.White.copy(alpha = 0.8f))
            }
        },
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Emoji("🗳️", size = 56)
            Text(stringResource(R.string.voting_title), color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(stringResource(R.string.voting_desc), color = Color.White.copy(alpha = 0.85f), textAlign = TextAlign.Center)
            Text(
                stringResource(R.string.voting_question),
                color = Neon.Amber,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(140.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(game.cards.filter { it.player in alive }, key = { it.player }) { card ->
                val selected = chosen == card.player
                val index = game.cards.indexOf(card)
                Box(
                    Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(if (selected) Neon.Night.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.1f))
                        .border(2.dp, if (selected) GoGreen else Color.White.copy(alpha = 0.25f), MaterialTheme.shapes.large)
                        .clickable { chosen = card.player }
                        .testTag("vote_${card.player}"),
                ) {
                    Column(Modifier.fillMaxWidth().padding(vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(64.dp).clip(CircleShape).background(avatarColors[index % avatarColors.size]),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(card.player.take(1).uppercase(), color = Neon.Night, fontSize = 30.sp, fontWeight = FontWeight.Black)
                        }
                        Text(card.player, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.padding(top = 10.dp))
                    }
                    if (selected) {
                        Box(
                            Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp).clip(CircleShape).background(GoGreen),
                            contentAlignment = Alignment.Center,
                        ) { Text("✓", color = Neon.Night, fontWeight = FontWeight.Black) }
                    }
                }
            }
        }
    }
}

/**
 * "Exposed in 3, 2, 1…" and then who the voted player really was. Game over goes straight to the
 * result ([onGameOver]); an eliminated drifter first gets one guess at the word.
 */
@Composable
fun ExposeScreen(
    game: Game,
    elimination: Elimination,
    onGuess: (String) -> Boolean,
    onContinue: () -> Unit,
    onGameOver: () -> Unit,
) {
    var count by rememberSaveable(elimination) { mutableIntStateOf(3) }
    var guessResult by rememberSaveable(elimination) { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(elimination) {
        while (count > 0) {
            delay(800)
            count--
        }
        if (game.isOver && game.pendingGuess == null) onGameOver()
    }
    val card = game.cardOf(elimination.player)
    val background = when {
        count > 0 -> Neon.RevealBackdrop
        card.role == Role.CIVILIAN -> Neon.ImpostorsBackdrop
        card.role == Role.DRIFTER -> Neon.DrifterBackdrop
        else -> Neon.CiviliansBackdrop
    }
    Page(
        title = null,
        onBack = null,
        background = background,
        bottom = {
            if (count == 0 && game.pendingGuess == null && !game.isOver) {
                BigButton(stringResource(R.string.next_vote), onContinue, Modifier.testTag("keep_playing"), color = Color.White, contentColor = Neon.Night)
            }
        },
    ) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (count > 0) {
                Emoji("👀", size = 110)
                Text(
                    stringResource(R.string.expose_in) + " " + (3 downTo count).joinToString(", ") + "…",
                    color = Color.White,
                    fontSize = 44.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp).testTag("exposing"),
                )
            } else {
                EliminatedCard(card, game, guessResult, onGuess = { guessResult = onGuess(it) })
            }
        }
    }
}

@Composable
private fun EliminatedCard(card: Card, game: Game, guessResult: Boolean?, onGuess: (String) -> Unit) {
    val (emoji, title, body) = when (card.role) {
        Role.CIVILIAN -> Triple("😇", stringResource(R.string.out_was_civilian, card.player), stringResource(R.string.out_civilian_desc))
        Role.IMPOSTOR -> Triple("🕵️", stringResource(R.string.out_was_impostor, card.player), stringResource(R.string.out_impostor_desc))
        Role.DRIFTER -> Triple("🤷", stringResource(R.string.out_was_drifter, card.player), stringResource(R.string.guess_desc))
    }
    Emoji(emoji, size = 100)
    Text(title, color = Color.White, fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.testTag("eliminated"))
    val word = card.word
    if (card.role == Role.IMPOSTOR && word != null) {
        Text(stringResource(R.string.out_their_word, word), color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
    }
    if (game.pendingGuess == card.player) {
        Text(stringResource(R.string.guess_title, card.player), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 20.dp))
        Text(body, color = Color.White.copy(alpha = 0.9f), textAlign = TextAlign.Center)
        var text by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(stringResource(R.string.guess_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (text.isNotBlank()) onGuess(text) }),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).testTag("guess_text"),
        )
        Spacer(Modifier.height(12.dp))
        BigButton(stringResource(R.string.guess_go), { onGuess(text) }, Modifier.testTag("guess"), enabled = text.isNotBlank(), color = Color.White, contentColor = Neon.Night)
    } else {
        Text(
            if (guessResult == false) stringResource(R.string.guess_wrong) else if (card.role == Role.DRIFTER) "" else body,
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

/** End of the game: who won, the words, who was who, and confetti when someone won. */
@Composable
fun ResultScreen(
    game: Game,
    lastElimination: Elimination?,
    drawing: List<DrawStroke>,
    onShareDrawing: () -> Unit,
    onScores: () -> Unit,
    onReport: (() -> Unit)?,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val outcome = game.outcome
    val impostors = game.cards.filter { it.role == Role.IMPOSTOR }
    val drifter = game.cards.firstOrNull { it.role == Role.DRIFTER }
    val (emojis, title, subtitle) = when (outcome) {
        Outcome.CiviliansWin -> Triple(
            "😎🕵️😱",
            pluralStringResource(R.plurals.result_caught, impostors.size + (if (drifter != null) 1 else 0)),
            stringResource(R.string.result_sub_civilians),
        )
        Outcome.DrifterGuessed -> Triple("🤷💡🤯", stringResource(R.string.result_drifter_guessed), stringResource(R.string.result_sub_drifter))
        Outcome.NoCivilians -> Triple("🎭🎭🎭", stringResource(R.string.result_nobody), stringResource(R.string.result_sub_nobody))
        Outcome.EndedEarly -> Triple("🏁", stringResource(R.string.result_ended), stringResource(R.string.result_sub_ended))
        is Outcome.InfiltratorsWin -> Triple(
            "😏🕵️😏",
            when (outcome.roles) {
                setOf(Role.DRIFTER) -> stringResource(R.string.result_drifter)
                setOf(Role.IMPOSTOR) -> stringResource(R.string.result_impostors)
                else -> stringResource(R.string.result_impostors_drifter)
            },
            if (lastElimination?.role == Role.CIVILIAN) stringResource(R.string.result_sub_infiltrators_vote) else stringResource(R.string.result_sub_infiltrators),
        )
        null -> Triple("🕵️", "", "")
    }
    val background = when (outcome) {
        Outcome.CiviliansWin -> Neon.CiviliansBackdrop
        Outcome.DrifterGuessed -> Neon.DrifterBackdrop
        is Outcome.InfiltratorsWin -> if (Role.IMPOSTOR in outcome.roles) Neon.ImpostorsBackdrop else Neon.DrifterBackdrop
        else -> Neon.RevealBackdrop
    }
    Box(Modifier.fillMaxSize()) {
        Page(
            title = null,
            onBack = null,
            background = background,
            bottom = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(60.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.25f)).clickable(onClick = onScores).testTag("scores"),
                        contentAlignment = Alignment.Center,
                    ) { Text("🏆", fontSize = 26.sp) }
                    Spacer(Modifier.width(12.dp))
                    BigButton(stringResource(R.string.play_again), onPlayAgain, Modifier.weight(1f).testTag("play_again"), color = GoGreen, contentColor = Neon.Night)
                }
                TextButton(onClick = onHome, modifier = Modifier.align(Alignment.CenterHorizontally).testTag("home")) {
                    Text(stringResource(R.string.home), color = Color.White.copy(alpha = 0.85f))
                }
            },
        ) {
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(emojis, fontSize = 64.sp)
                Text(title, color = Color.White, fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp).testTag("result_title"))
                Text(subtitle, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                Spacer(Modifier.height(20.dp))
                ResultPanel {
                    LabelValue(stringResource(R.string.result_secret_word), game.civilianWord)
                    impostors.firstNotNullOfOrNull { it.word }?.let { decoy ->
                        LabelValue(stringResource(R.string.result_decoy), decoy)
                    }
                    if (impostors.isNotEmpty() && impostors.size < game.cards.size) {
                        LabelValue(pluralStringResource(R.plurals.result_impostors_label, impostors.size), impostors.joinToString { it.player })
                    }
                    drifter?.let { LabelValue(stringResource(R.string.role_drifter), it.player) }
                }
                if (drawing.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    ResultPanel {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.result_drawing), color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                            TextButton(onClick = onShareDrawing, modifier = Modifier.testTag("share_drawing")) {
                                Text("↗ " + stringResource(R.string.result_share), color = Color.White)
                            }
                        }
                        DrawingPreview(drawing, Modifier.fillMaxWidth(0.8f).padding(top = 6.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                ResultPanel {
                    Text(stringResource(R.string.result_roles), color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge)
                    game.cards.forEach { card ->
                        Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).clip(CircleShape).background(roleColor(card.role)))
                            Spacer(Modifier.width(10.dp))
                            Text(card.player, Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(card.word ?: "—", color = Color.White.copy(alpha = 0.7f))
                            Spacer(Modifier.width(10.dp))
                            Text(roleLabel(card.role), color = roleColor(card.role), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (onReport != null) {
                    TextButton(onClick = onReport, modifier = Modifier.padding(top = 8.dp).testTag("report")) {
                        Text("⚠️  " + stringResource(R.string.report_action), color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }
        }
        if (outcome != null && outcome != Outcome.EndedEarly) Confetti(Modifier.fillMaxSize())
    }
}

@Composable
private fun ResultPanel(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large).background(Color.Black.copy(alpha = 0.25f)).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) { content() }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Text(label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 6.dp))
    Text(value, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
}

private class Piece(val x: Float, val speed: Float, val drift: Float, val spin: Float, val color: Color, val width: Float, val delay: Float)

/** A few seconds of falling confetti, drawn on a canvas (no assets, no library). */
@Composable
private fun Confetti(modifier: Modifier) {
    val colors = listOf(Neon.Amber, Neon.Pink, Neon.Turquoise, Color.White, Color(0xFF7CFF6B), Neon.Violet)
    val pieces = remember {
        val random = Random(System.nanoTime())
        List(90) {
            Piece(
                x = random.nextFloat(),
                speed = 0.18f + random.nextFloat() * 0.25f,
                drift = (random.nextFloat() - 0.5f) * 0.15f,
                spin = random.nextFloat() * 720f - 360f,
                color = colors[random.nextInt(colors.size)],
                width = 6f + random.nextFloat() * 8f,
                delay = random.nextFloat() * 1.2f,
            )
        }
    }
    var time by remember { mutableStateOf(0f) }
    val fade = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        val start = withFrameNanos { it }
        while (time < 6f) {
            time = (withFrameNanos { it } - start) / 1e9f
        }
        fade.animateTo(0f, tween(600))
    }
    if (fade.value == 0f) return
    Canvas(modifier) {
        pieces.forEach { p ->
            val t = (time - p.delay).coerceAtLeast(0f)
            val y = -0.05f + t * p.speed
            if (y > 1.05f || t == 0f) return@forEach
            val x = p.x + p.drift * t + 0.02f * kotlin.math.sin(t * 3f + p.x * 10f)
            val center = Offset(x * size.width, y * size.height)
            rotate(p.spin * t, center) {
                drawRect(
                    p.color.copy(alpha = fade.value),
                    topLeft = center - Offset(p.width / 2, p.width / 4),
                    size = Size(p.width, p.width / 2),
                )
            }
        }
    }
}

@Composable
internal fun roleLabel(role: Role): String = stringResource(
    when (role) {
        Role.CIVILIAN -> R.string.role_civilian
        Role.IMPOSTOR -> R.string.role_impostor
        Role.DRIFTER -> R.string.role_drifter
    },
)

internal fun roleColor(role: Role): Color = when (role) {
    Role.CIVILIAN -> Neon.Turquoise
    Role.IMPOSTOR -> Neon.Pink
    Role.DRIFTER -> Neon.Amber
}
