package pl.patrykbober.bloomer.user.request;

import java.util.List;

public record UserRolesRequest(
        List<String> roleNames
) {
}
