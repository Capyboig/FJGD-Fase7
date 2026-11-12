package com.example.fjgd_damflixfase7.model

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "peliculas")
data class PeliculaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val director: String,
    val anio: Int,
    val esVista: Boolean = false,
    val esFavorita: Boolean = false
)


@Dao
interface PeliculaDao {
    @Query("SELECT * FROM peliculas ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<PeliculaEntity>>

    @Query("SELECT * FROM peliculas WHERE id = :id")
    suspend fun obtenerPorId(id: Int): PeliculaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(pelicula: PeliculaEntity)

    @Update
    suspend fun actualizar(pelicula: PeliculaEntity)
}



@Database(entities = [PeliculaEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun peliculaDao(): PeliculaDao


    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "damflix_room.db"
                )
                .fallbackToDestructiveMigration()
                .build().also { INSTANCE = it }
                instance
            }
        }
    }
}


class PeliculaRepository(private val dao: PeliculaDao) {
    val todasPeliculas: Flow<List<PeliculaEntity>> = dao.obtenerTodas()

    suspend fun obtenerPorId(id: Int): PeliculaEntity? {
        return dao.obtenerPorId(id)
    }

    suspend fun insertar(pelicula: PeliculaEntity) = dao.insertar(pelicula)
    suspend fun actualizar(pelicula: PeliculaEntity) = dao.actualizar(pelicula)
}