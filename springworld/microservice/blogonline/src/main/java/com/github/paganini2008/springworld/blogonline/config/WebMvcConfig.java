package com.github.paganini2008.springworld.blogonline.config;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.github.paganini2008.springboot.authorization.AcceptedRequestFilter;
import com.github.paganini2008.springboot.authorization.AcceptedRequestFilter.HttpRequestMatcher;
import com.github.paganini2008.springworld.blogonline.config.Swagger2.Swagger2RequestMatcher;
import com.github.paganini2008.springworld.support.vo.ResultVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * WebMvcConfig
 * 
 * @author Fred Feng
 * @created 2019-06
 * @version 2.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(true).setUseTrailingSlashMatch(true);
	}

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.parse("100MB"));
		factory.setMaxRequestSize(DataSize.parse("100MB"));
		return factory.createMultipartConfig();
	}

	@Bean
	public HttpRequestMatcher swagger2RequestMatcher() {
		return new Swagger2RequestMatcher();
	}

	@Autowired
	public void acceptedRequest(AcceptedRequestFilter acceptedRequestFilter) {
		acceptedRequestFilter.addRequestMatcher(swagger2RequestMatcher());
	}

	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(signHandlerInterceptor()).addPathPatterns("/**");
	}

	@Bean
	public HandlerInterceptor signHandlerInterceptor() {
		return new SignHandlerInterceptor();
	}

	/**
	 * 
	 * SignHandlerInterceptor
	 * 
	 * @author Fred Feng
	 * @revised 2019-06
	 * @version 1.0
	 */
	@Slf4j
	public static class SignHandlerInterceptor implements HandlerInterceptor {

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			request.setAttribute("sign", System.currentTimeMillis());
			if (log.isTraceEnabled()) {
				log.trace(request.toString());
			}
			return true;
		}

		@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
				throws Exception {
			if (log.isTraceEnabled()) {
				long startTime = (Long) request.getAttribute("sign");
				log.trace(request.toString() + " take(ms): " + (System.currentTimeMillis() - startTime));
			}
		}

	}

	@Slf4j
	@RestControllerAdvice
	public static class NormalResponsePreHandler implements ResponseBodyAdvice<Object> {

		@Override
		public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
			return true;
		}

		@Override
		public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
				Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest req, ServerHttpResponse resp) {
			ServletServerHttpRequest request = (ServletServerHttpRequest) req;
			HttpServletRequest servletRequest = request.getServletRequest();
			long startTime = 0;
			if (servletRequest.getAttribute("sign") != null) {
				startTime = (Long) servletRequest.getAttribute("sign");
				if (log.isTraceEnabled()) {
					log.trace(request.toString() + " take(ms): " + (System.currentTimeMillis() - startTime));
				}
			}
			if (body instanceof ResultVO) {
				ResultVO<?> resultVO = (ResultVO<?>) body;
				resultVO.setElapsed(startTime > 0 ? System.currentTimeMillis() - startTime : 0);
				resultVO.setRequestUrl(servletRequest.getServletPath());
			}
			return body;
		}

	}
}
