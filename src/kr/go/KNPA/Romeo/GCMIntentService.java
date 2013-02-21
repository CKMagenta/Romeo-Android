package kr.go.KNPA.Romeo;
//���񽺴� �ݵ�� �ڽ��� �⺻ ��Ű���� ���ؾ� �ϸ� �̸��� GCMIntentService�� �ؾ� �Ѵ�.

import kr.go.KNPA.Romeo.GCM.GCMMessageManager;
import kr.go.KNPA.Romeo.GCM.GCMRegisterManager;
import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	//���񽺴� GCMBaseIntentService�� ��ӹ޾� �����ؾ� �Ѵ�.
	private static final String tag = "GCMIntentService";
    private static final String PROJECT_ID = "44570658441";
    //���� api ������ �ּ� [https://code.google.com/apis/console/#project:�� ��ȣ]
    //#project: ������ ���ڰ� ���� PROJECT_ID ���� �ش��Ѵ�
    
  //public �⺻ �����ڸ� ������ ������ �Ѵ�.
	public GCMIntentService() {
		 this(PROJECT_ID);
	}

	public GCMIntentService(String... senderIds) {
		super(senderIds);
	}

	@Override
	protected void onError(Context context, String errorId) {			/**���� �߻���*/
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onError(context, errorId);
    }

	@Override
	protected void onMessage(Context context, Intent intent) {			/** Ǫ�÷� ���� �޽��� */
		GCMMessageManager mm = GCMMessageManager.sharedManager();
        mm.onMessage(context, intent);
    }

	@Override
	protected void onRegistered(Context context, String regId) {		 /**�ܸ����� GCM ���� ��� ���� �� ��� id�� �޴´�*/
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onRegistered(context, regId);
    }

	@Override
	protected void onUnregistered(Context context, String regId) {		/**�ܸ����� GCM ���� ��� ������ �ϸ� ������ ��� id�� �޴´�*/
		GCMRegisterManager rm = GCMRegisterManager.sharedManager();
        rm.onUnregistered(context, regId);
    }

}
