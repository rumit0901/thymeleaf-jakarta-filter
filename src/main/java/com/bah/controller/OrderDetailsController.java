package com.bah.controller;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import com.bah.model.Order;
import com.bah.services.OrderService;

public class OrderDetailsController implements IController {

	@Override
	public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
		// TODO Auto-generated method stub
		final Integer orderId = Integer.valueOf(webExchange.getRequest().getParameterValue("orderId"));
		
		final OrderService orderService = new OrderService();
		final Order order = orderService.findById(orderId);
		
		final WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
		ctx.setVariable("order", order);
		
		templateEngine.process("order/details", ctx, writer);
	}

}
