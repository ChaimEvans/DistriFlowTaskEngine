package fun.chaim.DFTE.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import fun.chaim.DFTE.dto.ProcessingNodeStatusData;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class ProcessingNodeStatusDataDecoder implements Decoder.Text<ProcessingNodeStatusData> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ProcessingNodeStatusData decode(String s) throws DecodeException {
        try {
            return mapper.readValue(s, ProcessingNodeStatusData.class);
        } catch (Exception e) {
            throw new DecodeException(s, "JSON 解析失败", e);
        }
    }

    @Override
    public boolean willDecode(String s) {
        return s != null && !s.isEmpty();
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}