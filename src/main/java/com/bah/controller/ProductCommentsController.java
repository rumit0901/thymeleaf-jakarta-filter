package com.bah.controller;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import com.bah.model.Product;
import com.bah.services.ProductService;

public class ProductCommentsController implements IController {

    public ProductCommentsController() {
        super();
    }
    
    
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception {
        
        final Integer prodId = Integer.valueOf(webExchange.getRequest().getParameterValue("prodId"));
        
        final ProductService productService = new ProductService();
        final Product product = productService.findById(prodId);
        
        final WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        ctx.setVariable("prod", product);
        
        templateEngine.process("product/comments", ctx, writer);
        
    }

}
