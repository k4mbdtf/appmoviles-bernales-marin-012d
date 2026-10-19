package com.paolicias.sweet.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.paolicias.sweet.data.local.AppDatabase
import com.paolicias.sweet.data.local.Evento
import com.paolicias.sweet.data.local.Producto
import com.paolicias.sweet.data.local.TarifaRango
import com.paolicias.sweet.data.local.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object Rol {
    const val CLIENTE = "CLIENTE"
    const val ADMIN = "ADMIN"
}

object EstadoAbono {
    const val PENDIENTE = "PENDIENTE"
    const val ABONO_50 = "ABONO_50"
    const val COMPLETADO = "COMPLETADO"
}

/** Resultado de cotizar un evento segun el tarifario escalonado. */
data class Cotizacion(
    val asistentes: Int,
    val tarifaPorPersona: Int,
    val total: Int,
    val abono: Int
)

/**
 * Unica puerta de acceso a los datos. Los ViewModels dependen de esta clase y no
 * de los DAO, de modo que migrar a Retrofit despues solo cambia esta capa.
 */
class Repository private constructor(db: AppDatabase) {

    private val usuarioDao = db.usuarioDao()
    private val productoDao = db.productoDao()
    private val tarifaDao = db.tarifaDao()
    private val eventoDao = db.eventoDao()
    private val mutexPrecarga = Mutex()

    // ---------- Lecturas reactivas ----------

    val productos: Flow<List<Producto>> = productoDao.observarTodos()

    val todosLosEventos: Flow<List<Evento>> = eventoDao.observarTodos()

    fun eventosDeCliente(clienteId: Int): Flow<List<Evento>> =
        eventoDao.observarPorCliente(clienteId)

    fun observarEvento(id: Int): Flow<Evento?> = eventoDao.observarPorId(id)

    // ---------- Usuarios ----------

    /** Retorna el usuario si las credenciales son correctas, o null. */
    suspend fun iniciarSesion(email: String, password: String): Usuario? {
        precargarSiVacio()
        val usuario = usuarioDao.buscarPorEmail(email.trim().lowercase()) ?: return null
        return if (usuario.password == password) usuario else null
    }

    /** Retorna false si el correo ya estaba registrado. */
    suspend fun registrarUsuario(usuario: Usuario): Boolean =
        try {
            usuarioDao.insertar(usuario.copy(email = usuario.email.trim().lowercase()))
            true
        } catch (e: SQLiteConstraintException) {
            false
        }

    // ---------- Cotizacion ----------

    /** Retorna null si no existe un rango del tarifario para esa cantidad. */
    suspend fun cotizar(asistentes: Int): Cotizacion? {
        val tarifa = tarifaDao.buscarPorAsistentes(asistentes) ?: return null
        val total = tarifa.precioPorPersona * asistentes
        return Cotizacion(
            asistentes = asistentes,
            tarifaPorPersona = tarifa.precioPorPersona,
            total = total,
            abono = total / 2
        )
    }

    // ---------- Eventos ----------

    /**
     * Registra una reserva. El total se recalcula aqui con el tarifario y no se
     * confia en el valor que muestre la UI. Retorna el id o null si no hay tarifa.
     */
    suspend fun registrarEvento(
        clienteId: Int,
        fechaIso: String,
        bloqueHorario: String,
        asistentes: Int,
        observaciones: String
    ): Long? {
        val cotizacion = cotizar(asistentes) ?: return null
        return eventoDao.insertar(
            Evento(
                clienteId = clienteId,
                fecha = fechaIso,
                bloqueHorario = bloqueHorario,
                asistentes = asistentes,
                observaciones = observaciones.trim(),
                total = cotizacion.total,
                estadoAbono = EstadoAbono.PENDIENTE
            )
        )
    }

    suspend fun adjuntarComprobante(eventoId: Int, uri: String) =
        eventoDao.actualizarComprobante(eventoId, uri)

    suspend fun actualizarEstadoAbono(eventoId: Int, estado: String) =
        eventoDao.actualizarEstado(eventoId, estado)

    // ---------- Datos sinteticos ----------

