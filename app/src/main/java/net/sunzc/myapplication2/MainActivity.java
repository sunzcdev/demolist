package net.sunzc.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {

	private final ArrayMap<String, Class> demos = new ArrayMap<>();
	private boolean show = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SplashScreen.installSplashScreen(this);
		setContentView(R.layout.activity_main);
		View contentView = findViewById(android.R.id.content);
		contentView.getViewTreeObserver().addOnPreDrawListener(() -> show);
		ListView demoListView = findViewById(R.id.demoList);
		demos.put("ResultApi", MainActivity2.class);
		demos.put("CameraX", CameraXActivity.class);
		demoListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demos.keySet().toArray(new String[]{})));
		demoListView.setOnItemClickListener((parent, view, position, id) -> {
			Class clazz = demos.get(demos.keyAt(position));
			startActivity(new Intent(MainActivity.this, clazz));
		});
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					show = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}