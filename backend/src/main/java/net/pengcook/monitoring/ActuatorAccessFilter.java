package net.pengcook.monitoring;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ActuatorAccessFilter implements Filter {

    private static final String ALLOWED_IP = "127.0.0.1";
    private static final String ENDPOINT_PROMETHEUS = "/actuator/prometheus";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String remoteAddr = httpRequest.getRemoteAddr();

        if (httpRequest.getRequestURI().equals(ENDPOINT_PROMETHEUS) &&
                !remoteAddr.equals(ALLOWED_IP)) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }
}
