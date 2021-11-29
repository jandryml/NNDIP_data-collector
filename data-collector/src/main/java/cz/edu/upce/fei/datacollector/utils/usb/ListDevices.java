package cz.edu.upce.fei.datacollector.utils.usb;

import org.usb4java.*;

/**
 * Lists all available USB devices.
 */
public class ListDevices {
    public static void listUsbDevices() {
        // Create the libusb context
        Context context = new Context();

        // Initialize the libusb context
        int result = LibUsb.init(context);
        if (result < 0) {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Read the USB device list
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);

        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and list them
            for (Device device : list) {
                int address = LibUsb.getDeviceAddress(device);
                int busNumber = LibUsb.getBusNumber(device);
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result < 0) {
                    throw new LibUsbException(
                            "Unable to read device descriptor", result);
                }
                System.out.format(
                        "Bus %03d, Device %03d: Vendor %04x, Product %04x%n",
                        busNumber, address, descriptor.idVendor(),
                        descriptor.idProduct());
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Deinitialize the libusb context
        LibUsb.exit(context);
    }
}
