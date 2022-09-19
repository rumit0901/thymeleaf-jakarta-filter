package com.bah.controller;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

public class SubscribeController implements IController {

	public SubscribeController() {
		super();
	}

	public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
			throws Exception {

		WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
		templateEngine.process("subscribe", ctx, writer);

	}
}
