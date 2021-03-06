package com.petproject.ybiry.galleryonmap.arch;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModelActivity<T extends ViewDataBinding, V extends AndroidViewModel> extends BaseActivity<T> {

    private V mViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = initViewModel();
        getBinding().setVariable(getBindingVariable(), mViewModel);
        getBinding().setLifecycleOwner(this);
        getBinding().executePendingBindings();
    }

    public V getViewModel() {
        return mViewModel;
    }

    public abstract V initViewModel();

    public abstract int getBindingVariable();
}
