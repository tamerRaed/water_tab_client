
package com.tamer.alna99.watertabclient.model.findDriver;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class FindDriverResponse implements Parcelable {

    public static final Creator<FindDriverResponse> CREATOR = new Creator<FindDriverResponse>() {
        @Override
        public FindDriverResponse createFromParcel(Parcel in) {
            return new FindDriverResponse(in);
        }

        @Override
        public FindDriverResponse[] newArray(int size) {
            return new FindDriverResponse[size];
        }
    };
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private Boolean success;
    private Driver driver;

    public FindDriverResponse() {
        // Normal actions performed by class, since this is still a normal object!
    }

    protected FindDriverResponse(Parcel in) {
        byte tmpSuccess = in.readByte();
        success = tmpSuccess == 0 ? null : tmpSuccess == 1;
        driver = in.readParcelable(Driver.class.getClassLoader());

    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (success == null ? 0 : success ? 1 : 2));
        parcel.writeParcelable(driver, i);
    }
}
