package com.wotos.wotosvehicleservice.client.wot.extra;

public class WotArmorValues {

    private final Integer front;
    private final Integer sides;
    private final Integer rear;

    public WotArmorValues(Integer front, Integer sides, Integer rear) {
        this.front = front;
        this.sides = sides;
        this.rear = rear;
    }

    public Integer getFront() {
        return front;
    }

    public Integer getSides() {
        return sides;
    }

    public Integer getRear() {
        return rear;
    }
}
