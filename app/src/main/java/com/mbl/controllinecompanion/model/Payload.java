package com.mbl.controllinecompanion.model;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton class that contains the payload that will be sent to the CLDevice
 */
public  class Payload {

    private static Payload instance;
    private short throttle;
    private boolean armed;
    private short timer;
    ReentrantLock lock;

    private Payload(){
        this.armed = false;
        this.throttle = 1000;
        this.timer=0;
        lock = new ReentrantLock();
    }

    public static Payload getInstance(){
        if (instance == null){
            instance = new Payload();
        }
        return instance;
    }

    public short getThrottle(){
        return this.throttle;
    }

    public boolean getArmed(){
        return this.armed;
    }

    public void setThrottle(short throttle){
        lock.lock();
        //Utilizamos try-finally, por que, si no se utilizase y sucediese una excepcion en la asignación,
        // nunca se liberaría el recurso
        try{
            this.throttle = throttle;
        }finally{
            lock.unlock();
        }
    }

    public void setArmed(boolean armed){
        lock.lock();
        try{
            this.armed = armed;
        }finally{
            lock.unlock();
        }
    }

    public String getPayload(){
        return "thr="+this.throttle+";";
    }

    public void lock(){
        this.lock.lock();
    }
    public void unlock(){
        this.lock.unlock();
    }


}
