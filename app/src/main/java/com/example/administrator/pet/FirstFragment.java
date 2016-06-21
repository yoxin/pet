package com.example.administrator.pet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;

import com.demo.floatwindowdemo.R;
import com.example.administrator.pet.view.PetSelect;
import com.luna.anytime.AboutAppActivity;
import com.luna.anytime.LoginActivity;


public class FirstFragment extends Fragment {
    ToolbarView first_toolbarview;
    private PetSelect first,second,third,fourth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View rootView=inflater.inflate(R.layout.fragment_first,container, false);
        first_toolbarview = (ToolbarView)rootView.findViewById(R.id.first_toolbarview);
        first = (PetSelect)rootView.findViewById(R.id.select_1);
        second = (PetSelect)rootView.findViewById(R.id.select_2);
        third = (PetSelect)rootView.findViewById(R.id.select_3);
        fourth = (PetSelect)rootView.findViewById(R.id.select_4);
        init();
        setListener();
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(!sharedPreferences.getBoolean("isSecondUnlock",false))
        {
            second.setPetImg(R.drawable.kong_unlock);
            second.setenabled(false);
        }

        else{
            second.setName(sharedPreferences.getString("name2", "鳄鱼"));
            second.setCheck(sharedPreferences.getBoolean("isSecondOn", false));
            second.setPetImg(R.drawable.kong);
        }

        first.setName(sharedPreferences.getString("name1", "皮卡"));
        first.setCheck(sharedPreferences.getBoolean("isFirstOn", false));
    }

    private void init(){
//        first_toolbarview.settoolbar_more_Visibility(View.VISIBLE);
//        first_toolbarview.settoolbar_relative_Visibility(View.GONE);

        sharedPreferences = getActivity().getSharedPreferences("pet", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        third.setenabled(false);
        fourth.setenabled(false);
        first.setPetImg(R.drawable.pika);

        third.setPetImg(R.drawable.qiao_unlock);
        fourth.setPetImg(R.drawable.v_unlock);

    }

    private void setListener(){
        first.setEditListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("flag", "1");
                intent.putExtras(bundle);
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        second.setEditListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("flag", "2");
                intent.putExtras(bundle);
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        first_toolbarview.setrelativeclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        first_toolbarview.setmoreclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), first_toolbarview.toolbar_more);
                popup.getMenuInflater().inflate(R.menu.popup_item_other, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.about:
                                Intent aboutIntent = new Intent(getActivity(), AboutAppActivity.class);
                                startActivity(aboutIntent);
                                break;
                            case R.id.login:
                                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                loginIntent.putExtra("relogin", true);
                                startActivity(loginIntent);
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    //要获取Activity中的资源，就必须等Acitivity创建完成以后，所以必须放在onActivityCreated()回调函数中
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        first.setSwitchListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    second.setCheck(false);
                    if (!sharedPreferences.getBoolean("isFirstOn", false)) {
                        editor.putBoolean("isFirstOn", true);
                        editor.putBoolean("isSecondOn", false);
                        editor.commit();
                        System.out.println(sharedPreferences.getBoolean("isFirstOn", false));
                        Intent intent = new Intent(getActivity(), FloatWindowService.class);
                        getActivity().startService(intent);
                        getActivity().finish();
                    }
                } else {
                    editor.putBoolean("isFirstOn", false);
                    editor.commit();

                    Intent intent = new Intent(getActivity(), FloatWindowService.class);
                    getActivity().stopService(intent);
                }
            }
        });

        second.setSwitchListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    first.setCheck(false);
                    if (!sharedPreferences.getBoolean("isSecondOn", false)) {
                        editor.putBoolean("isFirstOn", false);
                        editor.putBoolean("isSecondOn", true);
                        editor.commit();
                        Intent intent = new Intent(getActivity(), FloatWindowService.class);
                        getActivity().startService(intent);
                        getActivity().finish();
                    }
                }
                else{
                    editor.putBoolean("isSecondOn",false);
                    editor.commit();
                    Intent intent = new Intent(getActivity(), FloatWindowService.class);
                    getActivity().stopService(intent);
                }
            }
        });

    }

}
