package cz.edu.upce.fei.datacollector.task;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import cz.edu.upce.fei.datacollector.service.DataProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;


@Slf4j
@EnableAsync
@Component
@RequiredArgsConstructor
public class ConnectionHandlerTask {

    private final Set<String> handledPorts = new HashSet<>();

    private final DataProcessService dataProcessService;

    @PreDestroy
    public void preDestroy() {
        log.info("Trying to clear Connection service");
//        for (handledPorts)
//            handledPort.removeDataListener();
//            handledPort.close();
    }

    @Async
    @Scheduled(cron = "${connectionHandlingTask}")
    public void run() {
        log.debug("Searching for new connections!");

        SerialPort[] actualPorts = SerialPort.getCommPorts();

        for (SerialPort port : actualPorts) {
            // skip port if already has registered handler or is not USB
            //TODO make "USB" configurable?
            if (handledPorts.contains(port.getPortLocation()) || !port.getDescriptivePortName().contains("USB")) {
                continue;
            }

            log.info(String.format("New connection found (%s)! Connecting on: %s\n"
                    , port.getDescriptivePortName(), port.getPortLocation()));

            port.openPort();
            registerPortListener(port);
            handledPorts.add(port.getPortLocation());
        }
    }

    private void registerPortListener(SerialPort port) {
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE | SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    byte[] newData = new byte[port.bytesAvailable()];
                    port.readBytes(newData, newData.length);
                    dataProcessService.addData(newData);
                } else if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
                    log.info("Disconnected port: " + port.getPortLocation());
                    handledPorts.remove(port.getPortLocation());
                    port.closePort();
                }
            }
        });
    }
}
