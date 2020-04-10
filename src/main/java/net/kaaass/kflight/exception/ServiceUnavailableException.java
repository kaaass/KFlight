package net.kaaass.kflight.exception;

import lombok.Getter;
import net.kaaass.kflight.util.StatusEnum;

/**
 * 服务器暂不可用
 */
@Getter
public class ServiceUnavailableException extends BaseException {

    StatusEnum status = StatusEnum.SERVICE_UNAVAILIBLE;

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
