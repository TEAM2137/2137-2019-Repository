package org.torc.robot2019.libraries;

public abstract class Constants {
    public enum stepState {
        STATE_INIT ("STATE_INIT"),
        STATE_START ("STATE_START"),
        STATE_RUNNING ("STATE_RUNNING"),
        STATE_PAUSE ("STATE_PAUSE"),
        STATE_COMPLETE ("STATE_COMPLETE"),
        STATE_TIMEOUT ("STATE_TIMEOUT"),
        STATE_ERROR ("STATE_ERROR"),
        STATE_FINISHED ("STATE_FINISHED");

        private final String name;

        stepState (String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }
}
