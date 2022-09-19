package com.bah.controller;

import java.io.Writer;
import java.util.List;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import com.bah.model.Order;
import com.bah.services.OrderService;

public class OrderListController implements IController {
	public OrderListController() {
		super();
	}
	
	public void process(IWebExchange webExchange,ITemplateEngine templateEngine,Writer writer)
			throws Exception {

		final OrderService orderService = new OrderService();
		final List<Order> allOrders = orderService.findAll();

		final WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
		ctx.setVariable("orders", allOrders);

		templateEngine.process("order/list", ctx, writer);

	}
}
