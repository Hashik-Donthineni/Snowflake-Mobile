package org.torproject.snowflake.services;

import org.torproject.snowflake.pojo.AnsResponse;
import org.torproject.snowflake.pojo.AnswerBody;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface SendAnswerService {
    @POST("answer")
    Observable<AnsResponse> sendAnswer(
            @HeaderMap Map<String, String> headersMap,
            @Body AnswerBody body);
}
