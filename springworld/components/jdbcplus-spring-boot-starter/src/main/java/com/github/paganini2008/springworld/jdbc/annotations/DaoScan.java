package com.github.paganini2008.springworld.jdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.github.paganini2008.springworld.jdbc.ApplicationContextUtils;
import com.github.paganini2008.springworld.jdbc.DaoScannerRegistrar;
import com.github.paganini2008.springworld.jdbc.EnhancedJdbcTemplate;

/**
 * 
 * DaoScan
 *
 * @author Fred Feng
 * @created 2019-10
 * @revised 2020-01
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ EnhancedJdbcTemplate.class, DaoScannerRegistrar.class, ApplicationContextUtils.class })
public @interface DaoScan {

	String[] basePackages();

}
