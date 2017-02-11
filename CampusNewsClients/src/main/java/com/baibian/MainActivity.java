package com.baibian;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baibian.adapter.Guide_adapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.baibian.adapter.NewsFragmentPagerAdapter;
import com.baibian.app.AppApplication;
import com.baibian.bean.ChannelItem;
import com.baibian.bean.ChannelManage;
import com.baibian.fragment.NewsFragment;
import com.baibian.load.refresh;
import com.baibian.tool.BaseTools;
import com.baibian.view.ColumnHorizontalScrollView;
import com.baibian.view.DrawerView;

import java.io.OutputStream;
import java.util.ArrayList;

/**
 *  模拟还原今日头条 --新闻阅读器
 * author:XZY && RA
 */
public class MainActivity extends FragmentActivity {
    /**
     * 自定义HorizontalScrollView
     */
    private ColumnHorizontalScrollView mColumnHorizontalScrollView;
    LinearLayout mRadioGroup_content;
    LinearLayout ll_more_columns;
    RelativeLayout rl_column;
    private ViewPager mViewPager;
    private ImageView button_more_columns;

    /**
     *      * 用户选择的新闻分类列表
     */
    private ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
    /**
     * * 当前选中的栏目
     */
    private int columnSelectIndex = 0;
    /**
     * 左阴影部分
     */
    public ImageView shade_left;
    /**
     *右阴影部分
     */
    public ImageView shade_right;
    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;
    /**
     * Item宽度
     */
    private int mItemWidth = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    protected SlidingMenu side_drawer;
    /**
     * head ??? ???м??loading
     */
    private ProgressBar top_progress;
    /**
     * head ??? ?м????°??
     */
    private ImageView top_refresh;
    /**
     * head ??? ??????? ???
     */
    private ImageView top_head;
    /**
     * head ??? ??????? ???
     */
    private ImageView top_more;
    /**
     * ????CODE
     */
    public final static int CHANNELREQUEST = 1;
    /**
     * ?????????RESULTCODE
     */
    public final static int CHANNELRESULT = 10;
//    private static final int STATE_REFRESHING = 3;

    private NewsFragmentPagerAdapter mAdapter;
    private int position1 = 0;

    /**
     * 实现登陆后更改UI为圆形头像
     */
    private ImageView baibian_btn;//通过百辩账号登录
    private final int LOGIN4_REQUEST=11;//进入login4activity的请求码
    private LinearLayout logout_layout_not_login;//未登录布局
    private LinearLayout login_layout;//带有圆形头像的布局，用来更换原来的布局

    /**
     * 引导界面添加内容
     */
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;//判断是否是第一次登陆使用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StrictMode.setThreadPolicy(new
//                StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//        StrictMode.setVmPolicy(
//                new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        setContentView(R.layout.main);
        mScreenWidth = BaseTools.getWindowsWidth(this);
        mItemWidth = mScreenWidth / 4;// ???Item?????????1/4
        initView();
        initSlidingMenu();
        init_guide();//引导界面的初始化

    }

    public interface OnRefreshListener {
        public void onRefresh();
    }

    private MainActivity.OnRefreshListener mListener;

    public void setOnRefreshListener(MainActivity.OnRefreshListener listener) {
        mListener = listener;
    }

    /**
     * ?????layout???
     */
    private void initView() {
        mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
        mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
        ll_more_columns = (LinearLayout) findViewById(R.id.ll_more_columns);
        rl_column = (RelativeLayout) findViewById(R.id.rl_column);
        button_more_columns = (ImageView) findViewById(R.id.button_more_columns);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        shade_left = (ImageView) findViewById(R.id.shade_left);
        shade_right = (ImageView) findViewById(R.id.shade_right);
        top_head = (ImageView) findViewById(R.id.top_head);
        top_more = (ImageView) findViewById(R.id.top_more);
        top_refresh = (ImageView) findViewById(R.id.top_refresh);
        top_progress = (ProgressBar) findViewById(R.id.top_progress);


//        setChangelView();
        button_more_columns.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent_channel = new Intent(getApplicationContext(), ChannelActivity.class);
                startActivityForResult(intent_channel, CHANNELREQUEST);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        top_head.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (side_drawer.isMenuShowing()) {
                    side_drawer.showContent();
                } else {
                    side_drawer.showMenu();
                }
            }
        });
        top_more.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (side_drawer.isSecondaryMenuShowing()) {
                    side_drawer.showContent();
                } else {
                    side_drawer.showSecondaryMenu();
                }
            }
        });
        top_refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                String name = makeFragmentName(container.getId(), itemId);
