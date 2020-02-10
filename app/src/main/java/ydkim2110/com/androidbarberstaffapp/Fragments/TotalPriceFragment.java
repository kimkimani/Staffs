package ydkim2110.com.androidbarberstaffapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ydkim2110.com.androidbarberstaffapp.Adapter.MyConfirmShoppingItemAdapter;
import ydkim2110.com.androidbarberstaffapp.Common.Common;
import ydkim2110.com.androidbarberstaffapp.Model.BarberServices;
import ydkim2110.com.androidbarberstaffapp.Model.CartItem;
import ydkim2110.com.androidbarberstaffapp.Model.EventBus.DismissFromBottomSheetEvent;
import ydkim2110.com.androidbarberstaffapp.Model.FCMResponse;
import ydkim2110.com.androidbarberstaffapp.Model.FCMSendData;
import ydkim2110.com.androidbarberstaffapp.Model.Invoice;
import ydkim2110.com.androidbarberstaffapp.Model.MyNotification;
import ydkim2110.com.androidbarberstaffapp.Model.MyToken;
import ydkim2110.com.androidbarberstaffapp.R;
import ydkim2110.com.androidbarberstaffapp.Retrofit.IFCMService;
import ydkim2110.com.androidbarberstaffapp.Retrofit.RetrofitClient;

public class TotalPriceFragment extends BottomSheetDialogFragment {

    private static final String TAG = TotalPriceFragment.class.getSimpleName();

    private Unbinder mUnbinder;

