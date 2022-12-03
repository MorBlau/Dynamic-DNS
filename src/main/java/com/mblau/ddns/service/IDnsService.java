package com.mblau.ddns.service;

public interface IDnsService {
    void notifyIpChanged(String ip) throws Exception;
}
