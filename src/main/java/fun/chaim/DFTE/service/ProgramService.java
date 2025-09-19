package fun.chaim.DFTE.service;

import fun.chaim.DFTE.dto.ParamDto;
import fun.chaim.DFTE.dto.ProgramDto;
import fun.chaim.DFTE.dto.ProgramSimpleDto;
import fun.chaim.DFTE.entity.Param;
import fun.chaim.DFTE.entity.Program;
import fun.chaim.DFTE.exception.ConflictException;
import fun.chaim.DFTE.exception.ForbiddenException;
import fun.chaim.DFTE.exception.ResourceNotFoundException;
import fun.chaim.DFTE.exception.ValidationException;
import fun.chaim.DFTE.repository.ParamRepository;
import fun.chaim.DFTE.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理程序服务
 * 
 * @author chaim
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {
    
    private final ProgramRepository programRepository;
    private final ParamRepository paramRepository;
    
    /**
     * 创建处理程序
     * 
     * @param program 处理程序信息
     * @return 创建的处理程序
     */
    @Transactional
    public ProgramDto createProgram(Program program) {
        // 检查名称是否已存在
        if (programRepository.existsByName(program.getName())) {
            throw new ConflictException("处理程序名称已存在: " + program.getName());
        }
        
        // 验证必填字段
        if (program.getFile() == null || program.getFile().trim().isEmpty()) {
            throw new ValidationException("文件路径不能为空");
        }
        if (program.getCmd() == null || program.getCmd().trim().isEmpty()) {
            throw new ValidationException("启动命令不能为空");
        }
        
        program.setLock(false);
        Program savedProgram = programRepository.save(program);
        log.info("创建处理程序成功: {}", savedProgram.getName());
        
        return convertToDto(savedProgram);
    }
    
    /**
     * 根据ID获取处理程序信息（简单版，排除file、cmd）
     * 
     * @param id 处理程序ID
     * @return 处理程序信息
     */
    public ProgramDto getProgramById(Integer id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("处理程序", id));
        
        ProgramDto dto = convertToDto(program);
        
        // 获取参数信息
        List<Param> params = paramRepository.findByProgram(id);
        List<ParamDto> inputParams = params.stream()
                .filter(p -> !p.getRetval())
                .map(this::convertParamToDto)
                .collect(Collectors.toList());
        List<ParamDto> outputParams = params.stream()
                .filter(Param::getRetval)
                .map(this::convertParamToDto)
                .collect(Collectors.toList());
        
        dto.setInputParams(inputParams);
        dto.setOutputParams(outputParams);
        
        return dto;
    }
    
    /**
     * 根据ID获取处理程序详细信息（包含file、cmd）
     * 
     * @param id 处理程序ID
     * @return 处理程序详细信息
     */
    public ProgramDto getProgramDetailById(Integer id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("处理程序", id));
        
        ProgramDto dto = convertToDto(program);
        
        // 获取参数信息
        List<Param> params = paramRepository.findByProgram(id);
        List<ParamDto> inputParams = params.stream()
                .filter(p -> !p.getRetval())
                .map(this::convertParamToDto)
                .collect(Collectors.toList());
        List<ParamDto> outputParams = params.stream()
                .filter(Param::getRetval)
                .map(this::convertParamToDto)
                .collect(Collectors.toList());
        
        dto.setInputParams(inputParams);
        dto.setOutputParams(outputParams);
        
        return dto;
    }
    
    /**
     * 获取所有处理程序列表（简单版，排除file、cmd）
     * 
     * @return 处理程序列表
     */
    public List<ProgramSimpleDto> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(this::convertToSimpleDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取处理程序名称
     * 
     * @param id 处理程序ID
     * @return 处理程序名称
     */
    public String getProgramNameById(Integer id) {
        return programRepository.findNameById(id)
                .orElseThrow(() -> new ResourceNotFoundException("处理程序", id));
    }
    
    /**
     * 删除处理程序
     * 
     * @param id 处理程序ID
     */
    @Transactional
    public void deleteProgram(Integer id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("处理程序", id));
        
        if (program.getLock()) {
            throw new ForbiddenException("处理程序已锁定，无法删除");
        }
        
        programRepository.delete(program);
        log.info("删除处理程序成功: {}", program.getName());
    }
    
    /**
     * 更新处理程序信息（排除lock字段）
     * 
     * @param id 处理程序ID
     * @param program 更新信息
     * @return 更新后的处理程序
     */
    @Transactional
    public ProgramDto updateProgram(Integer id, Program program) {
        Program existingProgram = programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("处理程序", id));
        
        if (existingProgram.getLock()) {
            throw new ForbiddenException("处理程序已锁定，无法修改");
        }
        
        // 检查名称是否与其他处理程序冲突
        if (!existingProgram.getName().equals(program.getName()) && 
            programRepository.existsByName(program.getName())) {
            throw new ConflictException("处理程序名称已存在: " + program.getName());
        }
        
        // 更新字段
        existingProgram.setName(program.getName());
        existingProgram.setTitle(program.getTitle());
        existingProgram.setDescription(program.getDescription());
        existingProgram.setFile(program.getFile());
        existingProgram.setCmd(program.getCmd());
        
        Program savedProgram = programRepository.save(existingProgram);
        log.info("更新处理程序成功: {}", savedProgram.getName());
        
        return convertToDto(savedProgram);
    }
    
    /**
     * 转换为DTO
     */
    private ProgramDto convertToDto(Program program) {
        ProgramDto dto = new ProgramDto();
        dto.setId(program.getId());
        dto.setName(program.getName());
        dto.setTitle(program.getTitle());
        dto.setDescription(program.getDescription());
        dto.setBuildin(program.getBuildin());
        dto.setFile(program.getFile());
        dto.setCmd(program.getCmd());
        dto.setLock(program.getLock());
        dto.setUpdatedAt(program.getUpdatedAt());
        return dto;
    }
    
    /**
     * 转换为简单DTO（排除file、cmd）
     */
    private ProgramSimpleDto convertToSimpleDto(Program program) {
        ProgramSimpleDto dto = new ProgramSimpleDto();
        dto.setId(program.getId());
        dto.setName(program.getName());
        dto.setTitle(program.getTitle());
        dto.setDescription(program.getDescription());
        dto.setBuildin(program.getBuildin());
        dto.setLock(program.getLock());
        return dto;
    }
    
    /**
     * 转换参数为DTO
     */
    private ParamDto convertParamToDto(Param param) {
        ParamDto dto = new ParamDto();
        dto.setId(param.getId());
        dto.setName(param.getName());
        dto.setType(param.getType());
        dto.setDescription(param.getDescription());
        dto.setRetval(param.getRetval());
        dto.setRequire(param.getRequire());
        return dto;
    }
}
