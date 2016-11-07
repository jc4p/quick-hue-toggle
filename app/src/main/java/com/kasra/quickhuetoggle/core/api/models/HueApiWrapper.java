package com.kasra.quickhuetoggle.core.api.models;


public abstract class HueApiWrapper {
    private HueError error;

    public boolean isSuccess() {
        return error == null;
    }

    public abstract Object get();

    public String getErrrorMessage() {
        return error.description;
    }

    public class HueError {
        private int type;
        private String address;
        private String description;

        public String getDescription() {
            return description;
        }

        public String getAddress() {
            return address;
        }

        public int getType() {
            return type;
        }

    }
}