//                View viewById = mAdapter.getItem(1).getView().findViewById(R.layout.pull_to_refresh_header);
//                getFragmentManager().findFragmentByTag()
//                View viewById = getSupportFragmentManager().findFragmentById(R.layout.news_fragment).getView().findViewById(R.layout.pull_to_refresh_header);
//                Log.d("position", String.valueOf("android:switcher:" + mViewPager.getId() + ":" + mViewPager.getCurrentItem()));
//                ???getFragmentManager??getSupportFragmentManager??????????????????????????
//                Log.d("position1", String.valueOf(getSupportFragmentManager().findFragmentByTag(fragments.get(1).getTag()).getView()));
//                Animation loadAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.refresh_rotate);
//                ??????????????
                rotateTopRefresh();
//                ?Щδ??????????????????????????fragment???????????????
                NewsFragment fragmentByTag = (NewsFragment) getSupportFragmentManager().findFragmentByTag(fragments.get(mViewPager.getCurrentItem()).getTag());
//                HeadListView headListView = fragmentByTag.getmHeadListView();
////                headListView.mCurrentState = STATE_REFRESHING;
//                ((TextView) headListView.findViewById(R.id.tv_title)).setText("???????...");
//                (headListView.findViewById(R.id.iv_arrow)).clearAnimation();
//                (headListView.findViewById(R.id.iv_arrow)).setVisibility(View.INVISIBLE);
//                headListView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                refresh.refreshHeadView(fragmentByTag);
                mListener.onRefresh();
//                fragmentByTag.refreshData();
//                top_refresh.clearAnimation();
//                ivArrow.clearAnimation();
//                pbProgress.setVisibility(VISIBLE);
//                ivArrow.setVisibility(INVISIBLE);
//                if (mAdapter.fragmentTAG == null) {
//                }
//                viewById.setPadding(0, 0, 0, 0);
//                HeadListView listView = currentFragment.mListView;
////                listView.initHeaderView();
//                View headerView = listView.getHeaderView();
////                View headerView = currentFragment.getListView().getHeaderView();
//                headerView.setPadding(0, 0, 0, 0);
            }
        });
        setChangelView();
    }

    /**
     * 引导界面初始化部分
     */
    private void init_guide(){
        preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
        if (preferences.getBoolean("firststart", true)) {
            editor = preferences.edit();
            //将登录标志位设置为false，下次登录时不在显示首次登录界面
            editor.putBoolean("firststart", false);
            editor.commit();
            Intent intent = new Intent(MainActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void rotateTopRefresh() {
        RotateAnimation rotateAnimation = refresh.refreshAnimation();
        top_refresh.startAnimation(rotateAnimation);
        rotateAnimation.startNow();
    }

    /**
     * ??????????仯??????
     */
    private void setChangelView() {
        initColumnData();
        initTabColumn();
        initFragment();
    }

    /**
     * ???Column??? ????
     */
    private void initColumnData() {
        userChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(AppApplication.getApp().getSQLHelper()).getUserChannel());
    }

    /**
     * ?????Column?????
     */
    private void initTabColumn() {
        mRadioGroup_content.removeAllViews();
        int count = userChannelList.size();
        mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left, shade_right, ll_more_columns, rl_column);
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
//			TextView localTextView = (TextView) mInflater.inflate(R.layout.column_radio_item, null);
            TextView columnTextView = new TextView(this);
            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
//			localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(5, 5, 5, 5);
            columnTextView.setId(i);
            columnTextView.setText(userChannelList.get(i).getName());
            columnTextView.setTextColor(getResources().getColorStateList(R.color.top_category_scroll_text_color_day));
            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                    Toast.makeText(getApplicationContext(), userChannelList.get(v.getId()).getName(), Toast.LENGTH_SHORT).show();
                }
            });
            mRadioGroup_content.addView(columnTextView, i, params);
        }
    }

    /**
     * ????Column????? Tab
     */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            // rg_nav_content.getParent()).smoothScrollTo(i2, 0);
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
            // mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
            // mItemWidth , 0);
        }
        //?ж???????
        for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
            } else {
                ischeck = false;
            }
            checkView.setSelected(ischeck);
        }
    }

    /**
     * ?????Fragment
     */
    private void initFragment() {
        fragments.clear();//???
        int count = userChannelList.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putString("text", userChannelList.get(i).getName());
            data.putInt("id", userChannelList.get(i).getId());
//            data.putString("source", this.getSharedPreferences("channelSource", Context.MODE_PRIVATE).getString(String.valueOf(i+1), "null"));
            NewsFragment newFragment = new NewsFragment();
//            currentFragment = newFragment;
            newFragment.setArguments(data);
            fragments.add(newFragment);
        }
