package com.pombingsoft.planet_iot.model;

/**
 * Created by Admin on 2/9/2018.
 */

public class DeviceNodeMcu {
    private String DeviceName;
    private String DeviceId;
    private String DeviceType;
    private String Availability;

    public DeviceNodeMcu(String DeviceName,
                         String DeviceId, String Availability, String DeviceType
    ) {
        this.DeviceName = DeviceName;
        this.DeviceId = DeviceId;
        this.Availability = Availability;
        this.DeviceType = DeviceType;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public String getAvailability() {
        return Availability;
    }

    @Override
    public String toString() {
        return DeviceName;
    }
}

