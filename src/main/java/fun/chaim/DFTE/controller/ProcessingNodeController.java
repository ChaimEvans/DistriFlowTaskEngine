package fun.chaim.DFTE.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fun.chaim.DFTE.common.ApiResponse;
import fun.chaim.DFTE.dto.ProcessingNodeStatusDto;
import fun.chaim.DFTE.service.ProcessingNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/processing-node")
@RequiredArgsConstructor
public class ProcessingNodeController {
    private final ProcessingNodeService processingNodeService;

    @GetMapping
    public ApiResponse<List<ProcessingNodeStatusDto>> getAllStatus() {
        return ApiResponse.success(processingNodeService.getAllStatus());
    }
}
