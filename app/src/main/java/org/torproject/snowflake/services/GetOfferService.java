package org.torproject.snowflake.services;

import org.torproject.snowflake.pojo.OfferRequestBody;
import org.torproject.snowflake.pojo.SDPOfferResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetOfferService {
    @POST("proxy")
    @Headers({"Content-type: application/json", "Host: 10.0.2.2:8080"})
    Observable<SDPOfferResponse> getOffer(@Body OfferRequestBody body);
}
