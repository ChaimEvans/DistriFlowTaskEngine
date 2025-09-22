package fun.chaim.DFTE.dto.projection;

public interface ParamProjections {
    interface ParamInfo {
        Integer getId();

        String getName();

        String getType();

        String getDescription();

        Boolean getRequire();

        Boolean getRetval();

        Boolean getIsWorkflow();

        Integer getParentId();

        String getParentName();
    }
}
