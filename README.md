# EOS - Elegant Object Scaffolder

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5+-green.svg)](https://spring.io/projects/spring-boot)

> 🚀 **A powerful Maven plugin that accelerates Spring Boot development by automating boilerplate creation**

EOS eliminates repetitive coding tasks by intelligently generating Spring Boot components with just a single command. Focus on building features, not writing boilerplate.

---

## ✨ Features

- 🎯 **Maven Plugin Integration** - Seamlessly integrates with your existing Maven workflow
- 🧠 **Smart Detection** - Automatically detects project structure and package conventions (singular/plural)
- 📦 **Intelligent Dependency Detection** - Adapts code generation based on project dependencies (Lombok, MapStruct)
- 🎨 **Flexible Architecture** - Works with any project structure and naming conventions
- ⚡ **Zero Configuration** - Works out of the box with any Spring Boot project
- 🔧 **Customizable** - Override defaults with command-line parameters

---

## 📦 Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- A Spring Boot project (2.5+)

### Add Plugin to Your Project

Add the EOS Maven plugin to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.example</groupId>
            <artifactId>eos</artifactId>
            <version>1.0-SNAPSHOT</version>
        </plugin>
    </plugins>
</build>
```

### Local Installation (Development)

Clone and install locally:

```bash
git clone https://github.com/igornunes-dev/eos.git
cd eos
git checkout dev
mvn clean install -DskipTests
```

---

## 🚀 Quick Start

Generate your first components in seconds:

```bash
# Generate a REST controller
mvn eos:controller -Dname=UserController

# Generate a service
mvn eos:service -Dname=UserService

# Generate an entity
mvn eos:entity -Dname=User

# Generate a repository
mvn eos:repository -Dname=UserRepository

# Generate a DTO
mvn eos:dto -Dname=UserDTO

# Generate an enum
mvn eos:enum -Dname=UserStatus

# Generate a mapper
mvn eos:mapper -Dname=UserMapper -Dentity=User -Ddto=UserDTO
```

---

## 📖 Available Commands

### 🎮 Controller Generator

Generates a REST controller with CRUD operations.

```bash
mvn eos:controller -Dname=ProductController
mvn eos:controller -Dname=OrderController -Dpackage=api.controllers
```

**Generated code includes:**
- CRUD endpoints (GET, POST, PUT, DELETE)
- Request/Response Entity handling
- Proper HTTP status codes
- Ready-to-inject service dependency

**Example output:**
```java
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    // private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<User>> findAll() { ... }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) { ... }
    
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User entity) { ... }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User entity) { ... }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { ... }
}
```

---

### ⚙️ Service Generator

Generates a service layer class with business logic structure.

```bash
mvn eos:service -Dname=ProductService
mvn eos:service -Dname=EmailService -Dpackage=application.services
```

**Generated code includes:**
- Standard CRUD methods
- Repository dependency injection
- Transaction-ready structure

**Example output:**
```java
@Service
@RequiredArgsConstructor
public class UserService {
    // private final UserRepository userRepository;
    
    public List<User> findAll() { ... }
    public Optional<User> findById(Long id) { ... }
    public User save(User entity) { ... }
    public User update(Long id, User entity) { ... }
    public void delete(Long id) { ... }
}
```

---

### 🗄️ Entity Generator

Generates a JPA entity with timestamps and relationship templates.

```bash
mvn eos:entity -Dname=Product
mvn eos:entity -Dname=Order -Dpackage=domain.models
```

**Smart features:**
- Auto-detects Lombok (generates `@Data` or getters/setters)
- Snake_case table naming (User → user, ProductOrder → product_order)
- Created/Updated timestamps with `@PrePersist`/`@PreUpdate`
- Relationship templates (OneToMany, ManyToOne, ManyToMany)

**Example output (with Lombok):**
```java
@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() { ... }
    
    @PreUpdate
    protected void onUpdate() { ... }
}
```

---

### 📚 Repository Generator

Generates a Spring Data JPA repository interface.

```bash
mvn eos:repository -Dname=ProductRepository
mvn eos:repository -Dname=UserRepository -Dpackage=infrastructure.persistence
```

**Generated code includes:**
- Extends `JpaRepository`
- Custom query method templates
- Ready for pagination and sorting

**Example output:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: Add custom query methods
    // Example:
    // Optional<User> findByEmail(String email);
    // List<User> findByActiveTrue();
}
```

