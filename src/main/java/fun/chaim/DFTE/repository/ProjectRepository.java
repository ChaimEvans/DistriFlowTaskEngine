package fun.chaim.DFTE.repository;

import fun.chaim.DFTE.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 项目数据访问层
 * 
 * @author chaim
 * @since 1.0.0
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    
    /**
     * 根据名称查找项目
     * 
     * @param name 项目名称
     * @return 项目实体
     */
    Optional<Project> findByName(String name);
    
    /**
     * 检查名称是否存在
     * 
     * @param name 项目名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 根据工作流ID查找所有项目
     * 
     * @param workflowId 工作流ID
     * @return 项目列表
     */
    List<Project> findByWorkflow(Integer workflowId);
    
    /**
     * 查找所有项目信息（排除workflow_input，联合查询工作流名称）
     * 
     * @return 项目信息列表
     */
    @Query("SELECT p.id, p.name, p.workflow, w.name as workflowName, p.createdAt " +
           "FROM Project p " +
           "LEFT JOIN Workflow w ON p.workflow = w.id " +
           "ORDER BY p.createdAt DESC")
    List<Object[]> findAllProjectInfo();
}
