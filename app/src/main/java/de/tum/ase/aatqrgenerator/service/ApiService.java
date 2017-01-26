package de.tum.ase.aatqrgenerator.service;

import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.tum.ase.aatqrgenerator.model.Attendance;
import de.tum.ase.aatqrgenerator.model.Lecture;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {
    private static final String TAG = "ApiService";

    public interface ApiLecturesListener {
        void gotLectures(List<Lecture> lectures);
    }

    public interface ApiAttendanceListener {
        void attendanceCreated(String verificationToken);
        void notSignedIn();
    }

    private ObjectMapper mapper;
    private OkHttpClient client;
    private String token;
    private ApiLecturesListener lectureListener;
    private ApiAttendanceListener attendanceListener;

    public ApiService(){
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS).build();
        mapper = new ObjectMapper();
    }

    public void setLectureListener(ApiLecturesListener lectureListener){
        this.lectureListener = lectureListener;
    }
    public void setAttendanceListener(ApiAttendanceListener attendanceListener) { this.attendanceListener = attendanceListener; }

    public void setToken(String token) { this.token = token; }

    public void getLectures() {
        Request.Builder reqBuilder = new Request.Builder()
                .url("https://ase2016-group-4-1.appspot.com/api/lectures");
        if(token != null && !token.isEmpty()){
            reqBuilder = reqBuilder.addHeader("Authorization", "Bearer " + token);
        }
        client.newCall(reqBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                List<Lecture> lectures = mapper.readValue(body,
                        new TypeReference<List<Lecture>>() { });
                Log.d(TAG, "got lectures " + body);
                lectureListener.gotLectures(lectures);
            }
        });
    }

    public void attend(String url) {
        if(token == null){
            attendanceListener.notSignedIn();
            return;
        }
        Request.Builder reqBuilder = new Request.Builder().url("https://ase2016-group-4-1.appspot.com" + url)
                .post(RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"),
                        ""
                ))
                .addHeader("Authorization", "Bearer " + token);
        client.newCall(reqBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Attendance att = mapper.readValue(body, Attendance.class);
                Log.d(TAG, "attendance created");
                attendanceListener.attendanceCreated(att.verificationToken);
            }
        });
    }
}