---

### 📋 DTO Generator

Generates Data Transfer Objects for API layer.

```bash
mvn eos:dto -Dname=UserDTO
mvn eos:dto -Dname=ProductResponseDTO -Dpackage=api.dto
```

**Smart features:**
- Auto-detects Lombok (generates `@Data` or getters/setters)
- Validation annotations template
- Serialization-ready

**Example output (with Lombok):**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    // TODO: Add fields
    // Example:
    // @NotNull(message = "Name is required")
    // @Size(min = 3, max = 100)
    // private String name;
    
    // @Email(message = "Invalid email format")
    // private String email;
}
```

---

### 🎲 Enum Generator

Generates Java enums for type-safe constants.

```bash
mvn eos:enum -Dname=UserStatus
mvn eos:enum -Dname=OrderType -Dpackage=domain.enums
```

**Smart features:**
- Clean enum structure
- Display name support
- Example values as templates
- Optional description field

**Example output:**
```java
public enum UserStatus {
    // TODO: Add enum constants
    // Example:
    // ACTIVE("Active"),
    // INACTIVE("Inactive"),
    // PENDING("Pending"),
    // BLOCKED("Blocked");
    
    // private final String displayName;
    
    // UserStatus(String displayName) {
    //     this.displayName = displayName;
    // }
    
    // public String getDisplayName() {
    //     return displayName;
    // }
}
```

---

### 🔄 Mapper Generator

Generates mappers to convert between entities and DTOs.

```bash
mvn eos:mapper -Dname=UserMapper -Dentity=User -Ddto=UserDTO
mvn eos:mapper -Dname=ProductMapper -Dentity=Product -Ddto=ProductDTO -Dpackage=mapper
```

**Smart features:**
- Auto-detects MapStruct (generates interface or manual class)
- Bidirectional mapping (toDTO/toEntity)
- Null-safe implementations

**Example output (with MapStruct):**
```java
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserDTO toDTO(User entity);
    User toEntity(UserDTO dto);
}
```

**Example output (without MapStruct):**
```java
public class UserMapper {
    public static UserDTO toDTO(User entity) {
        if (entity == null) return null;
        UserDTO dto = new UserDTO();
        // TODO: Map fields manually
        return dto;
    }
    
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User entity = new User();
        // TODO: Map fields manually
        return entity;
    }
}
```

---

## 💡 Usage Examples

### Example 1: Generate Complete CRUD Structure

Build a complete feature in under a minute:

```bash
# Generate all layers for a User feature
mvn eos:entity -Dname=User
mvn eos:repository -Dname=UserRepository
mvn eos:dto -Dname=UserDTO
mvn eos:enum -Dname=UserStatus
mvn eos:mapper -Dname=UserMapper -Dentity=User -Ddto=UserDTO
mvn eos:service -Dname=UserService
mvn eos:controller -Dname=UserController
```

**Result:** Complete layered architecture ready for implementation!

```
src/main/java/com/example/demo/
├── model/
│   └── User.java                    ✓ Generated
├── repository/
│   └── UserRepository.java          ✓ Generated
├── dto/
│   └── UserDTO.java                 ✓ Generated
├── enums/
│   └── UserStatus.java              ✓ Generated
├── mapper/
│   └── UserMapper.java              ✓ Generated
├── service/
│   └── UserService.java             ✓ Generated
└── controller/
    └── UserController.java          ✓ Generated
```

---

### Example 2: Custom Package Structure

Organize your code with custom package conventions:

```bash
# Domain-Driven Design structure
mvn eos:entity -Dname=Product -Dpackage=domain.model
mvn eos:repository -Dname=ProductRepository -Dpackage=domain.repository
mvn eos:enum -Dname=ProductCategory -Dpackage=domain.enums
mvn eos:service -Dname=ProductService -Dpackage=application.service
mvn eos:controller -Dname=ProductController -Dpackage=presentation.api

