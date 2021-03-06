package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.DB.DocuDAO;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Member.UserListActivity;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
class DocumentListAdapter extends CursorAdapter {
	public int type = Document.NOT_SPECIFIED;
	public DocumentListAdapter(Context context, Cursor c, boolean autoRequery) 				{	super(context, c, autoRequery);						}
	public DocumentListAdapter(Context context, Cursor c, boolean autoRequery, int type) 	{	super(context, c, autoRequery);	this.type = type;	}
	public DocumentListAdapter(Context context, Cursor c, int flags) 						{	super(context, c, flags);							}

	@Override
	public void bindView(View v, final Context ctx, Cursor c) {
		
		// 문서제목 (String)
		String title = c.getString(c.getColumnIndex(DocuDAO.COLUMN_DOC_TITLE));
		
		TextView titleTV = (TextView)v.findViewById(R.id.title);
		titleTV.setText(title);
		
		
		// 발신자 (String) TODO 통신
		final String userIdx = c.getString(c.getColumnIndex(DocuDAO.COLUMN_SENDER_IDX));
		
		final View _givenView = v;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				User sender = User.getUserWithIdx( userIdx );
				
				android.os.Message message = userNameHandler.obtainMessage();
				
				Bundle b = new Bundle();
				String senderInfo = sender.department.nameFull + " "+User.RANK[sender.rank] +" "+ sender.name;
				b.putString(KEY_SENDER_INFO, senderInfo);
				message.setData(b);
				
				message.obj = _givenView;
				
				userNameHandler.sendMessage(message);
			}
		}).start();
		
		
		// 발신일시 (long)
		long TS =  c.getLong(c.getColumnIndex(DocuDAO.COLUMN_CREATED_TS));
		String DT = Formatter.timeStampToRecentString(TS);
		
		TextView arrivalDTTV = (TextView)v.findViewById(R.id.tv_arrival_dt);
		arrivalDTTV.setText(DT);
		
		
		
		// 문서해쉬 (String)
		final String docIdx = c.getString(c.getColumnIndex(DocuDAO.COLUMN_DOC_IDX));
		
		if(this.type == Document.TYPE_DEPARTED) {
			Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
			goUnchecked.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(ctx, UserListActivity.class);
					Bundle b = new Bundle();
					b.putStringArrayList("idxs", Document.getUncheckersIdxsWithMessageTypeAndIndex(type, docIdx));
					intent.putExtras(b);
					ctx.startActivity(intent);
				}
			});
		}

	}

	
	
	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = null;
		switch(this.type) {
			case Document.TYPE_DEPARTED : v = inflater.inflate(R.layout.document_list_cell_departed, parent,false);		break;
			case Document.TYPE_RECEIVED : v = inflater.inflate(R.layout.document_list_cell_received, parent,false);		break;
			case Document.TYPE_FAVORITE : v = inflater.inflate(R.layout.document_list_cell_favorite, parent,false);		break;
			default :
			case Document.NOT_SPECIFIED : break;
			// ListView에서 tableName이 정해저야만 Adapter를 호출할 수 있는데,
			//이가 정해지기 위해서는 type이 정해진 상태이어야 하므로, 
			//이 지점에 도달 할 수가 없다. 불가능!!
		}
		
		return v;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
	
	private static String KEY_SENDER_INFO = "senderInfo";
	private static Handler userNameHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//String senderInfo = (String)msg.obj;
			Bundle b = msg.getData();
			View _givenView = (View)msg.obj;
			String senderInfo = b.getString(KEY_SENDER_INFO);
			TextView senderTV = (TextView)_givenView.findViewById(R.id.sender);
			senderTV.setText(senderInfo);
		}
	};
}
