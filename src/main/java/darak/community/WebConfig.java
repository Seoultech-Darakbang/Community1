package darak.community;

import darak.community.web.argumentresolver.LoginMemberArgumentResolver;
import darak.community.web.interceptor.LoginCheckInterceptor;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login/home", "/login", "/logout", "/members/new",
                        "/members/new/confirmLoginId", "/css/**", "/js/**", "/img/**",
                        "/uploads/**", "/api/uploads/**", "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/")
                .setCachePeriod(3600); // 1시간 캐시
                
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
