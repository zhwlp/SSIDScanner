package com.utils.BlueToothscanner;


import java.util.List;
import java.util.concurrent.Semaphore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import android.bluetooth.*;

public class SSIDUpdater {

	Context context;

	SSIDUpdateListener listener;
	int timeout = 1000; // milliseconds
	boolean running = false;

	BluetoothAdapter bluetooth = null;
	List<ScanResult> results;
	
	Semaphore lock = new Semaphore(1, false);

	public SSIDUpdater(Context context) {
		this.context = context;
	}

	public void setListener(SSIDUpdateListener listener) {
		this.listener = listener;
	}

	public void setInterval(int milliseconds) {
		this.timeout = milliseconds;
	}

	public void init() {
		running = false;

		bluetooth = (BluetoothAdapter) context.getSystemService(Context.BLUETOOTH_SERVICE);
		if (!bluetooth.isEnabled()) {
			Toast.makeText(context, "Wifi is disabled. I will enable it.",
					Toast.LENGTH_LONG).show();
			bluetooth.enable();
		}

		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent intent) {
				lock.release();
//				listener.update(bluetooth.getRemoteDevice(getResultData())); // get result
			}
		}, new IntentFilter(BluetoothAdapter.EXTRA_SCAN_MODE));

	}

	public void start() {
		if (running) {
			return;
		}
		running = true;
		
		lock.drainPermits();

		(new Thread(new SSIDUpdaterRunnable())).start();
	}

	public void stop() {
		running = false;
	}

	class SSIDUpdaterRunnable implements Runnable {

		@Override
		public void run() {
			while (running) {

//				wifi.startScan();
				
				try {
					lock.acquire();
					Thread.sleep(timeout);
				} catch (Exception e) {
				}
			}
		}

	}

	public static interface SSIDUpdateListener {
		public void update(final List<ScanResult> results);
	}
}
