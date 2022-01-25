package cz.edu.upce.fei.datacollector.tasks;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.*;

public class ConnectionHandlerTask implements Runnable {
    private static final int THREAD_SLEEP = 15 * 1000;
    private static final Set<String> handledPorts = new HashSet<>();

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Searching for new connection!");

                SerialPort[] actualPorts = SerialPort.getCommPorts();

                for (SerialPort port : actualPorts) {
                    //TODO make "USB" configurable?
                    if (handledPorts.contains(port.getPortLocation())
                            || !port.getDescriptivePortName().contains("USB")) {
                        continue;
                    }

                    System.out.println("New connection! Connecting on: " + port.getPortLocation());

                    port.openPort();
                    registerPortListener(port);
                    handledPorts.add(port.getPortLocation());
                }

                Thread.sleep(THREAD_SLEEP);
            }
        } catch (InterruptedException e) {
            System.out.println("Interup");
            e.printStackTrace();
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
                    System.out.print("Read: " + new String(newData).replace("\0", ""));
                } else if (event.getEventType() == SerialPort.LISTENING_EVENT_PORT_DISCONNECTED) {
                    System.out.println("Disconnected port: " + port.getPortLocation());
                    handledPorts.remove(port.getPortLocation());
                    port.closePort();
                }
            }
        });
    }
}
