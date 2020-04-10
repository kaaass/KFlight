package net.kaaass.kflight.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import net.kaaass.kflight.util.StatusEnum;
import org.springframework.lang.Nullable;

/**
 * 标准返回格式
 * @param <T> 返回类型
 */
@Data
@ToString
@AllArgsConstructor
public class GlobalResponse<T> {
    private int status;
    @Nullable
    private String message;
    @Nullable
    private T data;

    /**
     * 成功返回
     * @param data 返回数据
     * @param <T> 返回数据类型
     * @return 返回VO
     */
    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>(StatusEnum.SUCCESS.getCode(), null, data);
    }

    /**
     * 失败返回
     * @param status 状态码
     * @param message 错误信息
     * @param <T> 返回数据类型
     * @return 返回VO
     */
    public static <T> GlobalResponse<T> fail(StatusEnum status, String message) {
        return new GlobalResponse<>(status.getCode(), message == null ? status.getDescription(): message, null);
    }

    /**
     * 失败返回
     * @param status 状态码
     * @param <T> 返回数据类型
     * @return 返回VO
     */
    public static <T> GlobalResponse<T> fail(StatusEnum status) {
        return fail(status, null);
    }
}
