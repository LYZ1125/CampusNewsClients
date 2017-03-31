package com.baibian;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.content.Context;
import com.baibian.adapter.Tablayout_Adapter_Right;
import com.baibian.adapter.Users_Viwepager_Adapter;
import com.baibian.fragment.BaiBianImformationFragment;
import com.baibian.fragment.BaiBianStatementFragment;
import com.baibian.fragment.ClassificationFragment;
import com.baibian.fragment.FocusFragment;
import com.baibian.fragment.HotFragment;
import com.baibian.fragment.IntegrationFragment;
import com.baibian.fragment.RealTimeFragment;

import java.util.ArrayList;
import java.util.List;

public class UsersImformationActivity extends Activity {

    private Button BB_state_btn;
    private Button BB_imformation_btn;
    private Button choise_direction_back;
    private ViewPager users_imformation_pager;
    private Users_Viwepager_Adapter fAdapter;                               //定义adapter
    private List<Fragment> list_fragment;                                //定义要装fragment的列表
    private List<String> list_title;                                     //tab名称列表
    private List<View> viewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.usersimformationlayout);

        initview();
        init_viewpager();
        choise_direction_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        /**
         * 这种写法只有办法支持俩个Button如果进入了三个button的时候，请换一种使用方法
         */
        BB_state_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users_imformation_pager.arrowScroll(1);//向前翻页
            }
        });
        BB_imformation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users_imformation_pager.arrowScroll(2);//向后翻页
            }
        });
        users_imformation_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==0){

                    BB_state_btn.setBackground(getResources().getDrawable(R.drawable.yellow_rect));
                    BB_imformation_btn.setBackground(getResources().getDrawable(R.drawable.white_rect));
                }
                else if(position==1){
                    BB_imformation_btn.setBackground(getResources().getDrawable(R.drawable.yellow_rect));
                    BB_state_btn.setBackground(getResources().getDrawable(R.drawable.white_rect));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initview() {
        choise_direction_back = (Button) findViewById(R.id.choise_direction_back);
        users_imformation_pager = (ViewPager) findViewById(R.id.users_imformation_pager);

         BB_state_btn=(Button) findViewById(R.id.BB_state_btn);
        BB_imformation_btn=(Button) findViewById(R.id.BB_imformation_btn);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    private void init_viewpager() {
        viewList=new ArrayList<View>();
        /**
         * 2个View分别对应三个界面
         */
        View view1=View.inflate(this,R.layout.bb_statement_layout,null);
        View view2=View.inflate(this,R.layout.bb_imformation_layout,null);
        viewList.add(view1);
        viewList.add(view2);
        fAdapter=new Users_Viwepager_Adapter(viewList,UsersImformationActivity.this);
        //viewpager加载adapter
        users_imformation_pager.setAdapter(fAdapter);
        users_imformation_pager.setOffscreenPageLimit(0);
    }
}
