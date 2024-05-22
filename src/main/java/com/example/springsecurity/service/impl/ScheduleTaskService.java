package com.example.springsecurity.service.impl;

import com.example.springsecurity.ws.KeycloakCaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ScheduleTaskService {
    private final KeycloakCaller keycloakCaller;

    @Scheduled(fixedRateString = "${keycloak.admin.token-ttl}", initialDelay = 2000)
    public void getAdminToken() {
        log.debug("... start get admin token ...");
        String adminToken = keycloakCaller.getToken(
                keycloakCaller.getAdminUsername(),
                keycloakCaller.getAdminPassword()
        ).get(0);
        log.debug("... successfully get admin token ...");
        keycloakCaller.setAdminToken(adminToken);
    }
}