<h1 align="center">Shiny Shop API</h1>
<p align="center">API para um E-commerce desenvolvida com Spring Boot.</p>

<p align="center">
    <img src="./.github/shiny-shop-logo-api.png" width="120" />
</p>

## :wrench: Utilizando localmente

### :mag_right: Como executar?

* Docker e docker-compose.
* Java 11 ou superior.
* Maven(opcional pois no projeto já vem um binário do mesmo).
### :athletic_shoe: Steps:

1. Clone este repositório para sua máquina e abra o terminal já no diretório do projeto. 
2. Utilize o comando `docker-compose up`, para iniciar o container do MySQL.
3. Utilize o comando `./mvnw clean package` para gerar um *.jar* do projeto.
4. Utilize o comando `./mvnw spring-boot:run` para iniciar o servidor.

Pronto, caso tudo tenha ocorrido com sucesso, o projeto funcionará normalmente!

### :gear: Configuração padrão:

Por padrão um usuário é criado caso não existe nenhum outro usuário no banco de dados.


```json
{
  "email": "admin@mail.com",
  "password": "admin123",
  "username": "Admin"
}
```

Para alterar esse usuário padrão edite o arquivo de [propriedades padrão](https://github.com/SilvanoGPM/spring-boot-jwt-boilerplate/blob/main/src/main/resources/application.yml)(application.properties, application.yml), por exemplo:

```yml
app:
  init:
    user:
      email: testemail@mail.com
      password: somepass
      username: testname
```

Para uma melhor segurança é melhor alterar os valors para a criação de tokens JWT.

```yml
app:
  jwt:
    secret: YOUR_SECRET_KEY
    expirationMs: 3600000 # 1 hour
    refreshExpirationMs: 2592000000 # 30 days
```

## :rocket: Tecnologias

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Project Lombok](https://projectlombok.org/)
* [JUnit5](https://junit.org/junit5/)
* [H2](http://www.h2database.com/html/features.html)
* [MySQL](https://www.mysql.com/)
* [JJWT](https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt)
* [Map Struct](https://mapstruct.org/)
* [Stripe](https://stripe.com)

## :heart: Obrigado

- [Implementação JWT](https://github.com/bezkoder/spring-boot-refresh-token-jwt)
- [Modelo Entidade Relacionamento de um E-commerce](https://netbeans.apache.org/kb/docs/javaee/ecommerce/images/affablebean-erd.png)
