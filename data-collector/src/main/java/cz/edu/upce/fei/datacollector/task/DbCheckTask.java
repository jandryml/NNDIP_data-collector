package cz.edu.upce.fei.datacollector.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DbCheckTask {

	private final HealthIndicator healthIndicator;

	@Scheduled(fixedRateString = "${db.status.checkRate:60000}")
	public void checkDBHealth() {
		final Status status = healthIndicator.health().getStatus();
		log.trace("Checking db state.");
		if (!Status.UP.equals(status)) {
			log.error("DATABASE IS OFFLINE! SHUTTING DOWN!");
			System.exit(1);
		}
	}
}
