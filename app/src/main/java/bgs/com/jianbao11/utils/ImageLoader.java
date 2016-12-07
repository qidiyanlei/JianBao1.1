package bgs.com.jianbao11.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ͼƬ������������
 * */
@SuppressLint("NewApi")
public class ImageLoader {
	public static boolean lockFlag=false;//�������ı�־
	private static ImageLoader INTENCE;//��ǰ��loader����
	private FileUtils fileutils;//������
	private Context ctx;
	private final static int MAX_POOLS=5;//����һ�����ؿ����߳���
	private ExecutorService thread_pool;//����һ���̳߳�
	private Set<ImageView> imgs=new HashSet<ImageView>();//����һ���洢ͼƬ�ؼ���set����
	private int max_size=(int)(Runtime.getRuntime().maxMemory())/1024/5;//�涨ÿ���߳�����ڴ�
	//LruCache:������ǿ���û���ģ�һ���������ֵ�����Զ���ǰ����ӳ����棬�����������ջ��ƻ���
	//LruCache���洢��ʽ��map���ƣ��õ�K,V��ֵ�ķ�ʽ������ͼƬ���� 
	private LruCache<String, Bitmap> lru=new LruCache<String, Bitmap>(max_size){
		@Override
		protected int sizeOf(String key, Bitmap value) {
			// TODO Auto-generated method stub
			return value.getByteCount()/1024;
		}
	};
	//���loader����ķ���
	public static ImageLoader getIntence(){
		if(INTENCE==null){
			INTENCE=new ImageLoader();
		}
		return INTENCE;
	}
	//�����ķ���
	public void lock(){
		lockFlag=true;
	}
	//�����ķ���
	public void unlock(){
		lockFlag=false;
	}
	private Handler hand=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==200){
				Bitmap bitmap=(Bitmap) msg.obj;
				String name=msg.getData().getString("imgname");
				//����set���ϣ����жԱ�
				Iterator<ImageView> it = imgs.iterator();
				while(it.hasNext()){
					ImageView img = it.next();
					String tag=(String) img.getTag();
					//�����̳߳�����ͼƬ���ƺͿؼ���������Ӧ��ͼƬ���ƽ��бȽϣ������ͬ������
					if(tag.equals(name)){
						img.setImageBitmap(bitmap);
						return;
					}
				}
			}
		}
	};
	public void Load(String name,ImageView img,Context ctx){
		//Ϊ��ֱ������
		if(name==null)
			return;
		if(img==null)
			return;
		this.ctx=ctx;
		Bitmap bitmap;
		//name ���й���
		String name1=name.replace("/", "").replace(".", "").replace(":", "").replace("_", "");
		//img.���tag
		img.setTag(name1);//Ϊ�������ͼƬ��λ����ͼƬ���洢�������Ӧ�Ŀؼ��У������Ժ���жԱ�����
		//��img��ӵ�set����
		imgs.add(img);
		//��lru�в���ͼƬ
		bitmap=lru.get(name1);//K V-->��Lru�л�ȡͼƬ
		if(bitmap!=null){
			img.setImageBitmap(bitmap);
			return;
		}
		//�ӱ���sd�л�ȡͼƬ
		if(fileutils==null){
			fileutils=new FileUtils(ctx);
			bitmap=fileutils.getBitmap(name1);
			if(bitmap!=null){
				img.setImageBitmap(bitmap);
				//��ͼƬ��ӵ�Lru��
				lru.put(name1, bitmap);
				return;
			}
		}
		//����������
		if(thread_pool==null){
			thread_pool=Executors.newFixedThreadPool(MAX_POOLS);

		}
		//׼���������ͼƬ
		thread_pool.execute(new ImgThread(name));
	}
	public void LoadR(String name,ImageView img,Context ctx){
		//Ϊ��ֱ������
		if(name==null)
			return;
		if(img==null)
			return;
		this.ctx=ctx;
		Bitmap bitmap;
		//name ���й���
		String name1=name.replace("/", "").replace(".", "").replace(":", "").replace("_", "");
		//img.���tag
		img.setTag(name1);//Ϊ�������ͼƬ��λ����ͼƬ���洢�������Ӧ�Ŀؼ��У������Ժ���жԱ�����
		//��img��ӵ�set����
		imgs.add(img);
		//��lru�в���ͼƬ
		bitmap=lru.get(name1);//K V-->��Lru�л�ȡͼƬ

		if(bitmap!=null){
			img.setImageBitmap(cutCircle(bitmap));
			return;
		}
		//�ӱ���sd�л�ȡͼƬ
		if(fileutils==null){
			fileutils=new FileUtils(ctx);
			bitmap=fileutils.getBitmap(name1);
			if(bitmap!=null){
				img.setImageBitmap(cutCircle(bitmap));
				//��ͼƬ��ӵ�Lru��
				lru.put(name1, bitmap);
				return;
			}
		}
		//����������
		if(thread_pool==null){
			thread_pool=Executors.newFixedThreadPool(MAX_POOLS);

		}
		//׼���������ͼƬ
		thread_pool.execute(new ImgThread(name));
	}
	public Bitmap cutCircle(Bitmap bitmap){
		//依据原有图片 复制一张新的图片作为画布背景 但格式发生了变化Config.ARGB_4444
		Bitmap b=Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
		//创建画布
		Canvas canvas=new Canvas(b);
		//设置画布的透明度和三原色
		canvas.drawARGB(0, 0, 0, 0);
		//创建画笔
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		//画笔设置颜色
		paint.setColor(Color.WHITE);
		//取半径
		float radius=Math.min(bitmap.getHeight(), bitmap.getWidth())/2;
		//根据前边的两个参数来取到半径
		canvas.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, radius, paint);
		//重置画笔
		paint.reset();
		//调用截取图层的方法
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		//画图片
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return b;
	}
	//ͼƬ�첽�����ڲ���
	private class ImgThread implements Runnable{
		private String name;
		public ImgThread(String name){
			this.name=name;
		}
		@Override
		public void run() {
			//��ʼ��������
			try {
				URL url=new URL(name);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(30*1000);
				conn.setReadTimeout(30*1000);
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				Bitmap bitmap=BitmapFactory.decodeStream(conn.getInputStream());
				String name1=name.replace("/", "").replace(".", "").replace(":", "").replace("_", "");
				//������������ͼƬ�ٴ���ӵ�SD����lru��
				if(bitmap!=null){
					fileutils.SaveBitmap(name1, cutCircle(bitmap));
					lru.put(name1, cutCircle(bitmap));
					Message msg=hand.obtainMessage();
					msg.what=200;
					msg.obj=cutCircle(bitmap);
					Bundle data=new Bundle();
					data.putString("imgname", name1);
					msg.setData(data);
					hand.sendMessage(msg);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}








}
