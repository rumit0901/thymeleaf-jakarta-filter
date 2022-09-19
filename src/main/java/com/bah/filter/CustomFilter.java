package com.bah.filter;

import java.io.IOException;
import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.IWebRequest;
import org.thymeleaf.web.servlet.IServletWebApplication;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.bah.controller.IController;
import com.bah.mapping.ControllerMappings;
import com.bah.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomFilter implements Filter {

	private ITemplateEngine templateEngine;
	
	private JakartaServletWebApplication application;
	
	public CustomFilter() {
		super();
	}
	
	public static void addUserToSession(final HttpServletRequest request) {
		
		request.getSession().setAttribute("user", new User("Hao", "Bui", "VietNam", 27));
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		this.application = JakartaServletWebApplication.buildApplication(filterConfig.getServletContext());
		this.templateEngine = buildTemplateEngine(this.application);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		addUserToSession((HttpServletRequest) request);
		if(!process((HttpServletRequest) request, (HttpServletResponse) response)) {
			chain.doFilter(request, response);
		}
	}
	
	public boolean process(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		
		try {
			final IWebExchange webExchange = this.application.buildExchange(request, response);
			final IWebRequest webRequest = webExchange.getRequest();
			
			// this prevents triggering engine executions for resource URLS
			if(webRequest.getPathWithinApplication().startsWith("/css") ||
					webRequest.getPathWithinApplication().startsWith("/images") ||
					webRequest.getPathWithinApplication().startsWith("/favicon")) {
				return false;
			}
			
			/*
			 * Query Controller/URL mapping and obtain the controller
			 * that will process the request. If no controller is available,
			 * return false and let other filters process the request.
			 */
			final IController controller = ControllerMappings.resolveControllerForRequest(webRequest);
			if(controller == null)
				return false;
			
			// write the response header
			response.setContentType("text/html;charset=UTF-8");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            
            // obtain the response writer
            final Writer writer = response.getWriter();
            
            // Execute controller and process view template,
            // writing the results to response writer
            controller.process(webExchange, this.templateEngine, writer);
            
			return true;
		} catch(Exception e) {
			try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (final IOException ignored) {
                // Just ignore this
            }
            throw new ServletException(e);
		}
	}
	
	public static ITemplateEngine buildTemplateEngine(IServletWebApplication application) {
		
		final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
		
		// HTML is the default mode, but we will set it anyway for better understanding of code
		templateResolver.setTemplateMode(TemplateMode.HTML);
		
		// This will conver "home" (in controller) to "WEB-INF/templates/home.html"
		templateResolver.setPrefix("WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		// Set template cache TTL to 1 hour, if not set, entries would live in cache until expelled by LRU
		templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
		
		// Cache is set to true by default. Set to false if you want templates to
		// be automatically updated when modified
		templateResolver.setCacheable(false);
		
		final TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		
		return templateEngine;
	}

}
