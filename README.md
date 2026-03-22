# BancaMovil Interbank – Android

Aplicación de banca móvil para Android desarrollada como parte del Banking App Challenge. Implementa las tres pantallas principales (Login, Productos y Detalle de Cuenta) con servicios simulados localmente.

---

## Arquitectura implementada

El proyecto sigue **Clean Architecture** combinada con el patrón de presentación **MVVM (Model-View-ViewModel)**. El código se organiza en tres capas horizontales completamente independientes entre sí:

```
app/
├── data/           # Repositorios, DTOs, mocks, DataStore, interceptores
├── domain/         # Modelos puros Kotlin, interfaces de repositorio, casos de uso
└── presentation/   # Screens (Compose), ViewModels, componentes UI, navegación
```

### Diagrama de capas

```
┌──────────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                            │
│  LoginScreen · ProductsScreen · AccountDetailScreen                  │
│  LoginViewModel · ProductsViewModel · AccountDetailViewModel         │
│  MainViewModel (sesión global)                                       │
│  sealed UiState por pantalla → StateFlow unidireccional              │
└───────────────────────────┬──────────────────────────────────────────┘
                            │ invoca
┌───────────────────────────▼──────────────────────────────────────────┐
│                         Domain Layer                                 │
│  LoginUseCase · GetProductsUseCase · RefreshProductsUseCase          │
│  GetAccountMovementsUseCase · LogoutUseCase                          │
│  BankAccount · Transaction · AuthSession · User  (Kotlin puro)       │
│  AuthRepository (i) · ProductsRepository (i) · MovementsRepository (i) │
└───────────────────────────┬──────────────────────────────────────────┘
                            │ implementa
┌───────────────────────────▼──────────────────────────────────────────┐
│                          Data Layer                                  │
│  AuthRepositoryImpl · ProductsRepositoryImpl · MovementsRepositoryImpl │
│  MockAuthService · MockProductsService · MockMovementsService        │
│  UserPreferences (DataStore) · AuthInterceptor                       │
└──────────────────────────────────────────────────────────────────────┘
```

Las dependencias siempre apuntan hacia adentro: `Presentation → Domain ← Data`. La capa de dominio no conoce ni a Presentation ni a Data.

---

## Justificación del patrón

**Clean Architecture + MVVM** fue elegido por las siguientes razones:

1. **Separación de responsabilidades**: cada capa tiene una única razón para cambiar. Si los mocks se reemplazan por una API REST real, solo cambia la capa `data/` sin tocar dominio ni UI.

2. **Testabilidad**: los `UseCase` y los `ViewModel` reciben dependencias por constructor (Hilt), lo que permite sustituirlas por dobles de prueba sin instrumentación. Los tests unitarios y de UI ya incluidos demuestran esto.

3. **Escalabilidad**: añadir una nueva funcionalidad (transferencias, por ejemplo) implica crear un nuevo `UseCase`, repositorio y Screen sin modificar el código existente (Principio Abierto/Cerrado de SOLID).

4. **Estado predecible**: `StateFlow` con `sealed class` por pantalla hace que la UI sea unidireccional. La pantalla solo lee estado; toda la lógica vive en el ViewModel.

5. **Ciclo de vida seguro**: `viewModelScope` para corrutinas y `collectAsStateWithLifecycle` en Compose garantizan que no se produzcan fugas ni actualizaciones de UI en segundo plano.

---

## Stack tecnológico

| Categoría | Librería / Versión |
|---|---|
| Lenguaje | Kotlin 2.1.0 |
| UI | Jetpack Compose (BOM 2024.12.01) |
| DI | Hilt 2.52 |
| Navegación | Navigation Compose 2.8.5 |
| Async | Coroutines + Flow 1.9.0 |
| Persistencia | DataStore 1.1.1 |
| Pull-to-refresh | Accompanist SwipeRefresh 0.36.0 |
| Íconos | Material Symbols Outlined (Google Fonts via GMS) |
| Tests unitarios | MockK · Turbine |
| Tests de UI | Compose Test Rule · FakeViewModel |
| Mín. SDK | 24 |

---

## Decisiones técnicas destacadas

