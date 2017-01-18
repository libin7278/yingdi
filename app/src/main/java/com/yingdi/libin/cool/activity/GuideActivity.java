package com.yingdi.libin.cool.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.squareup.leakcanary.RefWatcher;
import com.yingdi.libin.cool.CoreApplication;
import com.yingdi.libin.cool.R;
import com.yingdi.libin.cool.widget.GuideVideoView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class GuideActivity extends Activity {
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    private ViewPager viewPager;
    private Indicator indicator;

    private GuideVideoView mVideoView;

    private Disposable mLoop;

    private int[] images = {R.drawable.p1, R.drawable.p2, R.drawable.p3};

    private int mCurrentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        RefWatcher refWatcher = CoreApplication.getRefWatcher(this);

        mVideoView = (GuideVideoView) findViewById(R.id.guide_videoview);
        viewPager = (ViewPager) findViewById(R.id.guide_viewPager);
        indicator = (Indicator) findViewById(R.id.guide_indicator);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);

        mVideoView.setVideoURI(Uri.parse(getVideoPath()));

        inflate = LayoutInflater.from(getApplicationContext());

        indicatorViewPager.setAdapter(adapter);

        startLoop();

        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                Log.e("TAG", "preItem : " + preItem + "  currentItem : " + currentItem);

                mCurrentPage = currentItem;
                indicator.setCurrentItem(mCurrentPage);
                startLoop();
            }
        });

        refWatcher.watch(indicatorViewPager);
        refWatcher.watch(viewPager);
        refWatcher.watch(indicator);

    }

    private IndicatorViewPager.IndicatorPagerAdapter adapter = new IndicatorViewPager.IndicatorViewPagerAdapter() {

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_guide, container, false);
            }
            return convertView;
        }

        @Override
        public View getViewForPage(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = new View(getApplicationContext());
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            convertView.setBackgroundResource(images[position]);
            return convertView;
        }

        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return images.length;
        }
    };

    /**
     * 获取video文件的路径
     *
     * @return 路径
     */
    private String getVideoPath() {
        return "android.resource://" + this.getPackageName() + "/" + R.raw.intro_video;
    }

    /**
     * 开启定时任务
     */
    private void startLoop() {
        if (null != mLoop) {
            mLoop.dispose();
        }

        mLoop = Flowable.interval(0, 6 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mVideoView.seekTo(mCurrentPage * 6 * 1000);
                        if (!mVideoView.isPlaying()) {
                            mVideoView.start();
                        }
                    }
                });
    }
}
