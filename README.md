# BancaMovil Interbank – Android

Aplicación de banca móvil para Android desarrollada como parte del Banking App Challenge. Implementa las tres pantallas principales (Login, Productos y Detalle de Cuenta) con servicios simulados localmente.

---

## Arquitectura implementada

El proyecto sigue **Clean Architecture** combinada con el patrón de presentación **MVVM (Model-View-ViewModel)**. El código se organiza en tres capas horizontales completamente independientes entre sí:

```
app/
├── data/           # Capa de datos: repositorios, DTOs, mocks, DataStore
├── domain/         # Capa de dominio: modelos, interfaces, casos de uso
└── presentation/   # Capa de presentación: Screens (Compose), ViewModels
    └── core/       # Componentes UI reutilizables, navegación, sesión
```

### Diagrama de capas

```
┌──────────────────────────────────────────────────────────────────┐
│                      Presentation Layer                          │
│  LoginScreen · ProductsScreen · AccountDetailScreen             │
│  LoginViewModel · ProductsViewModel · AccountDetailViewModel     │
│  (StateFlow + sealed UiState por pantalla)                       │
└───────────────────────────┬──────────────────────────────────────┘
                            │ invoca
┌───────────────────────────▼──────────────────────────────────────┐
│                       Domain Layer                               │
│  LoginUseCase · GetProductsUseCase · RefreshProductsUseCase      │
│  GetAccountMovementsUseCase · ValidateSessionUseCase             │
│  BankAccount · Transaction · AuthSession · User                  │
│  AuthRepository (i) · ProductsRepository (i) · MovementsRepository (i) │
└───────────────────────────┬──────────────────────────────────────┘
                            │ implementa
┌───────────────────────────▼──────────────────────────────────────┐
│                        Data Layer                                │
│  AuthRepositoryImpl · ProductsRepositoryImpl · MovementsRepositoryImpl │
│  MockAuthService · MockProductsService · MockMovementsService    │
│  UserPreferences (DataStore) · AuthInterceptor                   │
└──────────────────────────────────────────────────────────────────┘
```

Las dependencias siempre apuntan hacia adentro: `Presentation → Domain ← Data`. La capa de dominio no conoce ni a Presentation ni a Data.

---

## Justificación del patrón

**Clean Architecture + MVVM** fue elegido por las siguientes razones:

1. **Separación de responsabilidades**: cada capa tiene una única razón para cambiar. Si el día de mañana los mocks se reemplazan por llamadas reales a una API REST, solo cambia la capa `data/` sin tocar nada del dominio ni de la UI.

2. **Testabilidad**: los `UseCase` y los `ViewModel` reciben sus dependencias por constructor (Hilt), lo que permite sustituirlas por dobles de prueba en tests unitarios sin necesidad de instrumentación. Los tests de `LoginUseCase`, `AuthSession` y `LoginViewModel` ya incluidos en el proyecto demuestran esto.

3. **Escalabilidad**: añadir una nueva funcionalidad (por ejemplo, transferencias) implica crear un nuevo `UseCase`, un nuevo repositorio y una nueva Screen, sin modificar el código existente (Principio Abierto/Cerrado de SOLID).

4. **Mantenibilidad**: el `StateFlow` con `sealed class UiState` por pantalla hace que el estado de la UI sea predecible y unidireccional. La pantalla solo lee estado; toda la lógica vive en el ViewModel.

5. **Ciclo de vida seguro**: el uso de `viewModelScope` para corrutinas y `collectAsStateWithLifecycle` en Compose garantiza que no se produzcan fugas de memoria ni actualizaciones de UI cuando la app está en segundo plano.

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
| Mín. SDK | 24 |

---

## Servicios mock

Todos los servicios simulan una latencia de 3 segundos (`delay(3000)`). Para alternar entre éxito y error en los servicios de cuentas, modifica los flags en `MockProductsService`:

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
- Dispositivo o emulador con Google Play Services (necesario para la descarga del font Material Symbols Outlined)

### Pasos

1. Clona el repositorio:
   ```bash
   git clone https://github.com/marlonpya/BancaMovilBank.git
   cd BancaMovilBank
   ```

2. Abre el proyecto en Android Studio (`File → Open` y selecciona la carpeta raíz).

3. Espera a que Gradle sincronice las dependencias (puede tardar unos minutos la primera vez).

4. Selecciona un dispositivo/emulador con Google Play Services y ejecuta el proyecto con el botón Run (▶) o mediante:
   ```bash
   ./gradlew installDebug
   ```

5. La app abre directamente en la pantalla de Login. Usa cualquiera de las credenciales de la tabla anterior para ingresar.

> **Nota sobre íconos**: la fuente Material Symbols Outlined se descarga automáticamente desde Google Fonts la primera vez que se usa, por lo que el dispositivo/emulador necesita conexión a internet en el primer arranque.
