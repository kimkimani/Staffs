package ydkim2110.com.androidbarberstaffapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class Barber implements Parcelable {
    @Exclude
    private String id;
    private String name, username, password, barberId;
    private Long rating;
    private Long ratingTimes ;
    private  int slot;
    private  Long rent;

    public Barber() {
    }


    public Barber(String id, String name, String username, String password, String barberId, Long rating, Long ratingTimes, int slot, Long rent) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.barberId = barberId;
        this.rating = rating;
        this.ratingTimes = ratingTimes;
        this.slot = slot;
        this.rent = rent;
    }

    protected Barber(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        barberId = in.readString();
        slot = in.readInt();
        rent = in.readLong();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readLong();
            ratingTimes = in.readLong();
        }
    }

    public static final Creator<Barber> CREATOR = new Creator<Barber>() {
        @Override
        public Barber createFromParcel(Parcel in) {
            return new Barber(in);
        }

        @Override
        public Barber[] newArray(int size) {
            return new Barber[size];
        }
    };

    public Long getRatingTimes() {
        return ratingTimes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRatingTimes(Long ratingTimes) {
        this.ratingTimes = ratingTimes;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public Long getRent() {
        return rent;
    }

    public void setRent(Long rent) {
        this.rent = rent;
    }

    public static Creator<Barber> getCREATOR() {
        return CREATOR;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(barberId);
        dest.writeInt(slot);
        dest.writeLong(rent);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(rating);
            dest.writeLong(ratingTimes);
        }
    }
}
