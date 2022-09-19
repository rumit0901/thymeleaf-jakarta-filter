package com.bah.controller;

import java.io.Writer;
import java.util.List;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import com.bah.model.Product;
import com.bah.services.ProductService;

public class ProductListController implements IController {

    public ProductListController() {
        super();
    }
    
    
    public void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception {
        
        final ProductService productService = new ProductService();
        final List<Product> allProducts = productService.findAll(); 
        
        final WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
        ctx.setVariable("prods", allProducts);
        
        templateEngine.process("product/list", ctx, writer);
        
    }

}