# Clean Architecture structure
mvn eos:entity -Dname=Order -Dpackage=core.domain.entity
mvn eos:repository -Dname=OrderRepository -Dpackage=core.domain.repository
mvn eos:enum -Dname=OrderStatus -Dpackage=core.domain.enums
mvn eos:dto -Dname=OrderDTO -Dpackage=adapter.api.dto
mvn eos:controller -Dname=OrderController -Dpackage=adapter.api.controller
```

---

### Example 3: Force Overwrite Existing Files

Need to regenerate a file? Use the `-Dforce` flag:

```bash
# Regenerate and overwrite existing controller
mvn eos:controller -Dname=UserController -Dforce=true

# Regenerate entire feature
mvn eos:entity -Dname=Product -Dforce=true
mvn eos:repository -Dname=ProductRepository -Dforce=true
mvn eos:enum -Dname=ProductStatus -Dforce=true
mvn eos:service -Dname=ProductService -Dforce=true
```

---

### Example 4: Rapid Prototyping

Quickly prototype multiple features:

```bash
# E-commerce features
mvn eos:entity -Dname=Product
mvn eos:entity -Dname=Category
mvn eos:entity -Dname=Order
mvn eos:entity -Dname=Customer

# Generate enums for status fields
mvn eos:enum -Dname=ProductStatus
mvn eos:enum -Dname=OrderStatus
mvn eos:enum -Dname=PaymentMethod

# Generate repositories
mvn eos:repository -Dname=ProductRepository
mvn eos:repository -Dname=CategoryRepository
mvn eos:repository -Dname=OrderRepository
mvn eos:repository -Dname=CustomerRepository

# Generate services
mvn eos:service -Dname=ProductService
mvn eos:service -Dname=OrderService
mvn eos:service -Dname=CustomerService
```

---

## 🔧 Configuration

### Command Parameters

All generators support these common parameters:

| Parameter | Description | Required | Default | Example |
|-----------|-------------|----------|---------|---------|
| `-Dname` | Class name | ✅ Yes | - | `-Dname=UserController` |
| `-Dpackage` | Package name | ❌ No | Auto-detected | `-Dpackage=api.controllers` |
| `-Dforce` | Overwrite existing files | ❌ No | `false` | `-Dforce=true` |

**Mapper-specific parameters:**

| Parameter | Description | Required | Example |
|-----------|-------------|----------|---------|
| `-Dentity` | Entity class name | ✅ Yes | `-Dentity=User` |
| `-Ddto` | DTO class name | ✅ Yes | `-Ddto=UserDTO` |

---

### Smart Package Detection

EOS automatically detects your project's package naming conventions:

**How it works:**

1. **Checks for plural form first:** `controllers/`, `services/`, `models/`, `enums/`
2. **Falls back to singular:** `controller/`, `service/`, `model/`, `enum/`
3. **Creates singular by default:** If neither exists, uses singular form

**Example scenarios:**

```bash
# Scenario 1: Your project uses plural
# Structure: src/main/java/com/example/demo/controllers/
mvn eos:controller -Dname=UserController
# Result: Creates in controllers/ (plural detected ✓)

# Scenario 2: Your project uses singular
# Structure: src/main/java/com/example/demo/controller/
mvn eos:controller -Dname=UserController
# Result: Creates in controller/ (singular detected ✓)

# Scenario 3: First time generation
# Structure: src/main/java/com/example/demo/ (empty)
mvn eos:controller -Dname=UserController
# Result: Creates controller/ folder (singular default ✓)
```

**Override detection:**
```bash
# Force a specific package regardless of detection
mvn eos:controller -Dname=UserController -Dpackage=api.v1.controllers
mvn eos:enum -Dname=UserStatus -Dpackage=common.enums
```

---

### Dependency-Aware Generation

EOS intelligently detects dependencies in your `pom.xml` and adapts code generation accordingly.

#### 🎨 Lombok Detection

EOS checks for Lombok in your dependencies and generates code accordingly:

**With Lombok in pom.xml:**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```

