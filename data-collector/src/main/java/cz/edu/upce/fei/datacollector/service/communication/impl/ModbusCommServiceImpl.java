package cz.edu.upce.fei.datacollector.service.communication.impl;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.service.communication.ModbusCommService;
import de.re.easymodbus.modbusclient.ModbusClient;
import gnu.io.CommPortIdentifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Enumeration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModbusCommServiceImpl implements ModbusCommService {

    @Value("${modbus.usbPort}")
    private String configComPort;

    @Value("${modbus.targetId:1}")
    private byte targetId;

    @Override
    public boolean writeToCoil(Action action) {
        log.trace("Writing to Modbus coil");
        boolean result = false;
        try {
            String comPort = getComPort();
            log.trace("Connecting to modbus client");
            ModbusClient modbusClient = connectModbusClient(comPort);

            log.debug("Sending data to: " + comPort);

            modbusClient.WriteSingleCoil(Integer.parseInt(action.getAddress()), Integer.parseInt(action.getValue()) != 0);

            log.debug("Writing to comPort '{}', unitId '{}', coil address '{}', value '{}': Success!",
                    comPort, targetId, action.getAddress(), action.getValue());

            modbusClient.Disconnect();
            result = true;
        } catch (Exception e) {
            log.error("Error during writing to modbus: {}", e.getMessage());
        }
        log.trace("Writing to Modbus coil: finished");
        return result;
    }

    @Override
    public boolean writeToRegister(Action action) {
        log.trace("Writing to Modbus register");
        boolean result = false;
        try {
            String comPort = getComPort();
            log.trace("Connecting to modbus client");
            ModbusClient modbusClient = connectModbusClient(comPort);

            log.debug("Sending data to: " + comPort);

            modbusClient.WriteSingleRegister(Integer.parseInt(action.getAddress()), Integer.parseInt(action.getValue()));

            log.debug("Writing to comPort '{}', unitId '{}', register address '{}', value '{}': Sucess! ",
                    comPort, targetId, action.getAddress(), action.getValue());

            modbusClient.Disconnect();
            result = true;
        } catch (Exception e) {
            log.error("Error during writing to modbus: {}", e.getMessage());
        }
        log.trace("Writing to Modbus register: Finished");
        return result;
    }

    private ModbusClient connectModbusClient(String comPort) throws Exception {
        if (comPort.isEmpty()) {
            throw new Exception("Com port is not defined, nor port was found by autodetect!");
        }

        ModbusClient modbusClient = new ModbusClient();
        modbusClient.setUnitIdentifier(targetId);
        modbusClient.Connect(comPort);

        return modbusClient;
    }

    private String getComPort() {
        if (!configComPort.isEmpty()) {
            return configComPort;
        } else {
            log.debug("Com port is not defined, running autodetect");
            return autodetectPort();
        }
    }

    private String autodetectPort() {
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();

            // sends only if the port is either RS485 or Serial
            if (CommPortIdentifier.PORT_RS485 == port.getPortType()
                    || CommPortIdentifier.PORT_SERIAL == port.getPortType()) {
                return port.getName();
            }
        }
        return "";
    }
}
