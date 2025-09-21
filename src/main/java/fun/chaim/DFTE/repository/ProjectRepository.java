package fun.chaim.DFTE.repository;

import fun.chaim.DFTE.dto.projection.ProjectProjections;
import fun.chaim.DFTE.entity.Project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * 分页查找项目信息（可选过滤：workflow、finish，联合查询工作流名称）
     *
     * @param workflowId 工作流ID（可选）
     * @param finish 是否完成（可选）
     * @param pageable 分页参数
     * @return 项目信息分页
     */
    @Query("SELECT p.id AS id, p.name AS name, w.name AS workflowName, " +
           "p.finish AS finish, p.createdAt AS createdAt " +
           "FROM Project p " +
           "LEFT JOIN Workflow w ON p.workflow = w.id " +
           "WHERE (:workflowId IS NULL OR p.workflow = :workflowId) " +
           "AND (:finish IS NULL OR p.finish = :finish)")
    Page<ProjectProjections.ProjectInfo> findAllProjectInfo(@Param("workflowId") Integer workflowId,
                                                            @Param("finish") Boolean finish,
                                                            Pageable pageable);
}
