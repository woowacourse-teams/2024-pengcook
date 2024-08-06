package net.pengcook.monitoring;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
