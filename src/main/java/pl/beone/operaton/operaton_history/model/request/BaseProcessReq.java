package pl.beone.operaton.operaton_history.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.beone.operaton.operaton_history.model.User;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseProcessReq {
    private User currentUser;
    private String comment;
    private String nodeRef;
    private String processId;
    private String appName;
    private Map<String, Object> additionalProperties;
}
