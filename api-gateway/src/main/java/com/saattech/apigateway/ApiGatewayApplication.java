package com.saattech.apigateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@SpringBootApplication
public class ApiGatewayApplication {

	@Value("${GATEWAY_SECRET}")
	private String gatewaySecret;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("http://localhost:4200");
		config.addAllowedOrigin("http://localhost:4201");
		config.addAllowedOrigin("http://localhost:4202");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Bean
	public RouterFunction<ServerResponse> gatewayRoutes() {
		return GatewayRouterFunctions.route("core-service")
				.route(GatewayRequestPredicates.path("/api/contents/**", "/api/casts/**", "/api/licenses/**", "/api/contentSearch/**", "/uploads/**", "/api/contents-trailer/**"), http())
				.filter(lb("core-service"))
				.before(BeforeFilterFunctions.addRequestHeader("X-Gateway-Secret", gatewaySecret))
				.build()
				.and(GatewayRouterFunctions.route("auth-service")
						.route(GatewayRequestPredicates.path("/api/auth/**"), http())
						.filter(lb("auth-service"))
						.before(BeforeFilterFunctions.addRequestHeader("X-Gateway-Secret", gatewaySecret))
						.build());
	}
}
