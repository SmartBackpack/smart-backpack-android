package lt.ismaniojikuprine.smartbackpack;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SwingAnimator {

    private final int POSITION_CENTER = 0;
    private final int POSITION_LOW_LEFT = 1;
    private final int POSITION_LOW_RIGHT = 2;

    private int currentPosition = 0;

    private ImageView swing, leftChildImage, rightChildImage;
    private ImageView leftSad, rightSad, smile;

    private float lowMargin, highMargin;
    private int swingRotation, animationSpeed;

    public SwingAnimator(Activity activity) {
        swing = (ImageView) activity.findViewById(R.id.swing);
        leftChildImage = (ImageView) activity.findViewById(R.id.left_child);
        rightChildImage = (ImageView) activity.findViewById(R.id.right_child);

        leftSad = (ImageView) activity.findViewById(R.id.sad_left);
        rightSad = (ImageView) activity.findViewById(R.id.sad_right);
        smile = (ImageView) activity.findViewById(R.id.smile);

        lowMargin = activity.getResources().getDimension(R.dimen.child_low_margin);
        highMargin = activity.getResources().getDimension(R.dimen.child_high_margin);
        swingRotation = activity.getResources().getInteger(R.integer.swing_rotation);
        animationSpeed = activity.getResources().getInteger(R.integer.animation_speed);
    }

    public void lowerLeft() {
        if (currentPosition != POSITION_LOW_LEFT) {
            currentPosition = POSITION_LOW_LEFT;
            changeSwingBalanceWithAnimation(lowMargin, highMargin, -swingRotation);
            smile.setVisibility(View.GONE);
            leftSad.setVisibility(View.VISIBLE);
        }
    }

    public void lowerRight() {
        if (currentPosition != POSITION_LOW_RIGHT) {
            currentPosition = POSITION_LOW_RIGHT;
            changeSwingBalanceWithAnimation(highMargin, lowMargin, swingRotation);
            smile.setVisibility(View.GONE);
            leftSad.setVisibility(View.GONE);
            rightSad.setVisibility(View.VISIBLE);
        }
    }

    private void changeSwingBalanceWithAnimation(float leftChildMargin, float rightChildMargin, int swingRotation) {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setLayoutMarginBottom(leftChildImage, interpolatedTime * leftChildMargin);
                setLayoutMarginBottom(rightChildImage, interpolatedTime * rightChildMargin);
                swing.setRotation(swingRotation * interpolatedTime);

            }
        };
        animation.setDuration(animationSpeed);
        swing.startAnimation(animation);
    }

    private float getViewMarginBottom(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        return params.bottomMargin;
    }

    private void setLayoutMarginBottom(View view, float marginBottom) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.bottomMargin = (int) marginBottom;
        view.setLayoutParams(params);
    }

    public void balanceCenter() {
        if (currentPosition != POSITION_CENTER) {
            balanceSwingCenterWithAnimation();
            leftSad.setVisibility(View.GONE);
            rightSad.setVisibility(View.GONE);
            smile.setVisibility(View.VISIBLE);
            currentPosition = POSITION_CENTER;
        }
    }

    private void balanceSwingCenterWithAnimation() {
        float startLeftChildMarginBottom = getViewMarginBottom(leftChildImage);
        float startRightChildMarginBottom = getViewMarginBottom(rightChildImage);
        float startSwingRotation = swing.getRotation();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                setLayoutMarginBottom(leftChildImage, startLeftChildMarginBottom * (1 - interpolatedTime));
                setLayoutMarginBottom(rightChildImage, startRightChildMarginBottom * (1 - interpolatedTime));
                swing.setRotation(startSwingRotation * (1 - interpolatedTime));

            }
        };
        animation.setDuration(animationSpeed);
        swing.startAnimation(animation);
    }
}
