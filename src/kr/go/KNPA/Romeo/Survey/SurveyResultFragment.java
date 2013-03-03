package kr.go.KNPA.Romeo.Survey;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Base.Appendix;
import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Formatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SurveyResultFragment extends Fragment {

	private Context context;
	public Survey survey;
	public int type;
	
	
	public SurveyResultFragment() {
		
	}
	
	public SurveyResultFragment(Survey survey, int type) {
		this.survey = survey;
		this.type = type;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public void onResume() {
		super.onResume();
		
		survey.setChecked(getActivity());
		SurveyFragment.surveyFragment(Survey.TYPE_RECEIVED).getListView().refresh();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//super.onCreateView(inflater, container, savedInstanceState);
		//Intent intent = getIntent();
		//Bundle b = intent.getExtras();
		//this.survey = b.getParcelable("survey");
		//this.context = SurveyDetailFragment.this;

		
		this.context = getActivity();
		
		View view = inflater.inflate(R.layout.survey_result, null, false);
		
		initNavigationBar(
				view, 
				R.string.surveyTitle, 
				true, false, 
				R.string.menu, R.string.dummy, lbbOnClickListener, null);	
		

		TextView titleTV = (TextView)view.findViewById(R.id.title);
		titleTV.setText(this.survey.title);
		
		TextView  arrivalDTTV = (TextView)view.findViewById(R.id.arrivalDT);
		String arrivalDT = Formatter.timeStampToStringInRegularFormat(this.survey.TS, getActivity());
		arrivalDTTV.setText(arrivalDT);
		
		TextView senderTV = (TextView)view.findViewById(R.id.sender);
		User user = this.survey.sender;
		String sender = user.getDepartmentFull() + " " + User.RANK[user.rank] +" "  + user.name;
		senderTV.setText(sender);
		
		TextView openDTTV = (TextView)view.findViewById(R.id.openDT);
		String openDT = Formatter.timeStampToStringWithFormat(this.survey.openTS, getString(R.string.formatString_openDT));
		openDTTV.setText(openDT);
		
		TextView closeDTTV = (TextView)view.findViewById(R.id.closeDT);
		String closeDT = Formatter.timeStampToStringWithFormat(this.survey.closeTS, getString(R.string.formatString_closeDT));
		closeDTTV.setText(closeDT);
		
		TextView contentTV = (TextView)view.findViewById(R.id.content);
		String content = this.survey.content;
		contentTV.setText(content);
		
		
		// parsing Start
		Appendix adx = survey.appendix;
		String json = adx.getAttachmentWithKey(Message.MESSAGE_KEY_SURVEY).getJSON();
		
		JSONObject s = null;
		try {
			s = new JSONObject(json);
		} catch (JSONException e) {
		}
		if(s != null) {
			// ������ �̹� Survey Message Object�� ���� ó���� �͵� 
			//s.getString("title");
			//s.getString("content");
			//s.getLong("openTS");
			//s.getLong("closeTS");
			//s.getString("receivers");
			JSONObject q = null;
			JSONArray os = null;
			JSONObject o = null;
			JSONArray qs = null;
			
			String qTitle = null;
			boolean qIsMultiple = false;
			String oTitle = null;
			
			LinearLayout questionsLL = (LinearLayout)view.findViewById(R.id.questions);
			View questionView = null;
			LinearLayout optionsLL = null;
			View optionView = null;
			
			try {
				qs = s.getJSONArray("questions");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(qs != null) {
			
				
				for(int qi=0; qi<qs.length(); qi++) {
					try {
						q = qs.getJSONObject(qi);
						qTitle = q.getString("title");
						qIsMultiple = (q.getInt("isMultiple") == 1 ? true : false);
						os = q.getJSONArray("options");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					questionView = inflater.inflate(R.layout.survey_question_result, questionsLL, false);
					
					if( qIsMultiple) {
						TextView isMultipleTV = (TextView)questionView.findViewById(R.id.isMultiple);
						isMultipleTV.setVisibility(View.VISIBLE);
					} else {
						TextView isMultipleTV = (TextView)questionView.findViewById(R.id.isMultiple);
						isMultipleTV.setVisibility(View.INVISIBLE);
					}
					
					TextView qTV = (TextView)questionView.findViewById(R.id.title);
					qTV.setText(qTitle);
					
					WebView graphWV = (WebView)questionView.findViewById(R.id.graphView);
					graphWV.getSettings().setJavaScriptEnabled(true);
					graphWV.loadUrl("file:///android_asset/www/result.html");
					graphWV.addJavascriptInterface(new graphPlugin(), "GraphPlugin");
							// window.GraphPlugin. functionName(ARG); in webview html javascript
					//graphWV.setWebChromeClient(new WebChromeClient(){});
					
					// JAVASCRIPT ȣ�� :  loadURL("javascript::callJS("arg");
					optionsLL = (LinearLayout)questionView.findViewById(R.id.options);
					/*
					// Options Level�� ����
					for(int oi=0; oi<os.length(); oi++) {
						try {
							//o = os.getJSONObject(oi);
							oTitle = os.getString(oi);
						} catch (JSONException e) {
							e.printStackTrace();
						}

						// Option Level�� Manipulating
						optionView = inflater.inflate(R.layout.survey_option_result, optionsLL, false);
						Button optionControl = (Button)optionView.findViewById(R.id.control);
						
						optionControl.setTag(qi+":"+oi);
						
						TextView oTV = (TextView) optionView.findViewById(R.id.title);
						oTV.setText(oTitle);
						optionsLL.addView(optionView);
					} // for oi END
					*/
					questionsLL.addView(questionView);
				} // for qi END
				
				
			}// qs != null END
			
		}// s != null END
		return view;
		
	}
	final OnClickListener lbbOnClickListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			MainActivity.sharedActivity().toggle();
		}
	};
	
	protected void initNavigationBar(View parentView, String titleText, boolean lbbVisible, boolean rbbVisible, String lbbTitle, String rbbTitle, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		
		Button lbb = (Button)parentView.findViewById(R.id.left_bar_button);
		Button rbb = (Button)parentView.findViewById(R.id.right_bar_button);
		
		lbb.setVisibility((lbbVisible?View.VISIBLE:View.INVISIBLE));
		rbb.setVisibility((rbbVisible?View.VISIBLE:View.INVISIBLE));
		
		if(lbb.getVisibility() == View.VISIBLE) { lbb.setText(lbbTitle);	}
		if(rbb.getVisibility() == View.VISIBLE) { rbb.setText(rbbTitle);	}
		
		TextView titleView = (TextView)parentView.findViewById(R.id.title);
		titleView.setText(titleText);
		
		if(lbb.getVisibility() == View.VISIBLE) lbb.setOnClickListener(lbbOnClickListener);
		if(rbb.getVisibility() == View.VISIBLE) rbb.setOnClickListener(rbbOnClickListener);
	}
	
	protected void initNavigationBar(View parentView, int titleTextId, boolean lbbVisible, boolean rbbVisible, int lbbTitleId, int rbbTitleId, OnClickListener lbbOnClickListener, OnClickListener rbbOnClickListener) {
		initNavigationBar(parentView, getString(titleTextId), lbbVisible, rbbVisible, getString(lbbTitleId), getString(rbbTitleId), lbbOnClickListener, rbbOnClickListener);
	}
	

	/** Object exposed to JavaScript */
    private class graphPlugin {
       public void callAndroid(final String json) { // must be final
 //         handler.post(new Runnable() {
 //            public void run() {
 //               Log.d(TAG, "callAndroid(" + arg + ")");
  //              textView.setText(arg);
  //           }
   //       });
       }
    }
}
