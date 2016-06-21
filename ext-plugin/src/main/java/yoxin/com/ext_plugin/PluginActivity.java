package yoxin.com.ext_plugin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import yoxin.com.ext_plugin.controller.PagerAdapter;
import yoxin.com.ext_plugin.utils.PluginConsts;


/**
 * 主页面, 使用TabLayout+ViewPager.
 * 子页面, 使用RecyclerView.
 *
 * @author wangchenlong
 */
public class PluginActivity extends AppCompatActivity {

    TabLayout mTlTabs; // Tabs
    ViewPager mVpContainer; // ViewPager
    Button mBGoto; // 跳转插件的按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        mTlTabs = (TabLayout) findViewById(R.id.main_tl_tabs); // Tabs
        mVpContainer = (ViewPager) findViewById(R.id.main_vp_container); // ViewPager
        mBGoto = (Button) findViewById(R.id.main_b_goto); // 跳转插件的按钮
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        mVpContainer.setAdapter(adapter);
        mTlTabs.setupWithViewPager(mVpContainer);
        mBGoto.setOnClickListener(this::gotoPlugin);


        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra(PluginConsts.MASTER_EXTRA_STRING) != null) {
            String words = "say: " + intent.getStringExtra(PluginConsts.MASTER_EXTRA_STRING);
            Toast.makeText(this, words, Toast.LENGTH_SHORT).show();
        }
    }

    // 跳转控件
    private void gotoPlugin(View view) {
        if (isActionAvailable(view.getContext(), PluginConsts.PLUGIN_ACTION_MAIN)) {
            Intent intent = new Intent(PluginConsts.PLUGIN_ACTION_MAIN);
            intent.putExtra(PluginConsts.PLUGIN_EXTRA_STRING, "Hello, My Plugin!");
            startActivity(intent);
        } else {
            Toast.makeText(view.getContext(), "跳转失败", Toast.LENGTH_SHORT).show();
        }
    }

    // Action是否允许
    public static boolean isActionAvailable(Context context, String action) {
        Intent intent = new Intent(action);
        return context.getPackageManager().resolveActivity(intent, 0) != null;
    }
}
