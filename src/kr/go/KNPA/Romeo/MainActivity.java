package kr.go.KNPA.Romeo;
import com.google.android.gcm.GCMRegistrar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.GetChars;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import kr.go.KNPA.Romeo.BaseActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.Member.MemberFragment;
import kr.go.KNPA.Romeo.Menu.MenuListFragment;
import kr.go.KNPA.Romeo.Util.Preference;

import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends BaseActivity {
	static MainActivity _sharedActivity = null;
	
	private Fragment mContent;		// ���� �����׸�Ʈ 
	private Fragment oldFragment;
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
					mContent = new MemberFragment(MemberFragment.TYPE_MEMBERLIST);	// ùȭ��										// ���� 		���� �߿��� Ŭ������ �ƴϴ�.
				
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
	
	public void pushContent(Fragment fragment) {
		oldFragment = mContent;
		mContent = fragment;					
		getSupportFragmentManager()				
		.beginTransaction()
		.setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
		.addToBackStack(null)
		.add(R.id.content_frame, fragment)
		//.replace(R.id.content_frame, fragment)
		//.remove(mContent)
		.commit();
		
		//http://developer.android.com/guide/topics/resources/animation-resource.html#View
		
	}
	
	public void popContent(Fragment fragment) {
		mContent = oldFragment;
		// TODO : �� ��ư���� �ڵ��ƿö� mFragment�� ������ ����� ����. // savedInstace���� ���Ѱ��ε� 
		getSupportFragmentManager()				
		.beginTransaction()
		.setCustomAnimations(R.anim.slide_in_right, R.anim.stay, R.anim.stay, R.anim.slide_out_right)
		.remove(fragment)
		.commit();
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

	public static void showToast(String string) {
		Toast.makeText(_sharedActivity, string, Toast.LENGTH_SHORT).show();
		Log.d("GCM", string);
		//http://blog.daum.net/haha25/5388319
		//http://raid79.tistory.com/661
		//https://www.google.co.kr/#hl=ko&newwindow=1&sclient=psy-ab&q=GCM+%ED%86%A0%EC%8A%A4%ED%8A%B8&oq=GCM+%ED%86%A0%EC%8A%A4%ED%8A%B8&gs_l=hp.3...1011.7643.0.7696.17.13.3.0.0.3.132.1266.8j5.13.0.eappsweb..0.0...1.1j4.4.psy-ab.zyA55BqdMYc&pbx=1&bav=on.2,or.r_gc.r_pw.r_cp.r_qf.&bvm=bv.42768644,d.aGc&fp=45d2175d682c5ba8&biw=1024&bih=1185
	}
}





