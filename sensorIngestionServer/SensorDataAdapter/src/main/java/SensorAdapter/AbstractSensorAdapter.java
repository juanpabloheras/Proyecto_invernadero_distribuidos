package SensorAdapter;

public abstract class AbstractSensorAdapter implements ISensorAdapter {

    protected int calcularChecksum(byte[] data, int len) {
        int sum = 0;
        for (int i = 0; i < len; i++) {
            sum += (data[i] & 0xFF);
        }
        return sum & 0xFFFF;
    }
}
