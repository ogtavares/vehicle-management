# Vehicle Management API

API REST para gerenciamento de veículos, desenvolvida como parte de um teste técnico, com foco em boas práticas de arquitetura, segurança, testes automatizados e clareza de código.

---

## 📌 Visão Geral

Esta aplicação permite:

- Cadastro e autenticação de usuários com **JWT**
- Controle de acesso por **perfil (ADMIN / USER)**
- CRUD completo de veículos
- Filtros avançados de busca
- Relatórios agregados (veículos por marca)
- Conversão de preços BRL ⇄ USD com cache em Redis
- Testes unitários, de controller e integração ponta a ponta

### Idioma do projeto

- **Código-fonte:** inglês
- **Modelo de dados (banco):** inglês
- **Contratos de entrada e saída (DTOs / JSON):** português

Essa decisão foi tomada para manter boas práticas técnicas (código e banco em inglês) e, ao mesmo tempo, atender requisitos funcionais voltados ao usuário final.

---

## 🧱 Arquitetura

- **Spring Boot**
- **Arquitetura em camadas**
- Separação clara entre:
    - Controller
    - Service
    - Repository
    - DTOs
    - Segurança (auth / jwt)
- Uso de **DTO Projection** para relatórios
- Validações via **Bean Validation**
- Tratamento global de exceções com payload padronizado
- Segurança desacoplada da regra de negócio

---

## 🔐 Segurança

- Autenticação baseada em **JWT**
- Perfis suportados:
    - `ADMIN`
    - `USER`
- Regras de acesso:
    - **ADMIN:** criar, atualizar e deletar veículos
    - **USER:** apenas leitura
- Filtros de segurança integrados ao **Spring Security**
- Tratamento explícito para erros:
    - **401 Unauthorized** – não autenticado
    - **403 Forbidden** – sem permissão
    - **409 Conflict** – violação de regra de negócio

---

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 21**
    - Versão LTS atual
    - Melhor desempenho e recursos modernos
- **Spring Boot 3.5**
- **Spring Security**
- **Spring Data JPA**
- **Spring Validation**
- **JWT (jjwt)**

### Banco de Dados
- **H2 (em memória)**
    - Escolhido por simplicidade
    - Ideal para testes técnicos
    - Não exige setup externo
    - Inicialização rápida
    - Facilita execução local e testes automatizados

### Cache
- **Redis**
    - Utilizado para cache da cotação USD → BRL
    - Evita chamadas repetidas a APIs externas
    - TTL configurado
    - Isola dependências externas da regra de negócio

### Build e Dependências
- **Maven 3.9.9**

### Testes
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**
- **MockMvc**

Cobertura inclui:
- Services
- Controllers
- Repositórios
- Segurança
- Integração ponta a ponta

---

## 🧪 Testes Implementados

### Testes Unitários
- Services:
    - Regras de negócio
    - Validações
    - Conversão de valores
- Segurança:
    - `UserDetailsService`
    - `JwtService`

### Testes de Controller
- Cenários cobertos:
    - 201 - criado com sucesso 
    - 200 – sucesso
    - 400 – erro de validação
    - 401 – não autenticado
    - 403 – sem permissão
    - 409 – conflito de dados
- Validação de payload de erro padronizado

### Testes de Integração (End-to-End)
Fluxo completo validado:
1. Registro de usuário
2. Login e obtenção do token JWT
3. Criação de veículo (perfil ADMIN)
4. Listagem com filtros
5. Detalhamento do veículo por ID

Esses testes garantem que a aplicação funcione corretamente do ponto de vista do consumidor da API.

---

## ⚙️ Como Executar o Projeto Localmente

### Pré-requisitos

Certifique-se de ter instalado:

- **Java JDK 21**
- **Maven 3.9.9**
- **Docker**
- **Docker Compose**

---

### Subir o Redis

Na raiz do projeto, execute:

```bash
  docker-compose up -d

## 🚀 Execução da Aplicação

Isso irá iniciar o **Redis**, necessário para o cache de cotações de moeda.

---

### ▶️ Executar a aplicação

Com o Redis rodando, execute os comandos abaixo na raiz do projeto:

```
    mvn clean install
    mvn spring-boot:run

A aplicação ficará disponível em:

- **URL:** http://localhost:8080 (por padrão)

## 🧪 Executando os Testes

Para rodar toda a suíte de testes automatizados, utilize o comando abaixo na raiz do projeto:

```bash
    mvn clean test
```   

---

## 🗄 Console do H2

O banco de dados H2 (em memória) pode ser acessado em:

- **URL:** http://localhost:8080/h2-console (por padrão)

### ⚙️ Configurações padrão
- **JDBC URL:** `jdbc:h2:mem:vehicle-db`
- **Username:** `sa`
- **Password:** *(vazio)*

---

## 📬 Endpoints Principais

### 🔐 Autenticação
- `POST /auth/register`
- `POST /auth/login`

### 🚗 Veículos
- `GET /veiculos` (paginado e ordenado)
- `GET /veiculos/{id}`
- `POST /veiculos`
- `PUT /veiculos/{id}`
- `PATCH /veiculos/{id}`
- `DELETE /veiculos/{id}`

### 📊 Relatórios
- `GET /veiculos/relatorios/por-marca` (paginado e ordenado)

---

## 📄 Códigos HTTP Utilizados
- **200 OK** – requisição bem-sucedida
- **201 Created** – recurso criado com sucesso
- **400 Bad Request** – erro de validação
- **401 Unauthorized** – usuário não autenticado
- **403 Forbidden** – usuário sem permissão
- **409 Conflict** – conflito de dados ou regra de negócio

---

## 🗂️ Carga Inicial de Dados (Data Loader)

A aplicação possui uma classe de **loader** executada automaticamente na inicialização (`CommandLineRunner`).

Essa classe é responsável por **popular o banco de dados em memória** com dados iniciais, facilitando testes manuais e validações sem a necessidade de criar tudo via API.

### O que é criado automaticamente
- **Usuários**
  - Usuário com perfil **ADMIN e USER**
  - Usuário apenas **ADMIN**
  - Usuário apenas **USER**
- **Veículos**
  - Diversos veículos de marcas diferentes
  - Anos, cores e preços variados
  - Dados pensados para testar filtros, relatórios e regras de negócio

Essa carga inicial só é executada quando o banco está vazio, evitando duplicações a cada restart da aplicação.

---

## 📘 Documentação da API (Swagger / OpenAPI)

A API está documentada utilizando **OpenAPI 3 + Swagger UI**, permitindo visualizar, testar e entender todos os endpoints diretamente pelo navegador.

### Como acessar
Após subir a aplicação, acesse:

- **URL:** http://localhost:8080/swagger-ui/index.html

### Observações importantes
- Endpoints protegidos exigem **JWT**
- No Swagger, utilize o botão **Authorize**
- Informe o token no formato:

Bearer SEU_TOKEN_AQUI

- Os endpoints de autenticação (`/auth/registrar` e `/auth/logar`) são públicos
- Todos os contratos (request/response) estão documentados em português
- Códigos de retorno e possíveis erros também estão descritos na documentação

## 📌 Observações Finais
Este projeto foi desenvolvido com foco em:
- Clareza de código
- Boas práticas de arquitetura
- Segurança
- Testabilidade
- Organização e legibilidade

  Ele foi pensado para ser simples de executar localmente, mas ao mesmo tempo demonstrar maturidade técnica em um cenário realista de backend.

🚩 No diretório src/main/resources armazenei a postman collection que utilizei para testar a aplicação

