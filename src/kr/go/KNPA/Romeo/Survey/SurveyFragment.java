package kr.go.KNPA.Romeo.Survey;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class SurveyFragment extends RomeoFragment {

	// Managed Fragments
	public static SurveyFragment _departedFragment = null;
	public static SurveyFragment _receivedFragment = null;

	// Constructor
	public SurveyFragment() {
		this(Survey.TYPE_RECEIVED);
	}

	public SurveyFragment(int type) {
		super(type);
	}

	public static SurveyFragment surveyFragment(int type) {
		SurveyFragment f = null;
		
		if(type == Survey.TYPE_DEPARTED) {
			if(_departedFragment == null) 
				_departedFragment = new SurveyFragment(type);
			f = _departedFragment;
		} else {
			if(_receivedFragment == null) 
				_receivedFragment = new SurveyFragment(type);
			f = _receivedFragment;
		}
		
		return f;
	}

	// Manage List View
	public SurveyListView getListView() {
		View view = ((ViewGroup)getView());
		SurveyListView lv = null;
		
		if(view!=null) {
			lv = (SurveyListView)view.findViewById(R.id.surveyListView);
		}
		
		return lv;
	}
	
	// Fragment Life-cycle Management
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(type == Survey.TYPE_DEPARTED) {
			_departedFragment = null;
		} else if(type==Survey.TYPE_RECEIVED) {
			_receivedFragment = null;
		}
	}
	
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;

		OnClickListener lbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		OnClickListener rbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(getActivity(), SurveyComposeFragment.class);
				//TODO
				//startActivity(intent);
				SurveyComposeFragment f = new SurveyComposeFragment();
				MainActivity.sharedActivity().pushContent(f);
			}
		};
		
		switch(this.type) {
		case Survey.TYPE_RECEIVED :
			view = inflater.inflate(R.layout.survey_fragment, container, false);
			initNavigationBar(
							view, 
							R.string.surveyReceivedTitle, 
							true, 
							true, 
							R.string.menu, 
							R.string.compose, 
							lbbOnClickListener, rbbOnClickListener);

			break;
		case Survey.TYPE_DEPARTED :
			view = inflater.inflate(R.layout.survey_fragment, container, false);
			initNavigationBar(
					view, 
					R.string.surveyDepartedTitle, 
					true, 
					true, 
					R.string.menu, 
					R.string.compose, 
					lbbOnClickListener, rbbOnClickListener);
			break;
		}
		
		listView = (SurveyListView)initListViewWithType(this.type, R.id.surveyListView, view);
		
		return view;
	}
	
	
	public static void receive(Survey survey) {
		if(_receivedFragment != null) {
			View view = _receivedFragment.getView();
			SurveyListView slv = null;
			if(view!=null) {
				ViewGroup layout = (ViewGroup) view.findViewById(R.id.rootLayout);
				slv = (SurveyListView)layout.findViewById(R.id.surveyListView);
			}
			final SurveyListView lv = slv;
			if(slv != null) {
				_receivedFragment.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						lv.refresh();	
					}
				});
			}
		}
	}
}
