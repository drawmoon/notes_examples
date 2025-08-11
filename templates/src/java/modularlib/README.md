# Gradle Java Modular Project Template

## Project Structure Overview

```
project-root/
├── build.gradle              # 📄 Root project build configuration (Kotlin DSL)
├── settings.gradle           # 📄 Module inclusion & project hierarchy definition
├── gradle.properties         # 📊 Global configuration parameters
├── gradle/                   # 📁 Gradle wrapper distribution
│   └── wrapper/              # 📦 Distribution binaries & configuration
├── app/                      # 📱 Application module (primary artifact)
│   ├── build.gradle          # 📄 Module-specific build configuration
│   └── src/
│       ├── main/
│       │   ├── java/         # 💻 Core application logic
│       │   └── resources/    # 🎨 Application resources
│       └── test/
│           ├── java/         # 🧪 Application test suite
│           └── resources/    # 📁 Test resources
├── core/                     # ⚙️ Core library module (shared functionality)
│   ├── build.gradle          # 📄 Module build configuration
│   └── src/...
├── utils/                    # 🧰 Utility library module
│   ├── build.gradle          # 📄 Module build configuration
│   └── src/...
└── README.md                 # 📝 Project documentation
```

## Key Features

- ✅ **Modular Architecture** - Separation of concerns through multiple modules
- 🔄 **Inter-module Dependencies** - Type-safe dependencies between modules
- 🧱 **Scalable Structure** - Easy to add new modules
- 📦 **Standardized Configuration** - Consistent build settings
- 📚 **Multi-module Build** - Unified build pipeline

## Build Instructions

```bash
# 📦 Build all modules
./gradlew build

# 🧪 Run tests across all modules
./gradlew test

# 📦 Build specific module
./gradlew :app:build

# 🧪 Run tests for specific module
./gradlew :core:test

# 📦 Generate all artifacts
./gradlew assemble

# 📤 Publish all modules to repository
./gradlew publish

# 📈 View project dependency graph
./gradlew dependencies
```

## Best Practices

1. 📁 **Module Organization** - Group functionality by feature or layer
2. 🔐 **Dependency Management** - Keep dependencies private where possible
3. 📌 **Version Consistency** - Use root project properties for version management
4. 🧼 **Clean Architecture** - Maintain clear separation between modules

## License

[MIT License](LICENSE) - Permits modification and distribution with proper attribution

This template provides a foundation for scalable Java projects using Gradle's powerful multi-module capabilities. The structure encourages clean architecture patterns while maintaining build efficiency through shared configuration and dependency management.