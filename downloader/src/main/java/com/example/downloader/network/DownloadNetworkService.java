package com.example.downloader.network;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by vivek on 03/12/17.
 */

public interface DownloadNetworkService {

    @GET
    Call<ResponseBody> downloadFileUrl(@Url String fileUrl);

}