**Generated Entity:**
```java
@Entity
@Data                    // ← Lombok annotation
@NoArgsConstructor       // ← Lombok annotation
@AllArgsConstructor      // ← Lombok annotation
public class User {
    private Long id;
    // No getters/setters needed!
}
```

**Without Lombok:**
```java
@Entity
public class User {
    private Long id;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
```

#### 🗺️ MapStruct Detection

EOS checks for MapStruct and generates appropriate mapper code:

**With MapStruct in pom.xml:**
```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
</dependency>
```

**Generated Mapper:**
```java
@Mapper                  // ← MapStruct annotation
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    UserDTO toDTO(User entity);
    User toEntity(UserDTO dto);
}
```

**Without MapStruct:**
```java
public class UserMapper {
    public static UserDTO toDTO(User entity) {
        if (entity == null) return null;
        // Manual mapping implementation
        UserDTO dto = new UserDTO();
        return dto;
    }
}
```

---

## 🏗️ Architecture

EOS is built with a clean, modular architecture:

```
eos/
├── src/main/java/org/example/
│   ├── commands/              # CLI commands (future)
│   ├── core/                  # Project analysis
│   │   └── ProjectContext.java
│   ├── generator/             # Code generators
│   │   ├── CodeGenerator.java
│   │   ├── GenerationParams.java
│   │   ├── GenerationResult.java
│   │   └── impl/
│   │       ├── ControllerGenerator.java
│   │       ├── ServiceGenerator.java
│   │       ├── EntityGenerator.java
│   │       ├── RepositoryGenerator.java
│   │       ├── DtoGenerator.java
│   │       ├── EnumGenerator.java
│   │       └── MapperGenerator.java
│   ├── maven/                 # Maven Mojos
│   │   ├── AbstractEosMojo.java
│   │   ├── ControllerMojo.java
│   │   ├── ServiceMojo.java
│   │   ├── EntityMojo.java
│   │   ├── RepositoryMojo.java
│   │   ├── DtoMojo.java
│   │   ├── EnumMojo.java
│   │   └── MapperMojo.java
│   └── template/              # Template engine
│       └── TemplateEngine.java
│
└── src/main/resources/templates/  # Freemarker templates
    ├── controller.ftl
    ├── service.ftl
    ├── entity.ftl
    ├── repository.ftl
    ├── dto.ftl
    ├── enum.ftl
    └── mapper.ftl
```

### How it Works

1. **User runs command:** `mvn eos:controller -Dname=UserController`
2. **Maven invokes Mojo:** `ControllerMojo.execute()`
3. **Project Analysis:** `ProjectContext.analyze()` detects structure
4. **Package Detection:** Auto-detects or uses provided package
5. **Dependency Detection:** Checks for Lombok, MapStruct, etc.
6. **Template Processing:** Freemarker renders the template
7. **File Creation:** Writes generated code to disk

### Technology Stack

- **Java 17** - Modern Java features and performance
- **Maven Plugin API** - Native Maven integration
- **Freemarker 2.3.32** - Powerful and flexible template engine
- **Project Analysis** - Smart detection of project structure and dependencies

---

## 🤝 Contributing

Contributions are welcome and appreciated! Here's how you can help make EOS better:

### 🚀 Getting Started

1. **Fork the repository**
   ```bash
   git clone https://github.com/igornunes-dev/eos.git
   cd eos
   git checkout dev
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/my-amazing-feature
   ```

3. **Make your changes**
   - Add new generators
   - Improve existing templates
   - Fix bugs
   - Enhance documentation

4. **Test your changes**
   ```bash
   # Build and install locally
   mvn clean install -DskipTests
   
   # Test in a sample Spring Boot project
   cd /path/to/test-project
   mvn eos:controller -Dname=TestController
   mvn eos:enum -Dname=TestEnum
   ```

5. **Commit with clear messages**
   ```bash
   git add .
   git commit -m "feat: add GraphQL support for controllers"
   ```

