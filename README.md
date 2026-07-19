# 🚀 TaskBoard API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-red)

API REST para gerenciamento de tarefas com autenticação JWT, desenvolvida com Java e Spring Boot.

O projeto foi desenvolvido com foco em boas práticas de desenvolvimento backend, incluindo autenticação baseada em JWT, arquitetura em camadas, testes automatizados e documentação de API.

##  ✅ Funcionalidades Implementadas

- [x] Cadastro de usuários
- [x] Autenticação com JWT
- [x] CRUD completo de tarefas
- [x] Isolamento de tarefas por usuário
- [x] Validação de dados
- [x] Tratamento global de exceções
- [x] Documentação com Swagger/OpenAPI
- [x] Testes unitários
- [x] Testes de integração

## 🛠 Tecnologias

- Java 21
- Spring Boot 3
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- H2 Database (testes)
- JUnit 5
- Mockito
- Maven
- Swagger / OpenAPI

---

## 🏗 Arquitetura

```text
src/main/java/com/rshinna/taskboardapi
├── auth
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
└── TaskboardApiApplication
```

---

## 🔗 Endpoints

### Usuários

| Método | Endpoint | Descrição | Acesso |
|----------|----------|----------|----------|
| POST | `/users` | Criar usuário | Público |
| GET | `/users/me` | Dados do usuário autenticado | Autenticado |
| GET | `/users/admin` | Endpoint de exemplo restrito a admins | Somente ADMIN |
| PATCH | `/users/{id}/promote` | Promove um usuário para ADMIN | Somente ADMIN |

O projeto implementa controle de acesso baseado em papéis (RBAC), com duas roles: `USER` (padrão, atribuída automaticamente no cadastro) e `ADMIN` (atribuída apenas via `/users/{id}/promote`, por um usuário já admin).

### Autenticação

| Método | Endpoint | Descrição |
|----------|----------|----------|
| POST | `/auth/login` | Realizar login |

### Tarefas

| Método | Endpoint | Descrição |
|----------|----------|----------|
| POST | `/tasks` | Criar tarefa |
| GET | `/tasks` | Listar tarefas do usuário |
| GET | `/tasks/{id}` | Buscar tarefa por ID |
| PUT | `/tasks/{id}` | Atualizar tarefa |
| DELETE | `/tasks/{id}` | Remover tarefa |

Todos os endpoints de tarefas exigem autenticação JWT.

---

## ⚙️ Configuração

A aplicação utiliza as seguintes variáveis de ambiente:

```env
DB_URL=jdbc:postgresql://localhost:5432/taskboard
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=sua-chave-secreta
```

### Exemplo Linux/macOS

```bash
export DB_URL=jdbc:postgresql://localhost:5432/taskboard
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=minha-chave-super-secreta
```

### Exemplo Windows (PowerShell)

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/taskboard"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="minha-chave-super-secreta"
```

---
## 📦 Pré-requisitos

**Opção A — com Docker (recomendado):**
- Docker e Docker Compose

**Opção B — execução local sem Docker:**
- Java 21 ou superior
- Maven 3.8 ou superior
- PostgreSQL

## ▶️ Executando Localmente

### Clone o projeto

```bash
git clone https://github.com/Rshinna/taskboard-api.git
cd taskboard-api
```

### Opção A — com Docker (recomendado)

Sobe o Postgres e a aplicação juntos, sem precisar instalar Java, Maven ou Postgres na máquina:

```bash
docker compose up --build
```

Pra derrubar:

```bash
docker compose down          # mantém os dados do banco
docker compose down -v       # remove também o volume do banco
```

### Opção B — execução local sem Docker

Configure as variáveis de ambiente (ver seção [Configuração](#️-configuração)) e execute:

```bash
mvn spring-boot:run
```

### Acessando a aplicação

Em qualquer uma das opções, a aplicação ficará disponível em:

```text
http://localhost:8080
```

---

## 🧪 Executando os Testes

Executar todos os testes:

```bash
mvn test
```

Executar uma classe específica:

```bash
mvn test -Dtest=TaskServiceTest
```

---

## 📚 Swagger

Após iniciar a aplicação:

```text
http://localhost:8080/swagger-ui/index.html
```

![Swagger](docs/swagger.png)


---

## 🔐 Segurança

A autenticação é baseada em JWT.

Exemplo de header:

```http
Authorization: Bearer <token>
```

Cada usuário possui acesso apenas às suas próprias tarefas.

O acesso a endpoints administrativos é controlado via `@PreAuthorize`, com base na role do usuário autenticado (`USER` ou `ADMIN`). Por padrão, todo usuário nasce com role `USER`; a promoção a `ADMIN` só ocorre através do endpoint `/users/{id}/promote`, restrito a administradores.

---

## 📋 Testes

### Testes Unitários

- AuthServiceTest
- AuthenticatedUserServiceTest
- TaskServiceTest

### Testes de Integração

- TaskControllerIntegrationTest

---

## 🎯 Próximos Passos

- [x] Docker
- [ ] GitHub Actions (CI/CD)
- [ ] Paginação de tarefas
- [ ] Filtro por status
- [ ] Cobertura de testes ampliada
- [ ] Deploy em ambiente cloud

---

## 👨‍💻 Autor

**Rodrigo Franco Jorge**

GitHub:  
[![GitHub](https://img.shields.io/badge/GitHub-Rshinna-black?logo=github)](https://github.com/Rshinna)

---

## 📄 Licença

Este projeto foi desenvolvido para fins de estudo e portfólio.