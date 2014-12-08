/**
 *
 */
package pokazon.jp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 本採用
 * 画像つきのSimpleCursorAdapter拡張
 * @author student
 *
 */
public class CustomListAdapter extends SimpleCursorAdapter {
    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

    public CustomListAdapter(Context context, int layout, Cursor c,
            String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemview= inflater.inflate(R.layout.kamilist_item,null,true);

        ViewHolder holder = new ViewHolder();

        holder.imageView = (ImageView) itemview.findViewById(R.id.icon_sample);
        holder.textView = (TextView) itemview.findViewById(R.id.text_sample);

        itemview.setTag(holder);

        return itemview;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // SQLiteのテーブルの"page"という列のデータを取得して各行にセット
        int pageno = cursor.getInt(cursor.getColumnIndex("page"));
        holder.textView.setText(String.valueOf(pageno));

        // SQLiteのテーブルの"path"という列のデータを取得する(画像のファイルパス)
        // pathからBitmapを生成
	   	 // 選択した画像のファイルパスを取得し、Bitmapを生成
        String filepath = cursor.getString(cursor.getColumnIndex("pass"));
	   	 Bitmap bmp = null;
	   	 if(filepath != null){
	   		 bmp = BitmapFactory.decodeFile(filepath);
	   		 bmp = this.changeImageSize(bmp, (float)0.2, (float)0.2);
	         holder.imageView.setImageBitmap(bmp);
	         Log.d("filepath = ", filepath);
	   	 }
	   	 else{
	         Log.e("filepath no file is null ", "");
	   	 }

    }

    /**
     * bitmapを拡大縮小する
     * @param bmpSrc
     * @return
     */
    private Bitmap changeImageSize (Bitmap bmpSrc, float rsz_ratio_w, float rsz_ratio_h ){
        // 画像の大きさを最適化する
    	Bitmap bmpRsz;
    	Matrix matrix = new Matrix();

    	// 拡大比率
//    	float rsz_ratio_w = (float) 0.5;
//    	float rsz_ratio_h = (float) 0.5;
    	// 比率をMatrixに設定
    	matrix.postScale( rsz_ratio_w, rsz_ratio_h );
    	// リサイズ画像
    	bmpRsz = Bitmap.createBitmap(bmpSrc, 0, 0, bmpSrc.getWidth(),bmpSrc.getHeight(), matrix,true);
    	return bmpRsz;
    }
}
