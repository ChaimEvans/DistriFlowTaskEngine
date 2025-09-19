package fun.chaim.DFTE.exception;

/**
 * 参数验证异常
 * 用于处理参数验证失败的情况
 * 
 * @author chaim
 * @since 1.0.0
 */
public class ValidationException extends BusinessException {
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ValidationException(String message) {
        super(400, message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ValidationException(String message, Throwable cause) {
        super(400, message, cause);
    }
}
