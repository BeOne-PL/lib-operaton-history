package pl.beone.operaton.operaton_history.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.beone.operaton.operaton_history.service.AbstractProcessHistoryService;

@Slf4j
@Service
@ConditionalOnMissingBean(name = "processHistoryService")
@Primary
public class ProcessHistoryServiceImpl extends AbstractProcessHistoryService {
    public ProcessHistoryServiceImpl() {
        log.warn("Using default implementation of ProcessHistoryService.");
    }
}
