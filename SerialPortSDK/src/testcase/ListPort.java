package testcase;

import serialportutil.AbstractSerialCommand;

public class ListPort extends AbstractSerialCommand {
    public static void main(String[] args) {
        System.out.println(listPorts());
    }
}
