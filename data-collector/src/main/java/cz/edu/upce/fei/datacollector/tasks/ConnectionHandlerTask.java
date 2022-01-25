package cz.edu.upce.fei.datacollector.tasks;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.*;

public class ConnectionHandlerTask implements Runnable {
    private static final int THREAD_SLEEP = 15 * 1000;
    private static final Map<String, SerialPort> handledPorts = new HashMap<>();

    @Override
    public void run()  {
        try {
            while (true) {
                List<SerialPort> actualPorts = Arrays.asList(SerialPort.getCommPorts());

                System.out.println("Searching for new connection!");

                for (SerialPort port : actualPorts) {
                    if (handledPorts.containsKey(port.getDescriptivePortName())
                            || port.getDescriptivePortName().equals("User-Specified Port")) {
                        continue;
                    }
                    System.out.println("New connection! Connecting on: " + port.getDescriptivePortName());

                    port.openPort();
                    registerPortListener(port);

                    handledPorts.put(port.getDescriptivePortName(), port);
                }

                // removal/close of disconnected listeners
                // (needed to correctly terminate handling thread)
                if (!handledPorts.isEmpty()) {
                    System.out.println("Checking disconnected ports.");
                    disconnectedPortRemoval(actualPorts);
                }

                Thread.sleep(THREAD_SLEEP);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void disconnectedPortRemoval(List<SerialPort> actualPorts) {
        Iterator<SerialPort> iterator = handledPorts.values().iterator();
        while (iterator.hasNext()) {
            SerialPort port = iterator.next();
            if (actualPorts.stream().noneMatch(it -> it.getDescriptivePortName().equals(port.getDescriptivePortName()))) {
                System.out.println("Removing port on: " + port.getDescriptivePortName());
                port.closePort();
                iterator.remove();
            }
        }
    }

    private void registerPortListener(SerialPort port) {
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[port.bytesAvailable()];
                System.out.print("Read: " + new String(newData).replace("\0", ""));
            }
        });
    }
}
