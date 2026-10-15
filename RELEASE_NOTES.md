# Notas de release

Este documento registra as mudanças realizadas no projeto.

## [1.0.1-SNAPSHOT] — 2026-10-14

### Adicionado

- Checkstyle como plugin Maven, com análise das fontes em `src/test/java`, execução na fase `validate` e comandos documentados no `README.md`.
- Arquivo `checkstyle-suppressions.xml` para ignorar somente a regra de linhas longas (`LineLength`) nas fontes Java, mantendo as demais verificações ativas.
- Anotações de severidade Allure (`@Severity`) nos testes das suítes de login, carrinhos e carga; a suíte de usuários já possuía severidades.
- Catálogo `SWAGGER_TESTS.md` com cenários derivados do Swagger público do ServeRest, cobrindo as operações de login, usuários, produtos e carrinhos.
- Links no `README.md` para o catálogo Swagger e para estas notas de release.

### Alterado

- Pacote Java de `restassured_serverest` para `dev.serverest`, com atualização dos imports, diretórios e referências de documentação/comandos.
- Coordenadas Maven: `groupId` alterado de `com.example` para `dev.serverest` e URL do projeto definida como `https://serverest.dev/`.
- Versão Maven incrementada de `1.0.0-SNAPSHOT` para `1.0.1-SNAPSHOT`.
- `TESTING_API.MD` mantido como inventário dos testes automatizados existentes, distinto do catálogo completo do contrato Swagger.

### Validação

- Fontes Java formatadas segundo as regras Google; o Checkstyle passou sem violações após as correções.
- Checkstyle validado com a supressão da regra `LineLength`; as demais regras permanecem ativas.
- Compilação de testes verificada com `mvn -q -DskipTests test` após as alterações de pacote.
- Validação do POM verificada com `mvn -q -DskipTests validate` após as alterações dos metadados Maven.
