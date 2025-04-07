package com.daniil.otp.smpp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "smpp")
public class SmppProperties {

    private String host;
    private int port;
    private String systemId;
    private String password;
    private String systemType;
    private String sourceAddr;
    private int addrTon;
    private int addrNpi;

}
