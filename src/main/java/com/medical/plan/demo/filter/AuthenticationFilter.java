package com.medical.plan.demo.filter;


import com.medical.plan.demo.Tools.Utils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

@Order(1)
public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String auth = ((HttpServletRequest)servletRequest).getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            setErrorMsg(response);
        } else {
            String token = auth.substring(7);
            if(Utils.verifyJWT(token)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                setErrorMsg(response);
            }
        }

    }

    private void setErrorMsg(HttpServletResponse response) {
        PrintWriter writer = null;
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(response.getOutputStream(),
                    "UTF-8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        writer = new PrintWriter(osw, true);
        writer.write("Authentication Fail");
        response.setStatus(403);
        writer.flush();
        writer.close();
    }
}
