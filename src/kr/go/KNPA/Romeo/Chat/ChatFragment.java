/**
 * 
 */
package kr.go.KNPA.Romeo.Chat;

import java.util.ArrayList;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.R;
import kr.go.KNPA.Romeo.RomeoFragment;
import kr.go.KNPA.Romeo.Member.MemberSearch;
import kr.go.KNPA.Romeo.Member.User;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * 메뉴에서 Chat 종류의 기능을 선택했을 때 만날 수 있는 Fragment. \n
 * Meeting과 Command가 존재한다.
 */
public class ChatFragment extends RomeoFragment {

	
	

	/**
	 * @name Constructors
	 * @{
	 */
	public ChatFragment() 			{	this(Chat.TYPE_MEETING);	}
	public ChatFragment(int type) 	{	super(type);				}
	/** @} */
	
	/**
	 * @name Singleton (Fragment)
	 * @{
	 */
	private static ChatFragment _commandFragment = null;
	private static ChatFragment _meetingFragment = null;
	public static ChatFragment chatFragment(int type) {
		ChatFragment f = null;
		if(type == Chat.TYPE_COMMAND) {
			if(_commandFragment == null)
				_commandFragment = new ChatFragment(Chat.TYPE_COMMAND);
			f = _commandFragment;
		} else {
			if(_meetingFragment == null)
				_meetingFragment = new ChatFragment(Chat.TYPE_MEETING);
			f = _meetingFragment;
		}
		
		return f;
	}
	/** @} */
	
	/** 
	 * @name Singleton (Room)
	 * 현재 특정 Room Fragment안에 입장해 있다면, 해당 RoomFragment를 반환한다.
	 * @{
	 */
	private static RoomFragment _currentRoom = null;
	/*
	 * Manager Current Room
	 */
	public static RoomFragment 	getCurrentRoom() 					{	return _currentRoom;	}
	public static void		 	setCurrentRoom(RoomFragment ra) 	{	_currentRoom = ra;		}
	public static void 			unsetCurrentRoom() 					{	_currentRoom = null;	}
	/** @} */
	
	// Manage List View
	public RoomListView getListView() {
		View view = ((ViewGroup)getView());
		RoomListView lv = null;
		
		if(view!=null) {
			lv = (RoomListView)view.findViewById(R.id.roomListView);
		}
		
		return lv;
	}
	
	// Manage Fragment Life-cycle
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(type == Chat.TYPE_COMMAND) {
			_commandFragment = null;
		} else if(type==Chat.TYPE_MEETING) {
			_meetingFragment = null;
		}
	}
	
	@Override
	public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;


		OnClickListener lbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.sharedActivity().toggle();
			}
		};
		
		OnClickListener rbbOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MemberSearch.class);
				startActivityForResult(intent, MemberSearch.REQUEST_CODE);
			}
		};
		
		switch(this.type) {
		case Chat.TYPE_MEETING :
			view = inflater.inflate(R.layout.chat_fragment, container, false);
			initNavigationBar(
							view, 
							R.string.meetingTitle, 
							true, 
							true, 
							R.string.menu, 
							R.string.add, 
							lbbOnClickListener, rbbOnClickListener);

			break;
		case Chat.TYPE_COMMAND :
			view = inflater.inflate(R.layout.chat_fragment, container, false);
			initNavigationBar(
					view, 
					R.string.commandTitle, 
					true, 
					true, 
					R.string.menu, 
					R.string.add, 
					lbbOnClickListener, rbbOnClickListener);
			break;
		}
				
		listView = (RoomListView)initListViewWithType(this.type, R.id.roomListView, view);

		return view;
	}
	
	
	/**
	 * (Message Receiving) GCMMessageManager에서 onMessage, onChat을 거쳐 이 메서드가 호출되게 된다. \n
	 * GCM 모듈을 통해서 진입했으므로 당연히 별도의 thread상에서 작업이 이루어진다.
	 * @param chat 새로 도착한 chat의 instance
	 */

	public static void receive(Chat chat) {
		// 현재 방에 대해서 작업을 한다.
		// 현재 특정 방안에 입장해 있다면, 그 방의 인스턴스에도 메시지를 전달한다.
		RoomFragment rf = getCurrentRoom();
		if(	rf !=null && 
			rf.room != null && 
			rf.room.roomCode !=null &&
			rf.room.roomCode.equals(chat.roomCode))
			rf.receive(chat);
		
		// 현재 Fragment의 ListView에도 메시지를 전달하여 refresh할 수 있도록 한다.
		ChatFragment f = null;
		if(chat.subType() == Chat.TYPE_COMMAND) 
			f = _commandFragment;
		else if(chat.subType() == Chat.TYPE_MEETING)
			f = _meetingFragment;
	
		if(f == null) return;
		final RoomListView lv = f.getListView();
		
		if(lv == null) return;
		f.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				lv.refresh();	
			}
		});
	}

	
	/**
	 * ChatFragment의 RoomListView 상단의 NavigationBar의 버튼 중 새 채팅을 시작하는 버튼이 있다. \n
	 * 이 버튼을 누르면 조건부 검색 창이 떠서 사람들을 선택할 수 있고, 그 결과를 이 onActivityResult 에서 처리한다.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == MemberSearch.REQUEST_CODE) {
			if(resultCode != MemberSearch.RESULT_OK) {
				// onError
			} else {
				//data.getExtras().get;
				
				ArrayList<String> receiversIdxs = data.getExtras().getStringArrayList("receivers");
				
				ArrayList<User> newUsers = User.getUsersWithIdxs(receiversIdxs);
				RoomFragment fragment = new RoomFragment(new Room(this.type, Room.makeRoomCode(getActivity()), newUsers));
				MainActivity.sharedActivity().pushContent(fragment);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}