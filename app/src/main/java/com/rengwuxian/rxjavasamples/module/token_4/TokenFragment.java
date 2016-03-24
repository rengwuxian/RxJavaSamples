// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.module.token_4;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.network.Network;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.network.api.FakeApi;
import com.rengwuxian.rxjavasamples.model.FakeThing;
import com.rengwuxian.rxjavasamples.model.FakeToken;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TokenFragment extends BaseFragment {

    @Bind(R.id.tokenTv) TextView tokenTv;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;

    @OnClick(R.id.requestBt)
    void upload() {
        swipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        final FakeApi fakeApi = Network.getFakeApi();
        subscription = fakeApi.getFakeToken("fake_auth_code")
                .flatMap(new Func1<FakeToken, Observable<FakeThing>>() {
                    @Override
                    public Observable<FakeThing> call(FakeToken fakeToken) {
                        return fakeApi.getFakeData(fakeToken);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<FakeThing>() {
                    @Override
                    public void call(FakeThing fakeData) {
                        swipeRefreshLayout.setRefreshing(false);
                        tokenTv.setText(getString(R.string.got_data, fakeData.id, fakeData.name));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_token, container, false);
        ButterKnife.bind(this, view);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setEnabled(false);
        return view;
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_token;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_token;
    }
}
