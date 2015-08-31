package com.wonderful.lion.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wonderful.lion.R;


/**
 * Created by Sun Ruichuan on 2015/8/29.
 */
public class RefreshListView extends ListView implements OnScrollListener {

    private float mDownY;
    private float mMoveY;

    private int mHeaderHeight;
    private int realHeaderPadding;
    private int mCurrentScrollState;

    // 头部显示高度
    private final static int NONE_DISPLAY = 0;
    private final static int HALF_DISPLAY = 1;
    private final static int TOTAL_DISPLAY = 2;

    // 未加载状态
    private final static int NONE_PULL_REFRESH = 0; // 未加载，隐藏全部头部
    private final static int HALF_PULL_REFRESH = 1; // 未加载，隐藏部分头部
    private final static int OVER_PULL_REFRESH = 2; // 未加载，滑开头部
    // 加载状态下拉
    private final static int NONE_PULL_REFRESHING = 3; // 加载状态中，隐藏全部头部
    private final static int HALF_PULL_REFRESHING = 4; // 加载状态中，隐藏部分头部
    private final static int OVER_PULL_REFRESHING = 5; // 加载状态中，滑开头部
    // 加载完成状态
    private final static int NONE_PULL_REFRESHED = 6; // 加载完成，隐藏全部头部
    private final static int HALF_PULL_REFRESHED = 7; // 加载完成，隐藏部分头部
    private final static int OVER_PULL_REFRESHED = 8; // 加载完成，滑开头部
    private int currentRefreshState = NONE_PULL_REFRESH; // 记录当前刷新状态

    // 界面加载状态
    private final static int REFRESH_ORIGINAL = 0; // 未加载状态
    private final static int REFRESH_RETURNING = 1; // 界面反弹中
    private final static int REFRESH_REFRESHING = 2; // 加载数据中
    private final static int REFRESH_DONE = 3; // 加载数据完成
    public int currentHeaderState = REFRESH_ORIGINAL; // 记录当前数据加载状态

    private final static int DEFAULT_REFRESH_TIME = 1;
    private final static int DEFAULT_PULL_DISTANCE_DEVIDE = 3;

    private boolean headVisible = false;
    private boolean screenTouched = false;
    private boolean headBegin = false;

    private LinearLayout mHeaderLinearLayout = null;
    private TextView mHeaderTextView = null;
    private ImageView mHeaderPullDownImageView = null;
    private ImageView mHeaderLoadingImage = null;
    private ImageView mHeaderRefreshOkImage = null;
    private RefreshListener mRefreshListener = null;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;
    private boolean isBack = false;
    private Handler handler = new Handler();