    private IFCMService mIFCMApi;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    @BindView(R.id.chip_group_services)
    ChipGroup chip_group_services;
    @BindView(R.id.recycler_view_shopping)
    RecyclerView recycler_view_shopping;
    @BindView(R.id.txt_salon_name)
    TextView txt_salon_name;
    @BindView(R.id.txt_barber_name)
    TextView txt_barber_name;
    @BindView(R.id.txt_customer_name)
    TextView txt_customer_name;
    @BindView(R.id.txt_customer_phone)
    TextView txt_customer_phone;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.itemTitle)
    TextView itemTitle;
    @BindView(R.id.btn_confirm)
    TextView btn_confirm;

    private HashSet<BarberServices> mServicesAdded;
    //private List<ShoppingItem> mShoppingItemList;
    private IFCMService mIFCMService;
    private AlertDialog mDialog;
    private String image_url;

    private static TotalPriceFragment instance;

    public static TotalPriceFragment getInstance() {
        return instance == null ? new TotalPriceFragment() : instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        mIFCMService = RetrofitClient.getInstance().create(IFCMService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_total_price, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init();
        initView();
        getBundle(getArguments());
        setInformation();
        return view;
    }

    private void setInformation() {
        Log.d(TAG, "setInformation: called!!");
        txt_salon_name.setText(Common.selected_salon.getName());
        txt_barber_name.setText(Common.currentBarber.getName());
        txt_time.setText(Common.convertTimeSlotToString(Common.currentBookingInformation.getSlot().intValue()));
        txt_customer_name.setText(Common.currentBookingInformation.getCustomerName());
        txt_customer_phone.setText(Common.currentBookingInformation.getCustomerPhone());
        itemTitle.setText(Common.currentBookingInformation.getOrdernumber());


        if (mServicesAdded.size() > 0) {
            // Add to Chip Group
            int i = 0;
            for (BarberServices services : mServicesAdded) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, null);
                chip.setText(services.getName());
                chip.setTag(i);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mServicesAdded.remove(v.getTag());
                        chip_group_services.removeView(v);

                        calculatePrice();
                    }
                });

                chip_group_services.addView(chip);

                i++;
            }
        }

        if (Common.currentBookingInformation.getCartItemList() != null) {
            if (Common.currentBookingInformation.getCartItemList().size() > 0) {
                MyConfirmShoppingItemAdapter adapter = new MyConfirmShoppingItemAdapter(getContext(),
                        Common.currentBookingInformation.getCartItemList());
                recycler_view_shopping.setAdapter(adapter);
            }

            calculatePrice();
        }

    }

    private double calculatePrice() {
        Log.d(TAG, "calculatePrice: called!!");
        double price = Common.currentBarber.getRent();
        for (BarberServices services : mServicesAdded) {
            price += services.getPrice();
        }

//        if (Common.currentBookingInformation.getCartItemList() != null) {
//
//            for (CartItem cartItem : Common.currentBookingInformation.getCartItemList()) {
//                price += (cartItem.getProductPrice()*cartItem.getProductQuantity());
//            }
//        }

        txt_total_price.setText(new StringBuilder(Common.MONEY_SIGN).append(price));

        return price;
    }

    private void getBundle(Bundle arguments) {
        Log.d(TAG, "getBundle: called!!");
        this.mServicesAdded = new Gson()
                .fromJson(arguments.getString(Common.SERVICES_ADDED),
                        new TypeToken<HashSet<BarberServices>>() {
                        }.getType());

//        this.mShoppingItemList = new Gson()
//                .fromJson(arguments.getString(Common.SHOPPING_LIST),
//                        new TypeToken<List<ShoppingItem>>() {
//                        }.getType());

        image_url = arguments.getString(Common.IMAGE_DOWNLOADABLE_URL);
    }

    private void initView() {
        Log.d(TAG, "initView: called!");
        recycler_view_shopping.setHasFixedSize(true);
        recycler_view_shopping.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();

                // Update bookingInformation, set done = true
                ///gender/gents/Branch/4jydSfTfDi3o26owKCFp/Hostel
//                /User/+254740841375/Booking

                DocumentReference userbookingSet = FirebaseFirestore.getInstance()
                        .collection("User")
                        .document(Common.currentBookingInformation.getCustomerPhone())
                        .collection("Branch")
                        .document(Common.selected_salon.getSalonId())
                        .collection("Booking")
                        .document(Common.currentBookingInformation.getBookingId());
                userbookingSet.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        // Update
                                        Map<String, Object> dataUpdate = new HashMap<>();
                                        dataUpdate.put("done", true);
                                        userbookingSet.update(dataUpdate);

                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                DocumentReference bookingSet = FirebaseFirestore.getInstance()
                        .collection("gender")
                        .document(Common.state_name)
                        .collection("Branch")
                        .document(Common.selected_salon.getSalonId())
                        .collection("Hostel")
                        .document(Common.currentBarber.getBarberId())
                        .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                        .document(Common.currentBookingInformation.getBookingId());

                bookingSet.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        // Update
                                        Map<String, Object> dataUpdate = new HashMap<>();
                                        dataUpdate.put("done", true);
                                        bookingSet.update(dataUpdate)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        mDialog.dismiss();
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // if update is done, create invoice
                                                            createInvoice();
                                                          //  sendNotificationUpdateToUser(new Invoice());
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
    private void createInvoice() {
        Log.d(TAG, "createInvoice: called!!");
        mDialog.dismiss();
        //Create invoice
        ///gender/gents/Branch/4jydSfTfDi3o26owKCFp/Hostel

        CollectionReference invoiceRef = FirebaseFirestore.getInstance()
                .collection("gender")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selected_salon.getSalonId())
                .collection("Invoices");

        Invoice invoice = new Invoice();
        invoice.setBarberId(Common.currentBarber.getBarberId());
        invoice.setBarberName(Common.currentBarber.getName());

        invoice.setSalonId(Common.selected_salon.getSalonId());
        invoice.setSalonName(Common.selected_salon.getName());
        invoice.setSalonAddress(Common.selected_salon.getAddress());

        invoice.setCustomerName(Common.currentBookingInformation.getCustomerName());
        invoice.setCustomerPhone(Common.currentBookingInformation.getCustomerPhone());

        invoice.setImageUri(image_url);

        invoice.setBarberServices(new ArrayList<BarberServices>(mServicesAdded));
        invoice.setShoppingItemList(Common.currentBookingInformation.getCartItemList());
        invoice.setFinalPrice(calculatePrice());

        invoiceRef.document()
                .set(invoice)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendNotificationUpdateToUser(Common.currentBookingInformation.getCustomerPhone());
                            sendNotificationUpdateToUser(new Invoice());


                        }
                    }
                });

    }
    private void sendNotificationUpdateToUser(String customerPhone) {
        Log.d(TAG, "sendNotificationUpdateToUser: called!!");
        // Get Token of user first
        FirebaseFirestore.getInstance()
                .collection("Tokens")
                .whereEqualTo("userPhone", customerPhone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().size() > 0) {
                            MyToken myToken = new MyToken();
                            for (DocumentSnapshot tokenSnapshot : task.getResult()) {
                                myToken = tokenSnapshot.toObject(MyToken.class);
                            }

                            // Create notification to send
                            FCMSendData fcmSendData = new FCMSendData();
                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("update_done", "true");

                            /**
                             * we will send an notification with payload data is 'update_true' = true
                             * so, we will add more useful information
                             * state_name, salonId, salonName, barberId
                             * This information will help us query Hostel from Client app
                             */

                            // Information need for Rating
                            dataSend.put(Common.RATING_STATE_KEY, Common.state_name);
                            dataSend.put(Common.RATING_SALON_ID, Common.selected_salon.getSalonId());
                            dataSend.put(Common.RATING_SALON_NAME, Common.selected_salon.getName());
                            dataSend.put(Common.RATING_BARBER_ID, Common.currentBarber.getBarberId());

                            fcmSendData.setTo(myToken.getToken());
                            fcmSendData.setData(dataSend);

                            mIFCMService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.newThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            mDialog.dismiss();
                                            dismiss();


                                            // we just post and event
                                            EventBus.getDefault().postSticky(new DismissFromBottomSheetEvent(true));

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });


    }
    private void sendNotificationUpdateToUr(Invoice invoice) {
        Log.d(TAG, "addToUserBooking: called");

        // First, create new collection
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentBookingInformation.getCustomerPhone())
                .collection("Booking_Notifications_info");

        // Get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp todayTimestamp = new Timestamp(calendar.getTime());

        // Check if exist document in this collection
        // if have any document with field done = false;
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", todayTimestamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            // Set data
                            userBooking.document()
                                    .set(invoice)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            // Create notification
                                            MyNotification myNotification = new MyNotification();
                                            myNotification.setUid(UUID.randomUUID().toString());
                                            myNotification.setTitle("Room Reserved  SUCCESS");
                                            myNotification.setContent(
                                                    Common.currentBookingInformation.getCustomerName()+
                                                            "---Your Room booking has successfully  Reserved---In " +
                                                            Common.selected_salon.getName()+"---Hostel:"+
                                                            Common.currentBarber.getName()+"---Room number---"+
                                                            Common.currentBookingInformation.getTime()

                                            );

                                            // We will only filter notification with 'read' is false on barber staff
                                            myNotification.setRead(false);
                                            myNotification.setServerTimestamp(new Timestamp(calendar.getTime()));
