package fun.chaim.DFTE.exception;

/**
 * 禁止操作异常
 * 用于处理权限不足或操作被禁止的情况
 * 
 * @author chaim
 * @since 1.0.0
 */
public class ForbiddenException extends BusinessException {
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ForbiddenException(String message) {
        super(403, message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ForbiddenException(String message, Throwable cause) {
        super(403, message, cause);
    }
}