### Modelos de dominio sin Android
`BankAccount`, `AuthSession` y demás modelos son **data classes Kotlin puras**; no implementan `Parcelable` ni ninguna API de Android. La navegación entre pantallas pasa el `accountId` como argumento de ruta en lugar del objeto completo.

### Gestión de sesión
- `SESSION_DURATION_MS = 2 minutos`, `SESSION_CHECK_INTERVAL = 10 segundos` — ambos en `domain/util/SessionConstants.kt`.
- El polling de expiración vive en `AuthRepositoryImpl.isSessionExpired()` como un `Flow<Boolean>` que combina `flatMapLatest` + `delay`. `UserPreferences` solo persiste y expone el timestamp.
- `MainViewModel` observa ese Flow y emite un evento para mostrar el diálogo de sesión expirada.

### Estado UI con sealed classes
Cada ViewModel expone su estado a través de una `sealed class` dedicada:

| ViewModel | Sealed class |
|---|---|
| `LoginViewModel` | `LoginAuthState` (Idle, Loading, Success, Error) + `LoginFormState` |
| `ProductsViewModel` | `ProductsContent` (Loading, Success, LoadError, RefreshError) |
| `AccountDetailViewModel` | `AccountDetailUiState` (Loading, Success, Empty, Error) |

### Corrutinas en la capa correcta
Los repositorios usan `withContext(Dispatchers.IO)` para operaciones de red/disco, de forma que los ViewModels pueden llamar suspend functions desde `viewModelScope` sin preocuparse por el dispatcher.

### Logging condicional
`HttpLoggingInterceptor` registra el body completo solo en builds debug (`BuildConfig.DEBUG`); en release el nivel es `NONE`.

---

## Cobertura de tests

| Archivo | Tipo | Cobertura |
|---|---|---|
| `LoginUseCaseTest` | Unitario (MockK) | Casos de éxito, error y credenciales inválidas |
| `LoginViewModelTest` | Unitario (MockK + Turbine) | Estado inicial, login exitoso/fallido, doble clic, clearError |
| `ProductsViewModelTest` | Unitario (MockK + Turbine) | Carga, error, retry, refresh, diálogos |
| `AccountDetailViewModelTest` | Unitario (MockK) | Loading, éxito, sin movimientos, cuenta no encontrada, error |
| `LoginScreenTest` | UI (Compose + FakeViewModel) | Render inicial, estado de carga, error, navegación tras login |
| `AuthSessionTest` | Unitario | Expiración de sesión |

---

## Servicios mock

Todos los servicios simulan una latencia de 1-3 segundos. Para forzar errores en el servicio de productos, modifica los flags en `MockProductsService`:

```kotlin
mockProductsService.simulateGetError = true      // error en carga inicial
mockProductsService.simulateRefreshError = true  // error en pull-to-refresh
```

**Usuarios válidos para login:**

| Usuario | Contraseña |
|---|---|
| userTest1 | passTest1 |
| User@test | TestPass_ |
| user123& | 123456 |

---

## Instrucciones para correr el proyecto

### Requisitos previos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Android SDK con API nivel 24 como mínimo y API 36 (compileSdk)
- Dispositivo o emulador con Google Play Services (necesario para la fuente Material Symbols Outlined)

### Pasos

1. Clona el repositorio:
   ```bash
   git clone https://github.com/marlonpya/BancaMovilBank.git
   cd BancaMovilBank
   ```

2. Abre el proyecto en Android Studio (`File → Open` y selecciona la carpeta raíz).

3. Espera a que Gradle sincronice las dependencias (puede tardar unos minutos la primera vez).

4. Selecciona un dispositivo/emulador con Google Play Services y ejecuta el proyecto:
   ```bash
   ./gradlew installDebug
   ```

5. La app abre directamente en la pantalla de Login. Usa cualquiera de las credenciales de la tabla anterior para ingresar.

### Ejecutar los tests

```bash
# Tests unitarios
./gradlew test

# Tests de UI (requiere emulador o dispositivo conectado)
./gradlew connectedAndroidTest
```

> **Nota sobre íconos**: la fuente Material Symbols Outlined se descarga automáticamente desde Google Fonts la primera vez que se usa; el dispositivo/emulador necesita conexión a internet en el primer arranque.
