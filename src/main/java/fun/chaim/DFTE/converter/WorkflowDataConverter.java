package fun.chaim.DFTE.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

import fun.chaim.DFTE.entity.WorkflowData;

@Converter(autoApply = true)
public class WorkflowDataConverter implements AttributeConverter<WorkflowData, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public String convertToDatabaseColumn(WorkflowData attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("[工作流数据] 序列化失败", e);
        }
    }

    @Override
    public WorkflowData convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, WorkflowData.class).disposal();
        } catch (IOException e) {
            throw new RuntimeException("[工作流数据] 解析失败", e);
        }
    }
}
