# Paolicias.Sweet 🎂

Aplicación móvil Android para **Paolicias.Sweet**, banquetería y pastelería de Vallenar. Permite a los clientes explorar el catálogo, configurar y cotizar eventos en tiempo real, y adjuntar el comprobante del abono. La administradora gestiona la agenda de eventos, valida abonos y obtiene una estimación de insumos y personal.

Proyecto de la asignatura **Desarrollo de Aplicaciones Móviles (DSY1105)**, DUOC UC, Evaluación Parcial 2.

## Integrantes

| Nombre |

| Camilo Bernales | 
| Roberto Marin |

## Funcionalidades

### Cliente
- Inicio de sesión con validación por campo (correo y contraseña) y redirección según rol.
- Catálogo de productos con imágenes, precios y filtro por categoría.
- Configuración del evento: fecha (DatePicker), bloque horario, asistentes (10 a 100, en pasos de 10) y observaciones o alergias.
- **Cotización en tiempo real** según tarifario escalonado por cantidad de asistentes, con abono del 50%.
- Resumen de la reserva registrada.
- Mis reservas: adjuntar el comprobante del abono con **cámara** o **galería**.

### Administradora
- Agenda cronológica de eventos con estado del abono.
- Detalle del pedido con observaciones y alergias destacadas.
- Visualización del comprobante y **validación del abono**.
- Estimación de insumos y personal según asistentes.

### Recursos nativos

| Recurso | Uso | Permiso |
|---|---|---|
| Cámara | Fotografiar el comprobante de abono | `CAMERA`, solicitado en tiempo de ejecución |
| Galería (Photo Picker) | Elegir el comprobante desde las fotos | No requiere permiso |
| Notificaciones locales | Aviso de nueva reserva registrada | `POST_NOTIFICATIONS` en Android 13+ |

Si el usuario rechaza un permiso, la app sigue funcionando y muestra un mensaje con la alternativa.

## Tecnologías y arquitectura

- **Kotlin** + **Jetpack Compose** + **Material 3**
- Arquitectura **MVVM**: `UI (Compose) -> ViewModel (UiState) -> Repository -> DAO (Room)`
- **Room** para persistencia local (usuarios, productos, tarifario y eventos)
- **Navigation Compose** con flujo separado para cliente y administradora
- **Coil** para carga de imágenes
- Validación centralizada en `util/Validadores.kt`, desacoplada de la UI
- Animaciones: `AnimatedVisibility`, `animateColorAsState`, `AnimatedContent`, `Crossfade`, `animateIntAsState`, `animateContentSize`

```
com.paolicias.sweet
├── data/local        # Entidades, DAOs y AppDatabase (Room)
├── data/repository   # Repository: única fuente de datos para los ViewModels
├── viewmodel         # LoginViewModel, CatalogoViewModel, EventoViewModel, ReservasViewModel
├── util              # Validadores, Sesion, Formato, Notificaciones, Estimador
├── navigation        # Rutas y AppNavHost
└── ui                # theme, components (CampoValidado, EstadoChip...) y screens
```

El Repository aísla el origen de datos para migrar a una API REST (Retrofit + Spring Boot) en la siguiente etapa sin modificar los ViewModels.

## Mockups

Las capturas de las pantallas principales están en [`/mockups`](/mockups).

## Cómo ejecutar

1. Clonar el repositorio:
```
   git clone https://github.com/k4mbdtf/appmoviles-bernales-marin-012d.git
```
2. Abrir la carpeta **`PaoliciasSweet`** (no la raíz) en Android Studio.
3. Esperar la sincronización de Gradle.
4. Ejecutar en un emulador o dispositivo con **Android 8.0 (API 26)** o superior.
5. Aceptar los permisos de notificaciones y cámara cuando la app los solicite.

### Cuentas de prueba (datos sintéticos)

| Rol | Correo | Contraseña |
|---|---|---|
| Cliente | cliente@demo.cl | cliente123 |
| Administradora | admin@paolicias.cl | admin123 |

> Todos los datos de la aplicación son **sintéticos**, en conformidad con la Ley 19.628 y la Ley 21.719 de protección de datos personales.
