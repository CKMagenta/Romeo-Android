package kr.go.KNPA.Romeo.Config;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * 앱 실행 시 DB의 버전을 확인하고 생성한다. 각각의 모듈 별 DB 작업은 모듈 내에서 따로 처리함
 */
public class DBManager extends SQLiteOpenHelper {

	private final String TAG = this.getClass().getName();
	
	//! 로컬 DB 파일 이름
	private static final String DATABASE_NAME = "romeo.db"; 
	//! DB 버전
	private static final int DATABASE_VERSION = 1;
	//! 지시와 보고 테이블
	public static final String TABLE_COMMAND = "command";
	//! 회의 테이블
	public static final String TABLE_MEETING = "meeting";
	//! 설문조사 테이블
	public static final String TABLE_SURVEY = "survey";
	//! 업무연락 테이블
	public static final String TABLE_DOCUMENT = "document";
	//! 즐겨찾기 테이블
	public static final String TABLE_MEMBER_FAVORITE = "member_favorite";
	//! 회원 정보 테이블
	public static final String TABLE_MEMBER = "member";
	//! 부서 정보 테이블
	public static final String TABLE_DEPARTMENT = "department";
	
	/**
	 * 앱 실행 시 DBManager에 현재 context를 넣어 실행시킨다
	 * @param context 현재 context
	 */
	public DBManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * 생성자에서 언급한 DATABASE_NAME의 DB가 존재하지 않을 경우에만 onCreate가 호출된다.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = null;
		
		// Command Table
		sql = "CREATE  TABLE "+TABLE_COMMAND+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" content VARCHAR, appendix BLOB,"+
				" roomCode VARCHAR,"+
				" sender INTEGER, receivers TEXT,"+
				" received BOOL, TS INTEGER,"+
				" checked BOOL DEFAULT 0, " +
				" checkTS INTEGER, idx INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create command "+e.getMessage());
		}
		// Meeting Table
		sql = "CREATE  TABLE "+TABLE_MEETING+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" content VARCHAR, appendix BLOB, sender INTEGER,"+
				" roomCode VARCHAR,"+
				" receivers TEXT, received BOOL,"+
				" TS INTEGER,"+
				" checked BOOL DEFAULT 0, " +
				" checkTS INTEGER, idx INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create meeting "+e.getMessage());
		}
		
		// Survey Table
		sql = "CREATE  TABLE "+TABLE_SURVEY+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" title VARCHAR, content TEXT, appendix BLOB,"+
				" sender INTEGER, receivers TEXT, received BOOL,"+
				" TS INTEGER,"+
				" checked BOOL DEFAULT 0, " +
				" checkTS INTEGER, openTS INTEGER,"+
				" answersheet BLOB, answered BOOL DEFAULT 0,"+
				" closeTS INTEGER, idx INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create survey "+e.getMessage());
		}
		
		// Document Table
		sql = "CREATE  TABLE "+TABLE_DOCUMENT+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" title VARCHAR, content TEXT,"+
				" appendix BLOB, sender INTEGER,"+
				" receivers TEXT, received BOOL,"+
				" TS INTEGER,"+
				" checked BOOL DEFAULT 0, " +
				" checkTS INTEGER, favorite BOOL,"+
				" idx INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create document "+e.getMessage());
		}
		
		// Favorite Members Table
		sql = "CREATE  TABLE "+TABLE_MEMBER_FAVORITE+
				" ("+BaseColumns._ID+
				" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ,"+
				" idxs VARCHAR NOT NULL ," +
				" isGroup BOOL DEFAULT 0 ," +
				" title VARCHAR,"+
				" TS INTEGER)";
		try {
			db.execSQL(sql);
		} catch (SQLException e ) {
			Log.w(TAG, "create member(favorite) "+e.getMessage());
		}
	
	}

	/**
	 * DB의 버전이 바뀌었을 때 업데이트 한다
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {

	}

}
