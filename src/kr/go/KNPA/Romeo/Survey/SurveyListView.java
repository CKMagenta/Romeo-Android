package kr.go.KNPA.Romeo.Survey;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.DB.DBProcManager;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

public class SurveyListView extends RomeoListView implements OnItemClickListener{
	
	// Constructor
	public SurveyListView(Context context)	 									{	this(context, null);				}
	public SurveyListView(Context context, AttributeSet attrs) 					{	this(context, attrs, 0);			}
	public SurveyListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle);	}

	// Initialize
	@Override
	public SurveyListView initWithType (int type) {
		this.type = type;
		
		switch(this.type) {
		case Survey.TYPE_DEPARTED :
			listAdapter = new SurveyListAdapter(getContext(), null, false, this.type);
			this.setAdapter(listAdapter);
			this.setOnItemClickListener(this);
			break;
			
		case Survey.TYPE_RECEIVED :
			Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
				@Override
				public String getSectionTitleForItem(Cursor c) {
					boolean checked = c.getLong(c.getColumnIndex(KEY.SURVEY.IS_CHECKED)) > 0 ? true : false;
					return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));
				}
			};
			listAdapter = new SurveyListAdapter(getContext(), null, false, this.type);

			SimpleSectionAdapter<Cursor> sectionAdapter
				= new SimpleSectionAdapter<Cursor>(getContext(), listAdapter, R.layout.section_header, R.id.cell_title, sectionizer);
			this.setAdapter(sectionAdapter);
			this.setOnItemClickListener(this);
			break;
		}
		
		
		return this;
	}
	
	// Database management
	@Override
	protected Cursor query() {	return DBProcManager.sharedManager(getContext()).document().getDocumentList(this.type);	}

	// Click Listener
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
		Survey survey = new Survey(getContext(), c);
		SurveyAnswerFragment f = new SurveyAnswerFragment(survey, type);
		MainActivity.sharedActivity().pushContent(f);
	}

}
