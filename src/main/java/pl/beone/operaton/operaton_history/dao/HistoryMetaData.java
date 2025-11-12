package pl.beone.operaton.operaton_history.dao;

import lombok.Data;

@Data
public class HistoryMetaData {
    private String workflowId;
    private String title;
    private String initiatedBy;
    private Long started;
    private String finished;
}
