package fun.chaim.DFTE.repository;

import fun.chaim.DFTE.dto.projection.RunningRecordProjections;
import fun.chaim.DFTE.dto.projection.RunningRecordProjections.RunningRecordDetail;
import fun.chaim.DFTE.entity.RunningRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 运行记录数据访问层
 * 
 * @author chaim
 * @since 1.0.0
 */
@Repository
public interface RunningRecordRepository extends JpaRepository<RunningRecord, Integer> {

    /**
     * 根据工作流ID查找所有运行记录
     * 
     * @param workflowId 工作流ID
     * @return 运行记录列表
     */
    List<RunningRecord> findByWorkflow(Integer workflowId);

    /**
     * 根据项目ID查找所有运行记录
     * 
     * @param projectId 项目ID
     * @return 运行记录列表
     */
    List<RunningRecord> findByProject(Integer projectId);

    /**
     * 根据完成状态查找所有运行记录
     * 
     * @param finish 是否完成
     * @return 运行记录列表
     */
    List<RunningRecord> findByFinish(Boolean finish);

    /**
     * 根据ID查询运行记录信息（排除workflow_data/input/output，联合查询工作流名称和项目名称）
     * 
     * @param id 运行记录ID
     * @return 运行记录信息
     */
    @Query("SELECT r.id AS id, r.workflow AS workflow, w.name AS workflowName, " +
            "r.project AS project, p.name AS projectName, r.finish AS finish, " +
            "r.createdAt AS createdAt, r.updatedAt AS updatedAt " +
            "FROM RunningRecord r " +
            "LEFT JOIN Workflow w ON r.workflow = w.id " +
            "LEFT JOIN Project p ON r.project = p.id " +
            "WHERE r.id = :id")
    Optional<RunningRecordProjections.RunningRecordInfo> findRunningRecordInfoById(@Param("id") Integer id);

    /**
     * 分页按条件查询运行记录数据（排除workflow_data、workflow_input、workflow_output，联合查询工作流名称、项目名称）
     * 
     * @param workflowId 工作流ID（可选）
     * @param projectId  项目ID（可选）
     * @param finish     是否完成（可选）
     * @param pageable   分页参数
     * @return 运行记录信息分页
     */
    @Query("SELECT r.id AS id, r.workflow AS workflow, w.name AS workflowName, " +
            "r.project AS project, p.name AS projectName, r.finish AS finish, " +
            "r.createdAt AS createdAt, r.updatedAt AS updatedAt " +
            "FROM RunningRecord r " +
            "LEFT JOIN Workflow w ON r.workflow = w.id " +
            "LEFT JOIN Project p ON r.project = p.id " +
            "WHERE (:workflowId IS NULL OR r.workflow = :workflowId) " +
            "AND (:projectId IS NULL OR r.project = :projectId) " +
            "AND (:finish IS NULL OR r.finish = :finish) " +
            "ORDER BY r.createdAt DESC")
    Page<RunningRecordProjections.RunningRecordInfo> findRunningRecordInfoPage(
            @Param("workflowId") Integer workflowId,
            @Param("projectId") Integer projectId,
            @Param("finish") Boolean finish,
            Pageable pageable);

    /**
     * 查询工作流的详细信息
     * 
     * @param id 运行记录ID
     * @return 工作流的详细信息
     */
    @Query("SELECT r.id AS id, " +
            "r.workflow AS workflow, w.name AS workflowName, " +
            "r.workflowData AS workflowData, " +
            "r.workflowInput AS workflowInput, " +
            "r.workflowOutput AS workflowOutput, " +
            "r.project AS project, p.name AS projectName, " +
            "r.finish AS finish, " +
            "r.createdAt AS createdAt, " +
            "r.updatedAt AS updatedAt " +
            "FROM RunningRecord r " +
            "LEFT JOIN Workflow w ON r.workflow = w.id " +
            "LEFT JOIN Project p ON r.project = p.id " +
            "WHERE r.id = :id")
    Optional<RunningRecordDetail> findRunningRecordDetailById(@Param("id") Integer id);
}
