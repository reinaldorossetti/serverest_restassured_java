# 🔍 VADER — Heurísticas para Testes de API

Heurísticas são guias práticos e mnemônicos que ajudam a explorar e testar APIs de forma sistemática, identificando problemas comuns com eficiência. A heurística **VADER** funciona como um checklist rápido para cobrir aspectos críticos durante os testes.

## 🧭 Checklist VADER

### 1. 🔤 V — Verbs (Verbos)

Verifique se a API retorna valor real para o usuário, como dados úteis e precisos. Teste se as respostas agregam algo significativo, evitando retornos vazios ou irrelevantes. Por exemplo, um endpoint de busca deve retornar resultados relevantes.

### 2. 🔐 A — Authentication (Autenticação)

Teste os mecanismos de autenticação e autorização. Valide cenários como login inválido, tokens expirados ou acessos não autorizados, garantindo que apenas usuários permitidos acessem recursos sensíveis.

### 3. 🗃️ D — Data (Dados)

Analise a integridade e o formato dos dados. Verifique validações de entrada — como campos obrigatórios e tipos de dados — e de saída — como consistência do JSON. Inclua casos extremos (*edge cases*), como dados nulos ou excessivamente longos.

### 4. ⚠️ E — Errors (Erros)

Explore o tratamento de erros. Teste respostas para falhas esperadas — por exemplo, `400 Bad Request` para entradas inválidas — e inesperadas — por exemplo, `500 Internal Server Error`. Verifique se as mensagens são claras e úteis para o usuário final.

### 5. ⚡ R — Responsiveness (Capacidade de resposta)
-> Item não utilizado diretamente nos testes, mas relevante para a análise de desempenho da API.

Avalie os controles de taxa e o desempenho sob carga. Teste limites de requisições, o comportamento em picos de tráfego e a capacidade da API de lidar com sobrecargas sem apresentar latência elevada.

## 📊 Cobertura da heurística VADER

A tabela abaixo considera somente os itens **1 a 4** da heurística. O item 5 (Responsiveness) foi excluído desta avaliação, conforme definido acima.

| Item | O que está coberto | O que falta ou está parcialmente coberto | O que precisa melhorar | Avaliação |
|---|---|---|---|---|
| **1. V — Verbs (Verbos)** | Os principais verbos e fluxos da API são exercitados: criação, consulta, atualização e exclusão de usuários; login; criação, consulta, conclusão e cancelamento de carrinhos. As respostas validam status HTTP, mensagens, IDs, campos retornados e valores de negócio como total e quantidade do carrinho. | Nem todos os recursos têm a mesma profundidade de validação de resposta. A listagem de usuários valida estrutura, mas não cobre explicitamente paginação, ordenação, limites ou combinação de filtros. O cadastro de produtos aparece principalmente como apoio aos cenários de autenticação e carrinho. | Adicionar asserções de contrato e de regras de negócio para cada operação, incluindo paginação, ordenação, filtros sem resultado e consistência entre operações (por exemplo, estoque após concluir/cancelar compra). | **Boa, com lacunas de profundidade** |
| **2. A — Authentication (Autenticação)** | `LoginFeatureTests` cobre credenciais válidas e inválidas, campos obrigatórios, token retornado e tentativa de acesso de usuário comum a rota exclusiva de administrador. `CartsFeatureTests` também valida criação de carrinho sem token, retornando `401`. | Não há cenários específicos para token malformado, token expirado, cabeçalho `Authorization` com esquema inválido ou token de usuário inexistente. A matriz de autorização não cobre sistematicamente todas as rotas protegidas e perfis. | Criar uma matriz por endpoint e perfil (anônimo, usuário comum e administrador), incluindo token ausente, inválido, expirado e adulterado, além de verificar que nenhuma operação protegida é executada após a falha de autorização. | **Parcialmente coberta** |
| **3. D — Data (Dados)** | Há validação de campos obrigatórios no cadastro e login, formato de e-mail, duplicidade de e-mail, valores nulos/vazios na resposta, tipos básicos, ausência de campos inesperados e JSON Schema em `GET /usuarios/{id}`. Também existem cenários de estoque insuficiente e produto duplicado no carrinho. | Faltam testes sistemáticos para tipos incompatíveis, `null`, strings muito longas, valores negativos/zero, listas vazias e payloads com campos extras. O schema está aplicado ao usuário por ID, mas não a todas as respostas relevantes. | Ampliar a matriz de dados inválidos e limites por endpoint; validar o corpo completo das respostas, tipos e invariantes; adicionar schemas para login, usuários listados, produtos e carrinhos. | **Boa para usuários; parcial para a API completa** |
| **4. E — Errors (Erros)** | Os testes cobrem erros esperados com `400`, `401` e `403`, incluindo usuário/carrinho não encontrado, e-mail duplicado, campos obrigatórios, estoque insuficiente, produto duplicado, ausência de token e falta de permissão. As mensagens de erro são verificadas em vários cenários. | Não há injeção ou simulação controlada de falhas inesperadas para avaliar `5xx`, timeout, resposta malformada ou indisponibilidade. Também não existe uma validação uniforme de formato das mensagens e do corpo de erro em todos os endpoints. | Padronizar asserções de erro (status, mensagem, campos e schema), incluir casos de método/rota não suportados e validar a observabilidade da falha. Registrar explicitamente os limites do que pode ser testado sem controlar o servidor. | **Boa para erros funcionais; limitada para falhas inesperadas** |

### Síntese

Os testes cobrem bem os fluxos funcionais e os erros de negócio mais importantes, mas a cobertura VADER ainda não deve ser interpretada como cobertura total da API. A prioridade de evolução é: **(1)** matriz completa de autenticação/autorização, **(2)** dados inválidos e limites, **(3)** schemas para todos os contratos e **(4)** cenários controlados de falha inesperada.

Observando e aplicando essas heurísticas, é possível realizar testes de API mais completos e eficazes, identificando problemas que poderiam passar despercebidos em abordagens menos estruturadas.