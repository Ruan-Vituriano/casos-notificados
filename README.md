# API de Notificações SINAN

Este projeto é uma API RESTful desenvolvida com Spring Boot para o gerenciamento de casos notificados, baseada na Ficha de Notificação/Conclusão do SINAN / Ministério da Saúde. 

A aplicação foi desenvolvida como atividade prática para a disciplina de **Programação para a Web I** (IFPB - Campus Cajazeiras).

## 👥 Integrantes da Equipe
* Ruan Vituriano Claudino
* Luiz Henrique Moreira de Oliveira

## 🛠️ Tecnologias Utilizadas
* **Linguagem:** Java 21
* **Framework:** Spring Boot (Spring Web, Spring Validation)
* **Persistência:** Armazenamento em arquivos (I/O) temporários/locais (Sem banco de dados relacional)
* **Front-end:** HTML, CSS e JavaScript puros (Vanilla)

## ⚙️ Pré-requisitos
Para rodar este projeto, você precisará ter instalado em sua máquina:
* [Java 21 JDK](https://adoptium.net/pt-BR/) ou superior.
* Maven (opcional, o projeto utiliza o `mvnw` embutido).

## 🚀 Instruções para Executar o Projeto

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git
   ```

2. **Acesse o diretório do projeto:**
   ```bash
   cd SEU_REPOSITORIO
   ```

3. **Inicie a aplicação utilizando o Maven Wrapper:**
   * No **Windows**:
     ```cmd
     mvnw.cmd spring-boot:run
     ```
   * No **Linux/Mac**:
     ```bash
     ./mvnw spring-boot:run
     ```

4. **Acesse a aplicação:**
   * A API estará rodando em: `http://localhost:8080/notificacao`
   * Para visualizar o front-end (se os arquivos estáticos estiverem na pasta `src/main/resources/static` ou `public`), acesse: `http://localhost:XXXX/`

## 📁 Sobre a Persistência de Dados
Conforme decisão de projeto, esta aplicação **não utiliza um banco de dados tradicional (SQL/NoSQL)**. Todos os registros de notificações criados via API são salvos localmente em arquivos no diretório do projeto, garantindo o funcionamento do CRUD básico através de manipulação de arquivos com Java.