6. **Push and create Pull Request**
   ```bash
   git push origin feature/my-amazing-feature
   # Open PR on GitHub
   ```

### 💡 Contribution Ideas

**New Features:**
- 🧪 Test generators (Unit, Integration, E2E)
- 🌐 GraphQL support (schemas, resolvers)
- 🔒 Security configuration generator
- 📊 OpenAPI/Swagger annotations
- 🐳 Docker configuration templates

**Improvements:**
- 📝 Better documentation and examples
- 🎨 More template customization options
- 🐛 Bug fixes and performance improvements
- ✅ More comprehensive tests

**Templates:**
- Different architectural patterns
- Microservices templates
- Event-driven architecture
- CQRS patterns

### 📋 Contribution Guidelines

**Code Style:**
- Follow Java naming conventions
- Keep methods focused and small
- Add JavaDoc for public APIs
- Write self-documenting code

**Commit Messages:**
- Use conventional commits: `feat:`, `fix:`, `docs:`, `refactor:`
- Be descriptive but concise
- Reference issues when applicable

**Pull Requests:**
- Describe what changes and why
- Include examples of usage
- Update documentation if needed
- Ensure all tests pass

**Testing:**
- Test generators in real Spring Boot projects
- Verify with/without optional dependencies (Lombok, MapStruct)
- Test edge cases and error handling

---

## 🗺️ Roadmap

### ✅ Version 1.0 (Current)

- [x] Maven Plugin core architecture
- [x] Controller generator with CRUD endpoints
- [x] Service generator with business logic structure
- [x] Entity generator with JPA annotations
- [x] Repository generator (Spring Data JPA)
- [x] DTO generator
- [x] Enum generator
- [x] Mapper generator (MapStruct support)
- [x] Smart package detection (singular/plural)
- [x] Lombok dependency detection
- [x] MapStruct dependency detection
- [x] Custom package override support
- [x] Force overwrite functionality

---

### 🚧 Version 1.1 (Next Release)

**Testing Support:**
- [ ] Unit test generator for services
- [ ] Integration test generator for controllers
- [ ] Mock data generator for tests
- [ ] Test configuration templates

**API Enhancements:**
- [ ] Exception handler generator (`@ControllerAdvice`)
- [ ] Request/Response wrapper classes
- [ ] Pagination support in controllers
- [ ] Sorting and filtering templates

**Documentation:**
- [ ] OpenAPI/Swagger annotations
- [ ] JavaDoc template generation
- [ ] API documentation generator

---

### 📋 Version 1.2 (Planned)

**Advanced Features:**
- [ ] Specification generator (Spring Data JPA Criteria)
- [ ] Custom query method generator for repositories
- [ ] Event publisher/listener generator
- [ ] Validation group generator
- [ ] Audit entity support (`@CreatedBy`, `@LastModifiedBy`)

**Configuration:**
- [ ] Configuration file support (`.eos.yml`)
- [ ] Custom template directories
- [ ] Template inheritance and overrides
- [ ] Project-wide defaults

**Security:**
- [ ] Security configuration generator
- [ ] JWT authentication templates
- [ ] Authorization annotations

---

### 🔮 Version 2.0 (Future Vision)

**Multi-Module Support:**
- [ ] Detect and generate across multiple Maven modules
- [ ] Parent-child project support
- [ ] Shared model generation

**GraphQL Support:**
- [ ] GraphQL schema generator
- [ ] Resolver generator
- [ ] Input/Output type generator
- [ ] DataLoader templates

**Microservices:**
- [ ] Feign client generator (Spring Cloud)
- [ ] REST client templates
- [ ] Service discovery configuration
- [ ] Circuit breaker templates

**Database:**
- [ ] Flyway migration generator
- [ ] Liquibase changelog generator
- [ ] Database seeding scripts

**DevOps:**
- [ ] Dockerfile generator
- [ ] Docker Compose templates
- [ ] Kubernetes manifests
- [ ] CI/CD pipeline templates (GitHub Actions, GitLab CI)

---

### 💭 Future Considerations

**Build Tool Support:**
- [ ] Gradle plugin (in addition to Maven)
- [ ] Gradle Kotlin DSL support

