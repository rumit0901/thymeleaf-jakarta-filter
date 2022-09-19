package com.bah.mapping;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.web.IWebRequest;

import com.bah.controller.HomeController;
import com.bah.controller.IController;
import com.bah.controller.OrderDetailsController;
import com.bah.controller.OrderListController;
import com.bah.controller.ProductCommentsController;
import com.bah.controller.ProductListController;
import com.bah.controller.StudentController;
import com.bah.controller.SubscribeController;
import com.bah.controller.UserProfileController;

public class ControllerMappings {

	public static Map<String, IController> controllersByURL;
	
	static {
		controllersByURL = new HashMap<String, IController>();
		controllersByURL.put("/", new HomeController());
        controllersByURL.put("/product/list", new ProductListController());
        controllersByURL.put("/product/comments", new ProductCommentsController());
        controllersByURL.put("/order/list", new OrderListController());
        controllersByURL.put("/order/details", new OrderDetailsController());
        controllersByURL.put("/subscribe", new SubscribeController());
        controllersByURL.put("/userprofile", new UserProfileController());
        controllersByURL.put("/students", new StudentController());
	}
	
	public static IController resolveControllerForRequest(final IWebRequest request) {
		
		String[] paths = request.getPathWithinApplication().split("/");
		String path = paths.length == 0  ? "/" : "/" + paths[1];
		return controllersByURL.get(path);
	}
	
	private ControllerMappings() {
		super();
	}
}
