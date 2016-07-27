package com.philliphsu.clock2.timers;

import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philliphsu.clock2.BaseViewHolder;
import com.philliphsu.clock2.OnListItemInteractionListener;
import com.philliphsu.clock2.R;
import com.philliphsu.clock2.Timer;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Phillip Hsu on 7/25/2016.
 */
public class TimerViewHolder extends BaseViewHolder<Timer> {

    private TimerController mController;

    @Bind(R.id.label) TextView mLabel;
    @Bind(R.id.duration) CountdownChronometer mChronometer;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;
    @Bind(R.id.add_one_minute) ImageButton mAddOneMinute;
    @Bind(R.id.start_pause) ImageButton mStartPause;
    @Bind(R.id.stop) ImageButton mStop;

    // TODO: Controller param
    public TimerViewHolder(ViewGroup parent, OnListItemInteractionListener<Timer> listener) {
        super(parent, R.layout.item_timer, listener);
    }

    @Override
    public void onBind(Timer timer) {
        super.onBind(timer);
        bindLabel(timer.label());
        // We can't create the controller until this VH binds, because
        // the chronometer only exists after this point.
        mController = new TimerController(timer, mChronometer);
        bindChronometer(timer);
    }

    @OnClick(R.id.start_pause)
    void startPause() {
        Timer t = getItem();
        if (t.isRunning()) {
            mController.pause();
        } else {
            if (t.hasStarted()) {
                mController.resume();
            } else {
                mController.start();
            }
        }
    }

    private void bindLabel(String label) {
        if (!label.isEmpty()) {
            mLabel.setText(label);
        }
    }

    private void bindChronometer(Timer timer) {
        // In case we're reusing a chronometer instance that could be running:
        // If the Timer instance is not running, this just guarantees the chronometer
        // won't tick, regardless of whether it was running.
        // If the Timer instance is running, we don't care whether the chronometer is
        // also running, because we call start() right after. Stopping it just
        // guarantees that, if it was running, we don't deliver another set of
        // concurrent messages to its handler.
        mChronometer.stop();

        if (!timer.hasStarted()) {
            // Set the initial text
            // TODO: Verify the controller should already have initialized
            // the text when it was constructed.
//            mChronometer.setDuration(timer.duration());
        } else if (timer.isRunning()) {
            // Re-initialize the base
            mChronometer.setBase(timer.endTime());
            // Previously stopped, so no old messages will interfere.
            mChronometer.start();
        } else {
            // Set the text as last displayed before we stopped.
            // When you call stop() on a Chronometer, it freezes the current text shown,
            // so why do we need this? While that is sufficient for a static View layout,
            // VH recycling will reuse the same Chronometer widget across multiple VHs,
            // so we would have invalid data across those VHs.
            // If a new VH is created, then the chronometer it contains will be in its
            // uninitialized state. We will always need to set the Chronometer's base
            // every time VHs are bound/recycled.
            mChronometer.setDuration(timer.timeRemaining());
        }
    }
}