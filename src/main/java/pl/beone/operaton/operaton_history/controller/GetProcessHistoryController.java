package pl.beone.operaton.operaton_history.controller;

import lombok.extern.slf4j.Slf4j;
import org.operaton.bpm.engine.HistoryService;
import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.TaskService;
import org.operaton.bpm.engine.history.HistoricProcessInstance;
import org.operaton.bpm.engine.history.HistoricTaskInstance;
import org.operaton.bpm.engine.history.HistoricVariableInstance;
import org.operaton.bpm.engine.task.Comment;
import org.operaton.bpm.engine.task.IdentityLink;
import org.operaton.bpm.engine.task.IdentityLinkType;
import org.operaton.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.beone.operaton.operaton_history.dao.HistoryMetaData;
import pl.beone.operaton.operaton_history.dao.TaskHistoryDao;
import pl.beone.operaton.operaton_history.model.User;
import pl.beone.operaton.operaton_history.model.request.BaseCompleteTaskReq;
import pl.beone.operaton.operaton_history.model.request.BaseProcessReq;
import pl.beone.operaton.operaton_history.service.IProcessHistoryService;
import pl.beone.operaton.operaton_history.variables.CommonVariables;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class GetProcessHistoryController {

    @Autowired
    @Qualifier("processHistoryServiceImpl")
    IProcessHistoryService processHistoryService;

    @Autowired
    private ProcessEngine processEngine;


    @Autowired
    private TaskHistoryDao taskHistoryDao;

    private static final DateTimeFormatter dateFormat =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));
    @GetMapping("/processHistory")
    public  List<Map<String, Object>> getProcessHistory(
            @RequestParam(value = "nodeId", required = false) String nodeId,
            @RequestParam(value = "taskId", required = false) String taskId,
            @RequestParam(value = "procInstanceId", required = false) String procInstanceId
    ) {
        HistoryService historyService = processEngine.getHistoryService();
        TaskService taskService = processEngine.getTaskService();

        if (procInstanceId == null && taskId != null) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (task != null) {
                procInstanceId = task.getProcessInstanceId();

            }

        }

        // Retrieve historic process instance
        HistoricProcessInstance historicProcessInstance;
        if (procInstanceId != null){
            historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(procInstanceId)
                    .singleResult();
        }else{
             historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                     .variableValueEquals(CommonVariables.NODE_ID, nodeId)
                     .orderByProcessInstanceEndTime().desc()
                     .list().get(0);
             procInstanceId= historicProcessInstance.getId();
        }

        HistoricProcessInstance rootInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(procInstanceId)
                .singleResult();

        List<HistoricProcessInstance> processInstances;
        processInstances = getAllProcesses(historyService,procInstanceId,rootInstance);


        List<Map<String, Object>> fullHistory = new ArrayList<>();
        HistoricProcessInstance firstInstance = processInstances.stream().sorted(Comparator.comparing(HistoricProcessInstance::getStartTime)).collect(Collectors.toList()).get(0);

        fullHistory.add(getStartHistoryRecord(firstInstance, firstInstance.getId()));

        for (HistoricProcessInstance instance : processInstances) {
            fullHistory.addAll(getHistoricalData(historyService, instance, taskService));
            fullHistory.addAll(getActiveData(instance.getId(), taskService));
        }

        if (historicProcessInstance != null) {
            List<Map<String, Object>> sortedHistory = fullHistory.stream()
                    .sorted(Comparator.comparing(m -> (Date) m.get("createAt")))
                    .collect(Collectors.toList());

            HistoryMetaData historyMetaData = getMetaData(sortedHistory,historicProcessInstance.getId());

            Map<String, Object> responseMap = new HashMap<>();
            List<Map<String,Object>> resp = new ArrayList<>();
            responseMap.put("processHistoryMetaData", historyMetaData);
            responseMap.put("processHistory", sortedHistory);
            resp.add(responseMap);

            return  resp;

        } else {
            if (nodeId!= null){
                throw new IllegalArgumentException("No historic process instance found with node Id: " + nodeId);
            } else if (taskId != null) {
                throw new IllegalArgumentException("No historic process instance found with task Id: " + taskId);
            }
            throw new IllegalArgumentException("No historic process instance found with process instance Id: " + procInstanceId);

        }
    }


    public List<HistoricProcessInstance> getAllProcesses( HistoryService historyService,String procInstanceId, HistoricProcessInstance rootInstance){
        if (rootInstance != null) {
            // get the real root process instance (in case this is a subprocess)
            while (rootInstance.getSuperProcessInstanceId() != null) {
                rootInstance = historyService.createHistoricProcessInstanceQuery()
                        .processInstanceId(rootInstance.getSuperProcessInstanceId())
                        .singleResult();
            }

            return  getAllProcessInstancesIncludingSubprocesses(rootInstance.getId(), historyService);
        } else {
            throw new IllegalArgumentException("No historic process instance found with process instance Id: " + procInstanceId);
        }
    }
    public Date parseDate(Date value) {
        if ( value == null){
            return null;
        }
        LocalDateTime localDateTime = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        // Specify your local timezone
        ZoneId localZone = ZoneId.systemDefault();

        // Convert the local date and time to ZonedDateTime
        ZonedDateTime localZonedDateTime = ZonedDateTime.of(localDateTime, localZone);

        // Convert the ZonedDateTime to UTC+1 (CET)
        ZoneId cetZone = ZoneId.of("UTC");
        ZonedDateTime cetZonedDateTime = localZonedDateTime.withZoneSameInstant(cetZone);
        return Date.from(cetZonedDateTime.toInstant());
    }

    private User getIdentityLinkName(TaskService taskService, String taskId){
        User result = new User();
        List<IdentityLink> identities = taskService.getIdentityLinksForTask( taskId );
        for ( IdentityLink identity : identities ) {
            String type = identity.getType();
            switch ( type ) {
                case IdentityLinkType.CANDIDATE:
                    if (identity.getUserId() != null) {
                        result.setUsername(identity.getUserId());
                        return result;
                    } else if (identity.getGroupId() != null) {
                        result.setUsername(identity.getGroupId());
                        result.setFullName(identity.getGroupId());
                        return result;
                    }
                    break;
                default :
                    break;
            }
        }
        return result;
    }

    private Map<String,Object> getStartHistoryRecord(HistoricProcessInstance historicProcessInstance, String processInstanceId){
        org.operaton.bpm.engine.history.HistoricVariableInstanceQuery variableQuery = processEngine.getHistoryService().createHistoricVariableInstanceQuery();
        HistoricVariableInstance processInitiator = variableQuery.processInstanceId(processInstanceId)
                .variableName(CommonVariables.VARIABLE_PROCESS_USER_INITIATOR)
                .singleResult();


        User startUser;
        if (processInitiator != null){
            startUser = (User) processInitiator.getValue();

        }else{
            startUser = new User();
        }
        Date startTime = historicProcessInstance.getStartTime();
        BaseProcessReq body = new BaseProcessReq();
        body.setProcessId(processInstanceId);
        body.setCurrentUser(startUser);

        HistoricVariableInstance initialComment = variableQuery.processInstanceId(processInstanceId)
                        .variableName(CommonVariables.VARIABLE_INITIAL_COMMENT).singleResult();
        body.setComment(initialComment != null ? (String) initialComment.getValue() : null);

        Map<String, Object> historyRecord = processHistoryService.createTaskHistory(
                CommonVariables.TASK_TYPE_START,
                body,
                CommonVariables.OUTCOME_START_PROCESS);
        historyRecord.put("createAt",startTime);
        historyRecord.put("completeAt",startTime);
        return historyRecord;
    }


    private HistoryMetaData getMetaData(List<Map<String,Object>> history,String processInstanceId){
        HistoryMetaData historyMetaData = new HistoryMetaData();
        historyMetaData.setTitle("Title from Controller");
        historyMetaData.setFinished("<W Trakcie>");
        historyMetaData.setInitiatedBy((String) history.get(0).get("completeBy"));
        historyMetaData.setStarted( ((Date)history.get(0).get("createAt")).getTime());
        historyMetaData.setWorkflowId(processInstanceId);
        return historyMetaData;
    }



    private   List<Map<String,Object>> getActiveData(String processId,TaskService taskService) {
        List<Task> activeTasksInstances = taskService.createTaskQuery().processInstanceId(processId)
                .list();
        List<Map<String, Object>> history = new ArrayList<>();

        for (Task task : activeTasksInstances) {
            BaseCompleteTaskReq req = new BaseCompleteTaskReq();
            req.setTaskId(task.getId());
            req.setTaskKey(task.getTaskDefinitionKey());
            req.setResult("");
            List<Comment> comments = taskService.getTaskComments(task.getId());
            if (!comments.isEmpty()) {
                req.setComment(comments.get(0).getFullMessage());
            } else {
                req.setComment("");
            }
            req.setTaskId(task.getId());
            User user = new User();
            String activeAssignee = task.getAssignee();
            if (activeAssignee == null || activeAssignee.isEmpty() || activeAssignee.isBlank()){
                user = getIdentityLinkName(taskService, task.getId());
            } else {
                user.setUsername(activeAssignee);
            }

            if (user.getFullName() == null) {
                List<Map<String, String>> userToAssign = taskHistoryDao.getFullNameByUsername(user.getUsername());
                if (!userToAssign.isEmpty()) {
                    user.setFullName(userToAssign.get(0).get("firstname") + " " + userToAssign.get(0).get("lastname"));
                }
            }

            req.setCurrentUser(user);
            Map<String, Object> historyRecord = processHistoryService.createTaskHistory(task.getTaskDefinitionKey(), req, req.getResult());
            try {
                historyRecord.put("createAt", parseDate(task.getCreateTime()));
                historyRecord.put("completeAt", null);
            } catch (Exception e) {
                log.error("Couldn't parse date");
            }
            history.add(historyRecord);
        }
        return history;
    }
    private   List<Map<String,Object>> getHistoricalData(HistoryService historyService,HistoricProcessInstance historicProcessInstance,TaskService taskService) {
        List<Map<String, Object>> history = new ArrayList<>();
        // Retrieve historic activity instances
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .list();
        for (HistoricTaskInstance task : historicTaskInstances) {
            if (task.getEndTime() == null) {
                continue;
            }
            BaseCompleteTaskReq req = new BaseCompleteTaskReq();
            req.setTaskId(task.getId());
            req.setTaskKey(task.getTaskDefinitionKey());
            List<HistoricVariableInstance> outcomes = historyService.createHistoricVariableInstanceQuery().taskIdIn(task.getId()).variableName("outcome").list();
            String outcome = !outcomes.isEmpty() ? outcomes.get(0).getValue().toString() : "";
            req.setResult(outcome);
            List<Comment> comments = taskService.getTaskComments(task.getId());
            if (!comments.isEmpty()) {
                req.setComment(comments.get(0).getFullMessage());
            } else {
                req.setComment("");
            }
            req.setTaskId(task.getId());
            User user = new User();
            String activeAssignee = task.getAssignee();
            user.setUsername(activeAssignee);
            List<Map<String, String>> userToAssign = taskHistoryDao.getFullNameByUsername(activeAssignee);
            if (!userToAssign.isEmpty()) {
                user.setFullName(userToAssign.get(0).get("firstname") + " " + userToAssign.get(0).get("lastname"));
            }
            req.setCurrentUser(user);
            Map<String, Object> historyRecord = processHistoryService.createTaskHistory(task.getTaskDefinitionKey(), req, req.getResult());
            try {
                historyRecord.put("createAt", parseDate(task.getStartTime()));
                historyRecord.put("completeAt", parseDate(task.getEndTime()));
            } catch (Exception e) {
                log.error("Couldn't parse date");
            }
            history.add(historyRecord);
        }
        return history;
    }


    private List<HistoricProcessInstance> getAllProcessInstancesIncludingSubprocesses(String rootProcInstanceId, HistoryService historyService) {
        List<HistoricProcessInstance> allInstances = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(rootProcInstanceId);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            HistoricProcessInstance currentInstance = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(currentId)
                    .singleResult();

            if (currentInstance != null) {
                allInstances.add(currentInstance);

                // Find subprocesses started by this instance
                List<HistoricProcessInstance> subprocesses = historyService
                        .createHistoricProcessInstanceQuery()
                        .superProcessInstanceId(currentId)
                        .list();

                for (HistoricProcessInstance subprocess : subprocesses) {
                    queue.add(subprocess.getId());
                }
            }
        }

        return allInstances;
    }
}
