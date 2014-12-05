package pokazon.jp;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class suraid extends Activity implements View.OnTouchListener {

    private ViewFlipper viewFlipper;
    private float firstTouch;
    private boolean isFlip = false;
    private ImageView firstImageView;
    private ImageView secondImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suraid);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        firstImageView = (ImageView)findViewById(R.id.imageview_first);
        secondImageView = (ImageView)findViewById(R.id.imageview_second);
        findViewById(R.id.layout_first).setOnTouchListener(this);
        findViewById(R.id.layout_second).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int)event.getRawX();
        switch(v.getId()) {
        case R.id.layout_first:
        case R.id.layout_second:
            switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstTouch = event.getRawX();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(!isFlip) {
                    if(x - firstTouch > 50) {
                        isFlip = true;
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.move_in_left));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.move_out_right));
                        viewFlipper.showNext();
                    }
                    else if(firstTouch - x > 50) {
                        isFlip = true;
                        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.layout.move_in_right));
                        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.layout.move_out_left));
                        viewFlipper.showPrevious();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isFlip = false;
                break;
            }
        }

        return false;
    }
}