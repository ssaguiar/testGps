package br.com.ssaguiar.testGps;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener,
		LocationListener, OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener,
		GoogleMap.OnInfoWindowClickListener, OnMarkerDragListener, GpsStatus.NmeaListener
{
	// Update interval in milliseconds for location services
	private static final long UPDATE_INTERVAL = 0;//5000;

	// Fastest update interval in milliseconds for location services
	private static final long FASTEST_INTERVAL = 0;//1000;

	// Google Play diagnostics constant
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Speed threshold for orienting map in direction of motion (m/s) 
	private static final double SPEED_THRESH = 1;

	private static final String TAG = "Mapper";
	private LocationClient locationClient;
	private Location currentLocation;
	private boolean geocodeEnable;
	private double currentLat;
	private double currentLon;
	private GoogleMap map;
	private LatLng map_center;
	private int zoomOffset = 5;
	private int Satellites = 0;
	private float currentZoom;
	private float bearing;
	private float speed;
	private float acc;
	private Circle localCircle;
	private String endereco;
	private TextView statusText;
	private TextView statusSpeed;
	private TextView statusDistance;
	private TextView satellitesView;
	private TextView satellitesFix;
	private TextView tv_data;

	//private ImageView imView;
	private int timetoShowAddress = 0;
	private double lat;
	private double lon;
	private double oldlat;
	private double oldlon;
	private float totaldistance;
	static final int numberOptions = 10;
	String[] optionArray = new String[numberOptions];
	private String title;
	private String snippet;
	private AlertDialog alerta;
	private Context context;
	LocationRequest locationRequest;
	private String nodevalue;
	SharedPreferences prefs;
	SharedPreferences.Editor prefsEditor;
	private boolean isGPSFix;
	private long mLastLocationMillis;
	private Object mLastLocation;
	private int newSatTotal = 0;
	private int newSatUsed = 0;
	private int satellitesTotal;
	private int satellitesUsed;

	private ImageView BtnZoomIn;
	private ImageView BtnZoomOut;
	private ImageView BtnPlaceMark;
	private ImageView BtnCarPlus;
	private ImageView BtnMap3D;
	private ImageView BtnMapType;
	private ImageView BtnGpsStatus;	
	private ImageView GotoMyLocation;

	private String status;

	private Paint mPaint;
	private GeoPoint gP1;
	private GeoPoint gP2;

	// flag for GPS status
	private boolean isGPSEnabled = false;

	private boolean isNetworkEnabled = false;

	// flag for GPS status
	private boolean canGetLocation = false;

	// Declaring a Location Manager
	protected LocationManager locationManager;

	private gpsListener mgpsListener;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		context = getBaseContext();

		isGPSEnabled = false;
		isNetworkEnabled = false;
		canGetLocation = false;

		oldlat = 0;
		oldlon = 0;
		totaldistance = 0;

		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(4);

		BtnZoomIn = (ImageView) findViewById(R.id.btnZoomIn);
		BtnZoomOut = (ImageView) findViewById(R.id.btnZoomOut);
		BtnPlaceMark = (ImageView) findViewById(R.id.btnPlacemark);
		BtnCarPlus = (ImageView) findViewById(R.id.btnCarPlus);
		BtnMap3D = (ImageView) findViewById(R.id.btnMap3D);
		BtnMapType = (ImageView) findViewById(R.id.btnMapType);
		BtnGpsStatus = (ImageView) findViewById(R.id.btnGpsStatus);
		GotoMyLocation = (ImageView) findViewById(R.id.mm);


		statusText = (TextView) findViewById(R.id.tv_location);
		statusSpeed = (TextView) findViewById(R.id.tv_speed);
		statusDistance = (TextView) findViewById(R.id.tv_distance);
		satellitesView = (TextView) findViewById(R.id.tv_satelitesView);
		satellitesFix = (TextView) findViewById(R.id.tv_satelitesFix);
		tv_data = (TextView) findViewById(R.id.tv_data);
		//imView = (ImageView) findViewById(R.id.imView);

		Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/digital7italic.ttf");
		statusSpeed.setTypeface(myTypeface);
		statusDistance.setTypeface(myTypeface);
		satellitesView.setTypeface(myTypeface);
		satellitesFix.setTypeface(myTypeface);
		tv_data.setTypeface(myTypeface);

		if (android.os.Build.VERSION.SDK_INT > 9)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		if (map != null)
		{
			// Set the initial zoom level of the map
			currentZoom = map.getMaxZoomLevel() - zoomOffset;

			// Add a click listener to the map
			map.setOnMapClickListener(this);

			// Add a long-press listener to the map
			map.setOnMapLongClickListener(this);

			// Add Marker click listener to the map
			map.setOnMarkerClickListener(this);

			// Add marker info window click listener
			map.setOnInfoWindowClickListener(this);

			map.setOnMarkerDragListener(new OnMarkerDragListener()
			{
				@Override
				public void onMarkerDragStart(Marker marker)
				{
					System.out.println("Marker " + marker.getId() + " DragStart");
				}

				@Override
				public void onMarkerDragEnd(Marker marker)
				{
					System.out.println("Marker " + marker.getId() + " DragEnd");
				}

				@Override
				public void onMarkerDrag(Marker marker)
				{
					// Getting the current position of the marker
					LatLng pos = marker.getPosition();

					System.out.println("FUNCIONA, PORRA!!!");

					// Updating the infowindow contents with the new marker coordinates
					marker.setSnippet(pos.latitude + "," + pos.longitude);

					// Updating the infowindow for the user
					marker.showInfoWindow();
				}
			});
		}
		else
		{
			Toast.makeText(this, getString(R.string.nomap_error), Toast.LENGTH_LONG).show();
		}

		/*
		 * Create new location client. The first 'this' in args is the present
		 * context; the next two 'this' args indicate that this class will
		 * handle callbacks associated with connection and connection errors,
		 * respectively (see the onConnected, onDisconnected, and
		 * onConnectionError callbacks below). You cannot use the location
		 * client until the onConnected callback fires, indicating a valid
		 * connection. At that point you can access location services such as
		 * present position and location updates.
		 */

		locationClient = new LocationClient(this, this, this);

		// Create the LocationRequest object
		locationRequest = LocationRequest.create();
		// Set request for high accuracy
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set update interval
		locationRequest.setInterval(UPDATE_INTERVAL);
		// Set fastest update interval that we can accept
		locationRequest.setFastestInterval(FASTEST_INTERVAL);

		// Get a shared preferences
		prefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		// Get a SharedPreferences editor
		prefsEditor = prefs.edit();

		// Keep screen on while this map location tracking activity is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		map.setPadding(0, 180, 0, 6);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.getUiSettings().setMyLocationButtonEnabled(false);

		BtnZoomIn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				map.animateCamera(CameraUpdateFactory.zoomIn());
			}
		});

		GotoMyLocation.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				 map.animateCamera(CameraUpdateFactory.newLatLngZoom(new    
						 LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 21));
				 
				System.out.println("MyLocationListener clicked");
			}
		});
		
		BtnZoomOut.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				map.animateCamera(CameraUpdateFactory.zoomOut());
			}
		});

		BtnPlaceMark.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				geocodeEnable = !geocodeEnable;

				if (geocodeEnable == true)
				{
					Toast.makeText(context, "Geocode enabled", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(context, "Geocode disabled", Toast.LENGTH_SHORT).show();
					endereco = "";
				}
			}
		});

		BtnCarPlus.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				map.setTrafficEnabled(!map.isTrafficEnabled());
				if (!map.isTrafficEnabled())
				{
					BtnCarPlus.setBackgroundResource(R.drawable.carplus);
				}
				else
				{
					BtnCarPlus.setBackgroundResource(R.drawable.carminus);
				}

			}
		});

		BtnMap3D.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				map.setBuildingsEnabled(!map.isBuildingsEnabled());

				// Change camera tilt to view from angle if 3D
				if (map.isBuildingsEnabled())
				{
					changeCamera(map, map.getCameraPosition().target, currentZoom, map.getCameraPosition().bearing, 45,
							true);
				}
				else
				{
					changeCamera(map, map.getCameraPosition().target, currentZoom, map.getCameraPosition().bearing, 0,
							true);
				}
			}
		});

		BtnMapType.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				int mt = map.getMapType();
				if (mt == GoogleMap.MAP_TYPE_NORMAL)
				{
					map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				}
				else
				{
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				}
			}
		});

		map.setInfoWindowAdapter(new InfoWindowAdapter()
		{
			public View getInfoWindow(Marker arg0)
			{
				View v = getLayoutInflater().inflate(R.layout.windowlayout, null);
				v.setBackgroundResource(R.drawable.custom_info_bubble);
				LatLng latLng = arg0.getPosition();
				TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
				TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
				TextView tvTitle = (TextView) v.findViewById(R.id.tv_Title);
				TextView tvSpinner = (TextView) v.findViewById(R.id.tv_spinner);
				tvLat.setText("Latitude:" + latLng.latitude);
				tvLng.setText("Longitude:" + latLng.longitude);
				tvTitle.setText(title);
				tvSpinner.setText(snippet);

				return v;
			}

			public View getInfoContents(Marker arg0)
			{
				return null;
			}
		});

		BtnGpsStatus.setBackgroundResource(R.drawable.gpsdisconnected);

		endereco = "";
		geocodeEnable = false;

		statusText.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//Toast.makeText(MainActivity.this, "Image clicked", Toast.LENGTH_LONG).show();
				Intent myIntent = new Intent(context, showPicture.class);
				myIntent.putExtra("nodevalue", nodevalue);
				startActivity(myIntent);
			}
		});

	}

	// Following two methods display and handle the top bar options menu for maps

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if present.
		getMenuInflater().inflate(R.menu.mapme_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		if (map == null)
		{
			Toast.makeText(this, getString(R.string.nomap_error), Toast.LENGTH_LONG).show();
			return false;
		}

		// Handle item selection
		switch (item.getItemId())
		{
		// Toggle traffic overlay
			case R.id.traffic_mapme:
				map.setTrafficEnabled(!map.isTrafficEnabled());
				return true;

				// Toggle satellite overlay
			case R.id.satellite_mapme:
				int mt = map.getMapType();
				if (mt == GoogleMap.MAP_TYPE_NORMAL)
				{
					map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				}
				else
				{
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				}
				return true;

				// Toggle 3D building display
			case R.id.building_mapme:
				map.setBuildingsEnabled(!map.isBuildingsEnabled());

				// Change camera tilt to view from angle if 3D
				if (map.isBuildingsEnabled())
				{
					changeCamera(map, map.getCameraPosition().target, currentZoom, map.getCameraPosition().bearing, 45,
							true);
				}
				else
				{
					changeCamera(map, map.getCameraPosition().target, currentZoom, map.getCameraPosition().bearing, 0,
							true);
				}
				return true;

				// Toggle whether indoor maps displayed
			case R.id.indoor_mapme:
				map.setIndoorEnabled(!map.isIndoorEnabled());
				return true;

				// Toggle tracking enabled
			case R.id.track_mapme:
				if (locationClient != null)
				{
					if (locationClient.isConnected())
					{
						stopTracking();
					}
					else
					{
						startTracking();
					}
				}
				return true;

				// geocode enabled
			case R.id.action_geocode:
				geocodeEnable = !geocodeEnable;

				if (geocodeEnable == true)
				{
					Toast.makeText(this, "Geocode enabled", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(this, "Geocode disabled", Toast.LENGTH_SHORT).show();
					endereco = "";
				}

				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause()
	{
		// Store the current map zoom level
		if (map != null)
		{
			currentZoom = map.getCameraPosition().zoom;
			prefsEditor.putFloat("KEY_ZOOM", currentZoom);
			prefsEditor.putFloat("TotalDistance", totaldistance);
			prefsEditor.commit();
		}
		super.onPause();
		Log.i(TAG, "onPause: Zoom=" + currentZoom);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Restore previous zoom level (default to max zoom level if
		// no prefs stored)

		if (prefs.contains("KEY_ZOOM") && map != null)
		{
			currentZoom = prefs.getFloat("KEY_ZOOM", map.getMaxZoomLevel());
		}

		if (prefs.contains("TotalDistance") && map != null)
		{
			prefs.getFloat("TotalDistance", totaldistance);
		}

		Log.i(TAG, "onResume: Zoom=" + currentZoom);

		// Keep screen on while this map location tracking activity is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/*
	 * The following two lifecycle methods conserve resources by ensuring that
	 * location services are connected when the map is visible and disconnected
	 * when it is not.
	 */

	// Called by system when Activity becomes visible, so connect location client.

	@Override
	protected void onStart()
	{
		super.onStart();

		try
		{
			locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			mgpsListener = new gpsListener();
			locationManager.addGpsStatusListener(mgpsListener);
			locationManager.addNmeaListener(this);

			if (!isGPSEnabled)// || !isNetworkEnabled)
			{

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

				// Setting Dialog Title
				alertDialog.setTitle("GPS is settings");

				// Setting Dialog Message
				alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

				// On pressing Settings button
				alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
						finish();
					}
				});

				// on pressing cancel button
				alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				});

				// Showing Alert Message
				alertDialog.show();

			}
			else
			{
				locationClient.connect();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			finish();
		}

	}

	// Called by system when Activity is no longer visible, so disconnect location
	// client, which invalidates it.

	@Override
	protected void onStop()
	{
		// If the client is connected, remove location updates and disconnect
		if (locationClient.isConnected())
		{
			locationClient.removeLocationUpdates(this);
		}
		locationClient.disconnect();

		// Turn off the screen-always-on request
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		super.onStop();
	}

	// The following three callbacks indicate connections, disconnections, and 
	// connection errors, respectively.

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request current location or
	 * begin periodic location updates.
	 */

	@Override
	public void onConnected(Bundle dataBundle)
	{
		// Indicate that a connection has been established
		try
		{
			Toast.makeText(this, getString(R.string.connected_toast), Toast.LENGTH_SHORT).show();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		try
		{
			currentLocation = locationClient.getLastLocation();
			currentLat = currentLocation.getLatitude();
			currentLon = currentLocation.getLongitude();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			currentLat = 0.00;
			currentLon = 0.00;
			//currentZoom = map.getMaxZoomLevel() - zoomOffset;
		}

		// Center map on current location
		map_center = new LatLng(currentLat, currentLon);

		if (map != null)
		{
			initializeMap();
		}
		else
		{
			try
			{
				Toast.makeText(this, getString(R.string.nomap_error), Toast.LENGTH_LONG).show();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		// Start periodic updates.  This version of requestLocationUpdates is 
		// suitable for foreground activities when connected to a LocationClient. 
		// The second argument is the LocationListener, which is "this" since the 
		// present class implements the LocationListener interface.

		locationClient.requestLocationUpdates(locationRequest, this);
	}

	// Called by Location Services if the connection to location client fails

	@Override
	public void onDisconnected()
	{
		try
		{
			Toast.makeText(this, getString(R.string.disconnected_toast), Toast.LENGTH_SHORT).show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Called by Location Services if the attempt to connect to
	// Location Services fails.

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{
		System.out.println("connection failled");
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve the error.
		 */
		if (connectionResult.hasResolution())
		{
			try
			{
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				// Thrown if Google Play services canceled the original PendingIntent
			}
			catch (IntentSender.SendIntentException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			// If no resolution is available, display a dialog with the error.
			showErrorDialog(connectionResult.getErrorCode());
		}
	}

	public void showErrorDialog(int errorCode)
	{
		Log.e(TAG, "Error_Code =" + errorCode);
		// Create an error dialog display here
	}

	// Method to initialize the map.  Check that map != null before calling.
	private void initializeMap()
	{
		map.setMyLocationEnabled(true);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(map_center, currentZoom));
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setBuildingsEnabled(false);
		map.setIndoorEnabled(false);
		map.setTrafficEnabled(false);
		map.getUiSettings().setRotateGesturesEnabled(false);
	}

	// Starts location tracking
	private void startTracking()
	{
		locationClient.connect();
		Toast.makeText(this, "Location tracking started", Toast.LENGTH_SHORT).show();
	}

	// Stops location tracking
	private void stopTracking()
	{
		if (locationClient.isConnected())
		{
			locationClient.removeLocationUpdates(this);
		}
		locationClient.disconnect();
		Toast.makeText(this, "Location tracking halted", Toast.LENGTH_SHORT).show();
	}

	/*
	 * Method to add map marker at give latitude and longitude. The third arg is
	 * a float variable defining color for the marker. Pre-defined marker colors
	 * may be found at
	 * http://developer.android.com/reference/com/google/android/gms/maps/model
	 * /BitmapDescriptorFactory.html and should be specified in the format
	 * BitmapDescriptorFactory.HUE_RED, which is the default color, but various
	 * other ones are defined there such as HUE_ORANGE, HUE_BLUE, HUE_GREEN, ...
	 * The arguments title and snippet are for the window that will open if one
	 * clicks on the marker.
	 */
	private void addMapMarker(double lat, double lon, float markerColor, String title, String snippet)
	{
		if (map != null)
		{
			Marker marker = map.addMarker(new MarkerOptions().title(title).snippet(snippet)
					.position(new LatLng(lat, lon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.pushpin)));

			marker.setAlpha(0.6f);
			marker.setDraggable(true);
			marker.showInfoWindow();
		}
		else
		{
			Toast.makeText(this, getString(R.string.nomap_error), Toast.LENGTH_LONG).show();
		}
	}

	// Decimal output formatting class that uses Java DecimalFormat. See
	// http://developer.android.com/reference/java/text/DecimalFormat.html.
	// The string "formatPattern" specifies the output formatting pattern for
	// the float or double. For example, 35.8577877288 will be returned 
	// as the string "35.85779" if formatPattern = "0.00000", and as
	// the string "3.586E01" if formatPattern = "0.000E00".
	public String formatDecimal(double number, String formatPattern)
	{
		DecimalFormat df = new DecimalFormat(formatPattern);
		return df.format(number);
	}

	/*
	 * Method to change properties of camera. If your GoogleMaps instance is
	 * called map, you can use map.getCameraPosition().target
	 * map.getCameraPosition().zoom map.getCameraPosition().bearing
	 * map.getCameraPosition().tilt to get the current values of the camera
	 * position (target, which is a LatLng), zoom, bearing, and tilt,
	 * respectively. This permits changing only a subset of the camera
	 * properties by passing the current values for all arguments you do not
	 * wish to change.
	 */
	private void changeCamera(GoogleMap map, LatLng center, float zoom, float bearing, float tilt, boolean animate)
	{

		CameraPosition cameraPosition = new CameraPosition.Builder().target(center) // Sets the center of the map
				.zoom(zoom) // Sets the zoom
				.bearing(bearing) // Sets the bearing of the camera 
				.tilt(tilt) // Sets the tilt of the camera relative to nadir
				.build(); // Creates a CameraPosition from the builder

		if (animate)
		{
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
		else
		{
			map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	// Following callback associated with implementing LocationListener.
	// It fires when a location change is detected, passing in the new
	// location as the variable "newLocation".

	@Override
	public void onLocationChanged(Location newLocation)
	{
		newSatTotal = 0;
		newSatUsed = 0;

		if (newLocation == null) return;
		mLastLocationMillis = SystemClock.elapsedRealtime();
		mLastLocation = newLocation;

		bearing = newLocation.getBearing();
		speed = newLocation.getSpeed();
		acc = newLocation.getAccuracy();
		speed = ((speed * 3600) / 1000);

		String speed2 = formatDecimal(speed, "000.0");
		statusSpeed.setText(speed2);

		float zoomLevelMax = map.getMaxZoomLevel();

		//System.out.println("zoomLevelMax = " + zoomLevelMax);

		if (speed > 0.0)
		{
			int zoomLevel = 0;

			if (speed < 10.1) zoomLevel = 21;
			if ((speed > 10.0) && (speed < 30.1)) zoomLevel = 20;
			if ((speed > 30.1) && (speed < 50.1)) zoomLevel = 19;
			if ((speed > 50.1) && (speed < 70.1)) zoomLevel = 18;
			if ((speed > 70.1) && (speed < 90.1)) zoomLevel = 17;
			if ((speed > 90.1) && (speed < 120.1)) zoomLevel = 16;
			if (speed > 120.1) zoomLevel = 16;

			map.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
		}
		else
		{
			map.animateCamera(CameraUpdateFactory.zoomTo(21));
		}

		// number of satellites, not useful, but cool

		GpsStatus status = locationManager.getGpsStatus(null);
		for (GpsSatellite sat : status.getSatellites())
		{
			newSatTotal++;
			if (sat.usedInFix())
			{
				newSatUsed++;
			}
		}

		// Get latitude and longitude of updated location	
		double lat = newLocation.getLatitude();
		double lon = newLocation.getLongitude();

		LatLng currentLatLng = new LatLng(lat, lon);

		//double height = newLocation.getAltitude();

		long timestamp = newLocation.getTime();
		String data;
		Date date = new Date(timestamp);
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
		sdf.setCalendar(cal);
		cal.setTime(date);
		data = sdf.format(date).trim();
		Bundle locationExtras = newLocation.getExtras();
		int numberSatellites = -1;

		double distance2 = 0;

		if (oldlat == 0) oldlat = lat;
		if (oldlon == 0) oldlon = lon;

		Location loc1 = new Location("");
		loc1.setLatitude(oldlat);
		loc1.setLongitude(oldlon);

		Location loc2 = new Location("");
		loc2.setLatitude(lat);
		loc2.setLongitude(lon);

		distance2 = loc1.distanceTo(loc2);

		totaldistance += distance2;

		String distance = formatDecimal((totaldistance / 1000), "000,000.0");
		statusDistance.setText(distance);

		if (locationExtras != null)
		{
			//Log.i(TAG, "Extras:" + locationExtras.toString());
			if (locationExtras.containsKey("satellites"))
			{
				numberSatellites = locationExtras.getInt("satellites");
				System.out.println("Número de satélites: " + numberSatellites);
			}
		}

		if (geocodeEnable)
		{
			updateGeoCode(lat, lon);

			String streetlocation = "http://cbk0.google.com/cbk?output=xml&hl=x-local&ll=" + lat + "," + lon
					+ "&it=all";
			//System.out.println(streetlocation);

			XMLParser parser = new XMLParser();

			String xml = parser.getXmlFromUrl(streetlocation); // getting XML
			Document doc = parser.getDomElement(xml); // getting DOM element			
			NodeList nl = doc.getElementsByTagName("panorama");
			Node node = nl.item(0);
			Node temp = node.getChildNodes().item(0);
			//System.out.print("temp.getNodeName = " + temp.getNodeName() + "  ->  ");

			NamedNodeMap attributes = temp.getAttributes();
			Node namedItem = attributes.getNamedItem("pano_id");
			//System.out.println("\t\t"+namedItem!=null?namedItem.getNodeValue():"");

			nodevalue = namedItem.getNodeValue();
			//System.out.println(nodevalue);

			String picaddress = "http://cbk0.google.com/cbk?output=tile&panoid=" + nodevalue + "&zoom=3&x=6&y=1";
			//System.out.println("Picaddress = " + picaddress);

			//imView.setImageDrawable(LoadImageFromWebOperations(picaddress));
		}

		String latitudes = formatDecimal(lat, "0.00000");
		String longitudes = formatDecimal(lon, "0.00000");
		String satsview = formatDecimal(newSatTotal, "00");
		String satsused = formatDecimal(newSatUsed, "00");
		String bearings = formatDecimal(bearing, "000.0");
		String precisaos = formatDecimal(acc, "00.0");

		String statusTextT = endereco;

		satellitesView.setText("lat: " + latitudes + " Lon: " + longitudes);
		satellitesFix.setText("Sats: " + satsview + "/" + satsused + " Azim: " + bearings + " " + precisaos + "m");
		tv_data.setText(data);
		statusText.setText(statusTextT);

		if (map != null)
		{
			if (speed < SPEED_THRESH)
			{
				map.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
			}
			else
			{
				changeCamera(map, currentLatLng, map.getCameraPosition().zoom, bearing, map.getCameraPosition().tilt,
						true);
			}

		}
		else
		{
			Toast.makeText(this, getString(R.string.nomap_error), Toast.LENGTH_LONG).show();
		}
	}

	public void updateGeoCode(double lat, double lon)
	{
		Geocoder gc = new Geocoder(this, Locale.getDefault());
		List addresses = null;

		try
		{
			addresses = gc.getFromLocation(lat, lon, 1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (addresses != null && addresses.size() > 0)
		{
			endereco = "\n" + ((Address) addresses.get(0)).getAddressLine(0) + ", "
					+ ((Address) addresses.get(0)).getSubAdminArea() + " Cep: "
					+ ((Address) addresses.get(0)).getPostalCode();
		}
	}

	private String reverseGeocodeLocation(double latitude, double longitude)
	{
		boolean omitCountry = true;
		String returnString = "";
		Geocoder gcoder = new Geocoder(this);

		try
		{
			List<Address> results = null;

			if (Geocoder.isPresent())
			{
				results = gcoder.getFromLocation(latitude, longitude, numberOptions);
			}
			else
			{
				//Log.i(TAG, "No geocoder accessible on this device");
				return null;
			}

			Iterator<Address> locations = results.iterator();
			String raw = "\nRaw String:\n";
			String country;
			int opCount = 0;

			while (locations.hasNext())
			{
				Address location = locations.next();
				if (opCount == 0 && location != null)
				{
					lat = location.getLatitude();
					lon = location.getLongitude();
				}

				country = location.getCountryName();

				if (country == null)
				{
					country = "";
				}
				else
				{
					country = ", " + country;
				}

				raw += location + "\n";
				optionArray[opCount] = location.getAddressLine(0) + ", " + location.getAddressLine(1) + country + "\n";

				if (opCount == 0)
				{
					if (omitCountry)
					{
						returnString = location.getAddressLine(0) + ", " + location.getAddressLine(1) + "\n";
					}
					else
					{
						returnString = optionArray[opCount];
					}
				}

				opCount++;
			}

			//Log.i(TAG, raw);
			//Log.i(TAG, "\nOptions:\n");

			for (int i = 0; i < opCount; i++)
			{
				//Log.i(TAG, "(" + (i + 1) + ") " + optionArray[i]);
			}

			//Log.i(TAG, "lat=" + lat + " lon=" + lon);

		}
		catch (IOException e)
		{
			Log.e(TAG, "I/O Failure", e);
		}

		// Return the first location entry in the list.  A more sophisticated implementation 
		// would present all location entries in optionArray to the user for choice when more 
		// than one is returned by the geodecoder.

		return returnString;

	}

	// Callback that fires when map is tapped, passing in the latitude
	// and longitude coordinates of the tap (actually the point on the ground 
	// projected from the screen tap).  This will be invoked only if no overlays 
	// on the map intercept the click first.  Here we will just issue a Toast
	// displaying the map coordinates that were tapped.  See the onMapLongClick
	// handler for an example of additional actions that could be taken.

	@Override
	public void onMapClick(LatLng latlng)
	{
		String f = "0.000000";
		double lat = latlng.latitude;
		double lon = latlng.longitude;
		Toast.makeText(this, "Latitude=" + formatDecimal(lat, f) + " Longitude=" + formatDecimal(lon, f),
				Toast.LENGTH_LONG).show();
	}

	// This callback fires for long clicks on the map, passing in the LatLng coordinates

	@Override
	public void onMapLongClick(LatLng latlng)
	{
		double lat = latlng.latitude;
		double lon = latlng.longitude;
		title = reverseGeocodeLocation(latlng.latitude, latlng.longitude);
		snippet = "Tap marker to delete;\nTap window for Street View";
		addMapMarker(lat, lon, BitmapDescriptorFactory.HUE_BLUE, title, snippet);
		localCircle = addCircle(lat, lon, acc, "#00000000", "#40ff9900");
	}

	/*
	 * Add a circle at (lat, lon) with specified radius. Stroke and fill colors
	 * are specified as strings. Valid strings are those valid for the argument
	 * of Color.parseColor(string): for example, "#RRGGBB", "#AARRGGBB", "red",
	 * "blue", ...
	 */

	private Circle addCircle(double lat, double lon, float radius, String strokeColor, String fillColor)
	{
		if (map == null)
		{
			Toast.makeText(this, getString(R.string.nomap_error), Toast.LENGTH_LONG).show();
			return null;
		}

		CircleOptions circleOptions = new CircleOptions().center(new LatLng(lat, lon)).radius(radius).strokeWidth(1)
				.fillColor(Color.parseColor(fillColor)).strokeColor(Color.parseColor(strokeColor));

		return map.addCircle(circleOptions);

	}

	// Process clicks on markers

	@Override
	public boolean onMarkerClick(Marker marker)
	{
		// Remove the marker and its info window and circle if marker clicked
		marker.remove();
		localCircle.remove();
		// Return true to prevent default behavior of opening info window
		return true;
	}

	// Process clicks on the marker info window

	@Override
	public void onInfoWindowClick(Marker marker)
	{
		double lat = marker.getPosition().latitude;
		double lon = marker.getPosition().longitude;

		// Launch a StreetView on current location
		showStreetView(lat, lon);

		// Remove marker and circle
		marker.remove();
		localCircle.remove();
	}

	public void onProviderDisabled(String provider)
	{
		//Log.v("GPS", "Disabled");
		Toast.makeText(this, "GPS Disabled", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	public void onProviderEnabled(String provider)
	{
		//Log.v("GPS", "Enabled");
		Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		switch (status)
		{
			case LocationProvider.OUT_OF_SERVICE:
				//Log.v("GPS", "Status Changed: Out of Service");
				Toast.makeText(this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				//Log.v("GPS", "Status Changed: Temporarily Unavailable");
				Toast.makeText(this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
				break;
			case LocationProvider.AVAILABLE:
				//Log.v("GPS", "Status Changed: Available");
				Toast.makeText(this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
				break;
		}
	}

	/*
	 * Open a Street View. The user will have the choice of getting the Street
	 * View in a browser, or with the StreetView app if it is installed. If no
	 * Street View exists for a given location, this will present a blank page.
	 */

	private void showStreetView(double lat, double lon)
	{
		String uriString = "google.streetview:cbll=" + lat + "," + lon;
		Intent streetView = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uriString));
		startActivity(streetView);
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

	void testgps()
	{
		// Get Location Manager and check for GPS & Network location services
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			// Build the alert dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Location Services Not Active");
			builder.setMessage("Please enable Location Services and GPS");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialogInterface, int i)
				{
					// Show location settings when the user acknowledges the alert dialog
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			});
			Dialog alertDialog = builder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();
		}
	}

	private class gpsListener implements android.location.GpsStatus.Listener
	{
		public void onGpsStatusChanged(int event)
		{
			GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			switch (event)
			{
				case GpsStatus.GPS_EVENT_STARTED:
					status = "GPS INICIADO";
					Log.i(TAG, "onGpsStatusChanged(): GPS INICIADO");
					BtnGpsStatus.setBackgroundResource(R.drawable.gpssearching);
					break;

				case GpsStatus.GPS_EVENT_FIRST_FIX:
					Log.i(TAG, "onGpsStatusChanged(): " + gpsStatus.getTimeToFirstFix());
					BtnGpsStatus.setBackgroundResource(R.drawable.gpsreceiving);
					isGPSFix = true;
					break;

				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

					if (mLastLocation != null) isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;

					if (isGPSFix)
					{ // A fix has been acquired.

						@SuppressWarnings("rawtypes")
						Iterable satellites = gpsStatus.getSatellites();
						@SuppressWarnings("rawtypes")
						Iterator satI = satellites.iterator();
						int maxsatellites = gpsStatus.getMaxSatellites();
						int count = 0;

						while (satI.hasNext())
						{
							GpsSatellite satellite = (GpsSatellite) satI.next();
							if (satellite.usedInFix())
							{
								count++;
							}

/*							Log.d(TAG,
									"onGpsStatusChanged(): " + " Prn: " + satellite.getPrn() + "," + " UsedInFix: "
											+ satellite.usedInFix() + "," + " SNR: " + satellite.getSnr() + ","
											+ " Azimuth: " + satellite.getAzimuth() + "," + " Elevation: "
											+ satellite.getElevation() + "," + " Almanac: " + satellite.hasAlmanac()
											+ "," + " Ephemeris: " + satellite.hasEphemeris() + ","
											+ " MaxSatellites: " + maxsatellites + " Satellites in fix: "
											+ String.valueOf(count));*/
							
						}
						status = String.valueOf(count);
					}
					else
					{ // The fix has been lost.
						// Do something.
					}

					break;

				case GpsStatus.GPS_EVENT_STOPPED:
					Log.i(TAG, "onGpsStatusChanged(): GPS PARADO");
					status = "GPS PARADO";
					BtnGpsStatus.setBackgroundResource(R.drawable.gpsdisconnected);
					break;
			}
		}
	};

	@Override
	public void onBackPressed()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_LIGHT);

		builder.setTitle("Sair do sistema?");
		builder.setMessage("Você tem certeza\nque quer sair do sistema?");

		builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
				finish();
			}
		});

		builder.setNegativeButton("Não", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
			}
		});

		alerta = builder.create();
		alerta.show();

	}

	@Override
	public void onMarkerDrag(Marker arg0)
	{
		System.out.println("On DRAG!");
	}

	@Override
	public void onMarkerDragEnd(Marker arg0)
	{
		System.out.println("On DRAG END!");
	}

	@Override
	public void onMarkerDragStart(Marker arg0)
	{
		System.out.println("On DRAG START!");
	}

	public void onNmeaReceived(long timestamp, String nmea)
	{
		//System.out.println("NMEA Sentence: " + nmea);
	}
}
