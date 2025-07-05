package darak.community.web.config;

import darak.community.core.argumentresolver.LoginMemberArgumentResolver;
import darak.community.core.interceptor.AuthCheckInterceptor;
import darak.community.core.interceptor.LoginCheckInterceptor;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final AuthCheckInterceptor authCheckInterceptor;

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
                        "/uploads/**", "/api/uploads/**", "/error",
                        "/community/gifticons/*/claim", "/community/gifticons/use");

        registry.addInterceptor(authCheckInterceptor)
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/login/**", "/logout", "/members/**",
                        "/css/**", "/js/**", "/img/**", "/uploads/**", "/api/uploads/**",
                        "/error/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1) 업로드된 파일만 커스터마이징
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir)
                .setCacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .resourceChain(true);
    }
}
