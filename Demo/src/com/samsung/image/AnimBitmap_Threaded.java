package com.samsung.image;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class AnimBitmap_Threaded extends Thread {

	private boolean mRun = false; // Indicate whether the surface has been created & is ready to draw
	private SurfaceHolder mSurfaceHolder;
	private Core handle;
	private long timePrevFrame;

	public AnimBitmap_Threaded(SurfaceHolder surfaceHolder, Core hndl) {
		mSurfaceHolder = surfaceHolder;
		handle = hndl;
	}

	@Override
	public void run() {
		while (mRun) {
			Canvas canvas = null;
			
            //limit the frame rate to maximum 60 frames per second (16 milliseconds)
			synchronized (mSurfaceHolder) {
				long timeNow = System.currentTimeMillis();
				long timeDelta = timeNow - timePrevFrame;
				if ( timeDelta < 16)
					try{
						Thread.sleep(16 - timeDelta);
					}catch(InterruptedException e){}
				timePrevFrame = System.currentTimeMillis();
			}
            
			try {
				canvas = mSurfaceHolder.lockCanvas();
				synchronized (mSurfaceHolder) {
					canvas.drawColor(Color.BLACK); // clear the canvas (instead of drawing over the last frame)
					canvas.save();
			    	Bitmap bm = handle.getBitmap();
			    	int width = bm.getWidth();
			    	canvas.drawBitmap(handle.getOut(), 0, width, 0, 0, 
			    			width, bm.getHeight(), false, new Paint());
					canvas.restore();
					handle.newframe();                
				}
			} finally {
				if (canvas != null) 
					mSurfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	public void onTouchEvent(int x, int y) {
        (new Thread(new Disturb(x, y))).start();
	}
	
	public class Disturb implements Runnable {
	    private int x, y;

	    public Disturb(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }

	    public void run() {
			synchronized (mSurfaceHolder) {
				handle.disturb(x, y);
			}
	    }
	}

	public void setRunning(boolean b) {
		mRun = b;
	}
}