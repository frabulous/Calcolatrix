package com.example.francesco.calcolatrix;

import android.content.Context;

public class Bottonix<V> extends android.support.v7.widget.AppCompatButton {

    private V value;

    public Bottonix(Context context) {
        super(context);
        this.value = null;
    }
    public Bottonix(Context context, V value) {
        super(context);
        this.value = value;
    }

    public V getValue() {
        return value;
    }
}
