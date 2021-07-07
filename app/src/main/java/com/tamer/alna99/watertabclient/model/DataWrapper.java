package com.tamer.alna99.watertabclient.model;

import java.util.Observable;

public class DataWrapper<T> extends Observable {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
        setChanged();
        notifyObservers(data);
        deleteObservers();
    }
}
