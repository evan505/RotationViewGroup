package com.ebeijia.rotationanimation.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.ebeijia.rotationanimation.R;

/**
 * Created by zyf on 2016/4/21.
 */
public class RotationViewGroup extends ViewGroup {

    private boolean isMenuClose = true;

    private int radius;//动画旋转的路径半径

    private int bottom_padding;//主图标的下边距

    private View centerButton;//中心的按钮

    private boolean isFirst = true;

    private int childCount;

    private int childHeight;
    private int childWidth;

    private int centerX;
    private int centerY;

    private long durationMillis=200;


    public RotationViewGroup(Context context) {
        super(context);
    }

    public RotationViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RotationViewGroup);
        radius = (int) a.getDimension(R.styleable.RotationViewGroup_radius, 120);
        bottom_padding = (int) a.getDimension(R.styleable.RotationViewGroup_bottom_padding, 40);
        a.recycle();
    }

    public RotationViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RotationViewGroup, defStyleAttr, 0);
        radius = (int) a.getDimension(R.styleable.RotationViewGroup_radius, 120);
        bottom_padding = (int) a.getDimension(R.styleable.RotationViewGroup_bottom_padding, 40);
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        } else {
            width = widthSize;
        }

        childCount = getChildCount();


        for (int i = 0; i < childCount; i++) {
            // mesure child
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            childWidth = childWidth > childView.getMeasuredWidth() ? childWidth : childView.getMeasuredWidth();
            childHeight = childHeight > childView.getMeasuredHeight() ? childHeight : childView.getMeasuredHeight();
        }

        height = childHeight * 3 / 2 + radius + bottom_padding;
        Log.i("tag", "   childWidth " + childWidth +"   childHeight " + childHeight + "    radius " + radius + "    bottom_padding " + bottom_padding);
        centerX = getMeasuredWidth() / 2-childWidth/2;
        centerY = radius + childHeight * 3 / 2-childHeight/2;
        Log.i("tag", "------width------" + width);
        Log.i("tag", "--------height-----" + height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //初始化所有view的位置
        if (isFirst) {
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                int l1 = (getMeasuredWidth() - childWidth) / 2;
                int t1 = radius + childHeight;
                int r1 = l1 + childWidth;
                int b1 = t1 + childHeight;
                childView.layout(l1, t1, r1, b1);
                Log.i("tag", "   l1 " + l1 + "    t1 " + t1 + "    r1 " + r1 + "    b1  " + b1);
            }

        /*主菜单按钮，布局里的最后一个View*/
            centerButton = getChildAt(getChildCount() - 1);
            centerButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    RotateAnimation rotate;
                    if (isMenuClose) {
                        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                    } else {
                        rotate = new RotateAnimation(0f, -360f, Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                    }

                    rotate.setDuration(300);
                    rotate.setFillAfter(true);
                    centerButton.startAnimation(rotate);
                    menuAnim();
                    isMenuClose = !isMenuClose;
                }
            });
            isFirst = false;
        }

    }

    /*子按钮展开动画*/

    private void menuAnim() {
        int moveDuration = 100;
        int delay = 50;
        for (int i = 0; i < childCount - 1; i++) {
            double angle = ((Math.PI) / (childCount) * (i + 1));
            final View child = getChildAt(i);
            child.setTag(i);
            int x = radius +childWidth/2+ (int) (radius * Math.cos(angle));
            int y = childHeight * 3 / 2 + radius - (int) (radius * Math.sin(angle));
            final ObjectAnimator moveX;
            final ObjectAnimator moveY;
            ObjectAnimator alpha;
            if (isMenuClose) {
                moveX = ObjectAnimator.ofFloat(child, "x", centerX, x);
                moveY = ObjectAnimator.ofFloat(child, "y", centerY, y);
                child.setClickable(true);
                alpha = ObjectAnimator.ofFloat(child, "alpha", 0, 1);
            } else {
                moveX = ObjectAnimator.ofFloat(child, "x", x, centerX);
                moveY = ObjectAnimator.ofFloat(child, "y", y, centerY);
                child.setClickable(false);
                alpha = ObjectAnimator.ofFloat(child, "alpha", 1, 0);
            }
            moveX.setInterpolator(new DecelerateInterpolator());
            moveY.setInterpolator(new DecelerateInterpolator());
            moveX.setStartDelay(delay * i);
            moveY.setStartDelay(delay * i);
            moveX.setDuration(moveDuration);
            moveY.setDuration(moveDuration);
            moveX.start();
            moveY.start();
            alpha.setStartDelay(delay * i);
            alpha.start();
            moveX.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    child.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    child.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            final int index = i;
            final int cx = x;
            final int cy = y;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击消失动画
                    showMenuItemAnim(index);
                    dismissMenuItemAnim(index);
//                    menuItemAnin(index);
                    isMenuClose = true;
                    Toast.makeText(getContext(), " click"+(Integer)child.getTag(), Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "click");
                }
            });
        }
    }

    /**
     * 放大变淡动画
     * @param index
     */
    private void showMenuItemAnim(int index) {
        final View thisMenuItem = getChildAt(index);
        Animation alphaAnim = new AlphaAnimation(1, 0);

        Animation scaleAnim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnim.setFillAfter(false);
        alphaAnim.setFillAfter(false);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnim);
        animationSet.addAnimation(scaleAnim);
        animationSet.setDuration(durationMillis);
        thisMenuItem.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                thisMenuItem.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        thisMenuItem.setClickable(false);
    }


    /**
     * 背景变淡动画
     * @param index
     */
    private void dismissMenuItemAnim(int index) {
        for (int i = 0; i < childCount - 1; i++) {
            final View otherMenuItem = getChildAt(i);
            if (i != index) {
                Animation alphaAnim = new AlphaAnimation(1, 0);
                alphaAnim.setDuration(durationMillis);
                alphaAnim.setFillAfter(false);
                otherMenuItem.startAnimation(alphaAnim);
                alphaAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        otherMenuItem.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                otherMenuItem.setClickable(false);
            }
        }
    }


    /**
     * 开始菜单动画，点击的MenuItem放大消失，其他的缩小消失
     * @param item
     */
    private void menuItemAnin(int item)
    {
        for (int i = 0; i < childCount - 1; i++)
        {
            View childView = getChildAt(i);
            if (i == item)
            {
//                childView.startAnimation(scaleBigAnim(childView,300));
                scaleBigAnim(childView,durationMillis);
            } else
            {
                childView.startAnimation(scaleSmallAnim(durationMillis));
            }
            childView.setClickable(false);
            childView.setFocusable(false);

        }

    }

    /**
     * 缩小消失
     * @param durationMillis
     * @return
     */
    private Animation scaleSmallAnim(long durationMillis)
    {
        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);
        return anim;
    }
    /**
     * 放大，透明度降低
     * @param durationMillis
     * @return
     */
    private Animation scaleBigAnim(final View childView,long durationMillis)
    {
        AnimationSet animationset = new AnimationSet(true);

        Animation anim = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        animationset.addAnimation(anim);
        animationset.addAnimation(alphaAnimation);
        animationset.setDuration(durationMillis);
        animationset.setFillAfter(true);


//        ValueAnimator valueAnimator=ValueAnimator.ofFloat(1.0f, 4.0f);
//        valueAnimator.setDuration(durationMillis);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value=(float)animation.getAnimatedValue();
//                childView.setScaleX(value);
//                childView.setScaleY(value);
//            }
//
//        });
//        valueAnimator.start();



        return animationset;
    }
}

