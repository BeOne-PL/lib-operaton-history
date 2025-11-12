package pl.beone.operaton.operaton_history.variables;

public interface CommonVariables {
    String NODE_ID = "nodeId";
    String VARIABLE_PROCESS_NODE_ID = "PROCESS_NODE_ID";
    String PROCESS_DEFINITION_KEY = "base";
    String PROCESS_APP_NAME = "BaseProcess";
    String START_BASE_PROCESS_CONNECTOR_CHANNEL = "startBaseProcessConnectorChannel";
    String COMPLETE_BASE_TASK_CONNECTOR_CHANNEL = "completeBaseTaskConnectorChannel";
    String START_BASE_PROCESS_CONNECTOR_QUEUE = "startBaseProcessConnectorQueue";
    String COMPLETE_BASE_TASK_CONNECTOR_QUEUE = "completeBaseTaskServiceConnectorQueue";
    String VARIABLE_HISTORY = "HISTORY";
    String VARIABLE_PROCESS_USER_INITIATOR = "processUserInitiator";
    String VARIABLE_INITIAL_COMMENT = "initialComment";
    String TASK_TYPE_START = "start";
    String VARIABLE_ACCEPTANCE_USER = "acceptanceUser";
    String VARIABLE_ACCEPTANCE_LEVELS_LIST = "acceptanceLevelsList";
    String VARIABLE_ACCEPTED_BY = "acceptedBy";
    String VARIABLE_ACCEPTANCE_LEVEL_COMPLETION_CONDITION = "acceptanceLevelCompletionCondition";
    String VARIABLE_TASK_RESULT = "taskResult";
    String VARIABLE_ACCEPT_RESULT = "acceptResult";
    String VARIABLE_ACCEPT_RESULT_NONE = "none";
    String VARIABLE_TASK_RESULT_NONE = "none";
    String VARIABLE_TASK_RESULT_DONE = "done";
    String VARIABLE_TASK_ACCEPTED_BY_LEVEL = "acceptedByLevel";
    String VARIABLE_TASK_REJECTED_TO_REGISTRATION = "rejectedToRegistration";
    String VARIABLE_TASK_REJECTED_TO_EPROCUREMENT = "rejectedToEProcurement";
    String VARIABLE_TASK_REJECTED_TO_DESCRIPTION = "rejectedToDescription";
    String VARIABLE_TASK_DELETION = "taskDeleted";
    String OUTCOME_START_PROCESS = "START_PROCESS";
    String OUTCOME_SAVE = "SAVE";
    String OUTCOME_SAVE_AND_COMPLETE_TASK = "SAVE_AND_COMPLETE_TASK";
    String OUTCOME_SAVE_AND_COMPLETE_TASK_BATCH = "SAVE_AND_COMPLETE_TASK_BATCH";
    String OUTCOME_REJECT_TO_REGISTRATION = "REJECT_TO_REGISTRATION_AND_COMPLETE_TASK";
    String OUTCOME_REJECT_TO_EPROCUREMENT = "REJECT_TO_EPROCUREMENT_AND_COMPLETE_TASK";

    String OUTCOME_REJECT_TO_DESCRIPTION = "REJECT_TO_DESCRIPTION_AND_COMPLETE_TASK";
    String OUTCOME_TASK_DELETION = "TASK_DELETION";
    String OUTCOME_REJECT = "REJECT";
    String TASK_REDIRECT_USER = "redirectUser";
    String TASK_COMMENT = "taskComment";
}
