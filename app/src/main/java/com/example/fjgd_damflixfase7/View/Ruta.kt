package com.example.fjgd_damflixfase7.View

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fjgd_damflixfase7.R

sealed class Ruta(val path: String, val labelRes: Int, val icon: ImageVector) {
    object Inicio : Ruta("inicio", R.string.nav_inicio, Icons.Filled.Home)
    object Listado : Ruta("listado", R.string.nav_listado, Icons.Filled.List)
    object Formulario : Ruta("alta", R.string.nav_alta, Icons.Filled.Add)

    object Detalle : Ruta("detalle/{peliculaId}", R.string.nav_detalle, Icons.Filled.Info) {
        fun crearRuta(peliculaId: Int) = "detalle/$peliculaId"
    }
}
