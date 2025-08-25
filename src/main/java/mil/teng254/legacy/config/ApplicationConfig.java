package mil.teng254.legacy.config;

import mil.teng254.legacy.filter.PortFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public FilterRegistrationBean<PortFilter> portFilter() {
        FilterRegistrationBean<PortFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new PortFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}