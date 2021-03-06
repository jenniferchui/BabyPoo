package com.example.babypoo;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.MediaStore;

public class Camera extends ActionBarActivity {
	
	static final int REQUEST_TAKE_PHOTO = 1;
	String mCurrentPhotoPath;
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	
	class PoopColor{
		int[] colorRange;
		int count;
		int textID;
		String name;
	}
	
	
	PoopColor white = new PoopColor();
	PoopColor black = new PoopColor();
	PoopColor brown = new PoopColor();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageBitmap = null;

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
		white.colorRange = new int[] {0xf0f0f0};
		white.count = 0;
		white.textID = R.id.TotalWhite;
		white.name = "white";
		
		black.colorRange = new int[] {0x000000, 0x000040};
		black.count = 0;
		black.textID = R.id.TotalBlack;
		black.name = "black";
		
		brown.colorRange = new int[]  {0x400000, 0x800000, 0x804000,0x804040, 0xC08040 };
		brown.count = 0;
		brown.textID = R.id.TotalBrown;
		brown.name = "brown";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_camera,
					container, false);
			return rootView;
		}
	}
	
	public void dispatchTakePictureIntent(View view) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
//	        File photoFile = null;
//	        try {
//	            photoFile = createImageFile();
//	        } catch (IOException ex) {
//	        	System.out.println(ex.toString());
//	            // Error occurred while creating the File
//	        }
//	        // Continue only if the File was successfully created
//	        System.out.println("1");
//	        if (photoFile != null) {
//	        	 System.out.println("2");
//	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//	                    Uri.fromFile(photoFile));
//	            
//	        }
//	        
	        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	    }
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(0));
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    
	    System.out.println(mCurrentPhotoPath);
	    return image;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
	    	handleSmallCameraPhoto(data);
	    }
	}
	
	/**
	 * check for poop colour here 
	 **/
	private void handleSmallCameraPhoto(Intent intent) {
		
		PoopColor[] colors = new PoopColor[] {white, black, brown}; 
		
		Bundle extras = intent.getExtras();
		mImageBitmap = (Bitmap) extras.get("data");
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageBitmap(mImageBitmap);
		mImageView.setVisibility(View.VISIBLE);
		
		TextView textView = (TextView) findViewById(R.id.textView1);

		HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
		for(int x = 0; x < mImageBitmap.getWidth(); x++){
			for(int y = 0; y < mImageBitmap.getHeight(); y++){
				int currentColor = mImageBitmap.getPixel(x, y);
				
				//reduce R, G, B by 64
				int simpleColor = currentColor & 0x00c0c0c0; 
				// Find color
				boolean colorFound = false;
				for(PoopColor c: colors){
					for(int current : c.colorRange){
						// found matching color
						if( current == simpleColor){
							c.count++;
							colorFound = true;
							break;
						}
					}
					if(colorFound)
						break;
				}
				
				if( m.containsKey(simpleColor) ){
					m.put(simpleColor, m.get(simpleColor)+1);
				}
				else{
					m.put(simpleColor, 1);
				}		
			}
		}
		String message = "Height = " + mImageBitmap.getHeight() +"Width = " + mImageBitmap.getWidth() +"Mapsize = " + m.size();
		 textView.setText(message);
		// think about storage... hashmap? tree?
		for(PoopColor c: colors){
			textView = (TextView) findViewById(c.textID);
			message = c.name+" = " + c.count;
			textView.setText(message);
		}
	   

	}

	
	private void showPhotos() {

		if (mCurrentPhotoPath != null) {
			setPic();
			mCurrentPhotoPath = null;
		}

	}
	
	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(bitmap);
		mImageView.setVisibility(View.VISIBLE);
	}


}
