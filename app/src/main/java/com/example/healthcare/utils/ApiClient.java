package com.example.healthcare.utils;

import android.net.Uri;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    public static final String DISEASE_PREDICTION_BASE_URL = "http://192.168.39.245:5000/";

    public static final String URL_SEND_SYMPTOM = DISEASE_PREDICTION_BASE_URL + "symptoms";
    public static final String URL_FOUND_SYMPTOM = DISEASE_PREDICTION_BASE_URL + "found_symptoms";
    public static final String URL_CHOOSE_SYMPTOM = DISEASE_PREDICTION_BASE_URL + "index_selected";
    public static final String URL_GET_DICT_SIZE = DISEASE_PREDICTION_BASE_URL + "symptom_count_size";
    public static final String URL_GET_PAGE_SYMPTOM = DISEASE_PREDICTION_BASE_URL + "list_symptoms/";
    public static final String URL_CHOOSE_SYMPTOM_BY_PAGE = DISEASE_PREDICTION_BASE_URL + "selected_symptoms/";
    public static final String URL_GET_RESULT = DISEASE_PREDICTION_BASE_URL + "predict_disease";
    //
    public static final String FACE_RECOGNITION_BASE_URL = "http://192.168.39.245:5001/";

    public static final String URL_FACE_RECOGNITION = FACE_RECOGNITION_BASE_URL + "recognize_face";

    public static final String URL_REGISTER = FACE_RECOGNITION_BASE_URL + "register_face";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .build();

    public static void sendPostRequest(String url, JSONObject jsonInput, final ApiResponseListener listener) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonInput.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body().string());
                } else {
                    listener.onFailure("Error: " + response.code());
                }
            }
        });
    }

    public static void sendGetRequest(String url, final ApiResponseListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body().string());
                } else {
                    listener.onFailure("Error: " + response.code());
                }
            }
        });
    }

    public static void recognizeFace(Uri imageUri, ApiResponseListener listener) {
        // Lấy file từ Uri
        File imageFile = new File(imageUri.getPath());

        // Tạo MediaType cho hình ảnh JPEG
        MediaType mediaType = MediaType.parse("image/jpeg");

        // Tạo RequestBody từ file ảnh
        RequestBody fileBody = RequestBody.create(mediaType, imageFile);

        // Tạo MultipartBody.Part từ file ảnh
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), fileBody);

        // Tạo MultipartBody.Builder để gửi dữ liệu
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(body)
                .build();

        // Tạo request POST với URL của server
        Request request = new Request.Builder()
                .url(URL_FACE_RECOGNITION) // Đảm bảo URL chính xác
                .post(requestBody)
                .build();

        // Thực hiện request bất đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Nếu phản hồi thành công, gọi listener.onSuccess với nội dung phản hồi
                    listener.onSuccess(response.body().string());
                } else {
                    // Nếu phản hồi không thành công, gọi listener.onFailure
                    listener.onFailure("Error: " + response.code());
                }
            }
        });
    }

    public static void getRecognitionResult(ApiResponseListener listener) {
        sendGetRequest(URL_FACE_RECOGNITION, listener);
    }

    public static void registerFace(String classId, List<Uri> imageUris, ApiResponseListener listener) {
        MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/jpeg");

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        multipartBuilder.addFormDataPart("id", classId);

        for (Uri imageUri : imageUris) {
            File imageFile = new File(Objects.requireNonNull(imageUri.getPath()));
            RequestBody fileBody = RequestBody.create(MEDIA_TYPE_IMAGE, imageFile);
            multipartBuilder.addFormDataPart("images", imageFile.getName(), fileBody);
        }
        Request request = new Request.Builder()
                .url(URL_REGISTER)
                .post(multipartBuilder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    listener.onSuccess(response.body().string());
                } else {
                    listener.onFailure("Error: " + response.code());
                }
            }
        });
    }



    public static void postSymptoms(String symptoms, ApiResponseListener listener) {
        JSONObject jsonInput = new JSONObject();
        try {
            jsonInput.put("symptoms", symptoms);
            sendPostRequest(URL_SEND_SYMPTOM, jsonInput, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSymptoms(ApiResponseListener listener) {
        sendGetRequest(URL_FOUND_SYMPTOM, listener);
    }

    public static void getDictSize(ApiResponseListener listener) {
        sendGetRequest(URL_GET_DICT_SIZE, listener);
    }
    public static void postSelectedSymptoms(String selectedIndices, ApiResponseListener listener) {
        JSONObject jsonInput = new JSONObject();
        try {
            jsonInput.put("selected_indices", selectedIndices);
            sendPostRequest(URL_CHOOSE_SYMPTOM, jsonInput, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSymptomsByPage(int page, ApiResponseListener listener) {
        String url = URL_GET_PAGE_SYMPTOM + page;
        sendGetRequest(url, listener);
    }

    public static void getDiseasePrediction(ApiResponseListener listener) {
        sendGetRequest(URL_GET_RESULT, listener);
    }

    public static void postSelectedSymptomsByPage(int page, String indexStr, ApiResponseListener listener) {
        JSONObject jsonInput = new JSONObject();
        try {
            jsonInput.put("indexStr", indexStr);
            String url = URL_CHOOSE_SYMPTOM_BY_PAGE + page + "/" + indexStr;
            sendPostRequest(url, jsonInput, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface ApiResponseListener {
        void onSuccess(String response);
        void onFailure(String error);
    }
}
