package cz.edu.upce.fei.datacollector.modbus;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ModbusTest {

    @Test
    void connectionTCPTest() {
        // TODO extract to application properties
        ModbusClient modbusClient = new ModbusClient("127.0.0.1", 502);

        try {
            modbusClient.Connect();
            modbusClient.WriteSingleRegister(0, 420);
            modbusClient.WriteMultipleRegisters(1, ModbusClient.ConvertFloatToTwoRegisters(420.69f));
            modbusClient.WriteSingleCoil(0, true);

            modbusClient.ReadCoils(0,1);
            modbusClient.ReadHoldingRegisters(0,1);
            ModbusClient.ConvertRegistersToFloat(modbusClient.ReadHoldingRegisters(1,2));

            modbusClient.Disconnect();
        } catch (IOException | ModbusException e) {
            e.printStackTrace();
        }
    }

    @Test
    void connectionRTUTest() {
        ModbusClient modbusClient = new ModbusClient();
        try {
            modbusClient.Connect("COM12");
            modbusClient.WriteSingleCoil(1, true);
            modbusClient.WriteSingleRegister(1, 420);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
