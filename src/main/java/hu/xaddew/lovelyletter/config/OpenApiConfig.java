package hu.xaddew.lovelyletter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class OpenApiConfig implements WebMvcConfigurer {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(getInfo())
        .addSecurityItem(new SecurityRequirement().addList("JWT"))
        .components(new Components().addSecuritySchemes("JWT",
            new SecurityScheme()
                .type(Type.APIKEY)
                .name("Authorization")
                .in(In.HEADER)));
  }

  private Info getInfo() {
    return new Info()
        .title("Lovely Letter API");
  }

}
