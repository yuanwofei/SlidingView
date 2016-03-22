package com.fayuan.slidingview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.fayuan.slidingview.utils.Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fayuan on 2015/8/5.
 */
public class SlidingView extends HorizontalScrollView {

    private static final String TAG = "SlidingView";

    public static final int SLIDING_MENU_OPEN = 0x111;

    public static final int SLIDING_MENU_CLOSE = 0x112;

    public static final int DEFAULT_MENU_RIGHT_PADDING = 80;

    private LinearLayout mContainer;

    private View mMenu;

    private View mContent;

    private int mScreenWidth;

    private int mMenuWidth;

    private int mMenuRightPadding;

    private Rect mContentRect;

    private boolean isFirst = true;

    private boolean isMenuOpen;

    private boolean isContentViewClicked;

    private float downX, downY;

    private boolean isBeingHorizontalDrag = false;

    private boolean isBeingVerticalDrag = false;

    private boolean isUnableToDrag = false;

    private int touchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;

    private OnSlidingMenuListener onSlidingMenuListener;

    private List<View> noInterceptTouchEventChildViews = new ArrayList<>();

    public SlidingView(Context context) {
        this(context, null);
    }

    public SlidingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        mContentRect = new Rect();

        mScreenWidth = Utils.getScreenWidth(getContext());
        mMenuRightPadding = Utils.dip2px(getContext(), DEFAULT_MENU_RIGHT_PADDING);
        mMenuWidth = mScreenWidth - mMenuRightPadding;

        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public void addView(View child) {

        if (child == null) {
            throw new IllegalArgumentException("Cannot add a null child view to SlidingView");
        }

        if (mContainer == null) {
            mContainer = new LinearLayout(getContext());
            mContainer.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            mContainer.setOrientation(LinearLayout.HORIZONTAL);
        }

        mContainer.addView(child);
        if (mContainer.getChildCount() > 2) {
            throw new IllegalStateException("SlidingView can host only two direct child");
        }

        if (getChildCount() == 0) {
            super.addView(mContainer);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child == mContainer) {
            super.addView(child);
        } else {
            addView(child);
        }
    }

