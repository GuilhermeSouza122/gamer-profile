# Gamer Profile

**Sua biblioteca de jogos, conquistas e progresso em um só lugar.**

Aplicação web com backend em Java e interface em React. A integração com a Steam permite importar jogos e conquistas e acompanhar o progresso em um painel pessoal.

## O que o projeto reúne

- Autenticação pela Steam e consulta do perfil do jogador.
- Sincronização da biblioteca e das conquistas da Steam.
- Painel com jogos, tempo de jogo, conquistas e percentual de conclusão.
- Sistema de nível, experiência e conquistas da própria aplicação.
- Persistência em PostgreSQL com Spring Data JPA.

## Stack

Java 21 · Spring Boot · Spring Data JPA · PostgreSQL · React · Vite · Maven · Docker Compose

## Execução local

Pré-requisitos: JDK 21, Node.js com npm e Docker com Compose.

1. Clone o repositório e configure o ambiente:

   ```bash
   git clone https://github.com/GuilhermeSouza122/gamer-profile.git
   cd gamer-profile
   cp .env.example .env
   ```

   No PowerShell, use `Copy-Item .env.example .env`. Preencha `DB_URL`, `DB_USER`, `DB_PASSWORD`, `STEAM_API_KEY` e `STEAM_ID64` conforme seu ambiente. A URL do banco local pode usar o formato `jdbc:postgresql://localhost:5432/gamer_profile`.

2. Inicie o PostgreSQL e o backend:

   ```bash
   docker compose up -d
   ./mvnw spring-boot:run
   ```

   No Windows, use `.\mvnw.cmd spring-boot:run`.

3. Em outro terminal, inicie a interface:

   ```bash
   cd frontend
   npm ci
   npm run dev
   ```

Backend: `http://localhost:8080` · Interface: `http://localhost:5173`

A integração exige uma chave válida da API Steam e dados acessíveis conforme a privacidade do perfil Steam.

## Organização

- [`src/main/java/com/gamerprofile/api`](src/main/java/com/gamerprofile/api) — controllers e contratos da API.
- [`src/main/java/com/gamerprofile/integration`](src/main/java/com/gamerprofile/integration) — integração com plataformas.
- [`src/main/java/com/gamerprofile/model`](src/main/java/com/gamerprofile/model) — entidades do domínio.
- [`frontend`](frontend) — interface React.
- [`compose.yaml`](compose.yaml) — banco PostgreSQL local.

## Verificações disponíveis

```bash
./mvnw test
cd frontend
npm run lint
npm run build
```

Os testes de contexto do backend podem depender do banco e das variáveis de ambiente configurados. A integração implementada é a Steam; os pacotes de outras plataformas ainda são pontos de extensão.
