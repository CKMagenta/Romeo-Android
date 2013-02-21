package kr.go.KNPA.Romeo.GCM;

import java.util.Iterator;

import org.json.JSONObject;

import kr.go.KNPA.Romeo.Base.Packet;
import kr.go.KNPA.Romeo.Base.Payload;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.Survey.Survey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GCMMessageManager {
	
	public static final int NOT_SPECIFIED = -777;
	private static final String tag = "GCMMessageManager";
    
	private static GCMMessageManager _sharedManager = null;
	public GCMMessageManager() {
	}
	
	public static GCMMessageManager sharedManager() {
		if(_sharedManager == null) {
			_sharedManager = new GCMMessageManager();
		}
		return _sharedManager;
	}

	public void onMessage(Context context, Intent intent) {			/** Ǫ�÷� ���� �޽��� */
        Bundle b = intent.getExtras();

        Iterator<String> iterator = (Iterator<String>)b.keySet().iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            String value = b.get(key).toString();
            Log.d(tag, "onMessage. "+key+" : "+value);
        }
        
        
        // �޽����� ������, _packet�̴�. TODO
        JSONObject _p = null;
        // �̸� Packet���� �ѱ��.
        Packet packet = new Packet(_p);
        
        Payload payload = packet.payload;
        String event = payload.event;
        
        // event�� �Ľ��Ѵ�. TODO
        String[] events = event.split("/:/");
        
        // �Ľ̵� event�� ���� �۾��� �з��Ѵ�. TODO
        if(events[0].equalsIgnoreCase("CHAT")) {
        	Chat chat = new Chat.Builder()
								.idx(payload.message.idx)
        						.sender(payload.sender)
        						.receivers(payload.receivers)
        						.title(payload.message.title)
        						.content(payload.message.content)
        						.appendix(payload.message.appendix)
        						.received(true)
        						.favorite(false)
        						.roomCode(payload.roomCode)
        						.TS(System.currentTimeMillis())
        						.checkTS(NOT_SPECIFIED)
        						.build();
        	
        } else if(events[0].equalsIgnoreCase("DOCUMENT")) {
        	Document document = new Document.Builder()
											.idx(payload.message.idx)
											.sender(payload.sender)
											.receivers(payload.receivers)
											.title(payload.message.title)
											.content(payload.message.content)
											.appendix(payload.message.appendix)
											.received(true)
											.favorite(false)
											.roomCode(payload.roomCode)
											.TS(System.currentTimeMillis())
											.checkTS(NOT_SPECIFIED)
											.build();
        } else if(events[0].equalsIgnoreCase("SURVEY")) {
        	Survey survey = new Survey.Builder()
										.idx(payload.message.idx)
										.sender(payload.sender)
										.receivers(payload.receivers)
										.title(payload.message.title)
										.content(payload.message.content)
										.appendix(payload.message.appendix)
										.received(true)
										.roomCode(payload.roomCode)
										.TS(System.currentTimeMillis())
										.checkTS(NOT_SPECIFIED)
										.build();
        }
    }
}