**Language Support:**
- [ ] Kotlin code generation
- [ ] Kotlin DSL templates

**IDE Integration:**
- [ ] IntelliJ IDEA plugin
- [ ] VS Code extension
- [ ] Eclipse plugin

**Advanced Features:**
- [ ] Interactive CLI mode (wizard-style generation)
- [ ] Web UI for template customization
- [ ] Template marketplace/repository
- [ ] AI-assisted code generation

**Community:**
- [ ] Plugin ecosystem (third-party generators)
- [ ] Template sharing platform
- [ ] Community template gallery

---

### 📢 Request a Feature

Have an idea? We'd love to hear it!

- 💡 [Open a feature request](https://github.com/igornunes-dev/eos/issues/new?template=feature_request.md)
- 💬 [Join the discussion](https://github.com/igornunes-dev/eos/discussions)
- 🗳️ [Vote on existing requests](https://github.com/igornunes-dev/eos/issues?q=is%3Aissue+is%3Aopen+label%3Aenhancement)

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Igor Nunes

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

### Why MIT License?

- ✅ **Permissive** - Use in commercial and private projects
- ✅ **Simple** - Easy to understand terms
- ✅ **Compatible** - Works well with other open-source licenses
- ✅ **Popular** - Widely used in the Java ecosystem

---

## 🙏 Acknowledgments

EOS wouldn't be possible without these amazing projects and communities:

- **Spring Framework Team** - For creating the incredible Spring Boot framework that powers millions of applications worldwide
- **Maven Community** - For the robust build tool and excellent plugin architecture
- **Freemarker Team** - For the powerful and flexible template engine that makes code generation elegant
- **Lombok Project** - For pioneering annotation-based code generation in Java
- **MapStruct Team** - For the best bean mapping framework in the Java ecosystem
- **GitHub** - For providing an excellent platform for open-source collaboration
- **Java Community** - For continuous innovation and support
- **All Contributors** - Every PR, issue report, and suggestion makes EOS better!

---

## 📞 Support & Community

### 🐛 Found a Bug?

- **Search existing issues:** [Check if it's already reported](https://github.com/igornunes-dev/eos/issues)
- **Create new issue:** [Report a bug](https://github.com/igornunes-dev/eos/issues/new?template=bug_report.md)
- **Provide details:** Steps to reproduce, expected vs actual behavior, environment info

### 💡 Have a Question?

- **Check documentation:** Read through this README carefully
- **Search discussions:** [Browse Q&A](https://github.com/igornunes-dev/eos/discussions)
- **Ask the community:** [Start a discussion](https://github.com/igornunes-dev/eos/discussions/new)

### 🚀 Feature Request?

- **Check roadmap:** See if it's already planned above
- **Search existing:** [Check feature requests](https://github.com/igornunes-dev/eos/issues?q=is%3Aissue+label%3Aenhancement)
- **Submit new request:** [Request a feature](https://github.com/igornunes-dev/eos/issues/new?template=feature_request.md)

### 📧 Direct Contact

- **LinkedIn:** [Igor Nunes](https://www.linkedin.com/in/igor-nunes-1392782b4/) 
- **Email:** [igornunesle@gmail.com](mailto:igornunesle@gmail.com)

---

## 🌟 Show Your Support

If EOS saved you time and made your development workflow better:

### ⭐ Star the Repository
Show your appreciation by starring the project on GitHub!

### 🐦 Share on Social Media
Help others discover EOS:
- Twitter/X with `#EOS #SpringBoot #JavaDev`
- LinkedIn with a post about your experience
- Reddit on r/java or r/SpringBoot

### 📝 Write About It
- Blog post about your experience
- Tutorial on how you use EOS
- YouTube video demonstration

### 🤝 Contribute
- Submit a PR with improvements
- Help with documentation
- Answer questions in discussions
- Report bugs and suggest features

### 💼 Use in Production?
We'd love to know! Share your story:
- Add your company to "Who's using EOS" *(coming soon)*
- Write a case study
- Share metrics (time saved, productivity gains)

---
