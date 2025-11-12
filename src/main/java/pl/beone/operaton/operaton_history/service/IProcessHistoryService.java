package pl.beone.operaton.operaton_history.service;

import pl.beone.operaton.operaton_history.model.request.BaseCompleteTaskReq;
import pl.beone.operaton.operaton_history.model.request.BaseProcessReq;

import java.util.List;
import java.util.Map;

public interface IProcessHistoryService {
    Map<String, Object> createTaskHistory(String taskType, BaseProcessReq body, String outcome, String taskId);
    Map<String, Object> createTaskHistory(String taskType, BaseProcessReq body, String outcome);

    List<Map<String, Object>> initHistory(BaseProcessReq body);
    List<Map<String, Object>> addHistory(BaseCompleteTaskReq req);

}
