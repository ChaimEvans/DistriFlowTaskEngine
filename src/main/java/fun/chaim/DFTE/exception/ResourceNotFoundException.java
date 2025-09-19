package fun.chaim.DFTE.exception;

/**
 * 资源未找到异常
 * 用于处理查询不到指定资源的情况
 * 
 * @author chaim
 * @since 1.0.0
 */
public class ResourceNotFoundException extends BusinessException {
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ResourceNotFoundException(String message) {
        super(404, message);
    }
    
    /**
     * 构造函数
     * 
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(404, String.format("%s with id %s not found", resourceType, resourceId));
    }
}
