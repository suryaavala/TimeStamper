package com.sardox.timestamper.utils;


import com.sardox.timestamper.objects.Timestamp;

public class UserAction {
    private final ActionType actionType;
    private Timestamp timestamp;
    private int count = 0;

    public UserAction(ActionType actionType, Timestamp timestamp) {
        this.actionType = actionType;
        this.timestamp = timestamp;
    }

    public UserAction(ActionType actionType, Timestamp timestamp, int count) {
        this.count = count;
        this.actionType = actionType;
        this.timestamp = timestamp;
    }

    public int getCount() {
        return count;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
