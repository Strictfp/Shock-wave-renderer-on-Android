package com.samsung.image;

import android.graphics.Bitmap;

import com.samsung.ImgActivity;

public class Core  {

	private final Bitmap origmap;
	private int width,height,hwidth,hheight;
	private int i,a,b;
	private int size;
	private short ripplemap[];
	private int texture[];
	private int ripple[];
	private int oldind,newind,mapind;
	private int riprad;
	
	public Core(Bitmap oldmap) {
		this.origmap = oldmap;
		init();
	}

	public void init() {
		width = origmap.getWidth();
		height = origmap.getHeight();
		hwidth = width>>ImgActivity.RIPPLE_HEIGHT;
		hheight = height>>ImgActivity.RIPPLE_HEIGHT;
		riprad=ImgActivity.RIPPLE_RADIUS;

		size = width * (height+2) * 2;
		ripplemap = new short[size];
		ripple = new int[width*height];
		texture = new int[width*height];
		oldind = width;
		newind = width * (height+3);

		origmap.getPixels(texture, 0, width, 0, 0, width, height);

		origmap.getPixels(ripple, 0, width, 0, 0, width, height);
	}
	
	public Bitmap getBitmap() {
		return origmap;
	}

	public void disturb(int dx, int dy) {
		for (int j=dy-riprad;j<dy+riprad;j++) {
			for (int k=dx-riprad;k<dx+riprad;k++) {
				if (j>=0 && j<height && k>=0 && k<width) {
					ripplemap[oldind+(j*width)+k] += 512;            
				} 
			}
		}
	}

	public void newframe() {
		//Toggle maps each frame
		i=oldind;
		oldind=newind;
		newind=i;

		i=0;
		mapind=oldind;
		for (int y=0;y<height;y++) {
			for (int x=0;x<width;x++) {
				short data = (short)((ripplemap[mapind-width]+ripplemap[mapind+width]+ripplemap[mapind-1]+ripplemap[mapind+1])>>1);
				data -= ripplemap[newind+i];
				data -= data >> ImgActivity.RATE_OF_DECAY; // >>5 => 1/32
				ripplemap[newind+i]=data;

				//where data=0 then still, where data>0 then wave
				data = (short)(1024-data);

				//offsets
				a=((x-hwidth)*data/1024)+hwidth;
				b=((y-hheight)*data/1024)+hheight;

				//bounds check
				if (a>=width) a=width-1;
				if (a<0) a=0;
				if (b>=height) b=height-1;
				if (b<0) b=0;

				ripple[i]=texture[a+(b*width)];
				mapind++;
				i++;
			}
		}
	}

	public int[] getOut() {
		return ripple;
	}

}

