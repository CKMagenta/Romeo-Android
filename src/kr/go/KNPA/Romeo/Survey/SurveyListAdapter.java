package kr.go.KNPA.Romeo.Survey;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
class SurveyListAdapter extends CursorAdapter {
	// Variables
	public int type = Document.NOT_SPECIFIED;
	
	// Constructor
	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public SurveyListAdapter(Context context, Cursor c, boolean autoRequery, int type) {
		super(context, c, autoRequery);
		this.type = type;
	}
	
	public SurveyListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	@Override
	public void bindView(View v, Context ctx, Cursor c) {
		// TODO
		if(this.type == Survey.TYPE_DEPARTED) {
			LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_departed);
			TextView titleTV = (TextView)v.findViewById(R.id.title);
			TextView senderTV = (TextView)v.findViewById(R.id.sender);
			TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
			TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
			Button goUnchecked = (Button)v.findViewById(R.id.goUnchecked);
			
			String title = "";
			title = c.getString(c.getColumnIndex("title"));
			
			long senderIdx = c.getLong(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx(senderIdx);
			String sender = user.getDepartmentFull() +" "+User.RANK[user.rank]+" " +user.name;
			
			long openTS = c.getLong(c.getColumnIndex("openTS"));
			long closeTS = c.getLong(c.getColumnIndex("closeTS"));
			String openDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_openDT));
			String closeDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_closeDT));
			
			titleTV.setText(title);
			senderTV.setText(sender);
			openDTTV.setText(openDT);
			closeDTTV.setText(closeDT);
			
		} else if(this.type == Survey.TYPE_RECEIVED) {
			LinearLayout layout = (LinearLayout)v.findViewById(R.id.survey_list_cell_received);
			TextView titleTV = (TextView)v.findViewById(R.id.title);
			TextView senderTV = (TextView)v.findViewById(R.id.sender);
			TextView openDTTV = (TextView)v.findViewById(R.id.openDT);
			TextView closeDTTV = (TextView)v.findViewById(R.id.closeDT);
			Button goResult = (Button)v.findViewById(R.id.goResult);

			String title = "";
			title = c.getString(c.getColumnIndex("title"));
			
			long senderIdx = c.getLong(c.getColumnIndex("sender"));
			User user = User.getUserWithIdx(senderIdx);
			String sender = user.getDepartmentFull() +" "+User.RANK[user.rank]+" " +user.name;
			
			long openTS = c.getLong(c.getColumnIndex("openTS"));
			long closeTS = c.getLong(c.getColumnIndex("closeTS"));
			String openDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_openDT));
			String closeDT = Formatter.timeStampToStringWithFormat(openTS, ctx.getString(R.string.formatString_closeDT));
			
			titleTV.setText(title);
			senderTV.setText(sender);
			openDTTV.setText(openDT);
			closeDTTV.setText(closeDT);
		}
		//department.setText(c.getString(c.getColumnIndex("department")));
		//content.setText(c.getString(c.getColumnIndex("content")));

	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(ctx);
		View v = null;
		switch(this.type) {
		case Survey.TYPE_DEPARTED :	v = inflater.inflate(R.layout.survey_list_cell_departed, parent,false);		break;
		case Survey.TYPE_RECEIVED :	v = inflater.inflate(R.layout.survey_list_cell_received, parent,false);		break;
			
		default :
		case Survey.NOT_SPECIFIED :	break;	
			// ListView ���� tableName�� ������ ��쿡�� �Ѿ���Ƿ�, �� ������ ������ �� ����.
		}
		
		return v;
	}
	/*
	public Survey getSurvey(int position) {
		Cursor c = (Cursor)getItem(position);
		return new Survey(c);  
	}
	*/
}
