package bgs.com.jianbao11.jianbao;

import android.app.Application;

import bgs.com.jianbao11.utils.ImageLoader;
import bgs.com.jianbao11.utils.SharedUtils;


public class MyAppalication extends Application{
	public ImageLoader imageLoader;
	public SharedUtils utils;
	@Override
	public void onCreate() {
		super.onCreate();
		imageLoader=new ImageLoader();
		utils = new SharedUtils();
	}
}
