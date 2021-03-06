package kr.go.KNPA.Romeo.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.go.KNPA.Romeo.Base.Message;
import kr.go.KNPA.Romeo.Chat.Chat;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Document.Document;
import kr.go.KNPA.Romeo.GCM.GCMSendResult;
import kr.go.KNPA.Romeo.Survey.Survey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Payload의 Data 객체에 대한 parsing을 수행. 대부분 같은 자료구조를 가지고 있으므로\n basicParse만을 거치면
 * 되지만 간혹 특별한 자료구조를 가지고 있는 응답에 대해서는\n DataParser.parse() 함수의 내부에서 switch 문으로
 * 구별하여 개별적인 parsing을 수행한다.
 * 
 * @author 최영우
 * @since 2013.4.1
 */
public class DataParser {

	/**
	 * Payload에서 일단 response json을 JSONObject로 바꾼 후,\n key가 Payload.KEY_DATA로
	 * 설정된 json array 객체를 이 메소드로 넘기면\n 이 메소드에서 event와 status code에 따라 JSONArray를
	 * 네이티브 자바 콜렉션으로 파싱하여\n 리턴한다.\n DataParser.parse( EventEnum, StatusCodeEnum,
	 * JSONArray 타입의 Data )
	 * 
	 * @param event
	 * @param status
	 * @param dataJSONArray
	 * @return data
	 * @throws JSONException
	 */
	public Data parse(String event, int status, JSONArray dataJSONArray) throws JSONException
	{
		Data dataNative = null;

		if (event.equals(Event.Message.send()))
		{
			dataNative = parse_on_msg_send(dataJSONArray);
		}
		else if (event.equals(Event.PUSH_MESSAGE))
		{
			dataNative = parse_on_push_msg(dataJSONArray);
		}
		else if (event.equals(Event.PUSH_USER_JOIN_ROOM))
		{
			dataNative = parse_on_user_join_room(dataJSONArray);
		}
		else if (event.equals(Event.MESSAGE_SURVEY_GET_CONTENT))
		{
			dataNative = parseSurveyGetContent(dataJSONArray);
		}
		else if (event.equals(Event.Message.Survey.getResult()))
		{
			dataNative = parse_on_msg_survey_result(dataJSONArray);
		}
		else
		{
			dataNative = basicParse(dataJSONArray);
		}

		return dataNative;
	}
	/**
	 * 대부분의 event들이 공통적으로 가지는 구조를 parsing 한다.\n "data" : [ {"key1":obj1,
	 * "key2":obj2, ... }, { ... } ] 와 같은 JSONArray를
	 * ArrayList<HashMap<String,Object>> 형태로 변환한다.\n
	 * 
	 * @param dataJSONArray
	 * @return parsed data
	 * @throws JSONException
	 */
	private Data basicParse(JSONArray dataJSONArray) throws JSONException
	{

		Data dataNative = new Data();

		int i, n;
		n = dataJSONArray.length();
		for (i = 0; i < n; i++)
		{
			dataNative.add(this.<Object> JSONObjectToHashMap(dataJSONArray.getJSONObject(i)));
		}

		return dataNative;
	}

