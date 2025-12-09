package com.mbl.controllinecompanion.tools;

import android.content.Context;
import android.icu.text.DecimalFormat;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.TextView;

public class Chronometer extends TextView {
    @SuppressWarnings("unused")
    private static final String TAG = "Chronometer";

    // MODIFICADO: La interfaz ahora notifica el "tick" y cuando el tiempo termina.
    public interface OnChronometerTickListener {
        void onChronometerTick(Chronometer chronometer);
        void onChronometerFinish(Chronometer chronometer);
    }

    // AÑADIDO: Tiempo total de la cuenta atrás en milisegundos.
    private long mMillisInFuture;
    // AÑADIDO: Tiempo en el futuro cuando la cuenta atrás terminará.
    private long mFutureTime;

    private boolean mVisible;
    private boolean mStarted;
    private boolean mRunning;
    private OnChronometerTickListener mOnChronometerTickListener;

    private static final int TICK_WHAT = 2;

    private long timeElapsed; // AHORA REPRESENTA EL TIEMPO RESTANTE

    public Chronometer(Context context) {
        this (context, null, 0);
    }

    public Chronometer(Context context, AttributeSet attrs) {
        this (context, attrs, 0);
    }

    public Chronometer(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        init();
    }

    private void init() {
        // La inicialización ahora es más simple, el tiempo se establece después.
    }

    // MODIFICADO: setBase ya no es necesario. Lo reemplazamos por setMillisInFuture.
    public void setMillisInFuture(long millisInFuture) {
        this.mMillisInFuture = millisInFuture;
        mFutureTime = SystemClock.elapsedRealtime() + this.mMillisInFuture;
        updateText(SystemClock.elapsedRealtime());
    }

    public long getMillisInFuture() {
        return mMillisInFuture;
    }

    public void setOnChronometerTickListener(
            OnChronometerTickListener listener) {
        mOnChronometerTickListener = listener;
    }

    public OnChronometerTickListener getOnChronometerTickListener() {
        return mOnChronometerTickListener;
    }

    public void start() {
        // AÑADIDO: Al empezar, recalculamos el tiempo futuro para ser precisos.
        mFutureTime = SystemClock.elapsedRealtime() + mMillisInFuture;
        mStarted = true;
        updateRunning();
    }

    public void stop() {
        mStarted = false;
        updateRunning();
    }

    // AÑADIDO: Un método para reiniciar la cuenta atrás.
    public void reset() {
        stop(); // Detiene el bucle.
        setMillisInFuture(this.mMillisInFuture); // Restablece el tiempo.
    }

    public void setStarted(boolean started) {
        mStarted = started;
        updateRunning();
    }

    @Override
    protected void onDetachedFromWindow() {
        super .onDetachedFromWindow();
        mVisible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super .onWindowVisibilityChanged(visibility);
        mVisible = visibility == VISIBLE;
        updateRunning();
    }

    // MODIFICADO: La lógica para calcular y mostrar el tiempo ha cambiado por completo.
    private synchronized void updateText(long now) {
        long millisRemaining = mFutureTime - now;

        if (millisRemaining <= 0) {
            millisRemaining = 0;
            // Detenemos el cronómetro y notificamos que ha terminado.
            if (mRunning) { // Solo si estaba corriendo
                stop();
                dispatchChronometerFinish();
            }
        }

        timeElapsed = millisRemaining; // Guardamos el tiempo restante.

        DecimalFormat df = new DecimalFormat("00");

        int hours = (int)(millisRemaining / (3600 * 1000));
        int remaining = (int)(millisRemaining % (3600 * 1000));

        int minutes = (int)(remaining / (60 * 1000));
        remaining = (int)(remaining % (60 * 1000));

        int seconds = (int)(remaining / 1000);
        remaining = (int)(remaining % (1000));

        // MODIFICADO: Las décimas de segundo se calculan sobre el tiempo restante.
        int milliseconds = (int)(millisRemaining % 1000) / 100;

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
                // AÑADIDO: Asegurarse de que el tiempo futuro es correcto al reanudar.
                if(timeElapsed > 0) {
                    mFutureTime = SystemClock.elapsedRealtime() + timeElapsed;
                }

                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                mHandler.sendMessageDelayed(Message.obtain(mHandler,
                        TICK_WHAT), 100);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                sendMessageDelayed(Message.obtain(this , TICK_WHAT),
                        100);
            }
        }
    };

    void dispatchChronometerTick() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerTick(this);
        }
    }

    void dispatchChronometerFinish() {
        if (mOnChronometerTickListener != null) {
            mOnChronometerTickListener.onChronometerFinish(this);
        }
    }


    public long getTimeElapsed() {
        return timeElapsed;
    }
}
