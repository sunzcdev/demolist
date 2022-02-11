package net.sunzc.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	private final ArrayMap<String, Class> demos = new ArrayMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ListView demoListView = findViewById(R.id.demoList);
		demos.put("ResultApi", MainActivity2.class);
		demos.put("CameraX", CameraXActivity.class);
		demoListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demos.keySet().toArray(new String[]{})));
		demoListView.setOnItemClickListener((parent, view, position, id) -> {
			Class clazz = demos.get(demos.keyAt(position));
			startActivity(new Intent(MainActivity.this, clazz));
		});
	}

}