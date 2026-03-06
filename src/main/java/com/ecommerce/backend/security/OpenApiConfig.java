package com.ecommerce.backend.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(contact = @Contact(name = "Gersey Machava", email = "gerseymachava@gnmail.com", url = "https://github.com/GerseyMachava"), description = "API documentation for E-commerce Backend", title = "E-commerce Backend API", version = "1.0", license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT"), termsOfService = "https://example.com/terms"

), servers = {
        @Server(description = "Local Env", url = "http://localhost:8080"),
        @Server(description = "Prod Env", url = "http://localhost:8081"),
}, security = { @SecurityRequirement(name = "bearerAuth") })

@SecurityScheme(name = "bearerAuth", description = "JWT auth description", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

}
