package kr.go.KNPA.Romeo;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import kr.go.KNPA.Romeo.Member.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;


public class IntroActivity extends Activity{
	private static IntroActivity _sharedActivity; 
	
	private final int REQUEST_REGISTER_USER = 0;
	
	private boolean isUserRegistered;
	private boolean isUserEnabled;
	private boolean isDeviceRegistered;
	private boolean isDeviceEnabled;
	
	Handler h;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		_sharedActivity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);
		//h = new Handler();
		//h.post(irun);
		
		checkRegistered();
		//runOnUiThread(irun);
		
	}
	
	public static IntroActivity sharedActivity() {
		return _sharedActivity;
	}
	
	public void removeIntroView(ViewGroup v) {
		View view = (View)v.findViewById(R.id.intro);
		if(view == null)
			view = (View)v.findViewWithTag("intro");
		if(view != null)
			v.removeView(view);
		
	}
	
	Runnable logo = new Runnable() {
		@Override
		public void run() {
			
			
			
			
		
		}
	};
	
	Runnable check = new Runnable() {

		@Override
		public void run() {
			
			
		}
		
	};
	
	private void checkRegistered () {
		
		// ��� ���� : 
		// ���� ����� �Ǿ� �־�� �ϰ�, ������ ��� �����ؾ� �ϸ�,
		// ��� ����� �Ǿ� �־�� �ϰ�, ��Ⱑ ��� �����ؾ� �Ѵ�. 
		
		boolean isUserAlreadyRegistered = checkUserRegistered();
		if(isUserAlreadyRegistered == true) {
			// false �� ���, ActivityResult �� �帧�� ���� DeviceRegister ������ ��ġ�� �ȴ�.
			checkDeviceRegistered();
			runApplication();
		}
		// ����, false�� ����� �帧�� �������� �Ѵ�.
	}
	
	private boolean checkUserRegistered() {
		Intent intent = null;
		Bundle _bUserReg = MainActivity.isUserRegistered(IntroActivity.this);
		isUserRegistered = _bUserReg.getBoolean("isRegistered");
		isUserEnabled = _bUserReg.getBoolean("isEnabled");
		
		if(isUserRegistered == false) {
			// ���� ��� ����.
			intent = new Intent(IntroActivity.this, UserRegisterActivity.class);
			startActivityForResult(intent, REQUEST_REGISTER_USER);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			return false;
		} else {
			// ���� ����� �Ǿ� ���� ���.
			return true;
		}
	}
	
	private void checkDeviceRegistered() {
		Bundle _bDeviceReg = MainActivity.isDeviceRegistered(IntroActivity.this);
		isDeviceRegistered = _bDeviceReg.getBoolean("isRegistered");
		isDeviceEnabled = _bDeviceReg.getBoolean("isEnabled");
		
		if(isDeviceRegistered == false) {
			// ��� ��� ����.
			GCMRegisterManager.registerGCM(IntroActivity.this);
			
			// �ٸ� thread���� ��� ������ ����Ǹ�, 
			// GCMRegisterManager �� onRegister �޼ҵ�� �帧�� �Ѿ�µ�,
			// ������ �㰡�� ���� ����� �� �����Ƿ�, ��� ���� ���Ŀ� ���� ��� �� �� ����.
			// ���� ��� ���� Enabled ���� (disabled) ������ ����Ͽ� �� ���� �Ǵ��� ������ �����Ѵ�.
			
			// �ڵ����� �㰡������ �� ���� �ƴ϶��!!
		}
	}
	
	private void runApplication() {
		Intent intent = null;
		if(isUserEnabled && isDeviceEnabled ) {
			// ��� ����.
			// MainActivity�� �ѱ��.
			intent = new Intent(IntroActivity.this, MainActivity.class);
			
		} else {
			// ��� �Ұ� ��Ȳ
			// �ϴ� ��� ��� â���� ��ȯ.
			intent = new Intent(IntroActivity.this, NotRegisteredActivity.class);
		}
		
		startActivity(intent);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_REGISTER_USER) {
				checkDeviceRegistered();
				runApplication();
			}
		}
		//super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		//h.removeCallbacks(logo);
		return;
	}
	
}
