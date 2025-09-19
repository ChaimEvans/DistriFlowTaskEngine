package fun.chaim.DFTE.repository;

import fun.chaim.DFTE.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 任务数据访问层
 * 
 * @author chaim
 * @since 1.0.0
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    
    /**
     * 根据运行记录ID查找所有任务
     * 
     * @param runningRecordId 运行记录ID
     * @return 任务列表
     */
    List<Task> findByRunningRecord(Integer runningRecordId);
    
    /**
     * 根据项目ID查找所有任务
     * 
     * @param projectId 项目ID
     * @return 任务列表
     */
    List<Task> findByProject(Integer projectId);
    
    /**
     * 根据工作流ID查找所有任务
     * 
     * @param workflowId 工作流ID
     * @return 任务列表
     */
    List<Task> findByWorkflow(Integer workflowId);
    
    /**
     * 根据状态查找所有任务
     * 
     * @param status 任务状态
     * @return 任务列表
     */
    List<Task> findByStatus(Integer status);
    
    /**
     * 分页查询任务数据（排除params、retdata，联合查询项目名、工作流名、处理程序名）
     * 
     * @param pageable 分页参数
     * @return 任务信息分页
     */
    @Query("SELECT t.uuid, t.name, t.runningRecord, t.project, t.workflow, t.nodeId, " +
           "t.program, t.status, t.retmsg, t.createdAt, t.updatedAt, " +
           "p.name as projectName, w.name as workflowName, pr.name as programName " +
           "FROM Task t " +
           "LEFT JOIN Project p ON t.project = p.id " +
           "LEFT JOIN Workflow w ON t.workflow = w.id " +
           "LEFT JOIN Program pr ON t.program = pr.id " +
           "ORDER BY t.createdAt DESC")
    Page<Object[]> findTaskInfoPage(Pageable pageable);
    
    /**
     * 根据UUID查询任务信息（联合查询项目名、工作流名、处理程序名）
     * 
     * @param uuid 任务UUID
     * @return 任务信息
     */
//     @Query("SELECT t.uuid, t.name, t.runningRecord, t.project, t.workflow, t.nodeId, " +
//            "t.program, t.params, t.status, t.retmsg, t.retdata, " +
//            "t.createdAt, t.updatedAt, " +
//            "p.name as projectName, w.name as workflowName, pr.name as programName " +
//            "FROM Task t " +
//            "LEFT JOIN Project p ON t.project = p.id " +
//            "LEFT JOIN Workflow w ON t.workflow = w.id " +
//            "LEFT JOIN Program pr ON t.program = pr.id " +
//            "WHERE t.uuid = :uuid")
    @Query("""
SELECT
       t.uuid, 
       t.name, 
       t.parent AS parentUuid,
       pt.name AS parentName,
       t.runningRecord, 
       t.project, 
       t.workflow, 
       t.nodeId, 
       t.program, 
       t.params, 
       t.status, 
       t.retmsg, 
       t.retdata, 
       t.createdAt, 
       t.updatedAt, 
       p.name AS projectName, 
       w.name AS workflowName, 
       pr.name AS programName 
FROM Task t
LEFT JOIN Task pt ON t.parent = pt.uuid
LEFT JOIN Project p ON t.project = p.id 
LEFT JOIN Workflow w ON t.workflow = w.id 
LEFT JOIN Program pr ON t.program = pr.id 
WHERE t.uuid = :uuid
    """)
    Optional<Object[]> findTaskInfoByUuid(@Param("uuid") UUID uuid);
}
