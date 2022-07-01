package cz.edu.upce.fei.datacollector.service.communication;

import cz.edu.upce.fei.datacollector.model.Action;

/**
 * Handle communication to Modbus client.
 */
public interface ModbusCommService {

    /**
     * Writes value to coil of configured Modbus client.
     * All necessary data (address and value) are derived from provided action param.
     */
    void writeToCoil(Action action);

    /**
     * Writes value to register of configured Modbus client.
     * All necessary data (address and value) are derived from provided action param.
     */
    void writeToRegister(Action action);
}
