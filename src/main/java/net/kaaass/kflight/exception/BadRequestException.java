package net.kaaass.kflight.exception;

import lombok.Getter;
import net.kaaass.kflight.util.StatusEnum;

/**
 * 坏请求错误
 */
@Getter
public class BadRequestException extends BaseException {
    StatusEnum status = StatusEnum.BAD_REQUEST;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}