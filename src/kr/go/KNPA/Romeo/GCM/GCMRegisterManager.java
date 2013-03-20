package kr.go.KNPA.Romeo.GCM;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gcm.GCMRegistrar;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.Member.User;
import kr.go.KNPA.Romeo.Util.Connection;
import kr.go.KNPA.Romeo.Util.UserInfo;
import android.content.Context;
import android.util.Log;

public class GCMRegisterManager {
	private static final String tag = "GCMRegisterManager";
    
	private static GCMRegisterManager _sharedManager = null;
	//public boolean isRegistered = false;
	
	
	public GCMRegisterManager() {
	}
	
	public static GCMRegisterManager sharedManager() {
		if(_sharedManager == null) {
			_sharedManager = new GCMRegisterManager();
		}
		return _sharedManager;
	}

	public static void registerGCM(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		GCMRegistrar.register(context, "44570658441");

		
		   /*
		   	private void registerGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		if("".equals(regId) )   //���� ���̵忡�� regId.equals("")�� �Ǿ� �ִµ� Exception�� ���ϱ� ���� ����
		    GCMRegistrar.register(this, "44570658441");
		else {
		      Log.d("============== Already Registered ==============", regId);
		}
		
		}
		    */
	}
	
	public void onError(Context context, String errorId) {			/**���� �߻���*/
        Log.d(tag, "on_error. errorId : "+errorId);
    	// �ٽý����ϵ��� ����.
    	// ������ �ڲ� �߻��� �� �缳ġ�� ����.
    	//alert("������ �߻��߽��ϴ�. �ٽ� �õ����ֽñ� �ٶ��ϴ�. ������ �ݺ��Ǿ� �߻��ϴ� ��� �缳ġ���ֽñ� �ٶ��ϴ�.");
    }

	public void onRegistered(Context context, String regId) {		 /**�ܸ����� GCM ���� ��� ���� �� ��� id�� �޴´�*/

		Log.d(tag, "onRegistered. regId : "+regId);
        
		// ������ ������ Server��(ACTION : INSERT)���� �˾Ƽ� �̷�����Ƿ�, UUID���� REGID���� ã�Ƽ� �� �Ѱ��ش�.
		// DB�� UUID ���� regid ��, id���� ã�´�		
			// ��ϵ� regid���� DB���� regid ���� ������ pass
			// ��ϵ� regid���� DB���� regid ���� �ٸ��� UPDATE
		// DB�� �ش� ����� UUID ���� ������ INSERT
        
        UserInfo.setRegid(context, regId);
        String uuid = UserInfo.getUUID(context);
        if(uuid == null) {
        	UserInfo.setUUID(context);
        	uuid = UserInfo.getUUID(context);
        }
        long userIdx = UserInfo.getUserIdx(context);
        
        HashMap<String, String> data = new HashMap<String,String>();
        data.put("regid", regId);
        data.put("uuid", uuid);
        data.put("user", ""+userIdx);
        data.put("type", "a");
        
        Connection con = new Connection.Builder()
        							   .url(Connection.HOST_URL + "/device/register")
        							   .dataType(Connection.DATATYPE_JSON)
        							   .type(Connection.TYPE_POST)
        							   .data(data)
        							   .build();
        int responseCode = con.request();
        String response= null;
        if(responseCode == HttpURLConnection.HTTP_OK) {
        	response = con.response;
        	// SUCCESS : TODO deal with response;
        } else {
        	// ERROR : 
        }
      
    }
	
	public void onUnregistered(Context context, String regId) {		/**�ܸ����� GCM ���� ��� ������ �ϸ� ������ ��� id�� �޴´�*/
        Log.d(tag, "onUnregistered. regId : "+regId);
        
        
    	// ������ ������ Server��(ACTION : DELETE)���� �˾Ƽ� �̷�����Ƿ�, REGID���� ARRAY Type���� ã�Ƽ� �� �Ѱ��ش�.
		// DB�󿡼� �ش� regid���� ã�´�.
		// �����Ѵٸ� DELETE
		// ������.... PASS
        
        String uuid = UserInfo.getUUID(MainActivity.sharedActivity());
        HashMap<String, Object> data = new HashMap<String,Object>();
        
        ArrayList<String> regidids = new ArrayList<String>();
        regidids.add(regId);
        
        data.put("regidids", regidids);
        data.put("uuid", uuid);
        //data.put("action", "delete"); => method unregister
        
        Connection con = new Connection.Builder()
        							   .url(Connection.HOST_URL + "/device/unregister")
        							   .dataType(Connection.DATATYPE_JSON)
        							   .type(Connection.TYPE_POST)
        							   .data(data)
        							   .build();
        int responseCode = con.request();
        String response= null;
        if(responseCode == HttpURLConnection.HTTP_OK) {
        	response = con.response;
        	// SUCCESS : TODO deal with response;
        } else {
        	// ERROR : 
        }

    }

}
