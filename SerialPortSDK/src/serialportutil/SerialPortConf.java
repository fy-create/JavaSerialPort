package serialportutil;

public class SerialPortConf /* implements IPort */{
    private String portname;
    private int speed;
    private int databit;
    private int stopbit;
    private int parityBit;

    public SerialPortConf(String portname, int speed, int databit, int stopbit, int parityBit) {
        super();
        this.databit = databit;
        this.parityBit = parityBit;
        this.portname = portname;
        this.speed = speed;
        this.stopbit = stopbit;
    }

    public String  getPortname() {
        return portname;
    }

    public int  getSpeed() {
        return speed;
    }

    public int  getDatabit() {
        return databit;
    }

    public int  getStopbit() {
        return stopbit;
    }

    public int  getParityBit() {
        return parityBit;
    }

    @Override
    public String toString() {
        return "Portname:" + getPortname() + " Speed:" + getSpeed() + " Databit:" + getDatabit() + " Stopbit:" + getStopbit()
                + " ParityBit:" + getParityBit();
    }
}
