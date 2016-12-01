package bgs.com.jianbao11.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bgs.com.jianbao11.R;
import bgs.com.jianbao11.bean.Info_shouyel;
import bgs.com.jianbao11.jianbao.MyAppalication;
import bgs.com.jianbao11.utils.ImageLoader;


public class Adapter_fragment extends BaseAdapter {
	private List<Info_shouyel> list;
	private Context context;
	private ImageLoader imageLoader;
	public Adapter_fragment(List<Info_shouyel> list, Context context) {
		super();
		this.list = list;
		this.context = context;
		imageLoader=((MyAppalication)this.context.getApplicationContext()).imageLoader;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	public void refrush(List<Info_shouyel> list){
		this.list=list;
		notifyDataSetChanged();
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder;
		if (convertView==null){
			vHolder=new ViewHolder();
			convertView=View.inflate(context, R.layout.adapter_fragment_1,null);
			vHolder.Ltag= (ImageView) convertView.findViewById(R.id.Ltag);
			vHolder.Limg= (ImageView) convertView.findViewById(R.id.Limg);
			vHolder.user_icon= (ImageView) convertView.findViewById(R.id.user_icon);
			vHolder.Ltitle= (TextView) convertView.findViewById(R.id.Ltitle);
			vHolder.Lprice= (TextView) convertView.findViewById(R.id.Lprice);
		}else{
			vHolder= (ViewHolder) convertView.getTag();
		}
		imageLoader.LoadR(list.get(position).getUser_icon_url(),vHolder.user_icon,context);
		imageLoader.Load(list.get(position).getLimg_url(),vHolder.Limg,context);
		vHolder.Ltitle.setText(list.get(position).getLtitle());
		vHolder.Lprice.setText(list.get(position).getLprice());
		if (list.get(position).getTag()==0){
			vHolder.Ltag.setImageResource(R.drawable.normal);
		}else if (list.get(position).getTag()==1){
			vHolder.Ltag.setImageResource(R.drawable.sold);
		}else if (list.get(position).getTag()==2){
			vHolder.Ltag.setImageResource(R.drawable.out);
		}
		return convertView;
	}
	private class ViewHolder{
		private ImageView user_icon,Limg,Ltag;
		private TextView Ltitle,Lprice;
	}
	
}
