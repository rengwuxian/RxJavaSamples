// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.network.api;

import com.rengwuxian.rxjavasamples.model.GankBeautyResult;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface GankApi {
    @GET("data/福利/{number}/{page}")
    Observable<GankBeautyResult> getBeauties(@Path("number") int number, @Path("page") int page);
}
