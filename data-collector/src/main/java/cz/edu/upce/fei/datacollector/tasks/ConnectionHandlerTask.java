package cz.edu.upce.fei.datacollector.tasks;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;


public class ConnectionHandlerTask implements Runnable {
    private static final Logger logger = LogManager.getLogger();

    private static final int THREAD_SLEEP = 15 * 1000;
    private static final Set<String> handledPorts = new HashSet<>();

    @Override
    public void run() {
        try {
            while (true) {
                logger.debug("Searching for new connections!");

                SerialPort[] actualPorts = SerialPort.getCommPorts();

                for (SerialPort port : actualPorts) {
                    //TODO make "USB" configurable?
                    if (handledPorts.contains(port.getPortLocation())
                            || !port.getDescriptivePortName().contains("USB")) {
                        continue;
                    }

                    logger.info(String.format("New connection found (%s)! Connecting on: %s\n"
                            , port.getDescriptivePortName(), port.getPortLocation()));

                    port.openPort();
                    registerPortListener(port);
                    handledPorts.add(port.getPortLocation());
                }

                Thread.sleep(THREAD_SLEEP);
            }
        } catch (InterruptedException e) {
            logger.error("Error in Connection handler thread!", e);
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
                    //TODO work with data properly
                    logger.info("Read: " + new String(newData).replace("\0", ""));
                } else if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
                    logger.info("Disconnected port: " + port.getPortLocation());
                    handledPorts.remove(port.getPortLocation());
                    port.closePort();
                }
            }
        });
    }
}
