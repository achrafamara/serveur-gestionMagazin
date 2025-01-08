package com.modify.microservicecommandes.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("mes-configs")
@RefreshScope
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationpropertiesConfiguration {
    private int LastCommandes;

}
