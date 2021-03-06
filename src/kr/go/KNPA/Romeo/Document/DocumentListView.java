package kr.go.KNPA.Romeo.Document;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoListView;
import kr.go.KNPA.Romeo.DB.DAO;
import kr.go.KNPA.Romeo.DB.DocuDAO;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.Sectionizer;
import kr.go.KNPA.Romeo.SimpleSectionAdapter.SimpleSectionAdapter;
import kr.go.KNPA.Romeo.Util.WaiterView;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class DocumentListView extends RomeoListView implements android.widget.AdapterView.OnItemClickListener{

	// Constructor
	public DocumentListView(Context context) 									{	this(context, null);				}
	public DocumentListView(Context context, AttributeSet attrs) 				{	this(context, attrs, 0);			}
	public DocumentListView(Context context, AttributeSet attrs, int defStyle) 	{	super(context, attrs, defStyle);	}

	
	// initializer
	@Override
	public DocumentListView initWithType(int type) {
		this.subType = type;
		
		listAdapter = new DocumentListAdapter(getContext(), null, false, this.subType);
		switch(this.subType) {
		case Document.TYPE_RECEIVED :
			Sectionizer<Cursor> sectionizer = new Sectionizer<Cursor>() {
				@Override
				public String getSectionTitleForItem(Cursor c) {
					boolean checked = (c.getInt(c.getColumnIndex(
										DAO.document(getContext()).COLUMN_IS_CHECKED
										)) >0 ? true : false);
					return (checked ?  getContext().getString(R.string.checkedChat) : getContext().getString(R.string.unCheckedChat));
				}
			};
			
			SimpleSectionAdapter<Cursor> sectionAdapter
				= new SimpleSectionAdapter<Cursor>(getContext(), listAdapter, R.layout.section_header, R.id.title, sectionizer);
			this.setAdapter(sectionAdapter);
			this.setOnItemClickListener(this);
			break;
		case Document.TYPE_DEPARTED :
		case Document.TYPE_FAVORITE :
			this.setAdapter(listAdapter);
			this.setOnItemClickListener(this);
			break;
		default : break;
		
		}
		
		return this;
	}
	
	@Override
	protected Cursor query() {
		
		Cursor c = DAO.document(getContext()).getDocumentList(this.subType);
		return c;
		
		//return DBProcManager.sharedManager(getContext()).document().getDocumentList(this.type);
	}	

	// Click Listener
	@SuppressWarnings("rawtypes")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long l_position) {
		ListAdapter adapter = listAdapter;
		if(this.getAdapter() instanceof SimpleSectionAdapter)
			adapter= ((SimpleSectionAdapter)this.getAdapter());
		
		Cursor c = (Cursor)adapter.getItem(position);
	
		String documentIdx = c.getString(c.getColumnIndex(DocuDAO.COLUMN_DOC_IDX));
		
		DocumentDetailFragment fragment = new DocumentDetailFragment(documentIdx);//, type);
		MainActivity.sharedActivity().pushContent(fragment);
	}

	@Override
	public void onPreExecute() {
		WaiterView.showDialog(getContext());
	}
	@Override
	public void onPostExecute(boolean isValidCursor) {
		WaiterView.dismissDialog(getContext());
	}

}
