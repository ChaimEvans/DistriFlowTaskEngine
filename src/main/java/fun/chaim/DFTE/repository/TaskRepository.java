package fun.chaim.DFTE.repository;

import fun.chaim.DFTE.dto.TaskInQueueDto;
import fun.chaim.DFTE.dto.projection.TaskProjections;
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
     * 分页查询任务数据（排除 params、retdata，联合查询项目名、工作流名、处理程序名）
     * 可选过滤条件：runningRecord、project、workflow、nodeId、program、status
     *
     * @param runningRecord 运行记录 ID，可为空
     * @param project       项目 ID，可为空
     * @param workflow      工作流 ID，可为空
     * @param nodeId        节点 ID，可为空
     * @param program       程序 ID，可为空
     * @param status        状态，可为空
     * @param pageable      分页参数
     * @return 任务信息分页，返回 Object[]，后续在 Service 中转换为 DTO
     */
    /**
     * 分页查询任务数据（排除 params、retdata）
     */
    @Query("""
            SELECT t.uuid AS uuid, t.name AS name, t.runningRecord AS runningRecord, t.project AS project,
                   t.workflow AS workflow, t.nodeId AS nodeId, t.program AS program, t.status AS status,
                   t.retmsg AS retmsg, t.processingNodeMac AS processingNodeMac, t.createdAt AS createdAt, t.updatedAt AS updatedAt,
                   p.name AS projectName, w.name AS workflowName, pr.name AS programName
            FROM Task t
            LEFT JOIN Project p ON t.project = p.id
            LEFT JOIN Workflow w ON t.workflow = w.id
            LEFT JOIN Program pr ON t.program = pr.id
            WHERE (:#{#runningRecord} IS NULL OR t.runningRecord = :#{#runningRecord})
              AND (:#{#project} IS NULL OR t.project = :#{#project})
              AND (:#{#workflow} IS NULL OR t.workflow = :#{#workflow})
              AND (:#{#nodeId} IS NULL OR t.nodeId = :#{#nodeId})
              AND (:#{#program} IS NULL OR t.program = :#{#program})
              AND (:#{#status} IS NULL OR t.status = :#{#status})
            ORDER BY t.createdAt DESC
            """)
    Page<TaskProjections.TaskInfo> findTaskInfoPage(
            @Param("runningRecord") Integer runningRecord,
            @Param("project") Integer project,
            @Param("workflow") Integer workflow,
            @Param("nodeId") Integer nodeId,
            @Param("program") Integer program,
            @Param("status") Integer status,
            Pageable pageable);

    /**
     * 根据UUID查询任务信息（联合查询项目名、工作流名、处理程序名）
     * 
     * @param uuid 任务UUID
     * @return 任务信息
     */
    @Query("""
            SELECT
                t.uuid AS uuid, t.name AS name, t.parent AS parent, pt.name AS parentName,
                t.runningRecord AS runningRecord, t.project AS project, t.workflow AS workflow,
                t.nodeId AS nodeId, t.program AS program, t.params AS params, t.status AS status,
                t.retmsg AS retmsg, t.retdata AS retdata, t.processingNodeMac AS processingNodeMac,
                t.createdAt AS createdAt, t.updatedAt AS updatedAt, p.name AS projectName,
                w.name AS workflowName, pr.name AS programName
            FROM Task t
            LEFT JOIN Task pt ON t.parent = pt.uuid
            LEFT JOIN Project p ON t.project = p.id
            LEFT JOIN Workflow w ON t.workflow = w.id
            LEFT JOIN Program pr ON t.program = pr.id
            WHERE t.uuid = :uuid
            """)
    Optional<TaskProjections.TaskDetail> findTaskInfoByUuid(@Param("uuid") UUID uuid);

    /**
     * 查询队列任务信息（包含父任务名、项目名、工作流名、程序名）
     * 
     * @param uuid 任务UUID
     * @return 任务信息
     */
    @Query("""
            SELECT new fun.chaim.DFTE.dto.TaskInQueueDto(
                       t.uuid,
                       t.program,
                       t.params,
                       t.name,
                       pt.name,
                       p.name,
                       w.name,
                       pr.name
                   )
            FROM Task t
            LEFT JOIN Task pt ON t.parent = pt.uuid
            LEFT JOIN Project p ON t.project = p.id
            LEFT JOIN Workflow w ON t.workflow = w.id
            LEFT JOIN Program pr ON t.program = pr.id
            WHERE t.uuid = :uuid
            """)
    Optional<TaskInQueueDto> findTaskInQueueByUuid(@Param("uuid") UUID uuid);
}
