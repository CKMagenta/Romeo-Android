package kr.go.KNPA.Romeo.Util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import kr.go.KNPA.Romeo.MainActivity;
import kr.go.KNPA.Romeo.Config.ConnectionConfig;
import kr.go.KNPA.Romeo.Config.Event;
import kr.go.KNPA.Romeo.Config.KEY;
import kr.go.KNPA.Romeo.Config.MimeType;
import kr.go.KNPA.Romeo.Connection.Connection;
import kr.go.KNPA.Romeo.Connection.Data;
import kr.go.KNPA.Romeo.Connection.Payload;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

/**
 * 이미지 파일 관리
 */
public class ImageManager {
	private static final String							TAG						= ImageManager.class.getName();

	/**
	 * @name 프로필 사진 프로필 사진은 일반 리스트에서 사용하는 small\n MemberDetailActivity에서 사용하는
	 *       medium\n 그리고 원본 original의 세 사이즈로 저장한다.
	 * @{
	 */
	public static final int								PROFILE_SIZE_SMALL		= 1;
	public static final int								PROFILE_SIZE_MEDIUM		= 2;
	public static final int								PROFILE_SIZE_ORIGINAL	= 3;
	/**@}*/
	/**
	 * @name 채팅방 내 이미지 채팅방 내 채팅목록에서 보일 small 사이즈\n 원본 파일의 두 가지로 저장한다
	 * @{
	 */
	public static final int								CHAT_SIZE_SMALL			= 4;
	public static final int								CHAT_SIZE_ORIGINAL		= 5;
	/**@}*/

	private CallbackEvent<Payload, Integer, Payload>	callBack				= new CallbackEvent<Payload, Integer, Payload>();

	public ImageManager callBack(CallbackEvent<Payload, Integer, Payload> callback)
	{
		this.callBack = callback;
		return this;

	}

	/**
	 * 백그라운드에서 프로필 사진을 서버에 업로드\n 원본을 업로드 하면 세 가지 썸네일을 만든다.\n
	 * 
	 * @b callBackType CallBackEvent<Payload,Integer,Payload>
	 * @param imageType
	 *            ImageManager에 상수로 설정된 이미지 타입 값들
	 * @param imageHash
	 *            이미지에 할당된 해쉬값. 프로필사진이면 그냥 유저해쉬, 채팅사진이면 content에 들어있는 해쉬
	 * @param fileName
	 *            업로드할 파일의 로컬 디렉토리
	 * @return
	 */
	public boolean upload(int imageType, String imageHash, String fileName)
	{
		return upload(imageType, imageHash, fileName, true);
	}

	public boolean upload(int imageType, String imageHash, Uri picUri)
	{
		Cursor c = MainActivity.sharedActivity().getContentResolver().query(picUri, null, null, null, null);
		c.moveToNext();
		String filePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
		return upload(imageType, imageHash, filePath);
	}

	public boolean upload(int imageType, String imageHash, String fileName, boolean async)
	{
		switch (imageType)
		{
		case PROFILE_SIZE_ORIGINAL:
		case CHAT_SIZE_ORIGINAL:
			break;
		default:
			Log.e(TAG, "업로드 시 파일 타입은 원본만 가능");
			return false;
		}

		String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		String contentType = MimeType.jpeg;
		if (ext.equals("png"))
		{
			contentType = MimeType.png;
		}

		Payload requestPayload = new Payload().setEvent(Event.Upload.image());
		Data reqData = new Data();
		reqData.add(0, KEY.UPLOAD.FILE_IDX, imageHash);
		reqData.add(0, KEY.UPLOAD.FILE_TYPE, imageType);
		requestPayload.setData(reqData);
		Connection conn = new Connection().timeout(30000).requestPayload(requestPayload).async(async).contentType(contentType).attachFile(fileName).callBack(callBack);
		conn.request();
		return conn.successful;
	}

	/**
	 * 로컬 캐시나 서버로부터 사진을 불러와서 imageview에 설정
	 * 
	 * @param imageType
	 *            ImageManager에 상수로 설정된 이미지 타입 값들
	 * @param imageHash
	 *            이미지에 할당된 해쉬값. 프로필사진이면 그냥 유저해쉬, 채팅사진이면 content에 들어있는 해쉬
	 * @param imageView
	 *            로딩된 이미지를 삽입할 이미지뷰 객체
	 */
	public void loadToImageView(int imageType, String imageHash, ImageView imageView)
	{
		loadToImageView(imageType, imageHash, imageView, false);
	}

