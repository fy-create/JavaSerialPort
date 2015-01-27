package testcase;

import serialportutil.AbstractSerialCommand;
import serialportutil.CommandBatch;

/**
 * Simple console sample<BR>
 * System console as input and output
 * 
 */
public class SimpleConsole extends AbstractSerialCommand {
    @Override
    protected void setup() {
        // Setup serial
        setSerialPortConf(SerialFactory.DEFAULT_PORT);

        // Set output with time stamp
        // setSerialPortTimePrefix(true);

        // setSerialPortCRAsLFCR(true);

        // System out as output
        addOutputStream(System.out);
    }

    @Override
    protected void processCommand() throws Exception {
        // Add console input support
        addConsoleInputSupport();
    }

    public static void main(String[] args) {
        CommandBatch.addClazz(SimpleConsole.class);
        CommandBatch.go();
    }
}
