# API de Notificações SINAN

API RESTful em Spring Boot para o gerenciamento de casos notificados (Ficha de Notificação/Conclusão do SINAN), com front-end em HTML, CSS e JavaScript puros.

Atividade prática da disciplina de **Programação para a Web I** (IFPB - Campus Cajazeiras).

## 👥 Integrantes
* Ruan Vituriano Claudino
* Luiz Henrique Moreira de Oliveira

## 🚀 Como rodar

**Pré-requisito:** [Java 21 JDK](https://adoptium.net/pt-BR/) ou superior (o Maven já vem embutido pelo `mvnw`).

1. **Suba a API** (na pasta `sinan-api`):
   * Windows: `mvnw.cmd spring-boot:run`
   * Linux/Mac: `./mvnw spring-boot:run`

   A API fica em `http://localhost:8080/notificacao`.

2. **Abra o front-end** (pasta `sinan-frontend`), servindo por HTTP:
   * No IntelliJ: abra o `index.html` e clique no ícone de navegador no canto do editor; ou
   * Em um terminal dentro de `sinan-frontend`: `python -m http.server 5500` e acesse `http://localhost:5500`.

Os dados ficam em memória e são perdidos ao reiniciar a API.
