package com.example.woo.myapplication.maps;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;

import com.example.woo.myapplication.R;
import com.example.woo.myapplication.data.MapInfo;
import com.example.woo.myapplication.utils.LocationDistance;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.CameraUpdateParams;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.Projection;
import com.naver.maps.map.overlay.ArrowheadPathOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.util.MarkerIcons;

import java.util.ArrayList;
import java.util.Arrays;

public class  RegisterMapDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapInfo mapInfo;
    private Marker missingPoint;
    private LatLng centerCoord;
    private int scale = -1;
    private PolygonOverlay district;
    private LatLng[] tempPoints = new LatLng[4]; // up, down, left, right

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_map_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment_set_details);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment_set_details, mapFragment).commit();
        }
        mapFragment.getMapAsync(this); // 비동기적 NaverMap 객체 획득

        // 실종지점 및 중심지점 획득
        Intent intent = getIntent();
        LatLng missingCoord = new LatLng(
                intent.getDoubleExtra("missing_lat", 0),
                intent.getDoubleExtra("missing_lng", 0)
        );

        centerCoord = new LatLng(
                intent.getDoubleExtra("center_lat", 0),
                intent.getDoubleExtra("center_lng", 0)
        );


        mapInfo = new MapInfo();
        mapInfo.setMissing_lat(missingCoord.latitude);
        mapInfo.setMissing_lng(missingCoord.longitude);
        mapInfo.setCenter_lat(centerCoord.latitude);
        mapInfo.setCenter_lng(centerCoord.longitude);
        mapInfo.setBearing(intent.getDoubleExtra("bearing", 0));

        // 실종지점 마커 생성
        missingPoint = new Marker();
        missingPoint.setIcon(MarkerIcons.BLACK);
        missingPoint.setIconTintColor(Color.RED);
        missingPoint.setPosition(missingCoord);
        missingPoint.setCaptionText("실종 지점");
        missingPoint.setCaptionColor(Color.RED);

        district = createRedPolygon();
    }


    public void mOnClick(View v){
        Toast.makeText(this, "새로운 맵이 생성되었습니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("mapInfo", mapInfo);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        /* 기본 맵 세팅 */
        // 지도 줌버튼 비활성화
        naverMap.getUiSettings().setZoomControlEnabled(false);

        // 실종지점 표시
        missingPoint.setMap(naverMap);

        final CameraUpdateParams params = new CameraUpdateParams();
        params.rotateBy(mapInfo.getBearing());
        params.scrollTo(centerCoord);
        naverMap.moveCamera(CameraUpdate.withParams(params));

        final ArrowheadPathOverlay upArrow = new ArrowheadPathOverlay();
        final ArrowheadPathOverlay downArrow = new ArrowheadPathOverlay();
        final ArrowheadPathOverlay leftArrow = new ArrowheadPathOverlay();
        final ArrowheadPathOverlay rightArrow = new ArrowheadPathOverlay();

        Spinner scaleSpinner = findViewById(R.id.spinner_scale);
        final ArrayAdapter<CharSequence> scaleAdapter;
        scaleAdapter = ArrayAdapter.createFromResource(this, R.array.scale_type,
                android.R.layout.simple_spinner_item);
        scaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scaleSpinner.setAdapter(scaleAdapter);
        scaleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CharSequence scaleType = scaleAdapter.getItem(position);
                String temp = scaleType.toString();
                temp = temp.substring(0, temp.length()-1);
                scale = Integer.parseInt(temp);
                mapInfo.setUnit_scale(scale);

                ArrayList<String> sizeList = createSpinnerList(scale);
                ArrayAdapter<String> sizeAdapter;
                sizeAdapter = new ArrayAdapter<>(RegisterMapDetailsActivity.this, android.R.layout.simple_spinner_item, sizeList);
                sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // 윗폭
                Spinner upSpinner = findViewById(R.id.spinner_up_height);
                upSpinner.setAdapter(sizeAdapter);
                upSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        CharSequence upScale = sizeAdapter.getItem(position);
                        String temp = upScale.toString();
                        int upHeight = Integer.parseInt(temp);
                        mapInfo.setUp_height(upHeight);

                        double lat_offset = LocationDistance.LatitudeInDifference(upHeight * Math.cos(LocationDistance.deg2rad(mapInfo.getBearing())));
                        double lng_offset = LocationDistance.LongitudeInDifference(centerCoord.latitude, upHeight * Math.sin(LocationDistance.deg2rad(mapInfo.getBearing())));
                        LatLng upPoint =  new LatLng(centerCoord.latitude + lat_offset, centerCoord.longitude+lng_offset);
                        tempPoints[0] = upPoint;
                        upArrow.setMap(null);
                        upArrow.setColor(Color.DKGRAY);
                        upArrow.setCoords(Arrays.asList(
                                new LatLng(centerCoord.latitude, centerCoord.longitude),
                                new LatLng(upPoint.latitude, upPoint.longitude)
                        ));
                        upArrow.setMap(naverMap);

                        if(tempPoints[3] != null){
                            district.setCoords(findVertexByCenter(naverMap.getProjection(), tempPoints));
                            int color = ResourcesCompat.getColor(getResources(), R.color.light_gold, getTheme());
                            district.setColor(ColorUtils.setAlphaComponent(color, 150));
                            district.setMap(naverMap);

                            naverMap.moveCamera(CameraUpdate
                                    .fitBounds(district.getBounds())
                            );
                            naverMap.moveCamera(CameraUpdate
                                    .withParams(new CameraUpdateParams().rotateTo(mapInfo.getBearing()))
                            );


                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                // 아랫폭
                Spinner downSpinner = findViewById(R.id.spinner_down_height);
                downSpinner.setAdapter(sizeAdapter);
                downSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        CharSequence downScale = sizeAdapter.getItem(position);
                        String temp = downScale.toString();
                        int downHeight = Integer.parseInt(temp);
                        mapInfo.setDown_height(downHeight);

                        double lat_offset = LocationDistance.LatitudeInDifference(downHeight * Math.cos(LocationDistance.deg2rad(mapInfo.getBearing())));
                        double lng_offset = LocationDistance.LongitudeInDifference(centerCoord.latitude, downHeight * Math.sin(LocationDistance.deg2rad(mapInfo.getBearing())));
                        LatLng downPoint = new LatLng(centerCoord.latitude - lat_offset, centerCoord.longitude-lng_offset);
                        tempPoints[1] = downPoint;

                        downArrow.setMap(null);
                        downArrow.setColor(Color.DKGRAY);
                        downArrow.setCoords(Arrays.asList(
                                new LatLng(centerCoord.latitude, centerCoord.longitude),
                                new LatLng(downPoint.latitude, downPoint.longitude)
                        ));
                        downArrow.setMap(naverMap);

                        if(tempPoints[3] != null){
                            district.setCoords(findVertexByCenter(naverMap.getProjection(), tempPoints));
                            district.setMap(naverMap);

                            naverMap.moveCamera(CameraUpdate
                                    .fitBounds(district.getBounds())
                            );
                            naverMap.moveCamera(CameraUpdate
                                    .withParams(new CameraUpdateParams().rotateTo(mapInfo.getBearing()))
                            );

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                // 좌측폭
                Spinner leftSpinner = findViewById(R.id.spinner_left_width);
                leftSpinner.setAdapter(sizeAdapter);
                leftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        CharSequence leftScale = sizeAdapter.getItem(position);
                        String temp = leftScale.toString();
                        int leftWidth = Integer.parseInt(temp);
                        mapInfo.setLeft_width(leftWidth);

                        double lat_offset = LocationDistance.LatitudeInDifference(leftWidth * Math.sin(LocationDistance.deg2rad(mapInfo.getBearing())));
                        double lng_offset = LocationDistance.LongitudeInDifference(centerCoord.latitude, leftWidth * Math.cos(LocationDistance.deg2rad(mapInfo.getBearing())));
                        LatLng leftPoint = new LatLng(centerCoord.latitude + lat_offset, centerCoord.longitude - lng_offset);
                        tempPoints[2] = leftPoint;

                        leftArrow.setMap(null);
                        leftArrow.setColor(Color.DKGRAY);
                        leftArrow.setCoords(Arrays.asList(
                                new LatLng(centerCoord.latitude, centerCoord.longitude),
                                new LatLng(leftPoint.latitude, leftPoint.longitude)
                        ));
                        leftArrow.setMap(naverMap);

                        if(tempPoints[3] != null){
                            district.setCoords(findVertexByCenter(naverMap.getProjection(), tempPoints));
                            district.setMap(naverMap);

                            naverMap.moveCamera(CameraUpdate
                                    .fitBounds(district.getBounds())
                            );
                            naverMap.moveCamera(CameraUpdate
                                    .withParams(new CameraUpdateParams().rotateTo(mapInfo.getBearing()))
                            );


                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                // 우측폭
                Spinner rightSpinner = findViewById(R.id.spinner_right_width);
                rightSpinner.setAdapter(sizeAdapter);
                rightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        CharSequence rightScale = sizeAdapter.getItem(position);
                        String temp = rightScale.toString();
                        int rightWidth = Integer.parseInt(temp);
                        mapInfo.setRight_width(rightWidth);

                        double lat_offset = LocationDistance.LatitudeInDifference(rightWidth * Math.sin(LocationDistance.deg2rad(mapInfo.getBearing())));
                        double lng_offset = LocationDistance.LongitudeInDifference(centerCoord.latitude, rightWidth * Math.cos(LocationDistance.deg2rad(mapInfo.getBearing())));
                        LatLng rightPoint = new LatLng(centerCoord.latitude - lat_offset, centerCoord.longitude + lng_offset);
                        tempPoints[3] = rightPoint;

                        rightArrow.setMap(null);
                        rightArrow.setColor(Color.DKGRAY);
                        rightArrow.setCoords(Arrays.asList(
                                new LatLng(centerCoord.latitude, centerCoord.longitude),
                                new LatLng(rightPoint.latitude, rightPoint.longitude)
                        ));
                        rightArrow.setMap(naverMap);

                        if(tempPoints[3] != null){
                            district.setCoords(findVertexByCenter(naverMap.getProjection(), tempPoints));
                            district.setMap(naverMap);

                            naverMap.moveCamera(CameraUpdate
                                    .fitBounds(district.getBounds())
                            );
                            naverMap.moveCamera(CameraUpdate
                                    .withParams(new CameraUpdateParams().rotateTo(mapInfo.getBearing()))
                            );


                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });




    }

    public ArrayList<String> createSpinnerList(int scale){
        int offset = scale * 3;

        ArrayList<String> list = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            int value = (scale * 3 / 2) + offset * i;
            list.add(Integer.toString(value));
        }

        return list;
    }


    private PolygonOverlay createRedPolygon(){
        PolygonOverlay polygon = new PolygonOverlay();

        int gold = ResourcesCompat.getColor(getResources(), R.color.light_gold, getTheme());
        int red = ResourcesCompat.getColor(getResources(), R.color.primary, getTheme());
        polygon.setColor(ColorUtils.setAlphaComponent(gold, 150));
        polygon.setOutlineColor(red);
        polygon.setOutlineWidth(getResources().getDimensionPixelSize(R.dimen.path_overlay_outline_width));

        return polygon;
    }

    private ArrayList<LatLng> findVertexByCenter(Projection projection, LatLng[] centers){
        PointF up = projection.toScreenLocation(centers[0]);
        PointF down = projection.toScreenLocation(centers[1]);
        PointF left = projection.toScreenLocation(centers[2]);
        PointF right = projection.toScreenLocation(centers[3]);

        ArrayList<LatLng> vertex = new ArrayList<>();
        vertex.add(projection.fromScreenLocation(new PointF(left.x, up.y)));    // LU
        vertex.add(projection.fromScreenLocation(new PointF(right.x, up.y)));   // RU
        vertex.add(projection.fromScreenLocation(new PointF(right.x, down.y))); // RD
        vertex.add(projection.fromScreenLocation(new PointF(left.x, down.y)));  // LD

        return vertex;
    }

}