    /** Inserta los datos de ejemplo la primera vez. Es segura de llamar varias veces. */
    suspend fun precargarSiVacio() = mutexPrecarga.withLock {
        if (usuarioDao.contar() == 0) {
            usuarioDao.insertar(
                Usuario(
                    nombre = "Administradora Paolicias",
                    empresa = "Paolicias.Sweet",
                    telefono = "912345678",
                    email = "admin@paolicias.cl",
                    password = "admin123",
                    rol = Rol.ADMIN
                )
            )
            usuarioDao.insertar(
                Usuario(
                    nombre = "Cliente Demo",
                    empresa = "Empresa Demo SpA",
                    telefono = "987654321",
                    email = "cliente@demo.cl",
                    password = "cliente123",
                    rol = Rol.CLIENTE
                )
            )
        }
        if (productoDao.contar() == 0) {
            productoDao.insertarTodos(productosIniciales())
        }
        if (tarifaDao.contar() == 0) {
            tarifaDao.insertarTodas(tarifasIniciales())
        }
    }

    private fun imagen(semilla: String) = "https://picsum.photos/seed/$semilla/600/400"

    private fun productosIniciales() = listOf(
        Producto(nombre = "Torta de mil hojas manjar", descripcion = "Clasica torta de 12 porciones con manjar casero y nueces.", precioBase = 24000, categoria = "Tortas", imagenUrl = imagen("milhojas")),
        Producto(nombre = "Torta tres leches", descripcion = "Bizcocho humedo bañado en tres leches con crema chantilly.", precioBase = 22000, categoria = "Tortas", imagenUrl = imagen("tresleches")),
        Producto(nombre = "Torta chocolate y frambuesa", descripcion = "Bizcocho de cacao con relleno de frambuesa y ganache.", precioBase = 26000, categoria = "Tortas", imagenUrl = imagen("frambuesa")),
        Producto(nombre = "Cheesecake de frutos rojos", descripcion = "Base de galleta, crema suave y cobertura de frutos rojos.", precioBase = 20000, categoria = "Postres", imagenUrl = imagen("cheesecake")),
        Producto(nombre = "Tartaletas de limon", descripcion = "Docena de mini tartaletas con crema de limon y merengue.", precioBase = 12000, categoria = "Postres", imagenUrl = imagen("tartaletas")),
        Producto(nombre = "Cupcakes decorados", descripcion = "Docena de cupcakes de vainilla con decoracion tematica.", precioBase = 15000, categoria = "Postres", imagenUrl = imagen("cupcakes")),
        Producto(nombre = "Mesa de dulces premium", descripcion = "Seleccion de dulces, alfajores y mini postres para el evento.", precioBase = 85000, categoria = "Banqueteria", imagenUrl = imagen("mesadulces")),
        Producto(nombre = "Canapes salados variados", descripcion = "Bandeja de 30 canapes frios surtidos.", precioBase = 28000, categoria = "Banqueteria", imagenUrl = imagen("canapes")),
        Producto(nombre = "Brochetas de fruta con chocolate", descripcion = "Brochetas de fruta de temporada bañadas en chocolate.", precioBase = 18000, categoria = "Banqueteria", imagenUrl = imagen("brochetas")),
        Producto(nombre = "Jugo natural en dispensador", descripcion = "Dispensador de 10 litros de jugo natural de temporada.", precioBase = 12000, categoria = "Bebidas", imagenUrl = imagen("jugo"))
    )

    /** 10 rangos: 10-19, 20-29, ... 90-99 y 100. Mas asistentes, menor precio por persona. */
    private fun tarifasIniciales(): List<TarifaRango> {
        val precios = listOf(12000, 11500, 11000, 10500, 10000, 9500, 9000, 8500, 8000, 7500)
        return precios.mapIndexed { i, precio ->
            val min = 10 * (i + 1)
            val max = if (min == 100) 100 else min + 9
            TarifaRango(minPersonas = min, maxPersonas = max, precioPorPersona = precio)
        }
    }

    companion object {
        @Volatile
        private var instancia: Repository? = null

        fun obtener(context: Context): Repository =
            instancia ?: synchronized(this) {
                instancia ?: Repository(AppDatabase.obtener(context)).also { instancia = it }
            }
    }
}
