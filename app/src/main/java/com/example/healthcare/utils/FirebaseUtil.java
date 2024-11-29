package com.example.healthcare.utils;

import androidx.annotation.NonNull;

import com.example.healthcare.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtil {

    public static final String DATA_CHANGED_REQ_CODE = "DATA_CHANGED_REQ_CODE";

    public interface StateCallback {
        void onStateReceived(boolean isOnline);
    }

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn() {
        if (currentUserId() != null) {
            return true;
        }
        return false;
    }
    public static DocumentReference currentUserDetailsById() {
        return FirebaseFirestore.getInstance().collection("user").document(currentUserId());
    }
    public static CollectionReference scheduleAllCollection() {
        return FirebaseFirestore.getInstance().collection("schedule");
    }
    public static DocumentReference scheduleCollectionById(String documentId) {
        return FirebaseFirestore.getInstance().collection("schedule").document(documentId);
    }
    public static CollectionReference checkScheduleCollection() {
        return FirebaseFirestore.getInstance().collection("checkSchedule");
    }
    public static DocumentReference imageCollectionById(String documentId) {
        return FirebaseFirestore.getInstance().collection("drugImage").document(documentId);
    }

    public static DocumentReference repeatCollectionById(String documentId) {
        return FirebaseFirestore.getInstance().collection("repeat").document(documentId);
    }
    public static CollectionReference allDoctorCollection() {
        return FirebaseFirestore.getInstance().collection("doctor");
    }
    public static DocumentReference currentEmployeeDetails() {
        return FirebaseFirestore.getInstance().collection("employee").document(currentUserId());
    }

    public static DocumentReference getDoctorDetailsById(String doctorId) {
        return FirebaseFirestore.getInstance().collection("doctor").document(doctorId);
    }

    public static DocumentReference getRoomOnlineByDoctorId(String doctorId) {
        return FirebaseFirestore.getInstance().collection("online_appointment").document(doctorId);
    }
    public static CollectionReference allOnlineAppointmentByDoctorId(String doctorId) {
        return getRoomOnlineByDoctorId(doctorId).collection("appointment");
    }

    //    public static DocumentReference statusUser(){
//        return FirebaseFirestore.getInstance().collection("status").document(currentUserId());
//    }
//    public static void setStatus(boolean b){
//        Map<String, Boolean> statusUser = new HashMap<>();
//        statusUser.put("isStatus", b);
//        FirebaseUtil.statusUser().set(statusUser).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//            }
//        });
//    }
    public static DocumentReference stateUserOlline() {
        return FirebaseFirestore.getInstance().collection("state").document(currentUserId());
    }

    public static void setState(boolean b) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Map<String, Boolean> stateUser = new HashMap<>();
            stateUser.put("isState", b);
            FirebaseUtil.stateUserOlline().set(stateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }

    public static DocumentReference roleUser() {
        return FirebaseFirestore.getInstance().collection("role").document(currentUserId());
    }

    public static CollectionReference getTokenId() {
        return FirebaseFirestore.getInstance().collection("token");
    }

    public static DocumentReference getTokenByDocument(String documentId) {
        return FirebaseFirestore.getInstance().collection("token").document(documentId);
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("user");
    }

    public static CollectionReference allRoleCollectionReference() {
        return FirebaseFirestore.getInstance().collection("role");
    }

    public static CollectionReference allEmployeeCollectionReference() {
        return FirebaseFirestore.getInstance().collection("employee");
    }

    public static CollectionReference allDoctorCollectionReference() {
        return FirebaseFirestore.getInstance().collection("doctor");
    }

    // thêm một đối tượng appointment vào document của collection appointment với id được lấy tự động
    public static CollectionReference getAppointmentCollectionReference() {
        return FirebaseFirestore.getInstance().collection("appointment");
    }

    public static DocumentReference getAppointmentDetailsById(String appointmentId) {
        return FirebaseFirestore.getInstance().collection("appointment").document(appointmentId);
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static CollectionReference getDrugCollectionReference() {
        return FirebaseFirestore.getInstance().collection("medicine");
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static CollectionReference getNotificationsCollectionReference() {
        return FirebaseFirestore.getInstance().collection("notification");
    }

    public static DocumentReference getNotificationDetailsById(String notificationId) {
        return FirebaseFirestore.getInstance().collection("notification").document(notificationId);
    }

    public static CollectionReference getRelativeCollectionReference() {
        return FirebaseFirestore.getInstance().collection("relative");
    }


//    public static void sendMessageNotificationtoEmployeeTokenId(String requestCode, String message, String tokenId) {
//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                UserModel currentUser = task.getResult().toObject(UserModel.class);
//                try {
//                    JSONObject jsonObject = new JSONObject();
//
//                    JSONObject dataObj = new JSONObject();
//                    dataObj.put("requestCode", requestCode);
//                    dataObj.put("title", currentUser.getFullName());
//                    dataObj.put("body", message);
//                    dataObj.put("userId", currentUser.getUserId());
//                    jsonObject.put("data", dataObj);
//                    jsonObject.put("to", tokenId);
//
//                    callApi(jsonObject);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public static void sendNotifyDataChange() {
//        Query query = FirebaseUtil.getTokenId().whereEqualTo("userId", FirebaseAuth.getInstance().getUid());
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                QuerySnapshot querySnapshot = task.getResult();
//                if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
//                    Token token = documentSnapshot.toObject(Token.class);
//                    if (token != null) {
//                        List<String> tokenList = token.getTokenList();
//                        for (String tokenString : tokenList) {
//                            sendMessageNotificationToCustomerTokenId(DATA_CHANGED_REQ_CODE, tokenString, null, null);
//                        }
//                    }
//                }
//            } else {
//                Log.e("ERROR", "Lỗi kết nối");
//            }
//        });
//    }
//
//    public static void sendMessageNotificationToCustomerTokenId(String requestCode, String tokenId, String title, String body) {
//        try {
//            JSONObject jsonObject = new JSONObject();
//
//            JSONObject dataObj = new JSONObject();
//            dataObj.put("requestCode", requestCode);
//            dataObj.put("title", title);
//            dataObj.put("body", body);
//            jsonObject.put("data", dataObj);
//            jsonObject.put("to", tokenId);
//
//            callApi(jsonObject);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void callApi(JSONObject jsonObject) {
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        OkHttpClient client = new OkHttpClient();
//        String url = "https://fcm.googleapis.com/fcm/send";
//        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .header("Authorization", "Bearer AAAA2YcskTU:APA91bFbJHA1gMDoHTkHauaDOJZfuzxcYqq6TjBqUoyAp-0I8YHhjIxh3VvtJUS39Od1biRCgomTSO_C5AOMsAHf_ovnSCR6IwO_MglmxIUgWZuL-ADA0MRXqA-leMYLGODLnHx_v7AN")
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//
//            }
//        });
//    }
}










