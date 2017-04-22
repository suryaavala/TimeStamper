package com.sardox.timestamper.utils;


import com.sardox.timestamper.objects.Timestamp;

public class UserAction {
    private final ActionType actionType;
    private Timestamp timestamp;

    public UserAction(ActionType actionType, Timestamp timestamp) {
        this.actionType = actionType;
        this.timestamp = timestamp;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

}
