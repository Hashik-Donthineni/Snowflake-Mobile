package org.torproject.snowflake.services;

import org.torproject.snowflake.pojo.OfferRequestBody;
import org.torproject.snowflake.pojo.SDPOfferResponse;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetOfferService {
    @POST("proxy")
    Observable<SDPOfferResponse> getOffer(
            @HeaderMap Map<String, String> headersMap,
            @Body OfferRequestBody body);
}
