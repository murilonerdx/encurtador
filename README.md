# Projeto Encurtador de URLs

Este projeto implementa um microserviço de encurtamento de URLs utilizando Spring Boot e MongoDB. Ele permite criar URLs curtas que redirecionam para endereços longos, além de fornecer endpoints para consulta e redirecionamento.

## Tecnologias Utilizadas

- Java 17
- Spring Boot
    - Spring Web
    - Spring Data MongoDB
- MongoDB
- Docker / Docker Compose

## Funcionalidades

1. **Gerar URL curta**
2. **Obter detalhes de uma URL curta pelo ID interno**
3. **Redirecionar automaticamente da URL curta para a URL original**
4. **Obter a URL original sem redirecionamento (apenas devolve a string)**

## Endpoints da API

Base path: `/`

| Método | Rota                   | Descrição                                           | Parâmetros                     | Resposta                           |
| ------ | ---------------------- | --------------------------------------------------- | ------------------------------ | ---------------------------------- |
| POST   | `/`                    | Cria um novo short URL                              | JSON no corpo:                 | `ShortUrl` salvo (JSON)            |
|        |                        |                                                     | `{"urlDestiny": "<original>"}` |                                    |
| GET    | `/get/{id}`            | Busca o objeto `ShortUrl` pelo ID MongoDB interno   | `id` (String)                  | `ShortUrl` (JSON)                  |
| GET    | `/{urlShort}`          | Redireciona (HTTP 302) da URL curta para a original | `urlShort` (String)            | Redirecionamento para `urlDestiny` |
| GET    | `/original/{urlShort}` | Retorna a URL original sem redirecionar             | `urlShort` (String)            | `String` com a URL original        |

### Model `ShortUrl`

```java
@Document(collection = "short_urls")
public class ShortUrl {
    @Id
    private String id;           // ID interno no MongoDB
    private String urlDestiny;   // URL original completa
    private String urlShort;     // Chave curta gerada (ex: "a1b2C3d4")
}
```

### Request DTO `CreeateShortUrlRequest`

```java
public class CreeateShortUrlRequest {
    private String urlDestiny;
    // getters e setters
}
```

## Como Funciona

1. **Criação de uma URL curta**

    - Cliente faz `POST /` com JSON contendo `urlDestiny`.
    - O controller gera um ID aleatório de 8 caracteres (`UUID.randomUUID().toString().substring(0,8)`).
    - Salva um documento `ShortUrl(null, urlDestiny, urlShort)` no MongoDB.
    - Retorna o objeto salvo (incluindo `id` e `urlShort`).

2. **Redirecionamento**

    - Cliente acessa `GET /{urlShort}`.
    - O controller verifica se o path não é `favicon.ico` (para evitar requisições do navegador).
    - Busca no serviço `ShortUrlService#getByUrlShort(urlShort)`.
    - Executa `response.sendRedirect(urlDestiny)` para redirecionar para a URL original.

3. **Recuperar URL original sem redirecionar**

    - Cliente faz `GET /original/{urlShort}`.
    - Retorna diretamente a string com a URL original.

4. **Buscar por ID interno**

    - `GET /get/{id}` devolve o objeto completo `ShortUrl` do MongoDB.

## Configuração da Aplicação

No arquivo `application.yml` ou `application.properties`, configure as propriedades de conexão com o MongoDB:

```yaml
spring:
  data:
    mongodb:
      host: ${DB_HOST:localhost}
      port: ${DB_PORT:27017}
      database: ${DB_NAME:}
      username: ${DB_USER:}
      password: ${DB_PASSWORD:}
```

Ou como URI única:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://${DB_USER}:${DB_PASSWORD}@${DB_HOST}:${DB_PORT}/${DB_NAME}
```

E forneça no `docker-compose.yml` (ou variáveis de ambiente) as chaves `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` e `DB_PASSWORD`.

## Executando com Docker Compose

1. Suba o MongoDB em outro stack na mesma rede `services`:

   ```yaml
   services:
     mongodb:
       image: mongo:7
       command: --bind_ip_all
       networks:
         - services
       ports:
         - "27017:27017"
       environment:
         MONGO_INITDB_ROOT_USERNAME: 
         MONGO_INITDB_ROOT_PASSWORD: 
   networks:
     services:
       external: true
   ```

2. Suba o encurtador apontando para esse MongoDB:

   ```yaml
   version: "3.8"

   services:
     encurtador:
       image: murilonerdx/encurtador:latest
       ports:
         - "8099:8099"
       environment:
         - DB_HOST=mongodb
         - DB_PORT=27017
         - DB_NAME=
         - DB_USER=
         - DB_PASSWORD=
       networks:
         - services
         - traefik_public
       deploy:
         mode: replicated
         replicas: 1
         placement:
           constraints:
             - node.role == manager
         labels:
           - traefik.enable=true
           - traefik.http.routers.ms-encurtador.rule=Host(``)
           - traefik.http.routers.ms-encurtador.entrypoints=websecure
           - traefik.http.routers.ms-encurtador.tls.certresolver=le
           - traefik.http.services.ms-encurtador.loadbalancer.server.port=8099

   networks:
     services:
       external: true
     traefik_public:
       external: true
   ```

## Exemplos de Uso

- **Criar short URL**

  ```bash
  curl -X POST http://localhost:8099/ \
    -H "Content-Type: application/json" \
    -d '{"urlDestiny": "https://www.google.com"}'
  ```

- **Redirecionar** Abra no navegador: `http://localhost:8099/abc12345` (substitua `abc12345` pelo `urlShort` retornado)

- **Obter URL original**

  ```bash
  curl http://localhost:8099/original/abc12345
  ```

- **Buscar por ID interno**

  ```bash
  curl http://localhost:8099/get/teste
  ```

---

> **Observação:** para produção, considere usar um gerador de IDs de alta entropia (Base62), controles de taxa, logging e métricas.

---

## Licença

MIT © MuriloNerdX

