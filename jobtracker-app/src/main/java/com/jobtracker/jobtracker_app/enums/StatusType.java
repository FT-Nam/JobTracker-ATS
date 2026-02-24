package com.jobtracker.jobtracker_app.enums;

import lombok.Getter;

@Getter
public enum StatusType {

    APPLIED(1),
    SCREENING(2),
    INTERVIEW(3),
    OFFER(4),
    HIRED(5),
    REJECTED(99);

    private final int order;

    StatusType(int order) {
        this.order = order;
    }

    public boolean isTerminal() {
        return this == HIRED || this == REJECTED;
    }

    public boolean canMoveTo(StatusType target) {
        if (this.isTerminal()) {
            return false;
        }

        // Cho phép từ bất cứ status type nào cũng có thể chuyển về REJECTED
        if (target == REJECTED) {
            return true;
        }

        return target.getOrder() >= this.order;
    }
}

