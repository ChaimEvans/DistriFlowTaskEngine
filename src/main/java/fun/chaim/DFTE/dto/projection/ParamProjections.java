package fun.chaim.DFTE.dto.projection;

public interface ParamProjections {
    interface ParamInfo {
        Integer getId();

        String getName();

        String getType();

        String getDescription();

        Boolean getRequire();

        Integer getParentId();

        String getParentName();
    }
}
