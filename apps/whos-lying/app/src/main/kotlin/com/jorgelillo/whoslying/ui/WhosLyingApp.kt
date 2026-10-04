package com.jorgelillo.whoslying.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jorgelillo.whoslying.WhosLyingApplication
import com.jorgelillo.whoslying.domain.WordPack
import com.jorgelillo.whoslying.ui.screens.AboutSheet
import com.jorgelillo.whoslying.ui.screens.CountdownScreen
import com.jorgelillo.whoslying.ui.screens.HomeScreen
import com.jorgelillo.whoslying.ui.screens.HowToSheet
import com.jorgelillo.whoslying.ui.screens.PackEditScreen
import com.jorgelillo.whoslying.ui.screens.PacksScreen
import com.jorgelillo.whoslying.ui.screens.PlayersScreen
import com.jorgelillo.whoslying.ui.screens.ResultScreen
import com.jorgelillo.whoslying.ui.screens.RevealScreen
import com.jorgelillo.whoslying.ui.screens.SetupScreen
import com.jorgelillo.whoslying.ui.screens.VoteScreen

private const val NEW = "new"

@Composable
fun WhosLyingApp(app: WhosLyingApplication) {
    val language = LocalConfiguration.current.locales[0].language
    val vm: GameViewModel = viewModel(factory = GameViewModel.factory(app.repository, language))
    val saved by vm.saved.collectAsStateWithLifecycle()
    val state = saved ?: return // first disk read in progress (a few ms)
    val nav = rememberNavController()
    var showHowTo by rememberSaveable { mutableStateOf(false) }
    var showAbout by rememberSaveable { mutableStateOf(false) }

    NavHost(nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onPlay = { nav.navigate("players") },
                onHowTo = { showHowTo = true },
                onPacks = { nav.navigate("packs") },
                onAbout = { showAbout = true },
            )
        }
        composable("players") {
            PlayersScreen(state.players, vm::addPlayer, vm::removePlayer, onContinue = { nav.navigate("setup") }, onBack = { nav.popBackStack() })
        }
        composable("setup") {
            SetupScreen(
                playerCount = state.players.size,
                settings = state.settings,
                packs = vm.allPacks(state),
                onChange = vm::updateSettings,
                onStart = { if (vm.startGame()) nav.navigate("reveal") },
                onBack = { nav.popBackStack() },
            )
        }
        composable("reveal") {
            val game = vm.game ?: return@composable LaunchedEffect(Unit) { nav.goHome() }
            RevealScreen(game, onDone = { nav.navigate("countdown") { popUpTo("setup") } }, onQuit = { nav.goHome() })
        }
        composable("countdown") {
            val game = vm.game ?: return@composable LaunchedEffect(Unit) { nav.goHome() }
            CountdownScreen(game.starter, onDone = { nav.navigate("vote") { popUpTo("setup") } })
        }
        composable("vote") {
            val game = vm.game ?: return@composable LaunchedEffect(Unit) { nav.goHome() }
            LaunchedEffect(vm.revision) { if (game.isOver) nav.navigate("result") { popUpTo("setup") } }
            VoteScreen(
                game = game,
                alive = remember(vm.revision) { game.alive.map { it.player }.toSet() },
                elimination = vm.lastElimination,
                onVote = vm::eliminate,
                onGuess = vm::guess,
                onDismissElimination = vm::dismissElimination,
                onRevealAll = vm::revealAll,
                onQuit = { nav.goHome() },
            )
        }
        composable("result") {
            val game = vm.game ?: return@composable LaunchedEffect(Unit) { nav.goHome() }
            ResultScreen(
                game,
                onPlayAgain = { if (vm.startGame()) nav.navigate("reveal") { popUpTo("setup") } },
                onHome = { nav.goHome() },
            )
        }
        composable("packs") {
            PacksScreen(vm.builtInPacks, state.customPacks, onEdit = { id -> nav.navigate("pack/${id ?: NEW}") }, onBack = { nav.popBackStack() })
        }
        composable("pack/{id}") { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            val isNew = id == NEW
            val initial = remember(id) {
                if (isNew) WordPack(vm.newPackId(), "", "📝", emptyList(), custom = true) else state.customPacks.firstOrNull { it.id == id }
            } ?: return@composable LaunchedEffect(Unit) { nav.popBackStack() }
            PackEditScreen(
                initial = initial,
                isNew = isNew,
                onSave = { vm.savePack(it); nav.popBackStack() },
                onDelete = { vm.deletePack(initial.id); nav.popBackStack() },
                onBack = { nav.popBackStack() },
            )
        }
    }

    if (showHowTo) HowToSheet(onDismiss = { showHowTo = false })
    if (showAbout) AboutSheet(onDismiss = { showAbout = false })
}

private fun NavHostController.goHome() {
    popBackStack("home", inclusive = false)
}
