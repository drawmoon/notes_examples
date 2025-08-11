package io.github.drawmoon.analyticalretrieverexample;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Spring Boot configuration class for component integration
 *
 * <p>This class serves as the central entry point for integrating third-party components into the
 * Spring Boot application context. All shared utility beans that require dependency injection
 * should be configured here to maintain centralized management.
 */
@Configuration
@EnableOpenApi
@EnableKnife4j
public class AppConfig {

  /**
   * Creates and configures the Caffeine cache manager with standardized settings
   *
   * <p>Constructs a shared cache manager instance with optimized configuration for:
   *
   * <ul>
   *   <li>Memory-efficient caching with 100-entry maximum size
   *   <li>Automatic cache invalidation after 10 minutes of inactivity
   *   <li>Default cache name "DEFAULT" for centralized management
   *   <li>Prevention of memory leaks through time-based expiration
   * </ul>
   *
   * <p>The configuration ensures:
   *
   * <ul>
   *   <li>Consistent cache behavior across components
   *   <li>Optimized resource utilization
   *   <li>Easy maintenance of cache policies
   *   <li>Standardized cache management practices
   * </ul>
   *
   * @return configured CaffeineCacheManager instance with application-wide defaults
   */
  @Bean
  public CaffeineCacheManager caffeineCache() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("DEFAULT");
    cacheManager.setCaffeine(
        Caffeine.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES));
    return cacheManager;
  }

  /**
   * Creates and configures an OkHttpClient bean with standardized settings
   *
   * <p>Configures a shared OkHttpClient instance with consistent timeout settings and connection
   * pool management for all HTTP communication across the application.
   *
   * @return configured OkHttpClient instance
   */
  @Bean
  public OkHttpClient okHttpClient() {
    return new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .connectionPool(new ConnectionPool(5, 1, TimeUnit.MINUTES))
        .build();
  }

  /**
   * Flag controlling Swagger documentation enablement
   *
   * <p>Injected from application configuration with default value. This setting determines whether
   * API documentation endpoints should be:
   *
   * <ul>
   *   <li>Exposed in development/test environments
   *   <li>Secured/hidden in production deployments
   *   <li>Used for API testing and validation
   *   <li>Available for integration documentation
   * </ul>
   */
  @Value("${app.component.swagger-enabled:true}")
  private boolean swaggerEnabled;

  /**
   * Creates default API documentation metadata
   *
   * <p>Generates standardized API information used across all documentation endpoints. This shared
   * configuration ensures consistent documentation appearance and:
   *
   * <ul>
   *   <li>Centralized version management
   *   <li>Uniform service identification
   *   <li>Standardized metadata presentation
   *   <li>Consistent documentation styling
   * </ul>
   *
   * @return fully configured ApiInfo instance
   */
  private ApiInfo createDefaultApiInfo() {
    return new ApiInfoBuilder().title("AnalyticalRetrieverExample").version("1.0.0").build();
  }

  /**
   * Creates and configures the Swagger API documentation plugin
   *
   * <p>Constructs the Docket instance that drives Swagger endpoint discovery and:
   *
   * <ul>
   *   <li>Controls documentation enablement state
   *   <li>Configures API selection criteria
   *   <li>Manages endpoint filtering rules
   *   <li>Coordinates documentation generation
   * </ul>
   *
   * <p>The configuration:
   *
   * <ul>
   *   <li>Selects APIs annotated with @Api
   *   <li>Includes all available paths
   *   <li>Uses environment-controlled enablement flag
   *   <li>Applies standardized API metadata
   * </ul>
   *
   * @return configured Docket instance for Swagger integration
   */
  @Bean
  public Docket createRestApi() {
    ApiInfo info = createDefaultApiInfo();

    return new Docket(DocumentationType.OAS_30)
        .enable(swaggerEnabled)
        .apiInfo(info)
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
        .paths(PathSelectors.any())
        .build();
  }
}
