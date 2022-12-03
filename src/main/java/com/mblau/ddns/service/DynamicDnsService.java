package com.mblau.ddns.service;

import com.mblau.ddns.dto.Result;
import com.mblau.ddns.util.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Log4j2
@Service
public class DynamicDnsService {

    @Value("${ddns.ip.filename}")
    private String fileName;
    @Value("${ddns.ipresolver.url}")
    private String ipResolverUrl;
    @Autowired
    private HttpService httpService;
    @Autowired
    private IDnsService dnsService;

    public void execute() throws Exception {
        log.info("Running dynamic DNS service");

        log.info("Calling IP resolver");
        Result result = httpService.getCall(ipResolverUrl);

        if (result.error() != null || result.responseCode() >= 400) {
            handleError(result);
            return;
        }

        String ip = result.result();
        log.info("Comparing resolved IP [{}] with current IP", ip);
        boolean changed = hasIpChanged(result.result());
        if (changed) {
            handleIpChange(ip);
        } else {
            log.info("IP unchanged, nothing to do");
        }
    }

    private void handleIpChange(String ip) throws Exception {
        dnsService.notifyIpChanged(ip);
        log.info("Saving IP [{}] to file", ip);
        FileUtil.saveToFile(ip, fileName);
    }

    private void handleError(Result result) throws Exception {
        throw new Exception("caught error. Http code: " + result.responseCode() +
                ". Error message: " + result.error());
    }

    private boolean hasIpChanged(@NonNull String newIp) throws IOException {
        if (!FileUtil.fileExists(fileName)) {
            log.info("Current IP file does not exist");
            return true;
        }

        String currentIp = getCurrentIp();
        return !currentIp.equals(newIp);
    }

    private String getCurrentIp() throws IOException {
        return FileUtil.getStringFromFile(fileName);
    }
}
