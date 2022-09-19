package com.bah.controller;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;

import com.bah.model.Student;
import com.bah.util.ERequestMethod;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StudentController implements IController {
	// one instance, reuse
	private final CloseableHttpClient httpClient = HttpClients.createDefault();
	
	private final String baseServicePath = "http://localhost:8088";

	private final ObjectMapper mapper = new ObjectMapper();

	public StudentController() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {

		String requestPath = webExchange.getRequest().getPathWithinApplication();
		String requestMethod = webExchange.getRequest().getMethod();
		Student student = new Student();
		Long id = null;
		String stdId = webExchange.getRequest().getParameterValue("id");
		if (stdId != null && !"".equals(stdId)) {
			id = Long.parseLong(stdId);
			if(requestMethod.equals(ERequestMethod.GET.name())) {
				// find student 
				student = findById(id);
			}
		}

		switch (requestPath) {

		case "/students/new":

			if (requestMethod.equals(ERequestMethod.GET.name())) {
				addStudent(webExchange, templateEngine, writer, student);
			}
			if (requestMethod.equals(ERequestMethod.POST.name())) {
				postStudent(webExchange, templateEngine, writer);

			}
			break;
		case "/students":
			studentList(webExchange, templateEngine, writer);
			break;
		default:
			if (requestPath.matches("\\/students\\/edit?.*")) {
				if (requestMethod.equals(ERequestMethod.GET.name())) {
					addStudent(webExchange, templateEngine, writer, student);
				}

				if (requestMethod.equals(ERequestMethod.POST.name())) {
					postStudent(webExchange, templateEngine, writer);

				}
			}
			
			if (requestPath.matches("\\/students\\/delete?.*")) {

				deleteStudent(webExchange, templateEngine, writer, id);
				
			}
		}
	}

	public void postStudent(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer)
			throws ClientProtocolException, IOException {

		Long id = null;
		String stdId = webExchange.getRequest().getParameterValue("id");
		if(stdId != null &&
				!"".equals(stdId)) {
			id = Long.parseLong(stdId);
		}
		Student student = new Student(id,
				webExchange.getRequest().getParameterValue("name"),
				webExchange.getRequest().getParameterValue("passportNumber"));
		HttpEntityEnclosingRequestBase request;
		if (student.getId() == null) {
			// add new
			request = new HttpPost(baseServicePath + "/students");
		} else {
			request = new HttpPut(baseServicePath + "/students");
		}

		StringEntity entity = new StringEntity(mapper.writeValueAsString(student));
		request.setEntity(entity);
		request.setHeader("Content-Type", "application/json");

		try (CloseableHttpResponse response = httpClient.execute(request)) {

			// Get HttpResponse Status
			System.out.println(response.getStatusLine().toString());

			HttpEntity headerentity = response.getEntity();
			Header headers = headerentity.getContentType();
			System.out.println(headers);

			int status = response.getStatusLine().getStatusCode();

			if (status == HttpStatus.SC_OK) {
				WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
				ctx.setVariable("tourl", "students");
				templateEngine.process("redirectPage", ctx, writer);
			}
		}
	}

	public void addStudent(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer, Student student) {
		WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
		ctx.setVariable("student", student);
		templateEngine.process("student/add", ctx, writer);

	}

	@SuppressWarnings("unchecked")
	public void studentList(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer)
			throws ClientProtocolException, IOException {

		HttpGet request = new HttpGet(baseServicePath + "/students");

		List<Student> students = new ArrayList();

		try (CloseableHttpResponse response = httpClient.execute(request)) {

			// Get HttpResponse Status
			System.out.println(response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();
			Header headers = entity.getContentType();
			System.out.println(headers);

			if (entity != null) {
				// return it as a String
				String result = EntityUtils.toString(entity);
				students = (List<Student>) mapper.readValue(result, new TypeReference<List<Student>>() {
				});
				System.out.println(students);
			}

		}
		WebContext ctx = new WebContext(webExchange, webExchange.getLocale());
		ctx.setVariable("students", students);

		templateEngine.process("student/list", ctx, writer);
	}
	
	public void deleteStudent(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer, Long id) throws JsonParseException, JsonMappingException, IOException {
		
		HttpDelete request = new HttpDelete(baseServicePath + "/students/" + id);
		try (CloseableHttpResponse response = httpClient.execute(request)) {

			// Get HttpResponse Status
			System.out.println(response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();
			Header headers = entity.getContentType();
			System.out.println(headers);

			studentList(webExchange, templateEngine, writer);
		}
		
	}
	
	public Student findById(Long id) throws JsonParseException, JsonMappingException, IOException {
		
		Student res = null;
		HttpGet req = new HttpGet(baseServicePath + "/students/detail/" + id);
		try (CloseableHttpResponse response = httpClient.execute(req)) {

			// Get HttpResponse Status
			System.out.println(response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();
			Header headers = entity.getContentType();
			System.out.println(headers);

			if (entity != null) {
				// return it as a String
				String result = EntityUtils.toString(entity);
				res = mapper.readValue(result, Student.class); 
				System.out.println(res);
			}
		}
		
		return res;
	}

}
