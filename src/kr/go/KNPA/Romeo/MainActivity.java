package kr.go.KNPA.Romeo;
import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;

import kr.go.KNPA.Romeo.BaseActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import kr.go.KNPA.Romeo.Util.Preference;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
	static MainActivity _sharedActivity = null;
	
	private Fragment mContent;		// ���� �����׸�Ʈ 
	
	public MainActivity() {		// ������ 
		super(R.string.changing_fragments);
		_sharedActivity = this;
	}
	
	public static MainActivity sharedActivity() {
		return _sharedActivity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		// �θ� Ŭ������ ��ũ������
		Preference.initSharedPreference(getBaseContext());
		registerGCM();
		
		
		
		// set the Above View
				if (savedInstanceState != null)
					mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent"); // restore
				if (mContent == null)
					mContent = new MemberFragment();	// ùȭ��										// ���� 		���� �߿��� Ŭ������ �ƴϴ�.
				
				// set the Above View
				setContentView(R.layout.content_frame);					// ���̾ƿ��� �ִ� �� ��   
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, mContent)
				.commit();												// ����Ʈ �����Ӱ� ����(Ȥ�� ������) �����׸�Ʈ�� �ٲ۴�.
				
				// set the Behind View
				setBehindContentView(R.layout.menu_frame);				// �����ε� ��������, �޴� ���. �����׸�Ʈ�� �����ϱ� ���� ������� ����(���̽�������)
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.menu_frame, new MenuListFragment())
				.commit();
				
				// customize the SlidingMenu
				getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				// �����̵� �޴��� ����??????
				//?????	
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);	// Ű ������ ��
	}
	
	public void switchContent(Fragment fragment) {		// �� �ҽ� �������� ������ �ʾҴ�.
		mContent = fragment;							// �ٲ� �����׸�Ʈ�� fragment ������ �޾�, �� ��ü�� ���� ������ �Ҵ��Ѵ�.
		getSupportFragmentManager()						// �����׸�Ʈ �Ŵ����� ȣ���Ͽ� ��ü�Ѵ�.
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}
	
	
	////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void registerGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if("".equals(regId))   //���� ���̵忡�� regId.equals("")�� �Ǿ� �ִµ� Exception�� ���ϱ� ���� ����
			// TODO : �����ϴ���, ������ ����� �Ǿ����� ������ ����ϵ��� ��Ŵ.
		      GCMRegistrar.register(this, "44570658441");
		else
		      Log.d("==============", regId);
	}

}





