package bgs.com.jianbao11.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;

public class RoundBitmapUtils {
	public static Bitmap getBitmap(Bitmap bitmap){
		//����ԭ��ͼƬ�����´���һ���µ�ͼƬ ��ͼƬ��ʽ�����ı䣺ARGB-4444;
		Bitmap b=Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);
		//1.��ͼƬ���������ı���
		Canvas canvas=new Canvas(b);
		//2.��������
		Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		canvas.drawARGB(0, 0, 0, 0);
		//3.ȡ�뾶
		float radius=Math.min(bitmap.getWidth(), bitmap.getHeight())/2;
		canvas.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, radius, paint);
		//4.���û���
		paint.reset();
		//5.���ý�ͼͼ��ķ���
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		//6.��ͼƬ
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return b;
	}
}
