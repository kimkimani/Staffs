package ydkim2110.com.androidbarberstaffapp.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.paperdb.Paper;
import ydkim2110.com.androidbarberstaffapp.Model.Barber;
import ydkim2110.com.androidbarberstaffapp.Model.BookingInfomation;
import ydkim2110.com.androidbarberstaffapp.Model.MyToken;
import ydkim2110.com.androidbarberstaffapp.Model.Salon;
import ydkim2110.com.androidbarberstaffapp.R;

public class Common {

    public static final int MAX_NOTIFICATION_PER_LOAD = 10;
    public static final String SERVICES_ADDED = "SERVICES_ADDED";
    public static final double DEFAULT_PRICE = 30;
    public static final String MONEY_SIGN = "Ksh";
    public static final String SHOPPING_LIST = "SHOPPING_LIST_ITEMS";
    public static final String IMAGE_DOWNLOADABLE_URL = "DOWNLOADABLE_URL";

    public static final String RATING_STATE_KEY = "RATING_STATE";
    public static final String RATING_SALON_ID = "RATING_SALON_ID";
    public static final String RATING_SALON_NAME = "RATING_SALON_NAME";
    public static final String RATING_BARBER_ID = "RATING_BARBER_ID";

    private static final String TAG = Common.class.getSimpleName();

    public static final Object DISABLE_TAG = "DISABLE";
    public static final int TIME_SLOT_TOTAL = 20;
    public static final String LOGGED_KEY = "LOGGED_KEY";
    public static final String STATE_KEY = "STATE";
    public static final String SALON_KEY = "SALON";
    public static final String BARBER_KEY = "BARBER";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static String state_name = "";
    public static Barber currentBarber;
    public static Salon selected_salon;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static Calendar bookingDate = Calendar.getInstance();
    public static BookingInfomation currentBookingInformation;

