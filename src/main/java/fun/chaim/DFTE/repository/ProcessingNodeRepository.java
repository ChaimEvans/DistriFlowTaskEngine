package fun.chaim.DFTE.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import fun.chaim.DFTE.entity.ProcessingNode;

@Repository
public class ProcessingNodeRepository {
    private final Map<String, ProcessingNode> repository = new ConcurrentHashMap<>();
    private final Map<String, ProcessingNode> macRepository = new ConcurrentHashMap<>();

    public ProcessingNode save(ProcessingNode pn) {
        if (repository.containsKey(pn.getId())) {
            repository.put(pn.getId(), pn);
            macRepository.put(pn.getMac(), pn);
            return pn;
        }
        // 不存在id，创建
        // 尝试还原相同MAC地址的节点
        ProcessingNode node = macRepository.get(pn.getMac());
        if (node != null) {
            
        }
        repository.put(pn.getId(), pn);
        macRepository.put(pn.getMac(), pn);
        return pn;
    }

    public void delete(String id) {
        if (!repository.containsKey(id))
            return;
        ProcessingNode node = repository.get(id);
        if (node.getSession().isOpen()) {
            try {
                node.getSession().close();
            } catch (Exception e) { }
        }
        node.setOnline(false);
        repository.remove(id);
    }

    public Optional<ProcessingNode> findById(String id) {
        return Optional.ofNullable(repository.get(id));
    }

    public Optional<ProcessingNode> findByMac(String mac) {
        return Optional.ofNullable(macRepository.get(mac));
    }

    public List<ProcessingNode> findAll() {
        return repository.values().stream().toList();
    }

    public boolean existsById(String id) {
        return repository.containsKey(id);
    }
}
