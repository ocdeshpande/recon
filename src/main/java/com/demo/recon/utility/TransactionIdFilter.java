package com.demo.recon.utility;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * Filter to intercept each request and assign unique identifier
 * 
 * @author deshpande_o
 *
 */
@Component
public class TransactionIdFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		final String token;
		token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
		httpResponse.setHeader("uniqueStamentNo", token);

		chain.doFilter(request, response);

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

}
