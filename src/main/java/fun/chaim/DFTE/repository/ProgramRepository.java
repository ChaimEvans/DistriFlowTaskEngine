package fun.chaim.DFTE.repository;

import fun.chaim.DFTE.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 处理程序数据访问层
 * 
 * @author chaim
 * @since 1.0.0
 */
@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {
    
    /**
     * 根据名称查找处理程序
     * 
     * @param name 程序名称
     * @return 处理程序实体
     */
    Optional<Program> findByName(String name);
    
    /**
     * 检查名称是否存在
     * 
     * @param name 程序名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 查找所有未锁定的处理程序
     * 
     * @return 未锁定的处理程序列表
     */
    List<Program> findByLockFalse();
    
    /**
     * 根据ID查找处理程序名称
     * 
     * @param id 处理程序ID
     * @return 处理程序名称
     */
    @Query("SELECT p.name FROM Program p WHERE p.id = :id")
    Optional<String> findNameById(@Param("id") Integer id);
}
