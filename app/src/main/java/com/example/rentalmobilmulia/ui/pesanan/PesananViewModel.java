package com.example.rentalmobilmulia.ui.pesanan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PesananViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PesananViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}