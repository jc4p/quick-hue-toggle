package com.kasra.quickhuetoggle.core.services;

import com.kasra.quickhuetoggle.core.api.models.CreateUserResponse;

import java.util.HashMap;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface HueApiService {
    @POST("/api")
    Observable<List<CreateUserResponse>> createUser(@Body HashMap<String, String> body);

}
