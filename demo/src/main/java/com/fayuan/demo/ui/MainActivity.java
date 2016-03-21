package com.fayuan.demo.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.fayuan.demo.R;
import com.fayuan.bannerview.BannerView;
import com.fayuan.slidingview.SlidingView;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private ImageView mOpenMenu;

    private BannerView mBannerView;

	private SlidingView mSlidingView;

    private ListView mListView;

    private GridView mGridView;

    private String[] strings = {
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww",
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww",
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww",
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww",
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww",
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww",
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww",
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww",
            "wwww", "wwwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww", "wwww"
    };

    private String[] strings2 = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i"
    };

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
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings));

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings2));


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