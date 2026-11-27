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
- Idioma do código, nomes, logs e commits: {{idioma — padrão do kit: inglês}}. Documentação: {{idioma}}.
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

{{Termos do domínio, regras de negócio importantes e decisões já tomadas. Atualize conforme o projeto evolui.}}
