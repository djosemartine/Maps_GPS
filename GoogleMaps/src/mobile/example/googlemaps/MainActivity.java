package mobile.example.googlemaps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
	private double X = 6.267186400, Y = -75.569153100;
	String Lat, Long;
	private LocationManager locate;
	public GoogleMap map;
	//private SearchFragment fragment;
	private EditText Latitud, Longitud, Nombre;
	private ToggleButton MyLocation;
	private Button BuscarCoord, Buscarnombre;
	Geocoder geo;
	List<android.location.Address> address;
	MarkerOptions marker = new MarkerOptions();
	Marker Markadded;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//fragment = ((SearchFragment) getFragmentManager().findFragmentById(R.id.search_fragment));
		//fragment.onCreateView(getLayoutInflater(), null, null);
		Latitud = (EditText) findViewById(R.id.editLatitud);
		Longitud = (EditText) findViewById(R.id.editLongitud);
		Nombre = (EditText) findViewById(R.id.editNombre);
		MyLocation = (ToggleButton) findViewById(R.id.buttonLocation);
		BuscarCoord = (Button) findViewById(R.id.buttonCoordenadas);
		Buscarnombre = (Button) findViewById(R.id.buttonNombre);
		geo = new Geocoder(getApplicationContext(), Locale.getDefault());
		map.setMyLocationEnabled(true);
		marker.position(new LatLng(0, 0));
		Markadded = map.addMarker(marker);
		Markadded.setVisible(false);
		BuscarCoord.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Lat = Latitud.getText().toString();
				Long = Longitud.getText().toString();				 
				if(!(TextUtils.isEmpty(Lat) || TextUtils.isEmpty(Long))){
					X = Double.parseDouble(Lat);
					Y = Double.parseDouble(Long);
					try {
						address = geo.getFromLocation(X, Y, 1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Markadded.setPosition(new LatLng(X, Y));
					if (address.isEmpty())
						Markadded.setTitle("New Marker");
					else {
						Markadded.setTitle(address.get(0).getFeatureName());
						Markadded.setSnippet(address.get(0).getThoroughfare());
					}
					Nombre.setText(address.get(0).getFeatureName());
					Markadded.setVisible(true);
					CameraPosition campos = new CameraPosition.Builder().target(new LatLng(X, Y))
							.zoom(14).build();
					map.animateCamera(CameraUpdateFactory.newCameraPosition(campos), 750, null);
				}
				else{
					Toast.makeText(getApplicationContext(), "Please, enter a valid lacation",  Toast.LENGTH_SHORT).show();
					
				}
				Markadded.showInfoWindow();
				address.clear();
				MyLocation.setChecked(false);
			}
		});
		
		Buscarnombre.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String LocationName;
				LocationName = Nombre.getText().toString();
				if (!TextUtils.isEmpty(LocationName)){
					try {
						address = geo.getFromLocationName(LocationName, 1);
						if (address.isEmpty())
							Toast.makeText(getApplicationContext(), "No encontrado, Intente con un nombre diferente",  Toast.LENGTH_SHORT).show();
						else{
							Markadded.setVisible(true);
							Markadded.setPosition(new LatLng(address.get(0).getLatitude(),address.get(0).getLongitude()));
							Markadded.setTitle(address.get(0).getFeatureName());
							Markadded.setSnippet(address.get(0).getThoroughfare());
							CameraPosition campos = new CameraPosition.Builder().target(new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude()))
									.zoom(14).build();
							map.animateCamera(CameraUpdateFactory.newCameraPosition(campos), 2000, null);
							Latitud.setText(String.format("%.4f", address.get(0).getLatitude()));
							Longitud.setText(String.format("%.4f", address.get(0).getLongitude()));
							Markadded.showInfoWindow();
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
					Toast.makeText(getApplicationContext(), "Please, enter a valid name",  Toast.LENGTH_SHORT).show();
				address.clear();
				MyLocation.setChecked(false);
			}
		});
		MyLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(MyLocation.isPressed()){
					if(isGPSAvailable()){
					Toast.makeText(getApplicationContext(), "GPS ok", Toast.LENGTH_SHORT).show();
					locate = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
					Location loc = locate.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if (loc!=null){
						try {
							address = geo.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Markadded.setPosition(new LatLng(loc.getLatitude(),loc.getLongitude()));
						if(address.isEmpty()){
							Markadded.setTitle("Mi ubicación");
							Markadded.setSnippet("Actual");
						}
						else{
							Markadded.setTitle(address.get(0).getCountryName());
							Markadded.setSnippet(address.get(0).getFeatureName());
							Latitud.setText(String.format("%.4f", address.get(0).getLatitude()));
							Longitud.setText(String.format("%.4f", address.get(0).getLongitude()));
							Nombre.setText(address.get(0).getCountryName());
						}
						CameraPosition campos = new CameraPosition.Builder().target(new LatLng(loc.getLatitude(), loc.getLongitude()))
								.zoom(14).build();
						map.animateCamera(CameraUpdateFactory.newCameraPosition(campos), 750, null);
						Markadded.setVisible(true);
						Markadded.showInfoWindow();
					}
					else
						Toast.makeText(getApplicationContext(), "GPS is not available", Toast.LENGTH_SHORT).show();
					}
				else{ 
					Toast.makeText(getApplicationContext(), "Please, turn GPS on", Toast.LENGTH_SHORT).show();
					startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
					
				}
				
				//registerlocation();
				}
					
			}
		});
		
		/*map.setOnMarkerClickListener(new OnMarkerClickListener() {	
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), arg0.getTitle(), Toast.LENGTH_SHORT).show();
				try {
					Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
					List<android.location.Address> address;
					address = geo.getFromLocationName("Universidad de Antioquia", 1);
					Toast.makeText(getApplicationContext(), address.get(0).getLatitude()+", "+address.get(0).getLongitude() , Toast.LENGTH_SHORT).show();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return false;
			}
		});*/
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	private boolean isGPSAvailable(){
		locate = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!locate.isProviderEnabled(LocationManager.GPS_PROVIDER))
			return false;
		else return true;
	}
	private void registerlocation(){
		locate = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locate.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, new MyLocationListener(getApplicationContext()));
	}
}
