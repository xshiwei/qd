package com.qvd.smartswitch.model.device;

import java.util.List;

public class UpdateDeviceRoomVo {

    /**
     * room_id : R1165081998
     * device_id : ["a9cc31c3392a4b059cdc71cd4e223961","5ca483d1145414dd10ccff5c6d41be0c","03085447cdb05562c5585b5d50d36494"]
     */

    private String room_id;
    private List<String> device_id;

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public List<String> getDevice_id() {
        return device_id;
    }

    public void setDevice_id(List<String> device_id) {
        this.device_id = device_id;
    }
}
