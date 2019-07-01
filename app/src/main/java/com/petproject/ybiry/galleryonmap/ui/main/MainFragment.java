package com.petproject.ybiry.galleryonmap.ui.main;

import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.arch.BaseViewModelFragment;
import com.petproject.ybiry.galleryonmap.databinding.FragmentMainBinding;

public class MainFragment extends BaseViewModelFragment<FragmentMainBinding, MainFragmentViewModel> {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public MainFragmentViewModel initViewModel() {
        return null;
    }

    @Override
    protected void setSubscribers() {

    }
}
