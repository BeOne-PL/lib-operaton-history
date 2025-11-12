package pl.beone.operaton.operaton_history.model.request;

import lombok.Data;

import java.util.Map;

@Data
public class BaseCompleteTaskReq extends BaseProcessReq {
    private String taskId;
    private String taskKey;
    private String result;
    private Map<String, Object> properties;
}
