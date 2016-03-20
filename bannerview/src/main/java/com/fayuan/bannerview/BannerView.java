package com.fayuan.bannerview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A BannerView simply shows a common of banner.
 * <ul>
 * Usage:
 *  <li>
 *      In you layout file, set like below
 *     <code>
 *         <com.fayuan.bannerview.BannerView
 *            android:id="@+id/banner_view"
 *            android:layout_width="match_parent"
 *            android:layout_height="150dp"/>
 *     </code>
 *  </li>
 *  <li>
 *
 *  </li>
 * </ul>
 *
 *
 * @author fayuan
 */
public class BannerView extends RelativeLayout {

	private LinearLayout mIndicatorLayout;
	
	private ViewPager mViewPager;

	private OnCreateBannerViewListener listener;
	
	private int pageCount;
	
	private int curIndex;
	
	private Timer timer;

	public BannerView(Context context) {
		super(context);
        init();
	}

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

	private void init() {
        initViewPager();
        initIndicatorView();
	}

    private void initViewPager() {
        mViewPager = new ViewPager(getContext());
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setSelectedIndicator(position % pageCount);
            }
        });

        RelativeLayout.LayoutParams matchParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mViewPager, matchParams);
    }

    private void initIndicatorView() {
        RelativeLayout.LayoutParams wrapParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        wrapParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        wrapParams.bottomMargin = dip2px(3);

        mIndicatorLayout = new LinearLayout(getContext());
        mIndicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        mIndicatorLayout.setLayoutParams(wrapParams);
    }

    /**显示BannerView，必须调用该方法*/
	public void display(int pageCount) {
        if (listener == null) {
            throw new IllegalStateException("You must call this method after" +
                    " setOnCreateBannerViewListener");
        }

        this.pageCount = pageCount;
        if (pageCount < 1) {
            throw new IllegalStateException("pageCount < 1, " +
                    "BannerView must have at least one page");
        }

        initIndicator(pageCount);
        mViewPager.setAdapter(new BannerAdapter());
        mViewPager.setCurrentItem(50 - 50 % pageCount);

        startAutoScroll();
    }

    public interface OnCreateBannerViewListener {
		View onCreateBannerView(int position);
	}
	
	public void setOnCreateBannerViewListener(OnCreateBannerViewListener l) {
		this.listener = l;
	}

	private void initIndicator(int pageCount) {
		mIndicatorLayout.removeAllViews();

        if (pageCount <= 1) {
            return;
        }

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		
		for (int i = 0; i < pageCount; i++) {
			ImageView indicator = new ImageView(getContext());
			indicator.setId(i);
			indicator.setScaleType(ImageView.ScaleType.CENTER);

			indicator.setLayoutParams(params);
			indicator.setPadding(5, 5, 5, 5);
			indicator.setImageResource(R.drawable.banner_indicator_selector);
			
			mIndicatorLayout.addView(indicator);
		}
		
		addView(mIndicatorLayout);
	}

    private void setSelectedIndicator(int index) {
        if (pageCount <= 1) {
            return;
        }

        mIndicatorLayout.getChildAt(curIndex).setSelected(false);
        mIndicatorLayout.getChildAt(index).setSelected(true);

        curIndex = index;
    }

    private class BannerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view;
            if (listener != null) {
                view = listener.onCreateBannerView(position % pageCount);
            } else {
                throw new NullPointerException("OnCreateBannerViewListener can't be null");
            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return pageCount > 1 ? Integer.MAX_VALUE : 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }

    /**开始自动滚动BannerView，可以多次调用*/
    public void startAutoScroll() {
        startAutoScroll(2000, 3000);
    }

    /**开始自动滚动BannerView，可以多次调用*/
    public void startAutoScroll(long delay, long period) {
        if (pageCount <= 1 || listener == null) {
            return;
        }

        //开始之前先停止之前的，保证任何时候只有一个在运行
        stopAutoScroll();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                    }
                });
            }
        }, 2000, 3000);
    }

    /**停止自动滚动BannerView*/
    public void stopAutoScroll() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private int dip2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}