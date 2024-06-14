package com.nailcase.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes("bearer-key",
					new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
				.addSecuritySchemes("oauth2-key",
					new SecurityScheme()
						.type(SecurityScheme.Type.OAUTH2)
						.flows(new OAuthFlows()
							.authorizationCode(new OAuthFlow()
								.authorizationUrl("https://kauth.kakao.com/oauth/authorize")
								.tokenUrl("https://kauth.kakao.com/oauth/token")
								.scopes(new Scopes().addString("profile", "Access profile information"))))))
			.addSecurityItem(new SecurityRequirement().addList("bearer-key").addList("oauth2-key"))
			.info(new Info()
				.title("Nail Case API")
				.version("0.0.1")
				.description("API documentation for Nail Case application"));
	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
			.group("public")
			.pathsToMatch("/**")
			.build();
	}

}
