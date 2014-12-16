package edu.depaul.csc472.kuraszj_fieldme;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends ListActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private String sport = "soccer";
    private ArrayList<Park> PARKS = new ArrayList();
    public LocationClient mLocationClient;
    public static Location mLocation;
    public boolean showdirections;
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorListener;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    public void onConnected(Bundle dataBundle) {
        //Device Connected
        setupParse(sport);
        setListAdapter(new ParkAdapter());
    }

    @Override
    public void onDisconnected() {
        //Location disconnected
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, ConnectionResult.RESOLUTION_REQUIRED);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            showDialog(connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLocationClient = new LocationClient(this,this,this);
        showdirections = false;
        adapter = ArrayAdapter.createFromResource(this, R.array.sports_array, R.layout.spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initSensors();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    class ParkAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        @Override
        public int getCount() {
            return PARKS.size() + 1;
        }

        @Override
        public Object getItem(int i) {
            return PARKS.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() { return 2; }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 0;
            else
                return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (convertView == null) {
                if (inflater == null)
                    inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                int type = getItemViewType(position);
                if (type == 0) {
                    row = inflater.inflate(R.layout.spinner_row, parent, false);
                    spinner = (Spinner) row.findViewById(R.id.sport_spinner);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            sport = parent.getItemAtPosition(pos).toString();
                            PARKS.clear();
                            setupParse(sport);
                            ((ParkAdapter) getListAdapter()).notifyDataSetChanged();
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                            // Another interface callback
                        }
                    });
                }
                else
                    row = inflater.inflate(R.layout.park_list, parent, false);
            }

            if (getItemViewType(position) == 0) {
                return spinner;
            }
            else {
                TextView name = (TextView) row.findViewById(R.id.name);
                TextView dist = (TextView) row.findViewById(R.id.distance);
                TextView sport = (TextView) row.findViewById(R.id.parkSport);

                Park park = PARKS.get(position);
                name.setText(park.getName());
                dist.setText(String.format("%.2f", park.getDistToLoc()) + " miles from current location");
                sport.setText("Type: " + park.getSport());
            }
            return row;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String lat = PARKS.get(position-1).getLatitude();
        String lon = PARKS.get(position-1).getLongitude();
        String name = PARKS.get(position-1).getName();
        String myLat = Double.toString(mLocation.getLatitude());
        String myLon = Double.toString(mLocation.getLongitude());
        if (!showdirections) {
            Uri geoLocation = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + name + ")");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?saddr="+ myLat +"," + myLon + "&daddr=" + lat + "," + lon));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    private void setupParse(String s) {
        String sport = s;
        mLocation = mLocationClient.getLastLocation();
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();
            InputStream in_s = getApplicationContext().getResources().openRawResource(R.raw.parks);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            parseXML(parser, sport);
        }catch (XmlPullParserException e) {
            System.out.println("parser exception" + e);
        }catch (IOException e) {
            System.out.print("io exc" + e);
        }
    }

    private void parseXML(XmlPullParser parser, String s) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        Park currentPark = null;
        String str = s.toUpperCase();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            float conversion = 0.000621371f;
            switch(eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if(name.equals("row")) {
                        currentPark = new Park();
                    } else if (currentPark != null) {
                        if (name.equals("park")) {
                            currentPark.setName(parser.nextText());
                        }
                        else if (name.equals("park_number")) {
                            currentPark.setNumber(parser.nextText());
                        }
                        else if (name.equals("facility_name")) {
                            currentPark.setSport(parser.nextText());
                        }
                        else if (name.equals("location")) {
                            currentPark.setLatitude(parser.getAttributeValue(null, "latitude"));
                            currentPark.setLongitude(parser.getAttributeValue(null, "longitude"));
                            currentPark.setLocation(parser.getAttributeValue(null, "latitude"), parser.getAttributeValue(null, "longitude"));
                            currentPark.setDistToLoc((currentPark.getLocation().distanceTo(mLocation))*conversion);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if(name.equalsIgnoreCase("row") && currentPark != null && currentPark.getSport().contains(str)) {
                        PARKS.add(currentPark);
                    }
            }
            eventType = parser.next();
        }
        Collections.sort(PARKS, new ParkComparator());
    }

    private void initSensors() {
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorListener = new SensorEventListener() {
            public void onSensorChanged(SensorEvent event) {
                float x = Math.abs(event.values[0]);
                float y = Math.abs(event.values[1]);
                float z = Math.abs(event.values[2]);
                float sum = x+y+z;
                if(sum > 30) {
                    try {
                        Thread.currentThread().sleep(500);
                    }catch(InterruptedException e){
                        //caught exception
                    }
                    showSelection();
                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }

    public void showSelection() {
        sensorManager.unregisterListener(sensorListener);
        showdirections = !showdirections;
        if(showdirections) {
            Toast.makeText(getApplicationContext(),"Touch will show directions", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"Touch will not show directions", Toast.LENGTH_SHORT).show();
        }
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorListener);
        super.onPause();
    }
}
