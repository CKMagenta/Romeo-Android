package kr.go.KNPA.Romeo.search;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.CacheManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MemberSearchActivity extends Activity implements MemberSearchActivityLayout.Listener {
	public static final String			KEY_EXCLUDE_IDXS	= "excludeIdxs";
	public static final String			KEY_RESULT_IDXS		= "resultsIdxs";
	public static final int				REQUEST_CODE		= 102;

	private MemberSearchActivityLayout	mLayout;
	private ArrayList<String>			mExcludeIdxs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mLayout = new MemberSearchActivityLayout(this, R.layout.activity_member_search);
		mLayout.setListener(this);
		mLayout.setLeftNavBarBtnText(R.string.cancel);
		mLayout.setNavBarTitleTV("사용자 검색");

		Bundle b = getIntent().getExtras();

		mExcludeIdxs = b.getStringArrayList(KEY_EXCLUDE_IDXS);
		if (mExcludeIdxs == null)
		{
			mExcludeIdxs = new ArrayList<String>();
		}

		ArrayList<User> users = CacheManager.getCachedUsers();

		MemberSearchTextViewAdapter adapter = new MemberSearchTextViewAdapter(MemberSearchActivity.this, users);

		adapter.setExcludeIdxs(mExcludeIdxs);
		mLayout.getSearchInput().setAdapter(adapter);
	}

	@Override
	public void onLeftNavBarBtnClick()
	{
		finish();
	}

	@Override
	public void onRightNavBarBtnClick()
	{
	}

	@Override
	public void onGoToDeptTree()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onGoToFavorite()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubmit()
	{
		if (getMembersIdx().size() == 0)
		{
			Toast.makeText(this, "추가할 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent();
		intent.putExtra(KEY_RESULT_IDXS, getMembersIdx().toArray(new String[getMembersIdx().size()]));
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	public ArrayList<String> getMembersIdx()
	{
		return mLayout.getSearchInput().getMembersIdx();
	}
}
