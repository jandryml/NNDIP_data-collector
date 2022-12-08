package cz.edu.upce.fei.datacollector.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbCheckTask {

	private final DataSourceHealthIndicator healthIndicator;

	@Scheduled(fixedRate = 10000L)
	public void checkDBHealth() {
		final Status status = this.healthIndicator.health().getStatus();
		if (!Status.UP.equals(status)) {
			log.error("DATABASE IS OFFLINE! SHUTTING DOWN!");
			System.exit(1);
		}
	}
}
