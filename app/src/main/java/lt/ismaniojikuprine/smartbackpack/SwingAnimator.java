package lt.ismaniojikuprine.smartbackpack;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SwingAnimator {

    private ImageView swing, leftChildImage, rightChildImage;

    private float lowMargin, highMargin;
    private int swingRotation, animationSpeed;

    private Animation currentAnimation;

    public SwingAnimator(Activity activity) {
        swing = (ImageView) activity.findViewById(R.id.swing);
        leftChildImage = (ImageView) activity.findViewById(R.id.left_child);
        rightChildImage = (ImageView) activity.findViewById(R.id.right_child);

        lowMargin = activity.getResources().getDimension(R.dimen.child_low_margin);
        highMargin = activity.getResources().getDimension(R.dimen.child_high_margin);
        swingRotation = activity.getResources().getInteger(R.integer.swing_rotation);
        animationSpeed = activity.getResources().getInteger(R.integer.animation_speed);
    }

    public void lowerLeft() {
        changeSwingBalance(lowMargin, highMargin, -swingRotation);
    }

    public void lowerRight() {
        changeSwingBalance(highMargin, lowMargin, swingRotation);
    }

    private void changeSwingBalance(float leftChildMargin, float rightChildMargin, int swingRotation) {
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
