package testcase;

import serialportutil.CommandBatch;

public class SimpleBatch {
    public static void main(String[] args) {
        CommandBatch.addClazz(SimpleCommand.class);
        CommandBatch.addClazz(ConditionCheck.class);
        CommandBatch.go();
    }
}
