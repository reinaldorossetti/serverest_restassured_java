# Catálogo de testes da API ServeRest — Swagger

Este documento transforma em casos verificáveis as operações e respostas documentadas pelo Swagger público do ServeRest. Ele descreve o contrato da API; não significa que todos os cenários abaixo já estejam implementados ou executados neste repositório.

- **Swagger (português):** <https://serverest.dev/?lang=pt-BR>
- **OpenAPI JSON:** <https://serverest.dev/swagger.json?lang=pt-BR>
- **Versão indicada pelo Swagger consultado:** `3.2.2` (OpenAPI `3.0.0`)
- **Servidor público:** `https://serverest.dev`
- **Servidor local alternativo:** `http://localhost:3000`

> O ambiente público é compartilhado e seus dados são removidos periodicamente. Para testes destrutivos ou de carga, prefira um ambiente local. O projeto oficial também alerta que os testes de carga devem ser realizados localmente.

## Convenções de validação

- Enviar e validar JSON com `Content-Type: application/json` nos endpoints que recebem corpo.
- Verificar o código HTTP e os campos de resposta descritos em cada cenário.
- Para operações protegidas, obter o valor `authorization` da resposta de `POST /login` e enviá-lo no header `Authorization`.
- Os exemplos são ilustrativos. Use dados únicos (especialmente e-mail de usuário e nome de produto) nos testes de criação.
- Os filtros de listagem são query parameters opcionais. O Swagger documenta os nomes e tipos, mas não define paginação.
- Os cenários abaixo refletem códigos e regras publicados no Swagger; não acrescentam códigos que não estejam descritos no contrato.

## Resumo das operações

| Recurso | Operações Swagger |
|---|---:|
| Login | 1 |
| Usuários | 5 |
| Produtos | 5 |
| Carrinhos | 5 |
| **Total** | **16** |

O catálogo detalha **44 cenários verificáveis** para essas operações.

---

## 1. Login

### `POST /login` — realizar login

**Corpo de exemplo**

```json
{
  "email": "fulano@qa.com",
  "password": "teste"
}
```

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-LOGIN-01 | Enviar e-mail e senha válidos | `200`; `message` igual a `Login realizado com sucesso` e `authorization` preenchido. O token tem duração documentada de 600 segundos (10 minutos). |
| SW-LOGIN-02 | Enviar credenciais inválidas | `401`; `message` igual a `Email e/ou senha inválidos`. |

---

## 2. Usuários

### `GET /usuarios` — listar e filtrar usuários

**Filtros opcionais documentados:** `_id` (string), `nome` (string), `email` (string), `password` (string) e `administrador` (string `true` ou `false`).

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-USUARIOS-01 | Listar usuários sem filtros | `200`; objeto com `quantidade` e `usuarios`. Cada usuário expõe `nome`, `email`, `password`, `administrador` e `_id`. |
| SW-USUARIOS-02 | Consultar usando um ou mais filtros suportados | `200`; resposta mantém o schema de listagem e os registros correspondem aos filtros enviados. |

### `POST /usuarios` — cadastrar usuário

**Corpo de exemplo**

```json
{
  "nome": "Fulano da Silva",
  "email": "fulano.unico@qa.com",
  "password": "teste",
  "administrador": "true"
}
```

`administrador` é uma string com os valores documentados `true` ou `false`.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-USUARIOS-03 | Cadastrar usuário com e-mail ainda não utilizado | `201`; `message` igual a `Cadastro realizado com sucesso` e `_id` preenchido. |
| SW-USUARIOS-04 | Reutilizar um e-mail já cadastrado | `400`; `message` igual a `Este email já está sendo usado`. |

### `GET /usuarios/{_id}` — buscar usuário por ID

`_id` é obrigatório no path. O Swagger usa `0uxuPY0cbmQhpEz1` como exemplo.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-USUARIOS-05 | Consultar ID existente | `200`; objeto com `nome`, `email`, `password`, `administrador` e `_id`. |
| SW-USUARIOS-06 | Consultar ID inexistente | `400`; `message` igual a `Usuário não encontrado`. |

### `PUT /usuarios/{_id}` — editar usuário

**Corpo:** mesmo formato de `POST /usuarios`.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-USUARIOS-07 | Atualizar usuário existente com e-mail disponível | `200`; `message` igual a `Registro alterado com sucesso`. |
| SW-USUARIOS-08 | Enviar ID que não corresponde a usuário existente | `201`; o Swagger documenta criação do registro quando o ID informado não é encontrado. Validar `message` e `_id` conforme resposta de cadastro. |
| SW-USUARIOS-09 | Atualizar usando e-mail já utilizado por outro usuário | `400`; erro de e-mail duplicado: `Este email já está sendo usado`. |

