package com.github.paganini2008.springworld.webcrawler.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * Swagger2
 * 
 * @author Fred Feng
 *
 */
@Configuration
@EnableSwagger2
@Profile({ "local", "dev", "test", "fat" })
public class Swagger2 {

	private static final Collection<String> basePackageNames = Collections
			.unmodifiableCollection(Arrays.asList("com.github.paganini2008.springboot.examples.controller"));

	@Bean
	public Docket createRestApi() {// 创建API基本信息
		List<Predicate<RequestHandler>> predicates = new ArrayList<Predicate<RequestHandler>>();
		for (String packageName : basePackageNames) {
			predicates.add(RequestHandlerSelectors.basePackage(packageName));
		}
		Predicate<RequestHandler> predicate = Predicates.or(predicates);
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).securitySchemes(securitySchemes())

				.securityContexts(securityContexts()).select().apis(predicate).paths(PathSelectors.any()).build()
				.globalOperationParameters(getHeaders());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("SpringBoot使用Swagger2构建的RESTful风格的API").description("Allyes技术营销开发中心提供的RESTful风格的API")
				.contact(new Contact("allyes", "https://www.allyes.com", "mec@allyes.com")).version("2.0.0")// 版本号
				.build();
	}

	private List<Parameter> getHeaders() {
		List<Parameter> pairs = new ArrayList<Parameter>();
		ParameterBuilder tokenPair = new ParameterBuilder().name("token").description("token").modelRef(new ModelRef("string"))
				.parameterType("header").required(false);
		pairs.add(tokenPair.build());
		tokenPair = new ParameterBuilder().name("access_token").order(0).description("Spring OAuth2 认证").modelRef(new ModelRef("string"))
				.parameterType("query").required(false);
		pairs.add(tokenPair.build());
		return pairs;
	}

	private List<ApiKey> securitySchemes() {
		return Arrays.asList(new ApiKey("token", "token", "header"));
	}

	private List<SecurityContext> securityContexts() {
		return Arrays
				.asList(SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("^(?!auth).*$")).build());
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("token", authorizationScopes));
	}

}