//        getSupportFragmentManager().beginTransaction().add()
        mAdapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(), fragments);
//		mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(pageListener);

    }

    /**
     * ViewPager?л?????????
     */
    public OnPageChangeListener pageListener = new OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            mViewPager.setCurrentItem(position);
            selectTab(position);
            if (position1 != position) {
                top_refresh.clearAnimation();
            }
            position1 = position;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void initSlidingMenu() {
        side_drawer = new DrawerView(this).initSlidingMenu();
        /**
         * 登录切换顶部布局
         */
       login_layout=(LinearLayout) side_drawer.findViewById(R.id.login_layout);
        baibian_btn=(ImageView) side_drawer.findViewById(R.id.baibian_btn);//百辩登录按钮
        logout_layout_not_login=(LinearLayout) side_drawer.findViewById(R.id.logout_layout_not_login);//未登录布局
        baibian_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent_baibian_btn=new Intent(MainActivity.this,Login4Activity.class);
                startActivityForResult(intent_baibian_btn,LOGIN4_REQUEST);
            }
        });
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (side_drawer.isMenuShowing() || side_drawer.isSecondaryMenuShowing()) {
                side_drawer.showContent();
            } else {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, R.string.Press_again_to_exit,
                            Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }
            return true;
        }
        //????MENU??????????????????κβ???
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case CHANNELREQUEST:
                if (resultCode == CHANNELRESULT) {
                    setChangelView();
                }
                break;
            case LOGIN4_REQUEST:
                if(resultCode==LOGIN4_REQUEST){
                    /**
                     * 登录切换布局部分
                     */
                    logout_layout_not_login.setVisibility(View.GONE);//旧布局消失
                    login_layout.setVisibility(View.VISIBLE);//新布局出现
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
    * ?????????Scrollview?????????????????????   public boolean onTouchEvent(MotionEvent event){
     switch (event.getAction()){
     case MotionEvent.ACTION_DOWN:
     //????
     break;
     case MotionEvent.ACTION_MOVE:
     //???
     break;
     case MotionEvent.ACTION_UP:
     //???
     int y=(int) event.getY();
     if (y>=30d){
     imformation_viewPager.setVisibility(View.GONE);
     }
     if(y<=30d&&(imformation_viewPager.getVisibility()==View.VISIBLE)){
     imformation_viewPager.setVisibility(View.VISIBLE);
     }
     break;
     }
     return true;
     }
    */

}