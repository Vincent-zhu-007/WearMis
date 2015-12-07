package com.sg.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

public class SecurityFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest httpRequest = ((HttpServletRequest) request);
		String url = httpRequest.getRequestURI();
		int index = url.lastIndexOf("/");
		url = url.substring(index + 1);
		
		String doMain =  request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
		String contextPath = httpRequest.getContextPath();
		if(StringUtils.isNotEmpty(contextPath)){
			doMain = doMain + contextPath;
		}
		
		/*String method = httpRequest.getMethod();*/
		
		if (!url.equalsIgnoreCase("login.jsp") && !url.equalsIgnoreCase("login.do")) {
			HttpSession session = httpRequest.getSession();
			if(session != null){
				Object user = session.getAttribute("user");
				Object permission = session.getAttribute("permission");
				if (user == null || permission == null) {
					System.out.println("session is null");
					((HttpServletResponse) response).sendRedirect(doMain + "/pages/login.jsp");
				}
			}else {
				System.out.println("session is null");
				((HttpServletResponse) response).sendRedirect(doMain + "/pages/login.jsp");
			}
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
}
