package com.example.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 모든 MS 의 ServiceAddress 정보를 통합하는 클래스
 */
@AllArgsConstructor @Getter
public class ServiceAddresses {
    private final String cmp;
    private final String pro;
    private final String rev;
    private final String rec;

    public ServiceAddresses() {
        this.cmp = null;
        this.pro = null;
        this.rev = null;
        this.rec = null;
    }
}
