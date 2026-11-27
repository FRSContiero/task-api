# task-api

Task management API with JWT authentication

## Stack

- Java 25, Spring Boot 4.2.0, Maven (wrapper)
- PostgreSQL + Flyway (Docker Compose em compose.yaml)
- Testes: JUnit 5, Mockito, AssertJ, Testcontainers
- Docs de API: springdoc-openapi (Swagger UI em `/swagger-ui.html`)
- Formatação: Spotless (Google Java Format)

## Comandos

No PowerShell use `.\mvnw.cmd`; no Linux, macOS ou Git Bash use `./mvnw`.

- Rodar a aplicação: `./mvnw spring-boot:run` (sobe o `compose.yaml` sozinho)
- Subir só as dependências: `docker compose up -d`
- Testes unitários: `./mvnw test`
- Build completo (unitários + integração): `./mvnw verify`
- Formatar: `./mvnw spotless:apply` (o hook em `.claude/settings.json` roda sozinho ao fim de cada resposta)

## Estrutura

Pacote raiz: `io.github.frscontiero.taskapi`. Organização por feature, com camadas dentro de cada uma:

```
src/main/java/<pacote-raiz>/
  <feature>/
    api/              controllers, requests e responses (records)
    application/      use cases (um por operação) e commands
    domain/           entidades, regras, exceções e ports (interfaces)
    infrastructure/   adapters: JPA, Specifications, mensageria, clientes HTTP
  shared/             config (Clock, OpenAPI), error (handler e exceções base)
src/main/resources/db/migration/   migrations Flyway
```

## Regras para o Claude

- Código novo segue o skill `java-code-patterns`; testes seguem `java-testing`; README e OpenAPI seguem `project-docs`; `docs/MANUAL.md` segue `living-docs`.
- A seção "Exceções ao kit" prevalece sobre os skills e agentes.
- Idioma: código, nomes, logs e tudo que envolve o git (commits, branches, PRs) em inglês. Documentação e conversa com o usuário em pt-BR.
- Commits no padrão Conventional Commits (`feat:`, `fix:`, `test:`, `docs:`, `refactor:`, `chore:`).
- Nunca adicionar, remover ou trocar dependências sem perguntar antes.
- Comentários só quando o motivo não for óbvio pelo código.
- Design pattern ou abstração nova só com variação ou problema real; explique o motivo antes de aplicar.
- Melhore o código que tocar, mas sem refatorar o que está fora da tarefa.
- Ao terminar uma tarefa: `verify` passando. Formatação fica com o hook; sem hook, rode `spotless:apply`.

## Definição de pronto

- [ ] `./mvnw verify` passa
- [ ] Use cases e endpoints novos têm testes
- [ ] Endpoints novos documentados no OpenAPI
- [ ] README atualizado se algo de uso ou arquitetura mudou
- [ ] `docs/MANUAL.md` atualizado se o comportamento mudou (agente `docs-maintainer`)
- [ ] `compose.yaml` sobe tudo que a aplicação precisa

## Exceções ao kit

Nenhuma — projeto novo segue o kit integralmente. Em projeto existente, o agente `project-analyzer` preenche esta seção a partir de `docs/claude/analise-do-projeto.md`. Máximo de 15 linhas, uma por exceção; motivo e detalhe ficam no arquivo de análise (este arquivo entra no contexto de toda sessão).

## Domínio

Escopo v1: cada usuário gerencia só as próprias tarefas. Features: `user`, `auth`, `task`.

**Entidades**
- `User`: id (UUID), name, email (único, normalizado em minúsculas), passwordHash (BCrypt), createdAt.
- `RefreshToken`: id, userId, tokenHash (SHA-256, nunca o valor puro), expiresAt, revokedAt, createdAt.
- `Task`: id (UUID), ownerId, title (1–120), description (≤ 2000, opcional), status (`TODO`, `IN_PROGRESS`, `DONE`; padrão `TODO`), priority (`LOW`, `MEDIUM`, `HIGH`; padrão `MEDIUM`), dueDate (`LocalDate`, opcional), completedAt, createdAt, updatedAt, version (lock otimista).

**Regras**
- Senha: 8 a 72 caracteres (limite do BCrypt). Email duplicado no cadastro → 409.
- Login inválido → 401 com mensagem genérica (não revela se o email existe).
- JWT de acesso: HS256, `sub` = userId, validade 15 min, segredo por variável de ambiente. Refresh token opaco, 7 dias, rotacionado a cada uso; reuso de token revogado revoga todos os do usuário.
- Troca de senha exige a senha atual e revoga todos os refresh tokens.
- Tarefa de outro usuário → 404 (não 403), para não revelar que existe.
- dueDate não pode estar no passado ao criar ou ao alterar a data (usa o `Clock`).
- Transições de status livres; entrar em `DONE` preenche completedAt, sair de `DONE` limpa.
- "Atrasada" é derivado, não armazenado: dueDate < hoje e status ≠ `DONE`.
- Conflito de versão (lock otimista) → 409. Exclusão é física.
- Listagem paginada (padrão 20, máx. 100), ordenação padrão createdAt desc.

**Endpoints** (prefixo `/api`; tudo exige JWT, exceto `/auth/register|login|refresh`)
- `POST /auth/register` 201 · `POST /auth/login` 200 · `POST /auth/refresh` 200 · `POST /auth/logout` 204
- `GET /users/me` · `PUT /users/me/password` 204
- `POST /tasks` 201 + Location · `GET /tasks` (filtros: status, priority, overdue, dueFrom, dueTo, q no título; sort: createdAt, dueDate, priority) · `GET /tasks/{id}` · `PUT /tasks/{id}` (title, description, priority, dueDate) · `PATCH /tasks/{id}/status` · `DELETE /tasks/{id}` 204

**Fora do escopo v1**: papéis/admin, compartilhamento, tags, verificação de email, reset de senha por email, rate limiting.
