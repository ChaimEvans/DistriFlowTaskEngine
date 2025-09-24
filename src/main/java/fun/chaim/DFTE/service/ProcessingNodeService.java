package fun.chaim.DFTE.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fun.chaim.DFTE.dto.ProcessingNodeStatusData;
import fun.chaim.DFTE.dto.ProcessingNodeStatusDto;
import fun.chaim.DFTE.entity.ProcessingNode;
import fun.chaim.DFTE.repository.ProcessingNodeRepository;
import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingNodeService {
    private final ProcessingNodeRepository processingNodeRepository;

    /**
     * 注册节点
     * @param session
     * @param name
     * @param ip
     * @param mac
     */
    public void register(Session session, String name, String address, String mac) {
        ProcessingNode node = new ProcessingNode();
        node.setId(session.getId());
        node.setSession(session);
        node.setName(name);
        node.setAddresss(address);
        node.setMac(mac);
        processingNodeRepository.save(node);
    }

    /**
     * 注销节点
     * @param id
     */
    public void unregister(String id) {
        processingNodeRepository.delete(id);
    }

    /**
     * 广播消息
     * @param msg
     */
    public void broadcast(String msg) throws IOException {
        for (ProcessingNode node : processingNodeRepository.findAll()) {
            node.getSession().getBasicRemote().sendText(msg);
        }
    }

    /**
     * 设置节点状态数据
     * @param id
     * @param statusData
     */
    public void setStatusData(String id, ProcessingNodeStatusData statusData) {
        Optional<ProcessingNode> nodeOpt = processingNodeRepository.findById(id);
        if (nodeOpt.isPresent()) {
            ProcessingNode node = nodeOpt.get();
            node.setStatusData(statusData);
            processingNodeRepository.save(node);
        }
    }

    public List<ProcessingNodeStatusDto> getAllStatus() { 
        List<ProcessingNodeStatusDto> statusList = new ArrayList<>();
        for (ProcessingNode node : processingNodeRepository.findAll()) { 
            statusList.add(ProcessingNodeStatusDto.fromEntity(node));
        }
        return statusList;
    }
}
