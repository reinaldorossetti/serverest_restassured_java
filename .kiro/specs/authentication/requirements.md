# Requisitos: Autenticação no ServeRest [SPEC-AUTH-01]

## Objetivo

Garantir que o endpoint `POST /login` funcione conforme especificado, permitindo autenticação segura de usuários e geração de tokens JWT válidos.

## Requisitos de Comportamento (EARS Format + VADER)

### R1 [V, A]: Login com Credenciais Válidas (Caminho Feliz)

**Quando** o usuário enviar um POST para `/login` com um email registrado e senha correta

**Então** o sistema deve:
- Retornar status HTTP **200 OK**
- Incluir a mensagem: `"Login realizado com sucesso"`
- Gerar um token JWT no campo `authorization`
- O token deve ser válido para requisições subsequentes (autenticação)

**Exemplo de Resposta:**
```json
{
  "message": "Login realizado com sucesso",
  "authorization": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
}
```

---

### R2 [A, E]: Login com Credenciais Inválidas (Caso Negativo)

**Quando** o usuário enviar email ou senha incorretos

**Então** o sistema deve:
- Retornar status HTTP **401 Unauthorized**
- Incluir mensagem clara: `"Email e/ou senha inválidos"` ou similar
- **NÃO** incluir token no response
- **NÃO** expor detalhes de segurança (ex: "email não encontrado" vs "senha errada")

**Exemplo de Resposta:**
```json
{
  "message": "Email e/ou senha inválidos"
}
```

---

### R3 [D, E]: Validação de Formato de Email

**Quando** o usuário enviar um email com formato inválido (ex: `invalido@`, `usuario`, `@dominio.com`)

**Então** o sistema deve:
- Retornar status HTTP **400 Bad Request**
- Incluir validação no campo `email` com mensagem de erro específica

**Emails Inválidos a Testar:**
- `invalido@` (sem domínio)
- `usuario` (sem @)
- `@dominio.com` (sem usuário)
- `usuario@@dominio.com` (duplo @)
- `usuario@.com` (domínio incompleto)

---

### R4 [D, E]: Campos Obrigatórios

**Quando** o usuário omitir campos obrigatórios (email ou password)

**Então** o sistema deve:
- Retornar status HTTP **400 Bad Request**
- Indicar qual campo está faltando na resposta

**Exemplo:**
```json
{
  "email": "email é obrigatório",
  "password": "password é obrigatório"
}
```

---

### R5 [D, E]: Payload Vazio

**Quando** o usuário enviar um payload vazio ou inválido (JSON malformado)

**Então** o sistema deve:
- Retornar status HTTP **400 Bad Request**
- Incluir mensagem clara sobre o erro de parsing

---

### R6 [A, V]: Token JWT em Headers (Autenticação e Autorização Subsequente)

**Quando** o usuário fizer login com sucesso e receber um token

**E** enviar este token em requisições subsequentes no header `Authorization: Bearer <token>`

**Então** o sistema deve:
- Aceitar a requisição como autenticada
- Permitir operações protegidas (ex: criar produtos, deletar carrinho)
- Retornar **200 OK** ou código apropriado (não **401** ou **403**)

---

## Critérios de Aceite (Checklist)

- [ ] Teste CT01: Login com credenciais válidas retorna 200 + token
- [ ] Teste CT02: Login com email inválido retorna 401
- [ ] Teste CT03: Login com senha inválida retorna 401
- [ ] Teste CT04: Login sem campos obrigatórios retorna 400
- [ ] Teste CT05: Login com formato de email inválido retorna 400 (parametrizado)
- [ ] Teste CT06: Token JWT pode ser usado em requisições subsequentes

## Prioridade

🔴 **CRÍTICA** — Autenticação é o fundamento de toda a aplicação.

## Dependências

- Endpoint `/usuarios` deve estar funcional para criar usuários de teste
- Base de dados deve estar limpa ou permitir duplicação de emails em testes

## Observações

- O ServeRest não implementa rate limiting para login (ao contrário de APIs reais), mas isso pode ser tratado como NFR futura
- Tokens JWT não expiram durante a sessão de teste (sem complexidade de refresh)
