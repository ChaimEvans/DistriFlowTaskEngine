package fun.chaim.DFTE.repository;

import fun.chaim.DFTE.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 工作流数据访问层
 * 
 * @author chaim
 * @since 1.0.0
 */
@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Integer> {
    
    /**
     * 根据名称查找工作流
     * 
     * @param name 工作流名称
     * @return 工作流实体
     */
    Optional<Workflow> findByName(String name);
    
    /**
     * 检查名称是否存在
     * 
     * @param name 工作流名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 查找所有未锁定的工作流
     * 
     * @return 未锁定的工作流列表
     */
    List<Workflow> findByLockFalse();
    
    /**
     * 根据ID查找工作流名称
     * 
     * @param id 工作流ID
     * @return 工作流名称
     */
    @Query("SELECT w.name FROM Workflow w WHERE w.id = :id")
    Optional<String> findNameById(@Param("id") Integer id);
    
    /**
     * 查找使用指定工作流的所有项目ID和名称
     * 
     * @param workflowId 工作流ID
     * @return 项目信息列表
     */
    @Query("SELECT p.id, p.name FROM Project p WHERE p.workflow = :workflowId")
    List<Object[]> findProjectsByWorkflowId(@Param("workflowId") Integer workflowId);
}
