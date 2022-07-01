package cz.edu.upce.fei.datacollector.service.impl;

import cz.edu.upce.fei.datacollector.model.Action;
import cz.edu.upce.fei.datacollector.model.ActionOutput;
import cz.edu.upce.fei.datacollector.repository.AddressStateRepository;
import cz.edu.upce.fei.datacollector.service.CommService;
import cz.edu.upce.fei.datacollector.service.communication.ModbusCommService;
import cz.edu.upce.fei.datacollector.service.communication.RaspberryPiCommService;
import cz.edu.upce.fei.datacollector.service.impl.DataReactionServiceImpl.VerboseAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommServiceImpl implements CommService {

    private final ModbusCommService modbusCommService;
    private final RaspberryPiCommService rPiCommService;
    private final AddressStateRepository addressStateRepository;

    @Override
    public void writeToExternalDevices(List<VerboseAction> valuesList) {
        log.debug("Writing result actions list to external devices");
        Map<ActionOutput, String> actualAddressState = getActualAddressState();
        valuesList.forEach(verboseAction -> {
            ActionOutput actionOutput = new ActionOutput(verboseAction.getAction().getAddress(), verboseAction.getAction().getOutputType());
            if (!Objects.equals(actualAddressState.get(actionOutput), verboseAction.getAction().getValue())) {

                log.debug("Change in registers detected type '{}' address '{}': Previous value '{}', new value '{}'",
                        verboseAction.getAction().getOutputType(), verboseAction.getAction().getAddress(),
                        actualAddressState.get(actionOutput), verboseAction.getAction().getValue());

                writeAction(verboseAction.getAction());
            }
        });

        // write to registers
        log.trace("Writing register state to database");
        writeToRegisters(valuesList);
        log.trace("Writing to external devices: Finished");
    }

    private void writeAction(Action action) {
        switch (action.getOutputType()) {

            case MODBUS_VALUE: {
                modbusCommService.writeToRegister(action);
                break;
            }
            case MODBUS_BOOLEAN: {
                modbusCommService.writeToCoil(action);
                break;
            }
            case RASPBERRY_PIN: {
                rPiCommService.writeValue(action);
                break;
            }
        }
    }

    private Map<ActionOutput, String> getActualAddressState() {
        return addressStateRepository.getAllAddressStates().stream()
                .collect(Collectors.toMap(
                        value -> new ActionOutput(value.getAddress(), value.getOutputType()),
                        Action::getValue));
    }

    private void writeToRegisters(List<VerboseAction> resultActions) {
        addressStateRepository.removeAllAddressStates();
        addressStateRepository.setAddressStates(resultActions);
    }
}
