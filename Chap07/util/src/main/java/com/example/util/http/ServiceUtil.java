package com.example.util.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.InetAddress;

/**
 * service 의 호스트 이름과 아이피등을 찾아내는 Util Class -> Spring Bean
 */
@Component @Slf4j
public class ServiceUtil {
    private final String port;
    private String serviceAddress = null;

    /**
     * 호출 서버의 YAML 파일에 기재된 서버포트를 가져올 생각, 아직 Service Discovery 를 미적용한 상태
     * @param port
     */
    @Autowired
    public ServiceUtil(
            @Value("${server.port}") String port) {
        this.port = port;
    }

    /**
     * 싱글톤 패턴과 유사, 메모리 누수를 막으려는 듯
     * @return
     */
    public String getServiceAddress() {
        if (serviceAddress == null) {
            serviceAddress = findMyHostname() + "/" + findMyIpAddress() + ":" + port;
        }
        return serviceAddress;
    }

    private String findMyIpAddress() {
        try {
            return InetAddress.getLoopbackAddress().getHostAddress();
        }catch (Exception e) {
            return "unknown host address";
        }
    }

    private String findMyHostname() {
        try {
            return InetAddress.getLoopbackAddress().getHostName();
        }catch (Exception e) {
            return "unknown host name";
        }
    }


}
