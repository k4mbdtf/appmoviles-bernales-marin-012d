package com.paolicias.sweet.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos ORDER BY nombre")
    fun observarTodos(): Flow<List<Producto>>

    @Insert
    suspend fun insertarTodos(productos: List<Producto>)

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contar(): Int
}

@Dao
interface TarifaDao {
    @Query("SELECT * FROM tarifas WHERE :asistentes BETWEEN minPersonas AND maxPersonas LIMIT 1")
    suspend fun buscarPorAsistentes(asistentes: Int): TarifaRango?

    @Insert
    suspend fun insertarTodas(tarifas: List<TarifaRango>)

    @Query("SELECT COUNT(*) FROM tarifas")
    suspend fun contar(): Int
}

@Dao
interface EventoDao {
    @Insert
    suspend fun insertar(evento: Evento): Long

    @Query("SELECT * FROM eventos WHERE clienteId = :clienteId ORDER BY fecha")
    fun observarPorCliente(clienteId: Int): Flow<List<Evento>>

    @Query("SELECT * FROM eventos ORDER BY fecha")
    fun observarTodos(): Flow<List<Evento>>

    @Query("UPDATE eventos SET estadoAbono = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)
}
