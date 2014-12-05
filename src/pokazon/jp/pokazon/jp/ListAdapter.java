package pokazon.jp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * CursorAdapter を拡張して Cursor の内容を ListView にセット
 * @author student
 *
 */

public class ListAdapter extends CursorAdapter {
	
	private LayoutInflater mInflater;
	
	class ViewHolder {
		TextView id;
		TextView place_id;
		TextView place;
		TextView url;
	}
	
	/*
	 * 
	 */
	public ListAdapter(Context context, Cursor c, boolean autoRequery){
		super(context, c, autoRequery);
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// View を再利用してデータをセット
		ViewHolder holder = (ViewHolder)view.getTag();
		
		// Cursor からデータを取り出します
		
		
	}


	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

}
