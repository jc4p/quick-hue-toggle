package com.kasra.quickhuetoggle.core.api.models;

public class CreateUserResponse extends HueApiWrapper {
    public SuccessResponse success;

    public String get() {
        return success.username;
    }


    public class SuccessResponse {
        public String username;
    }
}
