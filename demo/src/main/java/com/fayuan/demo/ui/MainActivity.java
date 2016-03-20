package com.fayuan.demo.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fayuan.demo.R;
import com.fayuan.bannerview.BannerView;
import com.fayuan.slidingview.SlidingView;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private ImageView mOpenMenu;

    private BannerView mBannerView;

	private SlidingView mSlidingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        initView();
        initBanner();
	}

	private void initView() {
		mSlidingView = (SlidingView) findViewById(R.id.slidingview);
        mBannerView = (BannerView) findViewById(R.id.banner_view);
        mOpenMenu = (ImageView) findViewById(R.id.open_menu);

        mOpenMenu.setOnClickListener(this);
	}

    private void initBanner() {
        mSlidingView.addNoInterceptTouchEventChildView(mBannerView);

        final int[] resIds = new int[] {
                R.drawable.banner_1,
                R.drawable.banner_2,
                R.drawable.banner_3
        };
        mBannerView.setOnCreateBannerViewListener(new BannerView.OnCreateBannerViewListener() {
            @Override
            public View onCreateBannerView(final int position) {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("TAG", position + " positon view is clicked");
                    }
                });
                imageView.setImageResource(resIds[position]);
                return imageView;
            }
        });
        mBannerView.display(resIds.length);
    }

    @Override
    public void onResume() {
        super.onResume();

        mBannerView.startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();

        mBannerView.stopAutoScroll();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_menu:
                mSlidingView.openMenu();
                break;

            default:
                break;
        }
    }
}