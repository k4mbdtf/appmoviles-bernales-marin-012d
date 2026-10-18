package com.paolicias.sweet.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios", indices = [Index(value = ["email"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val empresa: String = "",
    val telefono: String,
    val email: String,
    val password: String,
    val rol: String = "CLIENTE" // CLIENTE o ADMIN
)

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precioBase: Int,
    val categoria: String,
    val imagenUrl: String = ""
)

@Entity(tableName = "tarifas")
data class TarifaRango(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val minPersonas: Int,
    val maxPersonas: Int,
    val precioPorPersona: Int
)

@Entity(tableName = "eventos")
data class Evento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clienteId: Int,
    val fecha: String,
    val bloqueHorario: String,
    val asistentes: Int,
    val observaciones: String = "",
    val total: Int,
    val estadoAbono: String = "PENDIENTE", // PENDIENTE, ABONO_50, COMPLETADO
    val comprobanteUri: String = ""
)
