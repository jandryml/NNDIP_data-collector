package cz.edu.upce.fei.datacollector;

import static cz.edu.upce.fei.datacollector.utils.usb.ListDevices.listUsbDevices;

public class Main {
    public static void main(String[] args) {
        listUsbDevices();
    }
}
