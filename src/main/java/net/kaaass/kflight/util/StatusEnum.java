package net.kaaass.kflight.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    SUCCESS(200, ""), // 成功则不返回message段

    BAD_REQUEST(400, "请求错误"),
    FORBIDDEN(403, "请登录后访问"),
    NOT_FOUND(404, "未找到相关信息"),

    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILIBLE(503, "服务器暂不可用");

    private int code;
    private String description;

    public boolean isSuccess() {
        return code == StatusEnum.SUCCESS.code;
    }

    public HttpStatus toHttpStatus() {
        var httpStatus = HttpStatus.resolve(code);
        if (httpStatus == null) {
            // 未知响应码
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return httpStatus;
    }

    public static String descriptionFor(int code) {
        return Stream.of(StatusEnum.values())
                .filter((cur) -> cur.code == code)
                .map((cur) -> cur.description)
                .findAny().orElse(Constants.UNKNOWN);
    }
}
