package io.landolfi.device;

public enum DeviceState {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    LOST("LOST");

    public final String state;

    DeviceState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}
