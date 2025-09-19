package fun.chaim.DFTE.repository;

import fun.chaim.DFTE.entity.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 参数数据访问层
 * 
 * @author chaim
 * @since 1.0.0
 */
@Repository
public interface ParamRepository extends JpaRepository<Param, Integer> {
    
    /**
     * 根据处理程序ID查找所有参数
     * 
     * @param programId 处理程序ID
     * @return 参数列表
     */
    List<Param> findByProgram(Integer programId);
    
    /**
     * 根据工作流ID查找所有参数
     * 
     * @param workflowId 工作流ID
     * @return 参数列表
     */
    List<Param> findByWorkflow(Integer workflowId);
    
    /**
     * 根据处理程序ID和参数名称查找参数
     * 
     * @param programId 处理程序ID
     * @param name 参数名称
     * @return 参数实体
     */
    Optional<Param> findByProgramAndName(Integer programId, String name);
    
    /**
     * 根据工作流ID和参数名称查找参数
     * 
     * @param workflowId 工作流ID
     * @param name 参数名称
     * @return 参数实体
     */
    Optional<Param> findByWorkflowAndName(Integer workflowId, String name);
    
    /**
     * 根据参数类型、返回值标识和所属类型查询参数信息
     * 
     * @param type 参数类型
     * @param retval 是否是返回值
     * @param isProgram 是否是处理程序参数
     * @return 参数信息列表
     */
    @Query("SELECT p.id, p.name, p.type, p.description, p.require, " +
           "CASE WHEN p.program IS NOT NULL THEN pr.name ELSE w.name END as parentName, " +
           "CASE WHEN p.program IS NOT NULL THEN p.program ELSE p.workflow END as parentId " +
           "FROM Param p " +
           "LEFT JOIN Program pr ON p.program = pr.id " +
           "LEFT JOIN Workflow w ON p.workflow = w.id " +
           "WHERE p.type = :type AND p.retval = :retval " +
           "AND (:isProgram = true AND p.program IS NOT NULL OR :isProgram = false AND p.workflow IS NOT NULL)")
    List<Object[]> findParamsByTypeAndRetval(String type, 
                                           Boolean retval, 
                                           Boolean isProgram);
}
