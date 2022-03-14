package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.model.LimitValue;
import cz.edu.upce.fei.datacollector.model.RegisterType;
import cz.edu.upce.fei.datacollector.model.ThresholdAction;
import cz.edu.upce.fei.datacollector.service.ModbusCommService;
import de.re.easymodbus.modbusclient.ModbusClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ModbusCommServiceImpl implements ModbusCommService {
    // TODO use values provided in application yaml
    ModbusClient modbusClient = new ModbusClient("127.0.0.1", 502);

    public void handleLimit(LimitValue limitValue) {
        limitValue.getActionList().forEach(action -> {
            if (RegisterType.INTEGER.equals(action.getRegisterType())) {
                writeToIntegerRegister(action);
            } else {
                writeToBooleanRegister(action);
            }
        });
    }

    private void writeToIntegerRegister(ThresholdAction thresholdAction) {
        try {
            int pin = Integer.parseInt(thresholdAction.getPin());
            int value = Integer.parseInt(thresholdAction.getValue());

            log.info("Writing to pin {} value {}", pin, value);

            modbusClient.Connect();
            modbusClient.WriteSingleRegister(pin, value);
            modbusClient.Disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeToBooleanRegister(ThresholdAction thresholdAction) {
        try {
            int pin = Integer.parseInt(thresholdAction.getPin());
            boolean value = !"0".equals(thresholdAction.getValue());

            log.info("Writing to pin {} value {}", pin, value);

            modbusClient.Connect();
            modbusClient.WriteSingleCoil(pin, value);
            modbusClient.Disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
