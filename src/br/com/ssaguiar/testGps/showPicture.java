package br.com.ssaguiar.testGps;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class showPicture extends Activity
{
	private ImageView imgStreet;
	private ImageView imgStreetMain;
	private int imgX = 1;
	private String nodevalue;
	private Bundle extras;
	private String picaddress;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.showpicture);

		extras = getIntent().getExtras();
		nodevalue = extras.getString("nodevalue");
		imgStreet = (ImageView) findViewById(R.id.streetPic);

		picaddress = "http://cbk0.google.com/cbk?output=tile&panoid=" + nodevalue + "&zoom=3&x=" + imgX + "&y=1";

		try
		{
			imgStreet.setImageDrawable(LoadImageFromWebOperations(picaddress));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		imgStreet.setOnClickListener(new View.OnClickListener()
		{
			//@Override
			public void onClick(View v)
			{
				if (imgX++ == 6)
				{
					imgX = 1;
				}
				
				try
				{
					picaddress = "http://cbk0.google.com/cbk?output=tile&panoid=" + nodevalue + "&zoom=3&x=" + imgX + "&y=1";
					imgStreet.setImageDrawable(LoadImageFromWebOperations(picaddress));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	public static Drawable LoadImageFromWebOperations(String url)
	{
		try
		{
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "picture.png");
			return d;
		}
		catch (Exception e)
		{
			return null;
		}
	}

}
