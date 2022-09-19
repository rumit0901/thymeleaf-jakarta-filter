package com.bah.controller;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.web.IWebExchange;

public interface IController {

	public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer) 
			throws Exception;
}
