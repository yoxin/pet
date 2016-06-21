package yoxin.com.ext_plugin.views;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;
import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import yoxin.com.ext_plugin.R;
import yoxin.com.ext_plugin.controller.ApkListAdapter;
import yoxin.com.ext_plugin.controller.ApkOperator;
import yoxin.com.ext_plugin.modules.ApkItem;

/**
 * 安装Apk的页面, 使用RecyclerView.
 * <p>
 * Created by wangchenlong on 16/1/8.
 */
public class StoreFragment extends Fragment {

    private static final String TAG = "StoreFragment";
    RecyclerView mRvRecycler;

    private ApkListAdapter mStoreAdapter; // 适配器

    // 服务连接
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            loadApks();
        }

        @Override public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRvRecycler = (RecyclerView) view.findViewById(R.id.list_rv_recycler);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRvRecycler.setLayoutManager(llm);
        Log.e(TAG,"onViewCreated");

        mStoreAdapter = new ApkListAdapter(getActivity(), ApkOperator.TYPE_STORE);
        mRvRecycler.setAdapter(mStoreAdapter);

        if (PluginManager.getInstance().isConnected()) {
            loadApks();
        } else {
            PluginManager.getInstance().addServiceConnection(mServiceConnection);
        }
    }

    // 加载Apk
    private void loadApks() {
        Log.e(TAG, "loadApks");
        // 异步加载, 防止Apk过多, 影响速度
        Observable.just(getApkFromDownload())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mStoreAdapter::setApkItems);
    }

    // 从下载文件夹获取Apk
    private ArrayList<ApkItem> getApkFromDownload() {
        File files = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        PackageManager pm = getActivity().getPackageManager();
        ArrayList<ApkItem> apkItems = new ArrayList<>();
        for (File file : files.listFiles()) {
            if (file.exists() && file.getPath().toLowerCase().endsWith(".apk")) {
                final PackageInfo info = pm.getPackageArchiveInfo(file.getPath(), 0);
                apkItems.add(new ApkItem(pm, info, file.getPath()));
                Log.e(TAG, file.getPath());
            }
        }
        return apkItems;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PluginManager.getInstance().removeServiceConnection(mServiceConnection);
    }
}
