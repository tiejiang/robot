package com.xiaole.xiaolerobot.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;

import com.xiaole.xiaolerobot.R;
import com.xiaole.xiaolerobot.instancefractory.InstanceHelper;
import com.xiaole.xiaolerobot.util.Constant;


public class MyCameraActivity extends Activity {

	private Camera camera = null;
	private MySurfaceView mySurfaceView = null;

	private PictureCallback pictureCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if (data == null){
				Log.i("MyPicture", "picture taken data: null");
			}else{
				Log.i("MyPicture", "picture taken data: " + data.length);
			}
			Bitmap b = null;
			if(null != data){
				b = BitmapFactory.decodeByteArray(data, 0, data.length);
				camera.stopPreview();
//				isPreviewing = false;

			}
			//save to sdcard
			if(null != b)
			{
//				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 270.0f);
				String imgUrl = FileUtil.saveBitmap(b).getPath();

				Intent mIntent = new Intent();
				mIntent.putExtra("photo_url", imgUrl);
				InstanceHelper.mMyCameraActivity.setResult(Constant.REQUEST_CODE_OK, mIntent);

			}else {
				Intent mIntent = new Intent();
				mIntent.putExtra("photo_url", "nothing");
				InstanceHelper.mMyCameraActivity.setResult(Constant.REQUEST_CODE_NULL, mIntent);
			}
			camera.release();
			camera = null;
			MyCameraActivity.this.finish();

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycamera_layout);

		InstanceHelper.mMyCameraActivity = this;

		if (!checkCameraHardWare(this)){
			Log.d("TIEJIANG", "MyCameraActivity---onCreate"+"camera lost");
//			Toast.makeText(this, "没有发现相机", Toast.LENGTH_SHORT).show();
		}

		// test auto take photos
		refresh();


	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		camera.release();
		camera = null;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (camera == null){
			camera = getCameraInstance();
		}
		//必须放在onResume中，不然会出现Home键之后，再回到该APP，黑屏
		mySurfaceView = new MySurfaceView(getApplicationContext(), camera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mySurfaceView);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.d("TIEJIANG", "MyCameraActivity---onDestory");

	}


	public void refresh(){

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				camera.takePicture(null, null, pictureCallback);
				Log.d("TIEJIANG", "take picture");

			}
		}, 1000);
	}

	/*检测相机是否存在*/
	private boolean checkCameraHardWare(Context context){
		PackageManager packageManager = context.getPackageManager();
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
			return true;
		}
		return false;
	}

	/*得到一相机对象*/
	private Camera getCameraInstance(){
		Camera camera = null;
		try{
			camera = camera.open();
		}catch(Exception e){
			e.printStackTrace();
		}
		return camera;
	}
}