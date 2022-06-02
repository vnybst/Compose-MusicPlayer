package vny.bst.composemusicplayer.ui.screens.main

import androidx.compose.runtime.Composable
import vny.bst.composemusicplayer.ui.screens.home.HomeScreen
import vny.bst.composemusicplayer.viewmodels.HomeViewModel

@Composable
fun MainScreen(homeViewModel: HomeViewModel){
    HomeScreen(homeViewModel)
}