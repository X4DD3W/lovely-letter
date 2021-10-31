package hu.xaddew.lovelyletter.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.web.bind.annotation.CrossOrigin;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@CrossOrigin(origins = { "http://localhost:4200" })
public @interface AllowCrossOriginPort4200 {

}
