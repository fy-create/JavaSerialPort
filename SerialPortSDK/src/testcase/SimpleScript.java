package testcase;

import serialportutil.AbstractSerialCommand;
import serialportutil.CommandBatch;

public class SimpleScript extends AbstractSerialCommand {
    @Override
    protected void setup() {
        // Setup serial
        setSerialPortConf(SerialFactory.DEFAULT_PORT);

        // Setup OutputStream
        addOutputStream(System.out);

        scriptFile = "./script/SimpleScript.js";
    }

    public static void main(String[] args) {
        CommandBatch.addClazz(SimpleScript.class);
        CommandBatch.goScript();
    }

}
