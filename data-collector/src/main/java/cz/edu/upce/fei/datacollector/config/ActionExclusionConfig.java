package cz.edu.upce.fei.datacollector.config;

import cz.edu.upce.fei.datacollector.model.ExclusionRule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "exclusion")
public class ActionExclusionConfig {
    private List<ExclusionRule> actionExclusionRules = new ArrayList<>();
}

