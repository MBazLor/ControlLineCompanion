package com.mbl.controllinecompanion.tools;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.Handler;import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

public class Chronometer extends TextView {

    private static final String TAG = "Chronometer";

    public enum Mode {
        STOPWATCH,
        TIMER
    }

    /**
     * Interface to notify chrono events
     */
    public interface OnChronometerTickListener {
        void onChronometerTick(Chronometer chronometer);
        void onChronometerFinish(Chronometer chronometer); // Añadido para el modo TIMER
    }

    private OnChronometerTickListener mOnChronometerTickListener;

    private Mode mMode = Mode.STOPWATCH; // Por defecto, funciona como cronómetro
    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;

    private static final int TICK_WHAT = 2;
    private long mTimeValue; // Basetime (stopwatch) remaining time (timer)

    // ---  STOPWATCH ---
    private long mBaseTime; // Tiempo de inicio del cronómetro

    // --- TIMER ---
    private long mFutureTime;       // Momento futuro en el que el temporizador termina
    private long mCountdownInterval; // Duración total de la cuenta atrás

    public Chronometer(Context context) {
        this(context, null, 0);
    }

    public Chronometer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Chronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mBaseTime = SystemClock.elapsedRealtime();
        updateText(mBaseTime);
    }

    // Set operation mode
    public void setMode(Mode mode) {
        this.mMode = mode;
    }

    public void setBase(long base) {
        if (mMode == Mode.STOPWATCH) {
            mBaseTime = base;
            dispatchChronometerTick();
            updateText(SystemClock.elapsedRealtime());
        }
    }

    // NUEVO: Método para configurar el tiempo de cuenta atrás (modo TIMER)
    public void setCountdownDuration(long millisInFuture) {
        if (mMode == Mode.TIMER) {
            mCountdownInterval = millisInFuture;
            // Inicializamos el texto con la duración total
            reset();
        }
    }

    public void setOnChronometerTickListener(OnChronometerTickListener listener) {
        mOnChronometerTickListener = listener;
    }

    public void start() {
        mStarted = true;
        // NUEVO: Al iniciar, el comportamiento depende del modo
        if (mMode == Mode.TIMER) {
            // Si el timer ya corrió, mTimeValue guardó lo que quedaba. Si no, usa la duración total.
            long duration = (mTimeValue > 0) ? mTimeValue : mCountdownInterval;
            mFutureTime = SystemClock.elapsedRealtime() + duration;
        } else {
            // Si el stopwatch ya corrió, mTimeValue guardó el tiempo transcurrido.
            if (mTimeValue > 0) {
                mBaseTime = SystemClock.elapsedRealtime() - mTimeValue;
            }
        }
        updateRunning();
    }

    public void stop() {
        mStarted = false;
        updateRunning();
    }

    // NUEVO: Método para reiniciar el cronómetro/temporizador a su estado inicial
    public void reset() {
        stop();
        mTimeValue = 0;
        if (mMode == Mode.TIMER) {
            mTimeValue = mCountdownInterval;
            mFutureTime = SystemClock.elapsedRealtime() + mCountdownInterval;
        } else {
            mTimeValue = 0;
            mBaseTime = SystemClock.elapsedRealtime();
        }
        updateText(SystemClock.elapsedRealtime());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    // MODIFICADO: La lógica de actualización ahora depende del modo
    private synchronized void updateText(long now) {
        if (mMode == Mode.TIMER) {
            long millisRemaining = mFutureTime - now;
            if (millisRemaining <= 0) {
                millisRemaining = 0;
                if (mRunning) { // Solo si estaba corriendo
                    stop();
                    dispatchChronometerFinish();
                }
            }
            mTimeValue = millisRemaining;
        } else { // Mode.STOPWATCH
            mTimeValue = now - mBaseTime;
        }

        long timeToFormat = mTimeValue;

        DecimalFormat df = new DecimalFormat("00");

        int hours = (int) (timeToFormat / (3600 * 1000));
        int remaining = (int) (timeToFormat % (3600 * 1000));
        int minutes = (int) (remaining / (60 * 1000));
        remaining = (int) (remaining % (60 * 1000));
        int seconds = (int) (remaining / 1000);
        int milliseconds = (int) ((timeToFormat % 1000) / 100);

        String text = "";
        if (hours > 0) {
            text += df.format(hours) + ":";
        }
        text += df.format(minutes) + ":";
        text += df.format(seconds) + ":";
        text += Integer.toString(milliseconds);

        setText(text);
    }

    private void updateRunning() {
        boolean running = mVisible && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 100);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 100);
            }
        }
    };

    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

    // NUEVO: Notifica cuando el temporizador termina
    void dispatchChronometerFinish() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerFinish(this);
        }
    }

    public long getCurrentTime() {
        return mTimeValue;
    }
}
