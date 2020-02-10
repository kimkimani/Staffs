package ydkim2110.com.androidbarberstaffapp.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ydkim2110.com.androidbarberstaffapp.Model.FCMResponse;
import ydkim2110.com.androidbarberstaffapp.Model.FCMSendData;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAdfwNHKM:APA91bH4GcYqry39SE8Zb_3uWmFqx4YXeTv3_tcPw5bIZdyVET1B7REWAml4Sxuo7w3i8lZ7hx6atYfGV5hVbm4cTZZAwTzao8A8Y82pLnvhF_v8K-tJLWZjWZzQQimkQekHR8TkuwLV"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
