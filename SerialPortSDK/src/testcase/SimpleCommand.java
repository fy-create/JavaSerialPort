package testcase;

import serialportutil.AbstractSerialCommand;
import serialportutil.CommandBatch;

/**
 * Direct send command to serial<BR>
 * Using System.out as output
 * 
 */
public class SimpleCommand extends AbstractSerialCommand {
    @Override
    protected void setup() {
        // Setup serial
        setSerialPortConf(SerialFactory.DEFAULT_PORT);
        addOutputStream(System.out);
    }

    @Override
    protected void processCommand() throws Exception {
        // Send command "ps" to serial port,
        sendCommand(0x3, 100, 2);
        sendCommand("ps | grep sh\n", 100, 10);
    }

    public static void main(String[] args) {
        CommandBatch.addClazz(SimpleCommand.class);
        CommandBatch.go();
    }

}
