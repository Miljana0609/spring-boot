# Backend – Social Media Spring Boot API

Detta projekt är ett gemensamt grupparbete där vi, tre personer, tillsammans byggde backend för en social media-applikation med Spring Boot som en del av en fullstack-applikation.
Frontend-delen finns som ett separat projekt, kallat spring-boot-frontend.
Under projektets gång har vi även fördjupat oss i Git och GitHub, lärt oss skapa branches, göra pull requests och samarbeta effektivt i en versionhanterad miljö.

## Syfte
- Bygga en strukturerad backend med Spring Boot och PostgreSQL
- Öva på OOP, JPA, DTO:er och validering
- Implementera säkerhet, autentisering och behörigheter
- Hantera data för användare, inlägg, kommentarer och gillningar

## Funktionalitet
- Registrering och inloggning av användare
- Uppdatering av användarprofil (bio, displayName, profilbild)
- Skapa, redigera och ta bort inlägg
- Skapa, redigera och ta bort kommentarer
- Gilla-markeringar för inlägg och kommentarer
- Relationshantering mellan användare, inlägg och kommentarer (one-to-many)
- CRUD-operationer via repositories och JPA
- API-dokumentation via Swagger
- Säkerhet med Spring Security och lösenords-kryptering
- Databasanslutning konfigurerad via `application.properties` och miljövariabler
- Transaktionshantering med `@Transactional` för korrekt laddning av relaterad data

## Deployment och miljö
- Backend deployad via Koyeb
- Databas hostad på Neon
- Miljövariabler används för att koppla backend till databasen och för säker hantering av credentials  
- Docker används för containerisering och enklare CI/CD med GitHub Actions

## Struktur
- Entities – User, Post, Comment, Like
- Repositories – hanterar databasanrop
- Services – affärslogik för CRUD och relationer
- Controllers – REST-endpoints med ResponseEntity
- DTOs – Request och Response DTOs för frontend-kommunikation
- Security – SecurityConfig, password encoder och exception handling
- Docker och CI/CD via GitHub Actions

## Teknik
- Java, Spring Boot
- PostgreSQL
- Spring Data JPA
- Spring Security
- Docker, Koyeb, Neon
- Swagger / OpenAPI