    public static String convertTimeSlotToString(int position) {
        switch (position) {
            case 0:
                return "RM 1 HEAD 1";
            case 1:
                return "RM 1 HEAD 2";
            case 2:
                return "RM 2 HEAD 1";
            case 3:
                return "RM 2 HEAD 2";
            case 4:
                return "RM 3 HEAD 1";
            case 5:
                return "RM 3 HEAD 2";
            case 6:
                return "RM 4 HEAD 1";
            case 7:
                return "RM 4 HEAD 2";
            case 8:
                return "RM 5 HEAD 1";
            case 9:
                return "RM 5 HEAD 2";
            case 10:
                return "RM 6 HEAD 1";
            case 11:
                return "RM 6 HEAD 2";
            case 12:
                return "RM 7 HEAD 1";
            case 13:
                return "RM 7 HEAD 2";
            case 14:
                return "RM 8 HEAD 1";
            case 16:
                return "RM 9 HEAD 1";
            case 17:
                return "RM 9 HEAD 2";
            case 18:
                return "RM 10 HEAD 1";
            case 19:
                return "RM 10 HEAD 2";
            case 20:
                return "RM 10 HEAD 3";
            case 21:
                return "RM 11 HEAD 1";
            case 22:
                return "RM 11 HEAD 2";
            case 23:
                return "RM 12 HEAD 1";
            case 24:
                return "RM 12 HEAD 2";
            case 25:
                return "RM 13 HEAD 1";
            case 26:
                return "RM 13 HEAD 2";
            case 27:
                return "RM 14 HEAD 1";
            case 28:
                return "RM 14 HEAD 2";
            case 29:
                return "RM 15 HEAD 1";
            case 30:
                return "RM 15 HEAD 2";
            case 31:
                return "RM 16 HEAD 1";
            case 32:
                return "RM 16 HEAD 2";
            case 33:
                return "RM 17 HEAD 1";
            case 34:
                return "RM 17 HEAD 2";
            case 35:
                return "RM 18 HEAD 1";
            case 36:
                return "RM 19 HEAD 1";
            case 37:
                return "RM 19 HEAD 2";
            case 38:
                return "RM 20 HEAD 1";
            case 39:
                return "RM 20 HEAD 2";
            case 40:
                return "RM 20 HEAD 3";
            case 41:
                return "RM 21 HEAD 1";
            case 42:
                return "RM 21 HEAD 2";
            case 43:
                return "RM 22 HEAD 1";
            case 44:
                return "RM 22 HEAD 2";
            case 45:
                return "RM 23 HEAD 1";
            case 46:
                return "RM 23 HEAD 2";
            case 47:
                return "RM 24 HEAD 1";
            case 48:
                return "RM 24 HEAD 2";
            case 49:
                return "RM 25 HEAD 1";
            case 50:
                return "RM 25 HEAD 2";
            case 51:
                return "RM 26 HEAD 1";
            case 52:
                return "RM 26 HEAD 2";
            case 53:
                return "RM 27 HEAD 1";
            case 54:
                return "RM 27 HEAD 2";
            case 55:
                return "RM 28 HEAD 1";
            case 56:
                return "RM 29 HEAD 1";
            case 57:
                return "RM 29 HEAD 2";
            case 58:
                return "RM 30 HEAD 1";
            case 59:
                return "RM 30 HEAD 2";
            case 60:
                return "RM 30 HEAD 3";
            case 61:
                return "RM 41 HEAD 1";
            case 62:
                return "RM 41 HEAD 2";
            case 63:
                return "RM 42 HEAD 1";
            case 64:
                return "RM 42 HEAD 2";
            case 65:
                return "RM 43 HEAD 1";
            case 66:
                return "RM 43 HEAD 2";
            case 67:
                return "RM 44 HEAD 1";
            case 68:
                return "RM 44 HEAD 2";
            case 69:
                return "RM 45 HEAD 1";
            case 70:
                return "RM 45 HEAD 2";
            case 71:
                return "RM 46 HEAD 1";
            case 72:
                return "RM 46 HEAD 2";
            case 73:
                return "RM 47 HEAD 1";
            case 74:
                return "RM 47 HEAD 2";
            case 75:
                return "RM 48 HEAD 1";
            case 76:
                return "RM 49 HEAD 1";
            case 77:
                return "RM 49 HEAD 2";
            case 78:
                return "RM 50 HEAD 1";
            case 79:
                return "RM 50 HEAD 2";
            case 80:
                return "RM 50 HEAD 3";
            case 81:
                return "RM 61 HEAD 1";
            case 82:
                return "RM 61 HEAD 2";
            case 83:
                return "RM 62 HEAD 1";
            case 84:
                return "RM 62 HEAD 2";
            case 85:
                return "RM 63 HEAD 1";
            case 86:
                return "RM 63 HEAD 2";
            case 87:
                return "RM 64 HEAD 1";
            case 88:
                return "RM 64 HEAD 2";
            case 89:
                return "RM 65 HEAD 1";
            case 90:
                return "RM 65 HEAD 2";
            case 91:
                return "RM 66 HEAD 1";
            case 92:
                return "RM 66 HEAD 2";
            case 93:
                return "RM 67 HEAD 1";
            case 94:
                return "RM 67 HEAD 2";
            case 95:
                return "RM 68 HEAD 1";
            case 96:
                return "RM 69 HEAD 1";
            case 97:
                return "RM 69 HEAD 2";
            case 98:
                return "RM 70 HEAD 1";
            case 99:
                return "RM 70 HEAD 2";
            case 100:
                return "RM 71 HEAD 1";
            case 101:
                return "RM 71 HEAD 2";
            default:
                return "Closed!";
        }
    }

    public static void showNotification(Context context, int noti_id, String title, String content, Intent intent) {
        Log.d(TAG, "showNotification: called!!");

        PendingIntent pendingIntent = null;
        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context,
                    noti_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String NOTIFICATION_CHANNEL_ID = "ydkim2110_barber_booking_channel_01";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Ydkim2110 Barber Booking Staff App",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Staff app");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        Notification notification = builder.build();

        notificationManager.notify(noti_id, notification);

    }

    public static String formatShoppingItemName(String name) {
        return name.length() > 13 ? new StringBuilder(name.substring(0, 10)).append("...").toString() : name;
    }

    public static String getFileName(ContentResolver contentResolver, Uri fileUri) {
        String result = null;
        if (fileUri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(fileUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = fileUri.getPath();
            int cut = result.lastIndexOf("/");
            if (cut != -1) {
                result = result.substring(cut+1);
            }
        }
        return result;
    }

    public enum TOKEN_TYPE {
//        CLIENT,
//        BARBER,
//        MANAGER
        CLIENT,
        Admin,
        MANAGER
    }

    public static void updateToken(Context context, String token) {
        // First, we need check if user still login
        // Because, we need store token belonging user
        // So, we need user store data
        Paper.init(context);
        String user = Paper.book().read(Common.LOGGED_KEY);
        if (user != null) {
            if (!TextUtils.isEmpty(user)) {
                MyToken myToken = new MyToken();
                myToken.setToken(token);
                // Because this code run from Barber Staff app
                myToken.setTokenType(TOKEN_TYPE.MANAGER);
                myToken.setUserPhone(user);
                // Submit on Firestore
                FirebaseFirestore.getInstance()
                        .collection("Tokens")
                        .document(user)
                        .set(myToken)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                }
                            }
                        });
            }
        }
    }
}