	/**
	 * MESSAGE:SURVEY:GET_CONTENT
	 * @param dataJSONArray
	 * @return
	 */
	private Data parseSurveyGetContent(JSONArray dataJSONArray)
	{	
		Data data = new Data();

		try
		{
			for (int i=0; i<dataJSONArray.length(); i++)
			{
				JSONObject jo;
					jo = dataJSONArray.getJSONObject(i);
				Survey svy = (Survey) Message.parseMessage(jo.get(KEY._MESSAGE).toString());
				data.add(i, KEY._MESSAGE, svy);
			}
			return data;
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * MESSAGE:SEND 이벤트에 대한 파싱
	 * 
	 * @param dataJSONArray
	 * @return
	 * @throws JSONException
	 */
	private Data parse_on_msg_send(JSONArray dataJSONArray) throws JSONException
	{
		Data dataNative = new Data();

		HashMap<String, Object> hm = new HashMap<String, Object>();
		JSONObject jo = dataJSONArray.getJSONObject(0);
		hm.put(KEY.MESSAGE.IDX, jo.get(KEY.MESSAGE.IDX));
		hm.put(KEY.MESSAGE.RECEIVERS_REGISTRATION_IDS, jo.get(KEY.MESSAGE.RECEIVERS_REGISTRATION_IDS));
		hm.put(KEY.MESSAGE.RECEIVERS_IDX, jo.get(KEY.MESSAGE.RECEIVERS_IDX));

		GCMSendResult result = new GCMSendResult();
		JSONObject gjo = jo.getJSONObject(KEY.GCM.SEND_RESULT);
		result.canonicalIds = gjo.getInt(KEY.GCM.CANONICAL_IDS);
		result.nSuccess = gjo.getInt(KEY.GCM.SUCCESS);
		result.nFailure = gjo.getInt(KEY.GCM.FAILURE);
		result.multicastId = gjo.getInt(KEY.GCM.MULTICAST_ID);

		ArrayList<HashMap<String, String>> eachResultAr = new ArrayList<HashMap<String, String>>();

		JSONArray jar = gjo.getJSONArray(KEY.GCM.RESULTS);
		for (int i = 0; i < jar.length(); i++)
		{
			JSONObject j = jar.getJSONObject(i);
			eachResultAr.add(this.<String> JSONObjectToHashMap(j));
		}
		result.eachResult = eachResultAr;

		hm.put(KEY.GCM.SEND_RESULT, result);

		dataNative.add(hm);
		return dataNative;
	}

	/**
	 * MESSAGE:RECEIVE 이벤트에 대한 파싱
	 * 
	 * @param dataJSONArray
	 * @return
	 * @throws JSONException
	 */
	private Data parse_on_push_msg(JSONArray dataJSONArray) throws JSONException
	{
		Data dataNative = new Data();

		JSONObject jo = dataJSONArray.getJSONObject(0);

		switch (jo.getJSONObject(KEY._MESSAGE).getInt(KEY.MESSAGE.TYPE) / Message.MESSAGE_TYPE_DIVIDER)
		{
		case Message.MESSAGE_TYPE_CHAT:
			// jsonobject를 다시 json으로 바꿔서 MessageParser를 통해 생성
			Chat chat = (Chat) Message.parseMessage(jo.get(KEY._MESSAGE).toString());
			dataNative.add(0, KEY._MESSAGE, chat);
			break;
		case Message.MESSAGE_TYPE_DOCUMENT:
			Document document = (Document) Message.parseMessage(jo.get(KEY._MESSAGE).toString());
			dataNative.add(0, KEY._MESSAGE, document);

			break;
		case Message.MESSAGE_TYPE_SURVEY:
			Survey svy = (Survey) Message.parseMessage(jo.get(KEY._MESSAGE).toString());
			dataNative.add(0, KEY._MESSAGE, svy);
			break;
		default:
			break;
		}

		return dataNative;
	}

	private Data parse_on_msg_survey_result(JSONArray dataJSONArray) throws JSONException
	{
		JSONObject jo = dataJSONArray.getJSONObject(0);
		Data data = new Data();
		data.add(0, KEY.SURVEY.NUM_RECEIVERS, jo.getInt(KEY.SURVEY.NUM_RECEIVERS));
		data.add(0, KEY.SURVEY.NUM_UNCHECKERS, jo.getInt(KEY.SURVEY.NUM_UNCHECKERS));
		data.add(0, KEY.SURVEY.NUM_CHECKERS, jo.getInt(KEY.SURVEY.NUM_CHECKERS));
		data.add(0, KEY.SURVEY.NUM_RESPONDERS, jo.getInt(KEY.SURVEY.NUM_RESPONDERS));
		data.add(0, KEY.SURVEY.NUM_RESPONDERS, jo.getInt(KEY.SURVEY.NUM_RESPONDERS));
		data.add(0, KEY.SURVEY.NUM_GIVE_UP, jo.getInt(KEY.SURVEY.NUM_GIVE_UP));
		ArrayList<ArrayList<Integer>> ar = new ArrayList<ArrayList<Integer>>();
		JSONArray ja = jo.getJSONArray(KEY.SURVEY.RESULT);
		for (int i = 0; i < ja.length(); i++)
		{
			ArrayList<Integer> iar = new ArrayList<Integer>();
			for (int j = 0; j < ja.getJSONArray(i).length(); j++)
			{
				iar.add(ja.getJSONArray(i).getInt(j));
			}
			ar.add(iar);
		}
		data.add(0, KEY.SURVEY.RESULT, ar);
		return data;
	}

	private Data parse_on_user_join_room(JSONArray jsonArray) throws JSONException
	{
		JSONObject jo = jsonArray.getJSONObject(0);
		Data data = new Data();
		data.add(0, KEY.CHAT.ROOM_CODE, jo.getString(KEY.CHAT.ROOM_CODE));
		data.add(0, KEY.USER.IDX, jo.getString(KEY.USER.IDX));
		data.add(0, KEY.CHAT.ROOM_MEMBER, this.<String> JSONArrayToArrayList(jo.getJSONArray(KEY.CHAT.ROOM_MEMBER)));
		return data;
	}

	@SuppressWarnings("unchecked")
	private <T> ArrayList<T> JSONArrayToArrayList(JSONArray jsonArray) throws JSONException
	{
		int i, n;
		ArrayList<T> arrayList;

		n = jsonArray.length();
		arrayList = new ArrayList<T>(n);
		for (i = 0; i < n; i++)
		{
			arrayList.add((T) jsonArray.get(i));
		}

		return arrayList;
	}

	@SuppressWarnings("unchecked")
	private <T> HashMap<String, T> JSONObjectToHashMap(JSONObject jsonObject) throws JSONException
	{

		HashMap<String, T> hm = new HashMap<String, T>();

		Iterator<?> keys = jsonObject.keys();

		while (keys.hasNext())
		{
			String key = (String) keys.next();
			hm.put(key, (T) jsonObject.get(key));
		}

		return hm;
	}
}