    @Override
    public void addView(View child, int index) {
        if (child == mContainer) {
            super.addView(child, index);
        } else {
            addView(child);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child == mContainer) {
            super.addView(child, index, params);
        } else {
            addView(child);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isFirst) {
            mMenu = mContainer.getChildAt(0);
            mContent = mContainer.getChildAt(1);

            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = mScreenWidth;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (isFirst) {
            isFirst = false;
            scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isBeingHorizontalDrag = false;
                isBeingVerticalDrag = false;
                isUnableToDrag = false;
                break;

            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();

                isContentViewClicked = false;
                if (isMenuOpen) {
                    int blank = (mContent.getHeight() -
                            (int) (mContent.getHeight() * ViewHelper.getScaleY(mContent))) / 2;
                    mContentRect.set(mContent.getLeft(), mContent.getTop() + blank,
                            mContent.getRight(), mContent.getBottom() - blank);

                    if (mContentRect.contains((int) ev.getX(), (int) ev.getY())) {
                        isContentViewClicked = true;
                        return true;
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:
                //不拦截左右滑动的子View
                for (View view : noInterceptTouchEventChildViews) {
                    view.getGlobalVisibleRect(mContentRect);
                    if (mContentRect.contains((int) ev.getX(), (int) ev.getY())) {
                        return false;
                    }
                }
                
                //不拦截上下滑动的子View
                if (Math.abs(ev.getY() - downY) > touchSlop) {
                    return false;
                }

                //在侧滑菜单在打开或不开打开的情况下，禁止SlidingView左右滑动，不禁止子View的左右滑动
                float deltaX = ev.getX() - downX;
                if (Math.abs(deltaX) > touchSlop) {
                    if ((isMenuOpen && deltaX > 0) || (!isMenuOpen && deltaX < 0)) {
                        Log.d(TAG, "Child view drag");
                        return false;
                    }
                }
            break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        createVelocityTracker(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float deltaX = ev.getX() - downX;
                float deltaY = ev.getY() - downY;

                downX = ev.getX();
                downY = ev.getY();

                if (Math.abs(deltaX) > touchSlop || Math.abs(deltaY) > touchSlop) {
                    Log.d(TAG, "Cancel the click event");
                    isContentViewClicked = false;
                }

                if (!isUnableToDrag && !isBeingHorizontalDrag && !isBeingVerticalDrag && Math.abs(deltaX) > touchSlop) {
                    if (isMenuOpen && deltaX > 0 || !isMenuOpen && deltaX < 0) {
                        isUnableToDrag = true;
                    }
                }
                //在侧滑菜单在打开或不开打开的情况下，禁止SlidingView左右滑动
                if (isUnableToDrag) {
                    Log.d(TAG, "horizontal unableToDrag");
                    return true;
                }

                if (!isBeingHorizontalDrag && !isBeingVerticalDrag && Math.abs(deltaX) > touchSlop) {
                    isBeingHorizontalDrag = true;
                    isBeingVerticalDrag = false;
                }

                if (isBeingHorizontalDrag) {
                    Log.d(TAG, "horizontal dragging");
                    break;
                }

                if (!isBeingVerticalDrag && Math.abs(deltaY) > touchSlop){
                    isBeingVerticalDrag = true;
                    isBeingHorizontalDrag = false;
                }
                if (isBeingVerticalDrag) {
                    Log.d(TAG, "vertical dragging");
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //fling滑动处理
                if (isBeingHorizontalDrag && !isBeingVerticalDrag && !isUnableToDrag) {
                    isBeingHorizontalDrag = false;
                    mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int xVelocity = (int) mVelocityTracker.getXVelocity();
                    recycleVelocityTracker();
                    if (Math.abs(xVelocity) > mMinimumVelocity) {
                        if (xVelocity > 0) {
                            openMenu();
                        } else {
                            closeMenu();
                        }
                        return true;
                    }
                }
                isBeingHorizontalDrag = false;
                isBeingVerticalDrag = false;
                isUnableToDrag = false;

                //单击mContent
                if (isContentViewClicked && getScrollX() == 0) {
                    Log.d(TAG, "perform click");
                    closeMenu();
                    return true;
                }

                //滑动释放时处理
                if (getScrollX() >= mMenuWidth / 2) {
                    closeMenu();
                    return true;
                } else {
                    openMenu();
                    return true;
                }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int hscroll, int t, int oldl, int oldt) {
        super.onScrollChanged(hscroll, t, oldl, oldt);

        float scale = hscroll * 1.0f / mMenuWidth;
        final float rightScale = 0.7f + 0.3f * scale;
        float leftScale = 1.0f - scale * 0.3f;
        float leftAlpha = 0.6f + 0.4f * (1 - scale);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(mMenu, "scaleX", leftScale),
                ObjectAnimator.ofFloat(mMenu, "scaleY", leftScale),
                ObjectAnimator.ofFloat(mMenu, "alpha", leftAlpha),
                ObjectAnimator.ofFloat(mMenu, "translationX", mMenuWidth * scale * 0.7f),

                ObjectAnimator.ofFloat(mContent, "scaleX", rightScale),
                ObjectAnimator.ofFloat(mContent, "scaleY", rightScale),
                ObjectAnimator.ofFloat(mContent, "pivotX", 0),
                ObjectAnimator.ofFloat(mContent, "pivotY", mContent.getHeight() / 2)
        );
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (rightScale == 1.0f) {
                    isMenuOpen = false;

                    if (onSlidingMenuListener != null) {
                        onSlidingMenuListener.onSliding(SLIDING_MENU_CLOSE);
                    }
                } else if (rightScale == 0.7f){
                    isMenuOpen = true;

                    if (onSlidingMenuListener != null) {
                        onSlidingMenuListener.onSliding(SLIDING_MENU_OPEN);
                    }
                }
            }
        });
        set.setDuration(0).start();
    }

    public void openMenu() {
        smoothScrollTo(0, 0);
    }

    public void closeMenu() {
        smoothScrollTo(mMenuWidth, 0);
    }

    public void toggle() {
        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    /**添加不拦截TouchEvent事件的子View*/
    public void addNoInterceptTouchEventChildView(View view) {
        noInterceptTouchEventChildViews.add(view);
    }

    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void setMenuRightPadding(int mMenuRightPadding) {
        this.mMenuRightPadding = mMenuRightPadding;
        isFirst = true;
        requestLayout();
    }

    public interface OnSlidingMenuListener {
        void onSliding(int flag);
    }

    public void setOnSlidingMenuListener(OnSlidingMenuListener l) {
        this.onSlidingMenuListener = l;
    }
}