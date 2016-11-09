package com.kasra.quickhuetoggle.core.services;

import com.kasra.quickhuetoggle.core.api.models.AllLightsResponse;
import com.kasra.quickhuetoggle.core.api.models.BridgeConfigResponse;
import com.kasra.quickhuetoggle.core.api.models.CreateUserResponse;

import java.util.HashMap;
import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

public interface HueApiService {
    @POST("/api")
    Observable<List<CreateUserResponse>> createUser(@Body HashMap<String, String> body);

    @GET("/api/{username}/config")
    Observable<BridgeConfigResponse> getConfig(@Path("username") String username);

    @GET("/api/{username}/lights")
    Observable<AllLightsResponse> getLights(@Path("username") String username);

    @PUT("/api/{username}/lights/{lightId}/state")
    Observable<Void> setBrightness(@Path("lightId") String lightId, @Path("username") String username, @Body HashMap<String, Object> body);
}
