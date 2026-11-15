---
inclusion: always
---

# Product Overview - ServeRest API Testing

## Propósito do Produto

Repositório de **automação de testes de API REST** para validar a plataforma **ServeRest** — uma API que simula os serviços de um e-commerce. O projeto utiliza **Rest Assured 6.1.0** e **Java 23** com foco em boas práticas de engenharia de qualidade (QA), cobertura de cenários críticos e geração de relatórios de qualidade.

## Público-Alvo

- **QA Engineers**: Automação e validação de APIs REST
- **DevOps/SRE**: Integração contínua e monitoramento de qualidade
- **Desenvolvedores**: Referência de padrões de teste e boas práticas

## Funcionalidades Críticas

| Recurso | Endpoints | Criticidade | Cobertura |
|---------|-----------|------------|-----------|
| **Autenticação** | `POST /login` | 🔴 CRÍTICA | 5 cenários |
| **Gestão de Usuários** | `GET, POST, PUT, DELETE /usuarios` | 🔴 CRÍTICA | 18 cenários |
| **Carrinho de Compras** | `GET, POST, DELETE /carrinhos` | 🟠 ALTA | 7 cenários |
| **Cancelamento de Compra** | `DELETE /carrinhos/cancelar-compra` | 🟠 ALTA | Incluso em Carrinhos |
| **Produtos** | `POST /produtos` | 🟡 MÉDIA | Cobertura autorização |

## Objetivos de Negócio

1. **Validação Funcional Contínua**: Garantir que todos os endpoints da API ServeRest funcionem conforme especificado.
2. **Detecção de Regressões**: Executar suítes automatizadas em CI/CD para impedir falhas em produção.
3. **Documentação Viva**: Usar Allure Reports para comunicar qualidade ao time de negócios.
4. **Escalabilidade de Testes**: Arquitetura que permita crescimento sem degradação de manutenibilidade.
5. **Análise de Carga**: Validar limites de taxa (rate limiting) e resiliência sob estresse.

## Estado Atual

- **Total de Cenários Automatizados**: 31 cenários funcionais + 1 NFR (teste de carga)
- **Status**: 🟢 100% de sucesso na última execução
- **Relatório**: Publicado em GitHub Pages
- **Esteira CI/CD**: GitHub Actions (Ubuntu + Windows)
