package com.police170m3.rpi.jjhchatbot.jjhTmapView;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.police170m3.rpi.jjhchatbot.R;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jaehun on 2017-03-05.
 */

public class TmapDrawpassActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{
    private TMapView tMapView;

    private Context mContext;

    private TMapGpsManager gps = null;
    private ProgressDialog dlg;

    ArrayList<String> mArrayMarkerID;
    private static int 	mMarkerID;

    private boolean m_bTrafficeMode = false;

    private LocationManager locationManager;
    private LocationListener locationListener;

    double lat,lon;

    public static TMapPoint point1;

    public static final String EXTRA_QUESTION = "com.police170m3.rpi.jjhchatbot.jjhTmapView.Tmap_Drawpass_Activity.EXTRA_QUESTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmapview);

        mContext = this;

        mArrayMarkerID	= new ArrayList<String>();
        mMarkerID = 0;

        RelativeLayout relativeLayout = new RelativeLayout(this);
        tMapView = new TMapView(this);

        tMapView.setSKPMapApiKey("a5c9afa4-c69a-37ab-8da5-d20a4a3176b5");

        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setCompassMode(true);
        tMapView.setIconVisibility(true);
        tMapView.setZoomLevel(16);
        tMapView.setMapType(TMapView.MAPTYPE_STANDARD);
        tMapView.setCompassMode(true);
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);

        gps = new TMapGpsManager(this);// 생성자 함수 정의
        gps.setMinTime(1000);//현재 위치를 찾을 최소 시간 (밀리초)
        gps.setMinDistance(5);//현재 위치를 갱신할 최소 거리
        gps.setProvider(gps.NETWORK_PROVIDER);//현재위치를 찾을 방법(네트워크)

        gps.OpenGps();//네트워크 위치 탐색 허용

        //setlocationthread(); //쓰레드를 통해 현재 위치 탐색을 하는 함수 실행

        relativeLayout.addView(tMapView);
        setContentView(relativeLayout);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        m_bTrafficeMode = false;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("test", "onLocationChanged, location:" + location);
                lat = location.getLatitude();
                lon = location.getLongitude();

               // getAddressInfo(lat, lon);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    @Override
    public void onLocationChange(Location location){
        TMapPoint tpoint = gps.getLocation();
        tMapView.setCenterPoint(tpoint.getLongitude(),tpoint.getLatitude());
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
        lat = location.getLatitude();
        lon = location.getLongitude();
        TMapMarkerItem tItem = new TMapMarkerItem();
        Log.e("onLocationChange","lat: "+lat+"lon: "+lon);
        point1 = gps.getLocation();
        Log.e("onLocationChange","lat: "+point1);
        tItem.setTMapPoint(tpoint);
        getAddressInfo(lat, lon);
        //프로그래스다이얼로그 추가시켜서 로딩 표시하기
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // 사용자의 위치정보를 불러오는 함수
    public void getAddressInfo(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (addressList != null && addressList.size() > 0) {
                String addressResult = addressList.get(0).toString();

                String labelAddressResult = "";
                for (int i = 0; i <= addressList.get(0).getMaxAddressLineIndex(); i++) {
                    if (i != addressList.get(0).getMaxAddressLineIndex())
                        labelAddressResult += addressList.get(0).getAddressLine(i) + ", ";
                    else
                        labelAddressResult += addressList.get(0).getAddressLine(i);

                }
                //tv.setText(labelAddressResult);
                Log.d("getAddressInfo", " Address: " + labelAddressResult);
                Log.d("getAddressInfo", " Address: " + addressResult);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        /////// 순방향 지오 코딩(주소를 받아서, 좌표를 출력하는 코드)
        String placeName = getIntent().getStringExtra(EXTRA_QUESTION);
        Geocoder coder = new Geocoder(getApplicationContext());
        Log.d("getAddressInfo","placeName: " + placeName);
        try
        {
            List<Address> addrList = coder.getFromLocationName(placeName, 3);
            Iterator<Address> addrs = addrList.iterator();

            String infoAddr = "";
            double lat = 0;
            double lng = 0;

            while (addrs.hasNext())
            {
                Address loc = addrs.next();
                infoAddr += String.format("Coord: %f. %f", loc.getLatitude(), loc.getLongitude());
                lat = loc.getLatitude();
                lng = loc.getLongitude();
            }

            TMapPoint tpointSt = gps.getLocation();
            TMapPoint tpointFin = new TMapPoint(lat, lng);
            final String gURlForm = String.format("geo: %f, %f",lat,lng);

            TMapData tmapdata = new TMapData();

            tmapdata.findPathData(tpointSt, tpointFin, new TMapData.FindPathDataListenerCallback() {
            //차량 검색 기준
                @Override
                public void onFindPathData(TMapPolyLine polyLine) {
                    tMapView.addTMapPath(polyLine);
                }
            });
            Log.d("getAddressInfo", " infoAddr: " + infoAddr);
            Log.d("getAddressInfo", " infoAddr: " + gURlForm);
        }catch (IOException e){

        }

    }
    //end getAddressInfo

}
