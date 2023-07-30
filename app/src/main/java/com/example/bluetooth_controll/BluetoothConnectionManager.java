package com.example.bluetooth_controll;

import java.io.OutputStream;

public class BluetoothConnectionManager {
    private static final BluetoothConnectionManager instance = new BluetoothConnectionManager();

    private OutputStream outputStream;

    private BluetoothConnectionManager() {
        // Private constructor to prevent external instantiation
    }

    public static BluetoothConnectionManager getInstance() {
        return instance;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
