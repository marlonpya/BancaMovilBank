# Banca Móvil Interbank

Aplicación móvil nativa Android desarrollada con Jetpack Compose que implementa un sistema bancario
básico con autenticación, visualización de productos-cuentas y consulta de movimientos.

## 📋 Características

### ✅ Funcionalidades Implementadas

- **Autenticación de Usuario**
  - Login con validación de credenciales
  - Manejo de tokens (accessToken, refreshToken)
  - Expiración automática de sesión (2 minutos)
  - Almacenamiento seguro de credenciales

- **Gestión de Productos Bancarios**
  - Lista de cuentas bancarias del usuario
  - Pull-to-refresh para actualizar información
  - Visualización de saldos en tiempo real
  - Soporte para múltiples tipos de cuenta (Soles, Dólares, Crédito)

- **Detalle de Cuenta y Movimientos**
  - Vista detallada de cada cuenta
  - Historial de transacciones
  - Información de saldos y referencias
  - Categorización de movimientos por tipo

- **Navegación y UX**
  - Bottom Navigation con pestañas
  - Navegación fluida entre pantallas
  - Estados de carga y error bien definidos
  - Feedback visual para todas las acciones

### 🏗️ Arquitectura

- **Clean Architecture** con separación en capas (Data, Domain, Presentation)
- **MVVM Pattern** con ViewModels reactivos
- **Inyección de Dependencias** con Hilt
- **Servicios Mock** para desarrollo y testing
- **Material Design 3** con tema personalizado

```
app/
├── src/main/java/com/microsol/bancamovilinterbank/
│   ├── data/                     # Capa de datos
│   │   ├── local/               # Almacenamiento local (DataStore)
│   │   ├── remote/              # API y servicios mock
│   │   └── repository/          # Implementación de repositorios
│   ├── domain/                  # Capa de negocio
│   │   ├── model/              # Entidades del dominio
│   │   ├── repository/         # Interfaces de repositorios
│   │   ├── usecase/           # Casos de uso
│   │   └── util/              # Utilidades (Result, etc.)
│   ├── presentation/           # Capa de presentación
│   │   ├── components/        # Composables reutilizables
│   │   ├── navigation/        # Navegación
│   │   ├── screens/          # Pantallas principales
│   │   └── viewmodel/        # ViewModels
│   ├── di/                   # Módulos de Hilt
│   └── ui/theme/            # Tema Material Design 3
└── src/test/                # Tests unitarios
```

## 🛠️ Tecnologías Utilizadas

### Core
- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño

### Arquitectura
- **Hilt** - Inyección de dependencias
- **Navigation Compose** - Navegación entre pantallas
- **ViewModel & LiveData** - Gestión de estado
- **Coroutines & Flow** - Programación asíncrona

### Almacenamiento
- **DataStore Preferences** - Almacenamiento de preferencias
- **Retrofit** - Cliente HTTP (configurado para futuro uso)