### `DELETE /usuarios/{_id}` — excluir usuário

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-USUARIOS-10 | Excluir usuário sem carrinho associado | `200`; `message` informa `Registro excluído com sucesso` (ou que nenhum registro foi excluído). |
| SW-USUARIOS-11 | Excluir usuário que possui carrinho | `400`; `message` igual a `Não é permitido excluir usuário com carrinho cadastrado` e `idCarrinho` informado. |

---

## 3. Produtos

### `GET /produtos` — listar e filtrar produtos

**Filtros opcionais documentados:** `_id` (string), `nome` (string), `preco` (inteiro maior ou igual a 1), `descricao` (string) e `quantidade` (inteiro maior ou igual a 0).

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-PRODUTOS-01 | Listar produtos sem filtros | `200`; objeto com `quantidade` e `produtos`. Cada item expõe `nome`, `preco`, `descricao`, `quantidade` e `_id`. |
| SW-PRODUTOS-02 | Consultar produtos com filtros suportados | `200`; resposta mantém o schema de listagem e os registros correspondem aos filtros válidos. |

### `POST /produtos` — cadastrar produto (**autenticado como administrador**)

**Corpo de exemplo**

```json
{
  "nome": "Produto de teste único",
  "preco": 470,
  "descricao": "Mouse",
  "quantidade": 381
}
```

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-PRODUTOS-03 | Cadastrar produto com nome ainda não utilizado e token de administrador | `201`; `message` igual a `Cadastro realizado com sucesso` e `_id` preenchido. |
| SW-PRODUTOS-04 | Cadastrar produto com nome já utilizado | `400`; `message` igual a `Já existe produto com esse nome`. |
| SW-PRODUTOS-05 | Enviar token ausente, inválido ou expirado | `401`; erro de token com `message` indicando que o token está ausente, inválido, expirado ou que o usuário não existe mais. |
| SW-PRODUTOS-06 | Autenticar como usuário não administrador | `403`; `message` igual a `Rota exclusiva para administradores`. |

### `GET /produtos/{_id}` — buscar produto por ID

`_id` é obrigatório no path. O exemplo publicado é `BeeJh5lz3k6kSIzA`.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-PRODUTOS-07 | Consultar ID existente | `200`; objeto com `nome`, `preco`, `descricao`, `quantidade` e `_id`. |
| SW-PRODUTOS-08 | Consultar ID inexistente | `400`; `message` igual a `Produto não encontrado`. |

### `PUT /produtos/{_id}` — editar produto (**autenticado como administrador**)

**Corpo:** mesmo formato de `POST /produtos`.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-PRODUTOS-09 | Atualizar produto existente | `200`; `message` igual a `Registro alterado com sucesso`. |
| SW-PRODUTOS-10 | Enviar ID não encontrado | `201`; o Swagger documenta criação de produto quando o ID não é encontrado. Validar a resposta de cadastro. |
| SW-PRODUTOS-11 | Usar nome já cadastrado por outro produto | `400`; `message` igual a `Já existe produto com esse nome`. |
| SW-PRODUTOS-12 | Enviar token ausente, inválido ou expirado | `401`; erro de token. |
| SW-PRODUTOS-13 | Autenticar como usuário não administrador | `403`; `message` igual a `Rota exclusiva para administradores`. |

### `DELETE /produtos/{_id}` — excluir produto (**autenticado como administrador**)

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-PRODUTOS-14 | Excluir produto não associado a carrinho | `200`; `message` informa `Registro excluído com sucesso` (ou que nenhum registro foi excluído). |
| SW-PRODUTOS-15 | Excluir produto que faz parte de carrinho | `400`; `message` igual a `Não é permitido excluir produto que faz parte de carrinho`; validar também `idCarrinho`. |
| SW-PRODUTOS-16 | Enviar token ausente, inválido ou expirado | `401`; erro de token. |
| SW-PRODUTOS-17 | Autenticar como usuário não administrador | `403`; `message` igual a `Rota exclusiva para administradores`. |

---

## 4. Carrinhos

### `GET /carrinhos` — listar e filtrar carrinhos

