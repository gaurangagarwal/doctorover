package com.gaurang.doctorover;

public interface TaskCompleted<E> {
    // Define data you like to return from AysncTask
    public void onTaskComplete(E result);
}