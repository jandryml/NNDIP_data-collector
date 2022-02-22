package cz.edu.upce.fei.datacollector.repository.impl;

import cz.edu.upce.fei.datacollector.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigRepositoryImpl implements ConfigRepository {

    private final JdbcTemplate jdbcTemplate;


}