	public void loadToImageView(int imageType, String imageHash, ImageView imageView, boolean showDialog)
	{
		// 이미지 타입에 따라 경로 설정
		String path = null;
		switch (imageType)
		{
		case PROFILE_SIZE_SMALL:
			path = ConnectionConfig.UploadPath.PROFILE_IMG_SMALL + imageHash + ".jpg";
			break;
		case PROFILE_SIZE_MEDIUM:
			path = ConnectionConfig.UploadPath.PROFILE_IMG_MEDIUM + imageHash + ".jpg";
			break;
		case PROFILE_SIZE_ORIGINAL:
			path = ConnectionConfig.UploadPath.PROFILE_IMG_ORIGINAL + imageHash + ".jpg";
			break;
		case CHAT_SIZE_SMALL:
			path = ConnectionConfig.UploadPath.CHAT_IMG_SMALL + imageHash + ".jpg";
			break;
		case CHAT_SIZE_ORIGINAL:
			path = ConnectionConfig.UploadPath.CHAT_IMG_ORIGINAL + imageHash + ".jpg";
			break;
		default:
			Log.e(TAG, "잘못된 이미지 타입 :" + String.valueOf(imageType));
			return;
		}

		// 캐시에서 먼저 검사
		String imageKey = getImageCacheKey(imageHash, imageType);
		final Bitmap bitmap = CacheManager.getBitmapFromMemCache(imageKey);

		if (bitmap == null)
		{
			// 없으면 서버에서 가져오고 캐싱
			LoadImageTask task = new LoadImageTask(imageView, showDialog);
			task.execute(path, imageKey);
		}
		else if (imageView != null)
		{
			imageView.setImageBitmap(bitmap);
		}
	}

	public Bitmap load(int imageType, String imageHash)
	{
		// 이미지 타입에 따라 경로 설정
		String path = null;
		switch (imageType)
		{
		case PROFILE_SIZE_SMALL:
			path = ConnectionConfig.UploadPath.PROFILE_IMG_SMALL + imageHash + ".jpg";
			break;
		case PROFILE_SIZE_MEDIUM:
			path = ConnectionConfig.UploadPath.PROFILE_IMG_MEDIUM + imageHash + ".jpg";
			break;
		case PROFILE_SIZE_ORIGINAL:
			path = ConnectionConfig.UploadPath.PROFILE_IMG_ORIGINAL + imageHash + ".jpg";
			break;
		case CHAT_SIZE_SMALL:
			path = ConnectionConfig.UploadPath.CHAT_IMG_SMALL + imageHash + ".jpg";
			break;
		case CHAT_SIZE_ORIGINAL:
			path = ConnectionConfig.UploadPath.CHAT_IMG_ORIGINAL + imageHash + ".jpg";
			break;
		default:
			Log.e(TAG, "잘못된 이미지 타입 :" + String.valueOf(imageType));
			return null;
		}

		// 캐시에서 먼저 검사
		final String imageKey = getImageCacheKey(imageHash, imageType);
		final Bitmap bitmap = CacheManager.getBitmapFromMemCache(imageKey);
		final String fPath = path;
		if (bitmap == null)
		{
			new Thread() {
				public void run()
				{
					Bitmap bitmap = downloadImage(fPath);

					if (bitmap != null)
					{
						CacheManager.addBitmapToMemCache(imageKey, bitmap);
					}

				}
			}.start();

			return CacheManager.getBitmapFromMemCache(imageKey);
		}
		else
		{
			return bitmap;
		}
	}

	/**
	 * LruCache를 이용해 메모리에 캐싱할 때의 key값을 만듬\n 키 규칙 : 유저해시+사이즈타입
	 * 
	 * @param userHash
	 * @param size
	 * @return
	 */
	protected String getImageCacheKey(String userHash, int size)
	{
		return userHash + size;
	}

	/**
	 * 이미지 사진을 로드해서 ImageView에 삽입하는 task를 수행
	 */
	class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

		private WeakReference<ImageView>	imageViewReference	= null;
		private boolean						showDialog			= false;

		/**
		 * 비트맵을 삽입할 이미지뷰에 대한 reference를 생성자에서 입력받음
		 * 
		 * @param imageView
		 */
		public LoadImageTask(ImageView imageView, boolean showDialog)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected void onPreExecute()
		{
			if (showDialog == true)
			{
				WaiterView.showDialog(imageViewReference.get().getContext());
			}
		}

		@Override
		protected Bitmap doInBackground(String... params)
		{

			String path = params[0];
			String imageKey = params[1];
			Bitmap bitmap = downloadImage(path);

			if (bitmap != null)
			{
				CacheManager.addBitmapToMemCache(imageKey, bitmap);
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			if (showDialog == true)
			{
				WaiterView.dismissDialog(imageViewReference.get().getContext());
			}

			if (isCancelled())
			{
				bitmap = null;
			}

			if (imageViewReference != null)
			{
				ImageView imageView = imageViewReference.get();
				if (imageView != null)
				{

					if (bitmap != null)
					{
						// 비트맵을 성공적으로 가져왔으면 이미지뷰에 설정
						imageView.setImageBitmap(bitmap);
					}
					else
					{
						// 아니면 걍 기본이미지로
						imageView.setImageResource(kr.go.KNPA.Romeo.R.drawable.user_pic_default);
					}

				}
			}
		}
	}

	/**
	 * 서버로부터 이미지를 다운로드
	 * 
	 * @param download
	 *            path
	 * @return 이미지 비트맵
	 */
	protected Bitmap downloadImage(String path)
	{
		Bitmap bitmap = null;
		URL url = null;
		try
		{
			url = new URL(path);
		}
		catch (MalformedURLException e)
		{
			Log.d(TAG, e.getMessage());
			callBack.onError("잘못된 url로 요청을 했습니다.", e);
			return null;
		}

		URLConnection conn = null;
		try
		{
			conn = url.openConnection();
			conn.connect();
			int nSize = conn.getContentLength();
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
			bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
		}
		catch (IOException e)
		{
			Log.d(TAG, e.getMessage());
			callBack.onError("서버와 통신에 실패했습니다.", e);
			return null;
		}

		return bitmap;
	}
}
