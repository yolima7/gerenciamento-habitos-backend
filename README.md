# Gerenciamento de Hábitos

API REST para cadastrar e acompanhar hábitos pessoais, com cadastro de usuários, login e autenticação via JWT. Este é meu primeiro projeto mais completo em back-end.

## Sobre o projeto

A aplicação permite que cada usuário crie uma conta, faça login e gerencie seus próprios hábitos.

Cada hábito possui:

* Título
* Descrição
* Quantidade
* Unidade (vezes, litros, horas etc.)
* Período: diário, semanal ou mensal

A autenticação é feita por email e senha. As senhas são armazenadas utilizando hash com BCrypt, e o login devolve um token JWT utilizado para autenticar as requisições protegidas.

Os hábitos são vinculados ao usuário autenticado, portanto o usuário não precisa informar seu próprio `usuarioId` ao criar ou consultar hábitos.

## Tecnologias

* Java 21
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* Bean Validation
* PostgreSQL
* Maven

## Como rodar

### Pré-requisitos

1. Instale o **JDK 21**.
2. Instale o **PostgreSQL**.
3. Crie um banco de dados chamado `gerenciamento_habitos`.

A aplicação utiliza a porta padrão `5432` do PostgreSQL.

### Variáveis de ambiente

Configure as seguintes variáveis de ambiente:

* `DB_PASSWORD`: senha do seu usuário `postgres`.
* `JWT_SECRET`: chave secreta utilizada para assinar os tokens JWT.

Para gerar uma chave no PowerShell:

```powershell
$b = New-Object byte[] 48; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)
```

Depois, configure a variável no Windows:

```powershell
setx JWT_SECRET "sua-chave-gerada"
```

Após utilizar `setx`, abra um novo terminal ou reinicie a IDE para que a variável seja reconhecida.

### Executando a aplicação

Na raiz do projeto, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Se a aplicação iniciar corretamente, o console exibirá uma mensagem semelhante a:

```text
Started GerenciamentoHabitosApplication in ... seconds
```

A API ficará disponível em:

```text
http://localhost:8080
```

Os endpoints podem ser testados pelo arquivo `teste.http` utilizando o cliente HTTP do IntelliJ IDEA ou por ferramentas como Postman.

## Autenticação

Os endpoints protegidos exigem um token JWT.

Primeiro, faça login através de:

```http
POST /usuarios/login
```

O servidor retornará um token:

```json
{
  "token": "seu-token-jwt"
}
```

Nas requisições protegidas, envie o token no header:

```http
Authorization: Bearer seu-token-jwt
```

O usuário autenticado é identificado através do token JWT.

## Endpoints

### Usuários

| Método | Rota              | Descrição                                              |
| ------ | ----------------- | ------------------------------------------------------ |
| POST   | `/usuarios`       | Cria um usuário                                        |
| POST   | `/usuarios/login` | Realiza login com email e senha e retorna um token JWT | |
| GET    | `/usuarios/{id}`  | Busca um usuário pelo ID                               |
| PUT    | `/usuarios/{id}`  | Atualiza os dados de um usuário                        |
| DELETE | `/usuarios/{id}`  | Remove um usuário                                      |

O cadastro e o login são públicos. Os demais endpoints exigem autenticação.

> A autorização para restringir completamente as operações de usuários ao próprio usuário autenticado ainda está em desenvolvimento.

### Hábitos

| Método | Rota            | Descrição                                                                              |
| ------ | --------------- | -------------------------------------------------------------------------------------- |
| POST   | `/habitos`      | Cria um hábito para o usuário autenticado                                              |
| GET    | `/habitos`      | Lista os hábitos do usuário autenticado, com paginação, ordenação e filtro por período |
| GET    | `/habitos/{id}` | Busca um hábito do usuário autenticado pelo ID                                         |
| PUT    | `/habitos/{id}` | Atualiza um hábito do usuário autenticado                                              |
| DELETE | `/habitos/{id}` | Remove um hábito do usuário autenticado                                                |

As operações de hábitos são protegidas por autenticação e autorização. Um usuário só pode acessar, alterar ou excluir seus próprios hábitos.

### Paginação, ordenação e filtros

O endpoint `GET /habitos` suporta paginação, ordenação e filtro por período.

Exemplo:

```http
GET /habitos?page=0&size=10
```

Ordenação:

```http
GET /habitos?page=0&size=10&sort=titulo,asc
```

Filtro por período:

```http
GET /habitos?periodo=DIARIO
```

Também é possível combinar os parâmetros:

```http
GET /habitos?page=0&size=10&sort=titulo,asc&periodo=DIARIO
```

## Status do projeto

Em desenvolvimento.

### Funcionalidades concluídas

* [x] Cadastro de usuários
* [x] Validação dos dados
* [x] Hash de senhas com BCrypt
* [x] Login de usuários
* [x] Geração de tokens JWT
* [x] Autenticação com Spring Security
* [x] Proteção dos endpoints
* [x] CRUD de usuários
* [x] CRUD de hábitos
* [x] Associação de hábitos ao usuário autenticado
* [x] Autorização dos hábitos por usuário
* [x] Paginação de hábitos
* [x] Ordenação de hábitos
* [x] Filtro de hábitos por período
* [x] Tratamento global de exceções
* [x] Restringir operações de usuários ao próprio usuário autenticado
* [x] Melhorar o tratamento de tokens inválidos ou expirados

### Próximos passos
* [ ] Remover redundâncias de código
* [ ] Padronizar o uso de DTOs por segurança
* [ ] Finalizar a integração com o front-end

## Front-end

O projeto também terá um front-end desenvolvido em React com TypeScript, ainda em desenvolvimento.

Como meu foco principal é o back-end, o front-end está sendo desenvolvido com apoio de IA.
