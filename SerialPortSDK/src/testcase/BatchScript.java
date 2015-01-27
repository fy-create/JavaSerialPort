package testcase;

import serialportutil.CommandBatch;


public class BatchScript {
    public static void main(String[] args) {
        CommandBatch.addClazz(SimpleScript.class);
        CommandBatch.goScript();
    }
}
