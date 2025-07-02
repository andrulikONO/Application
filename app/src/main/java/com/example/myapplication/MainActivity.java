package com.example.myapplication;

import android.os.Handler;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SculptureRoute";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final double ARRIVAL_DISTANCE_METERS = 50;
    private static final int SCULPTURE_ACTIVITY_REQUEST = 1;


    private MapView mapView;
    private UserLocationLayer userLocationLayer;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private Point[] sculpturePoints = {
            new Point(57.767370, 40.927227), // 1. Снегурочка
            new Point(57.766160, 40.929899), // 2. Ювелир
            new Point(57.761988, 40.928879), // 3. (Скамья)
            new Point(57.761670, 40.928958), // 4. (Любовь)
            new Point(57.761414, 40.929754), // 5. (Ладья)
            new Point(57.763802, 40.924201), // 6. (Водопроводчик)
            new Point(57.767449, 40.925813), // 7. (Кот)
            new Point(57.767868, 40.926894), // 8. (Собака)
            new Point(57.768041, 40.926923), // 9. (Голубь)
            new Point(57.770166, 40.931635)  // 10. (Островский)
    };
    private int currentSculptureIndex = 0;
    private Point lastKnownUserLocation;
    private boolean isNearSculpture = false;

    private final UserLocationObjectListener userLocationObjectListener =
            new UserLocationObjectListener() {
                @Override
                public void onObjectAdded(@NonNull UserLocationView userLocationView) {
                    lastKnownUserLocation = userLocationView.getArrow().getGeometry();
                    checkUserPosition();
                    updateMap();
                }

                @Override
                public void onObjectRemoved(@NonNull UserLocationView userLocationView) {}

                @Override
                public void onObjectUpdated(@NonNull UserLocationView userLocationView,
                                            @NonNull ObjectEvent objectEvent) {
                    lastKnownUserLocation = userLocationView.getArrow().getGeometry();
                    checkUserPosition();
                    updateMap();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            MapKitFactory.setApiKey("78126b39-de8b-48e2-bee9-61283bc89a6d");
            MapKitFactory.initialize(this);
            DirectionsFactory.initialize(this);

            setContentView(R.layout.activity_main);

            mapView = findViewById(R.id.map_view);
            Button nextButton = findViewById(R.id.next_point_button);
            Button skipButton = findViewById(R.id.skip_button);
            mapObjects = mapView.getMap().getMapObjects().addCollection();
            drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED);

            nextButton.setOnClickListener(v -> {
                if (isNearSculpture) {
                    openSculptureActivity();
                } else {
                    Toast.makeText(this, "Подойдите ближе к скульптуре", Toast.LENGTH_SHORT).show();
                }
            });

            skipButton.setOnClickListener(v -> {
                if (currentSculptureIndex >= 0 && currentSculptureIndex < sculpturePoints.length) {
                    // Сначала открываем Activity текущей скульптуры
                    openSculptureActivity();

                    // После возврата из Activity переходим к следующей точке
                    new Handler().postDelayed(() -> {
                        if (currentSculptureIndex < sculpturePoints.length - 1) {
                            currentSculptureIndex++;
                            isNearSculpture = false;
                            updateMap();
                        }
                    }, 100); // Небольшая задержка для гарантии завершения анимации
                }
            });

            if (checkLocationPermission()) {
                initUserLocation();
            }

        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации", e);
            Toast.makeText(this, "Ошибка инициализации карты", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void checkUserPosition() {
        if (lastKnownUserLocation == null || currentSculptureIndex < 0 ||
                currentSculptureIndex >= sculpturePoints.length) {
            return;
        }

        Point sculpturePoint = sculpturePoints[currentSculptureIndex];
        if (sculpturePoint.getLatitude() == 0 && sculpturePoint.getLongitude() == 0) {
            return;
        }

        double distance = calculateDistance(lastKnownUserLocation, sculpturePoint);
        isNearSculpture = distance <= ARRIVAL_DISTANCE_METERS;

        if (isNearSculpture) {
            runOnUiThread(() ->
                    Toast.makeText(this, "Вы у цели! Нажмите 'Следующая точка'", Toast.LENGTH_SHORT).show());
        }
    }
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void initUserLocation() {
        try {
            userLocationLayer = MapKitFactory.getInstance()
                    .createUserLocationLayer(mapView.getMapWindow());
            userLocationLayer.setVisible(true);
            userLocationLayer.setHeadingEnabled(true);
            userLocationLayer.setObjectListener(userLocationObjectListener);

        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации геолокации", e);
            Toast.makeText(this, "Ошибка инициализации геолокации", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMapWithUserPosition() {
        runOnUiThread(() -> {
            try {
                if (lastKnownUserLocation == null) return;

                // Очищаем только пользовательские метки (можно реализовать более точную очистку)
                mapObjects.clear();

                // Добавляем метку пользователя
                PlacemarkMapObject userMarker = mapObjects.addPlacemark(
                        lastKnownUserLocation,
                        ImageProvider.fromResource(this, R.drawable.ic_user_location));
                userMarker.setIconStyle(new IconStyle().setScale(1.2f));

                // Показываем текущую скульптуру
                showCurrentSculpture();

            } catch (Exception e) {
                Log.e(TAG, "Ошибка обновления карты", e);
            }
        });
    }

    private double calculateDistance(Point p1, Point p2) {
        final int R = 6371; // Радиус Земли в км
        double latDistance = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double lonDistance = Math.toRadians(p2.getLongitude() - p1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(p1.getLatitude())) * Math.cos(Math.toRadians(p2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // Расстояние в метрах
    }


    private void openSculptureActivity() {
        // Очищаем карту перед переходом
        mapObjects.clear();
        // Определяем какое Activity открывать
        Class<?> activityClass;
        switch (currentSculptureIndex) {
            case 0:
                activityClass = SnegurDialogActivity.class;
                break;
            case 1:
                activityClass = SnegurAndJeweler.class;
                break;
            case 2:
                activityClass = SnegurAndSkameika.class;
                break;
            case 3:
                activityClass = SnegurAndLove.class;
                break;
            case 4:
                activityClass = SnegurAndLadia.class;
                break;
            case 5:
                activityClass = SnegurAndVodoprovod.class;
                break;
            case 6:
                activityClass = SnegurAndCat.class;
                break;
            case 7:
                activityClass = SnegurAndDog.class;
                break;
            case 8:
                activityClass = SnegurAndGolub.class;
                break;
            case 9:
                activityClass = SnegurAndOstrovski.class;
                break;
            default:
                activityClass = StartActivity.class;
        }
        // Запускаем Activity с ожиданием результата
        Intent intent = new Intent(this, activityClass);
        startActivityForResult(intent, currentSculptureIndex);
    }

    private void updateMap() {
        runOnUiThread(() -> {
            try {
                mapObjects.clear();

                if (lastKnownUserLocation != null) {
                    PlacemarkMapObject userMarker = mapObjects.addPlacemark(
                            lastKnownUserLocation,
                            ImageProvider.fromResource(this, R.drawable.ic_user_location));
                    userMarker.setIconStyle(new IconStyle().setScale(1.2f));
                }

                showCurrentSculpture();

            } catch (Exception e) {
                Log.e(TAG, "Ошибка обновления карты", e);
            }
        });
    }

    private void showCurrentSculpture() {
        try {
            // Проверка индекса скульптуры
            if (currentSculptureIndex < 0 || currentSculptureIndex >= sculpturePoints.length) {
                Log.e(TAG, "Неверный индекс скульптуры");
                return;
            }

            Point sculpturePoint = sculpturePoints[currentSculptureIndex];
            if (sculpturePoint.getLatitude() == 0 && sculpturePoint.getLongitude() == 0) {
                Log.e(TAG, "Координаты скульптуры не заданы");
                return;
            }

            // Добавление метки скульптуры
            PlacemarkMapObject sculptureMarker = mapObjects.addPlacemark(
                    sculpturePoint,
                    ImageProvider.fromResource(this, R.drawable.ic_sculpture_normal));
            sculptureMarker.setIconStyle(new IconStyle().setScale(1.5f));

            // Построение маршрута, если есть позиция пользователя
            if (lastKnownUserLocation != null) {
                buildRoute(lastKnownUserLocation, sculpturePoint);
            }

            // Центрирование карты
            mapView.getMap().move(
                    new CameraPosition(
                            sculpturePoint,
                            14f,
                            0f,
                            30f));

        } catch (Exception e) {
            Log.e(TAG, "Ошибка отображения скульптуры", e);
            Toast.makeText(this, "Ошибка отображения скульптуры", Toast.LENGTH_SHORT).show();
        }
    }

    private void buildRoute(Point from, Point to) {
        if (drivingSession != null) {
            drivingSession.cancel();
        }

        List<RequestPoint> points = new ArrayList<>();
        points.add(new RequestPoint(from, RequestPointType.WAYPOINT, null, null));
        points.add(new RequestPoint(to, RequestPointType.WAYPOINT, null, null));

        com.yandex.mapkit.directions.driving.DrivingOptions options =
                new com.yandex.mapkit.directions.driving.DrivingOptions();
        options.setRoutesCount(1);
        options.setAvoidTolls(true);

        drivingSession = drivingRouter.requestRoutes(
                points,
                options,
                new VehicleOptions(),
                new DrivingSession.DrivingRouteListener() {
                    @Override
                    public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
                        if (!routes.isEmpty()) {
                            runOnUiThread(() -> {
                                PolylineMapObject route = mapObjects.addPolyline(routes.get(0).getGeometry());
                                route.setStrokeColor(Color.BLUE);
                                route.setStrokeWidth(5f);
                            });
                        } else {
                            Log.e(TAG, "Маршруты не найдены");
                        }
                    }

                    @Override
                    public void onDrivingRoutesError(@NonNull Error error) {
                        Log.e(TAG, "Ошибка маршрута: " + error);
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this,
                                        "Ошибка построения маршрута",
                                        Toast.LENGTH_LONG).show());
                    }
                });
    }

    private void showNextSculpture() {
        if (currentSculptureIndex < sculpturePoints.length - 1) {
            currentSculptureIndex++;
            showCurrentSculpture();
            Toast.makeText(this, "Идите к скульптуре " + (currentSculptureIndex + 1),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Поздравляем! Вы прошли весь маршрут!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initUserLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCULPTURE_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("move_to_next", false)) {
                // Переходим к следующей точке только если диалоги завершены
                if (currentSculptureIndex < sculpturePoints.length - 1) {
                    currentSculptureIndex++;
                    isNearSculpture = false;
                    updateMap();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        if (drivingSession != null) {
            drivingSession.cancel();
        }
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // При возврате из Activity переходим к следующей точке
        if (isNearSculpture && currentSculptureIndex < sculpturePoints.length - 1) {
            currentSculptureIndex++;
            isNearSculpture = false;
            updateMap();
        }
    }
}