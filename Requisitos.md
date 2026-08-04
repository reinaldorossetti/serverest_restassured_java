1. Contexto

Você está trabalhando em uma aplicação que gerencia informações de usuários (Criação,
atualização, exclusão e leitura de usuário). A aplicação expõe uma API RESTfull para
realizar essas operações. Os endpoints da API são os seguintes:
○ GET /users: Retorna uma lista de todos os usuários.
○ POST /users: Cria um novo usuário.
○ GET /users/{id}: Retorna os detalhes de um usuário específico.
○ PUT /users/{id}: Atualiza as informações de um usuário.
○ DELETE /users/{id}: Exclui um usuário.

Sugestão de API: https://serverest.dev/#/

2. Requisitos

○ A autenticação é feita via token JWT.
○ A API possui limitações de taxas: 100 requisições por minuto.
○ Para criar um usuário, é necessário enviar um corpo JSON com os seguintes
campos obrigatórios:
■ nome (string)
■ email (string)
■ password (string)
■ administrador (string)

3. Tarefa

O candidato deve desenvolver um conjunto de testes automatizados que garanta 100% de
cobertura para essa API, utilizando uma ferramenta de testes de API de sua escolha
(Postman, RestAssured, etc.). Também deve integrar esses testes a uma pipeline de CI
(como Jenkins, GitLab, GitHub, etc.) e gerar relatórios dos resultados dos testes,
disponibilizando-os como artefato na pipeline.

4. Documentação

O candidato deve fornecer uma documentação que descreva os testes implementados,
incluindo instruções sobre como rodar os testes e uma explicação dos casos cobertos.
Entrega:
● Código fonte completo do projeto pelo GitHub ou GitLab
● Documentação sobre a configuração do ambiente e a execução dos testes no
README.md do projeto.