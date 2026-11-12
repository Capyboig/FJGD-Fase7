package com.example.fjgd_damflixfase7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fjgd_damflixfase7.View.AppDamflixMainScreen
import com.example.fjgd_damflixfase7.ui.theme.FJGDDamflixFase7Theme
import com.example.fjgd_damflixfase7.ui.viewmodel.PeliculaViewModel
import com.example.fjgd_damflixfase7.ui.viewmodel.PeliculaViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as DAMFlixApplication
        val factory = PeliculaViewModelFactory(app.repository)

        setContent {
            FJGDDamflixFase7Theme {
                val viewModel: PeliculaViewModel = viewModel(factory = factory)
                AppDamflixMainScreen(viewModel = viewModel)
            }
        }
    }
}
