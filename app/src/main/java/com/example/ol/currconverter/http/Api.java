package com.example.ol.currconverter.http;

/*
 * URL example:
 * http://apilayer.net/api/live?access_key=e2c9410aabead14100bc12068ecc394b&format=1&currencies=EUR
 */

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface Api {
  @GET("/{endpoint_action}")
  public Response getResponse(
    @Path("endpoint_action") String endpointAction,
    @Query("access_key") String accessKey,
    @Query("format") String formatValue,
    @Query("currencies") String currencies);
  }

