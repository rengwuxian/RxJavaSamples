// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.module.zip_3;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.network.Network;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.adapter.ItemListAdapter;
import com.rengwuxian.rxjavasamples.model.Item;
import com.rengwuxian.rxjavasamples.model.ZhuangbiImage;
import com.rengwuxian.rxjavasamples.util.GankBeautyResultToItemsMapper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class ZipFragment extends BaseFragment {
    @Bind(R.id.gridRv) RecyclerView gridRv;
    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout swipeRefreshLayout;
    ItemListAdapter adapter = new ItemListAdapter();

    Observer<List<Item>> observer = new Observer<List<Item>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(List<Item> items) {
            swipeRefreshLayout.setRefreshing(false);
            adapter.setItems(items);
        }
    };

    @OnClick(R.id.zipLoadBt)
    void load() {
        swipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        subscription = Observable.zip(Network.getGankApi().getBeauties(200, 1).map(GankBeautyResultToItemsMapper.getInstance()),
                Network.getZhuangbiApi().search("装逼"),
                new Func2<List<Item>, List<ZhuangbiImage>, List<Item>>() {
                    @Override
                    public List<Item> call(List<Item> gankItems, List<ZhuangbiImage> zhuangbiImages) {
                        List<Item> items = new ArrayList<Item>();
                        for (int i = 0; i < gankItems.size() / 2 && i < zhuangbiImages.size(); i++) {
                            items.add(gankItems.get(i * 2));
                            items.add(gankItems.get(i * 2 + 1));
                            Item zhuangbiItem = new Item();
                            ZhuangbiImage zhuangbiImage = zhuangbiImages.get(i);
                            zhuangbiItem.description = zhuangbiImage.description;
                            zhuangbiItem.imageUrl = zhuangbiImage.image_url;
                            items.add(zhuangbiItem);
                        }
                        return items;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zip, container, false);
        ButterKnife.bind(this, view);

        gridRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        gridRv.setAdapter(adapter);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setEnabled(false);
        return view;
    }


    @Override
    protected int getDialogRes() {
        return R.layout.dialog_zip;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_zip;
    }
}
