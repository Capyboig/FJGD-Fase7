package com.example.fjgd_damflixfase7.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.fjgd_damflixfase7.DAMFlixApplication
import com.example.fjgd_damflixfase7.R
import com.example.fjgd_damflixfase7.model.PeliculaEntity
import com.example.fjgd_damflixfase7.ui.viewmodel.DetallePeliculaViewModel
import com.example.fjgd_damflixfase7.ui.viewmodel.DetallePeliculaViewModelFactory
import com.example.fjgd_damflixfase7.ui.viewmodel.DetalleUiState
import com.example.fjgd_damflixfase7.ui.viewmodel.PeliculaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDamflixMainScreen(viewModel: PeliculaViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                listOf(Ruta.Inicio, Ruta.Listado, Ruta.Formulario).forEach { opcion ->
                    NavigationDrawerItem(
                        icon = { Icon(opcion.icon, contentDescription = null) },
                        label = { Text(stringResource(opcion.labelRes)) },
                        selected = rutaActual == opcion.path,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(opcion.path)
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.abrir_menu))
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Ruta.Inicio.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Ruta.Inicio.path) { PantallaInicio() }

                composable(Ruta.Listado.path) {
                    ListaPeliculasScreen(
                        viewModel = viewModel,
                        onPeliculaClick = { peliculaId ->
                            navController.navigate(Ruta.Detalle.crearRuta(peliculaId))
                        }
                    )
                }

                composable(Ruta.Formulario.path) {
                    FormularioAgregarPeliculaScreen(
                        viewModel = viewModel,
                        onGuardado = { navController.navigate(Ruta.Listado.path) }
                    )
                }

                composable(
                    route = Ruta.Detalle.path,
                    arguments = listOf(navArgument("peliculaId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("peliculaId") ?: 0
                    val context = LocalContext.current
                    val app = context.applicationContext as DAMFlixApplication
                    val detalleViewModel: DetallePeliculaViewModel = viewModel(
                        factory = DetallePeliculaViewModelFactory(app.repository)
                    )
                    PantallaDetalleScreen(
                        peliculaId = id,
                        viewModel = detalleViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaInicio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.user_name),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.user_role),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ListaPeliculasScreen(
    viewModel: PeliculaViewModel,
    onPeliculaClick: (Int) -> Unit
) {
    val peliculas by viewModel.listaPeliculas.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(peliculas, key = { it.id }) { pelicula ->
            PeliculaItem(
                pelicula = pelicula,
                onClick = { onPeliculaClick(pelicula.id) },
                onFavoritoClick = { viewModel.conmutarFavorito(pelicula) }
            )
        }
    }
}

@Composable
fun PeliculaItem(
    pelicula: PeliculaEntity,
    onClick: () -> Unit,
    onFavoritoClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = pelicula.titulo, style = MaterialTheme.typography.titleMedium)
                Text(text = stringResource(R.string.detalle_director, pelicula.director), style = MaterialTheme.typography.bodyMedium)
                Text(text = stringResource(R.string.detalle_anio, pelicula.anio), style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onFavoritoClick) {
                Icon(
                    imageVector = if (pelicula.esFavorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun FormularioAgregarPeliculaScreen(
    viewModel: PeliculaViewModel,
    onGuardado: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var esVista by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(stringResource(R.string.label_titulo)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = director,
            onValueChange = { director = it },
            label = { Text(stringResource(R.string.label_director)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = anio,
            onValueChange = { anio = it },
            label = { Text(stringResource(R.string.label_anio)) },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = esVista,
                onCheckedChange = { esVista = it }
            )
            Text(text = stringResource(R.string.chk_vista))
        }
        Button(
            onClick = {
                val anioInt = anio.toIntOrNull() ?: 0
                if (titulo.isNotBlank()) {
                    viewModel.agregarPelicula(titulo, director, anioInt, esVista)
                    onGuardado()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_guardar))
        }
    }
}

@Composable
fun PantallaDetalleScreen(
    peliculaId: Int,
    viewModel: DetallePeliculaViewModel,
    onVolver: () -> Unit
) {
    LaunchedEffect(peliculaId) {
        viewModel.cargarDetallePelicula(peliculaId)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val uiState = state) {
            is DetalleUiState.Cargando -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.msg_cargando))
                }
            }
            is DetalleUiState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.msg_error))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onVolver) {
                        Text(stringResource(R.string.btn_volver))
                    }
                }
            }
            is DetalleUiState.Exito -> {
                val pelicula = uiState.pelicula
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = pelicula.titulo, style = MaterialTheme.typography.headlineMedium)
                        Text(text = stringResource(R.string.detalle_director, pelicula.director), style = MaterialTheme.typography.bodyLarge)
                        Text(text = stringResource(R.string.detalle_anio, pelicula.anio), style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = stringResource(if (pelicula.esVista) R.string.estado_vista else R.string.estado_pendiente),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.btn_volver))
                        }
                    }
                }
            }
        }
    }
}
