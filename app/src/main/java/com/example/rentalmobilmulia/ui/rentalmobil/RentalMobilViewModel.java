package com.example.rentalmobilmulia.ui.rentalmobil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RentalMobilViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RentalMobilViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}