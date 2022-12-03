package com.mblau.ddns.scheduling;

import com.mblau.ddns.service.DynamicDnsService;
import com.mblau.ddns.util.TimeUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class Scheduler {

    @Autowired
    private DynamicDnsService dynamicDnsService;

    @Scheduled(fixedDelayString = "${ddns.task.delay.milliseconds}")
    public void scheduleIpChangeCheck() {
        log.info("Task started: IP change check. Current time is {}", TimeUtil.prettyPrintCurrentTime());
        try {
            dynamicDnsService.execute();
        } catch (Exception e) {
            log.error("Caught exception on dynamic DNS service", e);
        }
    }
}
