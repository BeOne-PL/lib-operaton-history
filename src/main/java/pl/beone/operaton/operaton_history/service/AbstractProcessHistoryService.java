package pl.beone.operaton.operaton_history.service;

import org.operaton.bpm.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import pl.beone.operaton.operaton_history.model.request.BaseCompleteTaskReq;
import pl.beone.operaton.operaton_history.model.request.BaseProcessReq;
import pl.beone.operaton.operaton_history.variables.CommonVariables;


import java.util.*;

public abstract class AbstractProcessHistoryService implements IProcessHistoryService {

    @Autowired
    protected TaskService taskService;

    @Override
    public Map<String, Object> createTaskHistory(String taskType, BaseProcessReq body, String outcome) {
        Map<String, Object> history = new HashMap<>();

        history.put("taskType", taskType);
        history.put("outcome", outcome.isEmpty() ? "" : outcome);
        history.put("completeAt", new Date().getTime());
        history.put("comment", body.getComment());
        history.put("completeBy", body.getCurrentUser().getFullName());
        history.put("userName", body.getCurrentUser().getUsername());

        return history;
    }

    @Override
    public Map<String, Object> createTaskHistory(String taskType, BaseProcessReq body, String outcome, String taskId) {
        Map<String, Object> history = createTaskHistory(taskType,body,outcome);
        history.put("taskId", taskId);
        return history;
    }

    @Override
    public List<Map<String, Object>> initHistory(BaseProcessReq body) {
        List<Map<String, Object>> historyList = new ArrayList<>();
        historyList.add(
                createTaskHistory(
                        CommonVariables.TASK_TYPE_START,
                        body,
                        CommonVariables.OUTCOME_START_PROCESS)
        );
        return historyList;
    }

    @Override
    public List<Map<String, Object>> addHistory(BaseCompleteTaskReq req) {
        List<Map<String, Object>> history =
                (List<Map<String, Object>>) taskService
                        .getVariable(req.getTaskId(), CommonVariables.VARIABLE_HISTORY);
        history.add(
                createTaskHistory(
                        req.getTaskKey(),
                        req,
                        req.getResult(),
                        req.getTaskId()
                )
        );

        return history;
    }
    

}