**Filtros opcionais documentados:** `_id` (string), `precoTotal` (inteiro maior ou igual a 1), `quantidadeTotal` (inteiro maior ou igual a 0) e `idUsuario` (string). A listagem informa que os carrinhos retornados são únicos por usuário.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-CARRINHOS-01 | Listar carrinhos sem filtros | `200`; objeto com `quantidade` e `carrinhos`; validar itens/produtos, totais, `idUsuario` e `_id`. |
| SW-CARRINHOS-02 | Consultar carrinhos com filtros suportados | `200`; resposta mantém o schema de listagem e os registros correspondem aos filtros válidos. |

### `POST /carrinhos` — cadastrar carrinho (**autenticado**)

**Corpo de exemplo**

```json
{
  "produtos": [
    {
      "idProduto": "BeeJh5lz3k6kSIzA",
      "quantidade": 1
    },
    {
      "idProduto": "YaeJ455lz3k6kSIzA",
      "quantidade": 3
    }
  ]
}
```

O carrinho fica vinculado ao usuário do token. A API permite apenas um carrinho por usuário e, ao cadastrar com sucesso, reduz do estoque as quantidades solicitadas.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-CARRINHOS-03 | Criar carrinho válido com token e produtos disponíveis | `201`; `message` igual a `Cadastro realizado com sucesso` e `_id` preenchido. |
| SW-CARRINHOS-04 | Criar carrinho sem token válido | `401`; erro de token. |
| SW-CARRINHOS-05 | Enviar produto duplicado no mesmo carrinho | `400`; `message` informa `Não é permitido possuir produto duplicado`. |
| SW-CARRINHOS-06 | Tentar criar segundo carrinho para o mesmo usuário | `400`; `message` informa `Não é permitido ter mais de 1 carrinho`. |
| SW-CARRINHOS-07 | Informar produto inexistente | `400`; `message` informa `Produto não encontrado`. |
| SW-CARRINHOS-08 | Solicitar quantidade maior que o estoque | `400`; `message` informa `Produto não possui quantidade suficiente`. |

### `GET /carrinhos/{_id}` — buscar carrinho por ID

`_id` é obrigatório no path. O Swagger usa `qbMqntef4iTOwWfg` como exemplo.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-CARRINHOS-09 | Consultar ID existente | `200`; objeto com `produtos`, `precoTotal`, `quantidadeTotal`, `idUsuario` e `_id`. |
| SW-CARRINHOS-10 | Consultar ID inexistente | `400`; `message` igual a `Carrinho não encontrado`. |

### `DELETE /carrinhos/concluir-compra` — concluir compra (**autenticado**)

A operação remove o carrinho vinculado ao usuário do token.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-CARRINHOS-11 | Concluir compra com token válido | `200`; `message` informa `Registro excluído com sucesso` ou que não foi encontrado carrinho para esse usuário. |
| SW-CARRINHOS-12 | Enviar token ausente, inválido ou expirado | `401`; erro de token. |

### `DELETE /carrinhos/cancelar-compra` — cancelar compra (**autenticado**)

A operação remove o carrinho vinculado ao usuário do token e devolve ao estoque os produtos desse carrinho.

| ID | Cenário | Resultado esperado |
|---|---|---|
| SW-CARRINHOS-13 | Cancelar compra com token válido | `200`; `message` informa `Registro excluído com sucesso` ou que não foi encontrado carrinho para esse usuário. |
| SW-CARRINHOS-14 | Enviar token ausente, inválido ou expirado | `401`; erro de token. |

---

## Schemas e respostas reutilizadas

| Tipo de resposta | Estrutura/campo relevante |
|---|---|
| Cadastro com sucesso | `message` + `_id` |
| Registro alterado | `message` |
| Login com sucesso | `message` + `authorization` |
| E-mail duplicado | `message: Este email já está sendo usado` |
| Nome de produto duplicado | `message: Já existe produto com esse nome` |
| Token inválido/ausente/expirado | `message` |
| Rota exclusiva para administrador | `message: Rota exclusiva para administradores` |
| Usuário com carrinho | `message` + `idCarrinho` |
| Produto em carrinho | `message` + `idCarrinho` |

## Comparação com os testes deste repositório

O inventário dos testes automatizados já presentes no projeto continua em [`TESTING_API.MD`](TESTING_API.MD). Este arquivo é o catálogo derivado do Swagger e pode incluir operações ainda não automatizadas. Use-o como referência para identificar lacunas de cobertura; não o interprete como evidência de execução.
