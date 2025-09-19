package fun.chaim.DFTE.exception;

/**
 * 冲突异常
 * 用于处理资源冲突的情况，如重复创建、状态冲突等
 * 
 * @author chaim
 * @since 1.0.0
 */
public class ConflictException extends BusinessException {
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ConflictException(String message) {
        super(409, message);
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因异常
     */
    public ConflictException(String message, Throwable cause) {
        super(409, message, cause);
    }
}
