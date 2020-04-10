package net.kaaass.kflight.exception;

import lombok.Getter;
import net.kaaass.kflight.util.StatusEnum;

/**
 * 未找到错误
 */
@Getter
public class NotFoundException extends BaseException {
    StatusEnum status = StatusEnum.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }
}
