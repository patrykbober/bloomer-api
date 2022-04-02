package pl.patrykbober.bloomer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BloomerException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus status;

    public BloomerException(ErrorCode errorCode, HttpStatus status) {
        this.errorCode = errorCode;
        this.status = status;
    }
}
