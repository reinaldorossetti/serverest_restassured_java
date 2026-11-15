---
inclusion: manual
---

# Requisitos: Gestão de Usuários - Casos de Borda [SPEC-USERS-EDGE-01]

## Objetivo

Cobrir cenários críticos de casos-limite em gestão de usuários não cobertos pela suíte inicial, focando em semântica REST (upsert) e validação de autenticação malformada.

## Requisitos de Comportamento (EARS Format)

### R1: PUT com ID Inexistente Deve Executar Upsert [V]

**VADER Dimension:** V (Verbs - Semântica REST)

**Quando** o usuário enviar `PUT /usuarios/{id}` com um ID válido (formato: 16 caracteres alfanuméricos) que não existe no banco de dados

**E** o payload contiver todos os dados obrigatórios (nome, email, password, administrador)

**Então** o sistema deve:
- Retornar status HTTP **201 Created** (não 404)
- Criar um novo usuário com os dados fornecidos
- Incluir o novo `_id` na resposta
- Inserir mensagem: `"Cadastro realizado com sucesso"`
- O novo usuário poder ser consultado imediatamente via `GET /usuarios/{id}`

**Exemplo de Fluxo:**
```json
// PUT /usuarios/randomNonExistentId (id não existe)
Request:
{
  "nome": "João Silva",
  "email": "joao@teste.com",
  "password": "Senha@123",
  "administrador": "false"
}

Response (201):
{
  "message": "Cadastro realizado com sucesso",
  "_id": "<newId>"
}

// GET /usuarios/<newId> (valida criação)
Response (200):
{
  "_id": "<newId>",
  "nome": "João Silva",
  "email": "joao@teste.com",
  "password": "Senha@123",
  "administrador": "false"
}
```

---

### R2: Token JWT Inválido/Corrompido Deve Retornar 401 [A]

**VADER Dimension:** A (Authentication & Authorization)

**Quando** o usuário tentar acessar um endpoint protegido (ex: `DELETE /usuarios/{id}`, `DELETE /carrinhos/cancelar-compra`)

**COM** um token JWT completamente inválido ou corrompido no header `Authorization` (token com assinatura inválida, payload corrompido, ou fabricado)

**Então** o sistema deve:
- Retornar status HTTP **401 Unauthorized**
- Incluir mensagem de erro clara: `"Token de autenticação ausente, inválido, expirado ou usuário do token foi deletado"`
- **NÃO** permitir acesso ao recurso protegido
- **NÃO** executar a ação (delete, create, etc)

**Exemplo de Fluxo (INVÁLIDO):**
```
Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid_payload.invalid_signature
(token com payload e assinatura inválidos)

Response (401):
{
  "message": "Token de autenticação ausente, inválido, expirado ou usuário do token foi deletado"
}
```

**Exemplo de Fluxo (VÁLIDO):**
```
Header: Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InV...
(token JWT válido com assinatura correta)

Response (200 ou 204):
(sucesso, acesso permitido)
```

---

## Critérios de Aceite (Checklist)

- [ ] Teste CT19: PUT em ID inexistente retorna 201 + cria usuário
- [ ] Teste CT20: Token sem Bearer prefix retorna 401
- [ ] Ambos os testes rodam de forma isolada (sem compartilhamento de estado)
- [ ] Ambos os testes herdam `BaseApiTest` com `@Execution(CONCURRENT)`
- [ ] Dados dinâmicos com `FakerUtils` (email, nome, password)
- [ ] Validações explícitas de status code, mensagem e contrato (onde aplicável)
- [ ] DisplayName descritivo com mapeamento (CT19, CT20)

---

## Prioridade

🔴 **CRÍTICA** — Casos-limite de semântica REST e autenticação são fundamentais para a segurança e confiabilidade da API.

## Dependências

- Endpoint `/usuarios` deve estar funcional
- Endpoint `/login` deve gerar tokens JWT válidos
- FakerUtils deve ter método para gerar IDs aleatórios e emails únicos

## Observações

- **Upsert em PUT**: Comportamento comum em APIs REST (ex: AWS, Google Cloud)
- **Token Malformado**: Validação crítica de segurança (OWASP A01 - Broken Access Control)
- **Isolamento**: Ambos testes criam seus próprios dados (não dependem de execução anterior)
- **Execução Paralela**: Ambos testes suportam execução concorrente via `@Execution(CONCURRENT)`
