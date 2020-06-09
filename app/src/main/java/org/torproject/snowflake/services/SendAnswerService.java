package org.torproject.snowflake.services;

import org.torproject.snowflake.pojo.AnsResponse;
import org.torproject.snowflake.pojo.AnswerBody;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SendAnswerService {
    @POST("answer")
    @Headers({"Content-type: application/json", "Host: 10.0.2.2:8080"})
    Observable<AnsResponse> sendAnswer(@Body AnswerBody body);
}
