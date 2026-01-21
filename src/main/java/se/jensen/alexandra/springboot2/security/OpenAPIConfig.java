package se.jensen.alexandra.springboot2.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * OpenAPIConfig är en konfigurationsklass som används för att ställa in API-dokumentationen
 * med Swagger/OpenAPI. Klassen beskriver grundläggande information om API:t, som namn och version,
 * samt att API:t är skyddat med JWT-baserad autentisering.
 * Klassen konfigurerar även ett säkerhetsschema som talar om för Swagger att API:t använder
 * Bearer-token (JWT) för autentisering. Detta gör att man kan testa skyddade endpoints direkt i
 * Swagger genom att ange en JWT-token.
 */
@OpenAPIDefinition(
        info = @Info(title = "API med JWT", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenAPIConfig {
}