///gender/gents/Branch/4jydSfTfDi3o26owKCFp/Hostel
                                            // Submit Notification to 'Notifications' collection of Hostel
                                            FirebaseFirestore.getInstance()
                                                    .collection("User")
                                                    .document(Common.currentBookingInformation.getCustomerPhone())
                                                    // If  it not available, it will be create automatically
                                                    .collection("InvoiceAprovalNotifications")
                                                    // Create unique key
                                                    .document(myNotification.getUid())
                                                    .set(myNotification)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // First, get Token base on Hostel id
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Tokens")
                                                                    .whereEqualTo("userPhone",Common.currentBookingInformation.getCustomerPhone())
                                                                    .limit(1)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @SuppressLint("CheckResult")
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful() && task.getResult().size() > 0) {
                                                                                MyToken myToken = new MyToken();
                                                                                for (DocumentSnapshot tokenSnapshot : task.getResult()) {
                                                                                    myToken = tokenSnapshot.toObject(MyToken.class);
                                                                                }

                                                                                // Create data to send
                                                                                FCMSendData sendRequest = new FCMSendData();
                                                                                Map<String, String> dataSend = new HashMap<>();
                                                                                dataSend.put(Common.TITLE_KEY, "Room Reserved  SUCCESS");
                                                                                dataSend.put(Common.CONTENT_KEY, "Your Room booking has successfully  Reserved  "+
                                                                                        Common.currentBookingInformation.getSalonName()+
                                                                                        "---"+Common.currentBookingInformation.getBarberName()+
                                                                                        "----"+Common.currentBookingInformation.getTime());

                                                                                sendRequest.setTo(myToken.getToken());
                                                                                sendRequest.setData(dataSend);

                                                                                mCompositeDisposable.add(mIFCMService.sendNotification(sendRequest)
                                                                                        .subscribeOn(Schedulers.io())
                                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                                        .subscribe(new Consumer<FCMResponse>() {
                                                                                            @Override
                                                                                            public void accept(FCMResponse fcmResponse) throws Exception {

                                                                                            }
                                                                                        } ));
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mDialog.dismiss();
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            if (mDialog.isShowing())
                                mDialog.dismiss();

//                            getActivity().finish();
                            Toast.makeText(getContext(), "Success!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void sendNotificationUpdateToUser(Invoice invoice) {
        Log.d(TAG, "addToUserBooking: called");

        // First, create new collection
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentBookingInformation.getCustomerPhone())
                .collection("Booking_Notifications_info");

        // Get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp todayTimestamp = new Timestamp(calendar.getTime());

        // Check if exist document in this collection
        // if have any document with field done = false;
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", todayTimestamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            // Set data
                            userBooking.document()
                                    .set(invoice)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            // Create notification
                                            MyNotification myNotification = new MyNotification();
                                            myNotification.setUid(UUID.randomUUID().toString());
                                            myNotification.setTitle("New Invoice Approval");
                                            myNotification.setContent(
                                                    Common.currentBookingInformation.getCustomerName()+
                                                            "---Your booking invoice have been approved---In " +
                                                            Common.selected_salon.getName()+"---Hostel:"+
                                                            Common.currentBarber.getName()+"---Room number---"+
                                                            Common.currentBookingInformation.getTime()+"---"+
                                                            "---Extra Items---"+ new CartItem().getProductName() +

                                                            "---"+"Final price---"+
                                                            (calculatePrice())+
                                                            "---Extra Items---"+ new Invoice().getShoppingItemList()

                                            );

                                            // We will only filter notification with 'read' is false on barber staff
                                            myNotification.setRead(false);
                                            myNotification.setServerTimestamp(new Timestamp(calendar.getTime()));
///gender/gents/Branch/4jydSfTfDi3o26owKCFp/Hostel
                                            // Submit Notification to 'Notifications' collection of Hostel
                                            FirebaseFirestore.getInstance()
                                                    .collection("User")
                                                    .document(Common.currentBookingInformation.getCustomerPhone())
                                                    // If  it not available, it will be create automatically
                                                    .collection("InvoiceAprovalNotifications")
                                                    // Create unique key
                                                    .document(myNotification.getUid())
                                                    .set(myNotification)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // First, get Token base on Hostel id
                                                            FirebaseFirestore.getInstance()
                                                                    .collection("Tokens")
                                                                    .whereEqualTo("userPhone",Common.currentBookingInformation.getCustomerPhone())
                                                                    .limit(1)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @SuppressLint("CheckResult")
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if (task.isSuccessful() && task.getResult().size() > 0) {
                                                                                MyToken myToken = new MyToken();
                                                                                for (DocumentSnapshot tokenSnapshot : task.getResult()) {
                                                                                    myToken = tokenSnapshot.toObject(MyToken.class);
                                                                                }

                                                                                // Create data to send
                                                                                FCMSendData sendRequest = new FCMSendData();
                                                                                Map<String, String> dataSend = new HashMap<>();
                                                                                dataSend.put(Common.TITLE_KEY, "Room Reserved ");
                                                                                dataSend.put(Common.CONTENT_KEY, "Your Room  have been successfully Reserved"+
                                                                                        Common.currentBookingInformation.getSalonName()+
                                                                                        "---"+Common.currentBookingInformation.getBarberName()+
                                                                                        "----"+Common.currentBookingInformation.getTime());

                                                                                sendRequest.setTo(myToken.getToken());
                                                                                sendRequest.setData(dataSend);

                                                                                mCompositeDisposable.add(mIFCMService.sendNotification(sendRequest)
                                                                                        .subscribeOn(Schedulers.io())
                                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                                        .subscribe(new Consumer<FCMResponse>() {
                                                                                            @Override
                                                                                            public void accept(FCMResponse fcmResponse) throws Exception {

                                                                                                mDialog.dismiss();


//                                                                                                getActivity().finish();
                                                                                            }
                                                                                        }));
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            mDialog.dismiss();
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            if (mDialog.isShowing())
                                mDialog.dismiss();

                            getActivity().finish();
                            Toast.makeText(getContext(), "Success!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void init() {
        Log.d(TAG, "init: called!!");
    }
}
