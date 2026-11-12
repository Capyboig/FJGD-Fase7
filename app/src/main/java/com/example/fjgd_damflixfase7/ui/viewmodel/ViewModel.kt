package com.example.fjgd_damflixfase7.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fjgd_damflixfase7.model.PeliculaEntity
import com.example.fjgd_damflixfase7.model.PeliculaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PeliculaViewModel(private val repository: PeliculaRepository) : ViewModel() {
    val listaPeliculas: StateFlow<List<PeliculaEntity>> = repository.todasPeliculas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



    fun agregarPelicula(titulo: String, director: String, anio: Int, esVista: Boolean) {
        viewModelScope.launch {
            repository.insertar(PeliculaEntity(titulo = titulo, director = director, anio = anio, esVista = esVista))
        }
    }

    fun conmutarFavorito(pelicula: PeliculaEntity) {
        viewModelScope.launch {
            repository.actualizar(pelicula.copy(esFavorita = !pelicula.esFavorita))
        }
    }
}


class PeliculaViewModelFactory(private val repository: PeliculaRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return PeliculaViewModel(repository) as T
    }
}


sealed interface DetalleUiState {
    object Cargando : DetalleUiState
    data class Exito(val pelicula: PeliculaEntity) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}


class DetallePeliculaViewModel(private val repository: PeliculaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    fun cargarDetallePelicula(peliculaId: Int) {
        viewModelScope.launch {
            _uiState.value = DetalleUiState.Cargando
            val resultado = withContext(Dispatchers.IO) {
                delay(1000)
                repository.obtenerPorId(peliculaId)
            }

            if (resultado != null) {
                _uiState.value = DetalleUiState.Exito(resultado)
            } else {
                _uiState.value = DetalleUiState.Error("Pelicula no encontrada")
            }
        }
    }
}

class DetallePeliculaViewModelFactory(private val repository: PeliculaRepository): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return DetallePeliculaViewModel(repository) as T
    }
}