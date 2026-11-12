package com.example.fjgd_damflixfase7

import android.app.Application
import com.example.fjgd_damflixfase7.model.AppDatabase
import com.example.fjgd_damflixfase7.model.PeliculaRepository

class DAMFlixApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { PeliculaRepository(database.peliculaDao()) }
}
