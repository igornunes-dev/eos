# EOS - Elegant Object Scaffolder

🚀 **A powerful code generation tool for Spring Boot projects**

EOS streamlines Spring Boot development by automating the creation of common components and boilerplate code. Focus on building features, not repetitive scaffolding.

## ✨ Features

- 🎯 **Maven Plugin Integration** - Seamlessly integrates with your existing Maven workflow
- 🔧 **CLI Tool** - Standalone executable for quick scaffolding
- 🧠 **Smart Detection** - Automatically detects your project structure and package naming conventions
- 📦 **Template-based Generation** - Customizable Freemarker templates
- 🎨 **Multiple Architectures** - Supports layered, hexagonal, and clean architecture patterns
- ⚡ **Zero Configuration** - Works out of the box with any Spring Boot project

## 🚀 Quick Start

### Maven Plugin

Add the plugin to your `pom.xml`:

```xml
<plugin>
    <groupId>org.example</groupId>
    <artifactId>eos-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
</plugin>
```

Generate components:

```bash
# Generate a REST controller
mvn eos:controller -Dname=UserController

# Generate a service
mvn eos:service -Dname=ProductService

# Custom package
mvn eos:controller -Dname=OrderController -Dpackage=api.controllers
```

### CLI Tool

Install globally and use anywhere:

```bash
# Install
./install.sh

# Use
eos eos:controller UserController
eos eos:service ProductService
```

## 📋 Available Generators

- `eos:controller` - Generate REST controllers with CRUD operations
- `eos:service` - Generate service layer classes
- `eos:entity` - Generate JPA entities
- `eos:repository` - Generate Spring Data repositories
- `eos:dto` - Generate Data Transfer Objects

## 🎯 Why EOS?

**Before EOS:**
```java
// Manually create:
// - UserController.java (50+ lines)
// - UserService.java (40+ lines)
// - User.java (30+ lines)
// - UserRepository.java (10+ lines)
// - UserDTO.java (25+ lines)
// ... hours of repetitive coding
```

**With EOS:**
```bash
mvn eos:controller -Dname=UserController
mvn eos:service -Dname=UserService
mvn eos:entity -Dname=User
# Done in seconds! ✨
```

## 🏗️ Architecture

EOS is built with a modular architecture:

- **eos-core** - Core generation engine and project analysis
- **eos-maven-plugin** - Maven plugin for seamless IDE integration
- **eos-cli** - Standalone command-line interface

## 🛠️ Requirements

- Java 17+
- Maven 3.6+
- Spring Boot 2.5+ (for target projects)

## 📖 Documentation

For detailed documentation, examples, and customization guides, visit our [Wiki](link-to-wiki).

## 🎨 Customization

EOS uses Freemarker templates, making it easy to customize generated code to match your team's conventions and standards.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 💡 Roadmap

- [ ] Support for Gradle projects
- [ ] GraphQL support
- [ ] Integration tests generation
- [ ] Custom template repositories
- [ ] Interactive mode for guided generation
- [ ] Multi-module project support
- [ ] OpenAPI/Swagger integration
- [ ] Database migration generators

## 🙏 Acknowledgments

Built for the Spring Boot community by developers who understand the pain of repetitive boilerplate code.

---

**Made with ❤️ for productive Spring Boot development**
