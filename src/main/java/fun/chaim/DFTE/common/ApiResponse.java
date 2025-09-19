package fun.chaim.DFTE.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应格式
 * 
 * @author chaim
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 响应码，成功为200，失败为对应的HTTP状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 堆栈跟踪信息（仅在开发环境或调试时返回）
     */
    private String stacktrace;
    
    /**
     * 成功响应
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data, null);
    }
    
    /**
     * 成功响应（无数据）
     * 
     * @return API响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null, null);
    }
    
    /**
     * 成功响应（自定义消息）
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null);
    }
    
    /**
     * 失败响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null, null);
    }
    
    /**
     * 失败响应（带堆栈跟踪）
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param stacktrace 堆栈跟踪
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> error(Integer code, String message, String stacktrace) {
        return new ApiResponse<>(code, message, null, stacktrace);
    }
    
    /**
     * 失败响应（默认错误码-1）
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(-1, message, null, null);
    }
}
