package com.petproject.ybiry.galleryonmap.arch;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;

public abstract class BaseViewModelFragment<T extends ViewDataBinding, V extends AndroidViewModel> extends BaseFragment<T> {

    private V mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = initViewModel();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBinding().executePendingBindings();
        getBinding().setLifecycleOwner(getViewLifecycleOwner());
        setSubscribers();
    }

    public abstract V initViewModel();

    public V getViewModel() {
        return mViewModel;
    }

    protected abstract void setSubscribers();
}