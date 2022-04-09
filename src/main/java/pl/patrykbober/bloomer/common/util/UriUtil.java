package pl.patrykbober.bloomer.common.util;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

public class UriUtil {

    private UriUtil() {
    }

    public static URI getBaseAppUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUri();
    }

    public static URI requestConfirmationLink(String token) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users")
                .path("/confirm")
                .queryParam("token", token)
                .buildAndExpand(token)
                .toUri();
    }

    public static URI requestUriWithId(Object id) {
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    }

    public static URI requestUriWithPathAndId(String path, Object id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
    }

}
