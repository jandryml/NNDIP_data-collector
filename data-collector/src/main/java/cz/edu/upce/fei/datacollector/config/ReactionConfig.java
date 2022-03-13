package cz.edu.upce.fei.datacollector.config;

import cz.edu.upce.fei.datacollector.model.LimitValuesConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "reaction")
// TODO rename to ReactionDefaultConfig
public class ReactionConfig {
    private LimitValuesConfig defaultLimits;
}

