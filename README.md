# Catálogo de Produtos

Link: https://crud-project-pb-dev-187480176525.us-central1.run.app

Este projeto apresenta um sistema CRUD de produtos desenvolvido em Java com Spring Boot, interface web em Thymeleaf e persistência em memória para fins acadêmicos. A aplicação permite cadastrar, consultar, editar e remover produtos com validações de entrada, tratamento de falhas e retorno de mensagens claras ao usuário.
## Estrutura e qualidade do código

O código foi organizado em camadas de domínio, serviço, repositório, formulário e interface web, com responsabilidades bem definidas e baixo acoplamento entre os componentes. A modelagem foi mantida simples e coesa, com foco em legibilidade, modularidade e facilidade de manutenção. Também foram aplicados ajustes de refatoração para remover redundâncias, melhorar nomes, separar melhor as responsabilidades e manter o sistema mais próximo das práticas de Clean Code.

Além disso, o projeto adota imutabilidade no modelo principal e separa de forma clara as operações de leitura e escrita dentro da lógica de negócio. O tratamento de erros segue a ideia de fail early e fail gracefully, evitando estados inválidos e exibindo respostas controladas quando ocorrem entradas incorretas, indisponibilidade de serviço, timeout ou sobrecarga.

## Testes e validação

Foram implementados testes automatizados para as operações de cadastro, consulta, atualização e remoção, cobrindo cenários válidos e inválidos. A suíte inclui testes unitários, testes de integração, testes parametrizados, validação de limites de entrada, simulação de falhas e fuzz testing, com o objetivo de aumentar a confiabilidade e a segurança do sistema.

Na interface web, a validação foi feita com Selenium WebDriver, utilizando Page Object Model para encapsular a interação com as páginas e facilitar a reutilização dos testes. Também foram verificados fluxos completos de uso, mensagens de erro e comportamento da aplicação diante de entradas inesperadas, tanto localmente quanto após o deploy.

## CI/CD, segurança e deploy

A automação foi construída com GitHub Actions. O workflow de CI realiza build com Gradle, executa os testes automatizados e verifica a cobertura do projeto. O workflow de CodeQL faz a análise estática de segurança. Já o workflow de delivery organiza o deploy para os ambientes de desenvolvimento, teste e produção, com gerenciamento de variáveis e credenciais por meio de secrets e variables do GitHub.

Após a publicação, a aplicação ainda passa por testes pós-deploy com Selenium e por uma verificação de segurança com DAST, o que ajuda a confirmar que o sistema continua funcional no ambiente publicado. Os workflows também geram logs resumidos, artefatos e badges de status, facilitando o acompanhamento da esteira e a identificação de falhas.

O sistema foi preparado para hospedagem no Google Cloud Run, que foi escolhido por ser uma solução serverless, reduzindo a necessidade de administração de infraestrutura e permitindo escalar a aplicação de acordo com a demanda. Essa escolha torna o deploy mais simples, mantém o ambiente padronizado e oferece boa elasticidade para publicação em múltiplos cenários de uso.

## Execução do projeto

Para executar localmente:

```bash
./gradlew bootRun
```

Para rodar a suíte principal de testes:

```bash
./gradlew clean check
```

Para validar uma aplicação já publicada:

```bash
.\gradlew.bat postDeployTest "-Papp.base-url=https://crud-project-pb-dev-187480176525.us-central1.run.app"
Start-Process ".\build\reports\tests\postDeployTest\index.html"
```