    public void setOnRefreshListener(RefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(final Context context) {
        mHeaderLinearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.refresh_list_header, null);
        addHeaderView(mHeaderLinearLayout);
        mHeaderTextView = (TextView) findViewById(R.id.refresh_list_header_text);
        mHeaderPullDownImageView = (ImageView) findViewById(R.id.refresh_list_header_pull_down);
        mHeaderLoadingImage = (ImageView) findViewById(R.id.refresh_list_header_loading);
        mHeaderRefreshOkImage = (ImageView) findViewById(R.id.refresh_list_header_success);

        setSelection(1);
        setOnScrollListener(this);
        measureView(mHeaderLinearLayout);
        mHeaderHeight = mHeaderLinearLayout.getMeasuredHeight();
        mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), -mHeaderHeight, mHeaderLinearLayout.getPaddingRight(),
                mHeaderLinearLayout.getPaddingBottom());
        realHeaderPadding = -mHeaderHeight;

        animation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(150);
        animation.setFillAfter(true);// 特效animation设置

        reverseAnimation = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(150);
        reverseAnimation.setFillAfter(true);// 特效reverseAnimation设置
    }

    private int getCurrentHeaderDisplayState() {
        if (mHeaderLinearLayout.getBottom() >= 0 && mHeaderLinearLayout.getBottom() < mHeaderHeight) {
            return HALF_DISPLAY;
        } else if (mHeaderLinearLayout.getBottom() >= mHeaderHeight) {
            return TOTAL_DISPLAY;
        } else {
            return NONE_DISPLAY;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
            if (currentHeaderState == REFRESH_ORIGINAL) {
                if (firstVisibleItem == 0 && getCurrentHeaderDisplayState() == HALF_DISPLAY) {
                    if (currentRefreshState == NONE_PULL_REFRESH) {
                        mHeaderTextView.setText(R.string.app_list_header_refresh_down);
                        mHeaderPullDownImageView.setVisibility(View.VISIBLE);
                        mHeaderLoadingImage.setVisibility(View.GONE);
                        mHeaderRefreshOkImage.setVisibility(View.GONE);
                        currentRefreshState = HALF_PULL_REFRESH;
                        headBegin = true;
                        mDownY = mMoveY;
                    } else if (currentRefreshState == OVER_PULL_REFRESH) {
                        mHeaderTextView.setText(R.string.app_list_header_refresh_down);
                        currentRefreshState = HALF_PULL_REFRESH;
                        headBegin = true;
                    }
                    if (isBack) {
                        isBack = false;
                        mHeaderPullDownImageView.clearAnimation();
                        mHeaderPullDownImageView.startAnimation(reverseAnimation);
                    }
                } else if (firstVisibleItem == 0 && getCurrentHeaderDisplayState() == TOTAL_DISPLAY) {
                    isBack = true;
                    if (currentRefreshState == HALF_PULL_REFRESH || currentRefreshState == NONE_PULL_REFRESH) {
                        currentRefreshState = OVER_PULL_REFRESH;
                        mHeaderTextView.setText(R.string.app_list_header_refresh);
                        mHeaderPullDownImageView.clearAnimation();
                        mHeaderPullDownImageView.startAnimation(animation);
                    }
                } else if (firstVisibleItem != 0) {
                    resetHeaderPadding();
                    currentRefreshState = NONE_PULL_REFRESH;
                }
            } else if (currentHeaderState == REFRESH_RETURNING) {
                // 回弹状态，暂不做处理
                if (firstVisibleItem == 0 && getCurrentHeaderDisplayState() == HALF_DISPLAY) {
                } else if (firstVisibleItem == 0 && getCurrentHeaderDisplayState() == TOTAL_DISPLAY) {
                } else if (firstVisibleItem != 0) {
                }
            } else if (currentHeaderState == REFRESH_REFRESHING) {
                if (firstVisibleItem == 0 && getCurrentHeaderDisplayState() == HALF_DISPLAY) {
                    if (currentRefreshState == NONE_PULL_REFRESHING) {
                        currentRefreshState = HALF_PULL_REFRESHING;
                        headBegin = true;
                        mDownY = mMoveY;
                    } else if (currentRefreshState == OVER_PULL_REFRESHING) {
                        currentRefreshState = HALF_PULL_REFRESHING;
                        headBegin = true;
                    }
                } else if (firstVisibleItem == 0 && getCurrentHeaderDisplayState() == TOTAL_DISPLAY) {
                    currentRefreshState = OVER_PULL_REFRESHING;
                } else if (firstVisibleItem != 0) {
                    resetHeaderPadding();
                    currentRefreshState = NONE_PULL_REFRESHING;
                }
            } else if (currentHeaderState == REFRESH_DONE) {
                if (firstVisibleItem == 0 && getCurrentHeaderDisplayState() == HALF_DISPLAY) {
                    if (currentRefreshState == NONE_PULL_REFRESHED) {
                        currentRefreshState = HALF_PULL_REFRESHED;
                        headBegin = true;
                        mDownY = mMoveY;
                        realHeaderPadding = mHeaderLinearLayout.getPaddingTop();
                    } else if (currentRefreshState == OVER_PULL_REFRESHED) {
                        currentRefreshState = HALF_PULL_REFRESHED;
                        headBegin = true;
                    }
                } else if (firstVisibleItem == 0 && getCurrentHeaderDisplayState() == TOTAL_DISPLAY) {
                    if (currentRefreshState == NONE_PULL_REFRESHED || currentRefreshState == HALF_PULL_REFRESHED) {
                        currentRefreshState = OVER_PULL_REFRESHED;
                    }
                } else if (firstVisibleItem != 0) {
                    resetHeaderPadding();
                    currentRefreshState = NONE_PULL_REFRESHED;
                }
            }
        }

        if (mCurrentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0) {
            setSelection(1);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 根据界面当前状态，改变mDownY
                mDownY = ev.getY();
                screenTouched = true;
                handler.removeCallbacks(headHideAnimation);
                handler.removeCallbacks(headBackAnimation);
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = ev.getY();
                screenTouched = true;
                if (mCurrentScrollState == SCROLL_STATE_IDLE) {
                    mCurrentScrollState = SCROLL_STATE_TOUCH_SCROLL;
                }

                if (headBegin) {
                    if (currentHeaderState == REFRESH_ORIGINAL) {
                        mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), -mHeaderHeight
                                        + (int) ((mMoveY - mDownY) / DEFAULT_PULL_DISTANCE_DEVIDE), mHeaderLinearLayout.getPaddingRight(),
                                mHeaderLinearLayout.getPaddingBottom());
                    } else if (currentHeaderState == REFRESH_REFRESHING && headVisible == true) {
                        mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), (int) ((mMoveY - mDownY) / DEFAULT_PULL_DISTANCE_DEVIDE),
                                mHeaderLinearLayout.getPaddingRight(), mHeaderLinearLayout.getPaddingBottom());
                    } else if (currentHeaderState == REFRESH_REFRESHING && headVisible == false) {
                        mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), -mHeaderHeight
                                        + (int) ((mMoveY - mDownY) / DEFAULT_PULL_DISTANCE_DEVIDE), mHeaderLinearLayout.getPaddingRight(),
                                mHeaderLinearLayout.getPaddingBottom());
                    } else if (currentHeaderState == REFRESH_DONE) {
                        mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), realHeaderPadding
                                        + (int) ((mMoveY - mDownY) / DEFAULT_PULL_DISTANCE_DEVIDE), mHeaderLinearLayout.getPaddingRight(),
                                mHeaderLinearLayout.getPaddingBottom());
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                screenTouched = false;
                refreshViewByPullState();
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void refreshViewByPullState() {
        switch (currentRefreshState) {
            case NONE_PULL_REFRESH:
                resetHeaderPadding();
                break;
            case HALF_PULL_REFRESH:
                handler.postDelayed(headHideAnimation, DEFAULT_REFRESH_TIME);
                break;
            case OVER_PULL_REFRESH:
                changeHeaderToRefreshing();
                if (mRefreshListener != null) {
                    mRefreshListener.refreshing();
                }
                handler.postDelayed(headBackAnimation, DEFAULT_REFRESH_TIME);
                break;
            case NONE_PULL_REFRESHING:
                resetHeaderPadding();
                break;
            case HALF_PULL_REFRESHING:
                handler.postDelayed(headHideAnimation, DEFAULT_REFRESH_TIME);
                break;
            case OVER_PULL_REFRESHING:
                handler.postDelayed(headBackAnimation, DEFAULT_REFRESH_TIME);
                break;
            case NONE_PULL_REFRESHED:
                resetHeaderPadding();
                changeHeaderToOriginal();
                currentRefreshState = NONE_PULL_REFRESH;
                break;
            case HALF_PULL_REFRESHED:
                handler.postDelayed(headHideAnimation, DEFAULT_REFRESH_TIME);
                break;
            case OVER_PULL_REFRESHED:
                handler.postDelayed(headHideAnimation, DEFAULT_REFRESH_TIME);
                break;
            default:
                break;
        }
    }

    private void resetHeaderPadding() {
        headVisible = false;
        headBegin = false;
        mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), -mHeaderHeight, mHeaderLinearLayout.getPaddingRight(),
                mHeaderLinearLayout.getPaddingBottom());
    }

    private void changeHeaderToRefreshing() {
        mHeaderTextView.setText(R.string.app_list_loading);
        mHeaderPullDownImageView.clearAnimation();
        mHeaderPullDownImageView.setVisibility(View.GONE);
        mHeaderLoadingImage.setVisibility(View.VISIBLE);
        mHeaderRefreshOkImage.setVisibility(View.GONE);
        currentHeaderState = REFRESH_REFRESHING;
        currentRefreshState = OVER_PULL_REFRESHING;
        headVisible = true;
    }

    public void changeHeaderToRefershed() {
        currentHeaderState = REFRESH_DONE;
        mHeaderTextView.setText(R.string.app_list_refresh_done);
        mHeaderPullDownImageView.clearAnimation();
        mHeaderPullDownImageView.setVisibility(View.GONE);
        mHeaderLoadingImage.setVisibility(View.GONE);
        mHeaderRefreshOkImage.setVisibility(View.VISIBLE);

        if (this.getFirstVisiblePosition() == 0) {
            currentRefreshState = OVER_PULL_REFRESHED;
        } else if (this.getFirstVisiblePosition() != 0) {
            currentRefreshState = NONE_PULL_REFRESHED;
        }
        // 重新设置开始滑动位置
        mDownY = mMoveY;
        realHeaderPadding = mHeaderLinearLayout.getPaddingTop();
        if (!screenTouched) {
            handler.postDelayed(headHideAnimation, 500);
        }
    }

    private void changeHeaderToOriginal() {
        isBack = false;
        mHeaderTextView.setText(R.string.app_list_header_refresh_down);
        mHeaderPullDownImageView.clearAnimation();
        mHeaderPullDownImageView.setVisibility(View.VISIBLE);
        mHeaderLoadingImage.setVisibility(View.GONE);
        mHeaderRefreshOkImage.setVisibility(View.GONE);
        currentHeaderState = REFRESH_ORIGINAL;
        currentRefreshState = NONE_PULL_REFRESH;
    }

    public void refreshViewByRefreshingState() {
        switch (currentHeaderState) {
            case REFRESH_ORIGINAL:
                break;
            case REFRESH_RETURNING:
                break;
            case REFRESH_REFRESHING:
                break;
            case REFRESH_DONE:
                changeHeaderToRefershed();
                break;
            default:
                break;
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        setSelection(1);
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    Runnable headHideAnimation = new Runnable() {
        public void run() {
            if (mHeaderLinearLayout.getBottom() > 0 && getFirstVisiblePosition() == 0) {
                int paddingTop = (int) (-mHeaderHeight * 0.3f + mHeaderLinearLayout.getPaddingTop() * 0.7f) - 1;
                if (paddingTop < -mHeaderHeight) {
                    paddingTop = -mHeaderHeight;
                }
                mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), paddingTop, mHeaderLinearLayout.getPaddingRight(),
                        mHeaderLinearLayout.getPaddingBottom());
                handler.postDelayed(headHideAnimation, DEFAULT_REFRESH_TIME);
            } else {
                resetHeaderPadding();
                if (currentRefreshState != NONE_PULL_REFRESH && currentRefreshState != NONE_PULL_REFRESHING
                        && currentRefreshState != NONE_PULL_REFRESHED) {
                    setSelection(1);
                }

                if (currentHeaderState == REFRESH_DONE) {
                    changeHeaderToOriginal();
                } else if (currentHeaderState == REFRESH_REFRESHING) {
                    currentRefreshState = NONE_PULL_REFRESHING;
                } else if (currentHeaderState == REFRESH_ORIGINAL) {
                    currentRefreshState = NONE_PULL_REFRESH;
                }

                handler.removeCallbacks(headHideAnimation);
            }
        }
    };

    Runnable headBackAnimation = new Runnable() {
        public void run() {
            if (mHeaderLinearLayout.getPaddingTop() > 1) {
                mHeaderLinearLayout.setPadding(mHeaderLinearLayout.getPaddingLeft(), (int) (mHeaderLinearLayout.getPaddingTop() * 0.75f),
                        mHeaderLinearLayout.getPaddingRight(), mHeaderLinearLayout.getPaddingBottom());
                handler.postDelayed(headBackAnimation, DEFAULT_REFRESH_TIME);
            } else {
                headVisible = true;
                handler.removeCallbacks(headBackAnimation);
            }
        }
    };

    public interface RefreshListener {
        // 正在下拉刷新
         void refreshing();
    }
}
