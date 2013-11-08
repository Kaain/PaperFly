package de.fhb.mi.paperfly.rest;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

/**
 * @author Christoph Ott
 */
@Rest(rootUrl = "http://46.137.173.175:8080/PaperFlyServer-web/rest/service/v1", converters = {MappingJacksonHttpMessageConverter.class})
public interface RestService {

    @Get("/logout")
    Boolean logout();

    @Get("/login")
    Boolean login();
}
