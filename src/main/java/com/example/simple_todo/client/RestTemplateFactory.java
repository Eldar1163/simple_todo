package com.example.simple_todo.client;

import com.example.simple_todo.config.ImageServerConfig;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean {
    private RestTemplate restTemplate;
    private final ImageServerConfig config;

    public RestTemplateFactory(ImageServerConfig config) {
        super();
        this.config = config;
    }

    @Override
    public RestTemplate getObject() {
        return restTemplate;
    }

    @Override
    public Class<RestTemplate> getObjectType() {
        return RestTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        HttpHost host = new HttpHost(config.getPath());
        final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactoryBasicAuth(host);
        restTemplate = new RestTemplate(requestFactory);
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(config.getUsername(), config.getPassword()));
    }

}