package testcase;

import serialportutil.SerialPortConf;

public class SerialFactory {
    public static SerialPortConf COM1 = new SerialPortConf("COM1", 115200, 8, 1, 0);

    public static SerialPortConf COM15 = new SerialPortConf("COM15", 115200, 8, 1, 0);

    public static SerialPortConf ttyUSB0 = new SerialPortConf("/dev/ttyUSB0", 115200, 8, 1, 0);

    public static SerialPortConf DEFAULT_PORT = COM15;

}
