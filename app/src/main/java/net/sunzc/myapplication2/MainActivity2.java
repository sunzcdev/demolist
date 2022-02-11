package net.sunzc.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {
	private ActivityResultLauncher<Intent> forResult;
	private static int num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		Intent data = getIntent();
		String num = data.getStringExtra("num");
		TextView tv = findViewById(R.id.textView);
		tv.setText(num);
		forResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
			@Override
			public void onActivityResult(ActivityResult result) {
				if (result.getResultCode() == RESULT_OK) {
					String str = result.getData().getStringExtra("data");
					Log.d("ddd", str);
				}
			}
		});
	}

	public void jump(View view) {
		Intent intent = new Intent(this, MainActivity2.class);
		intent.putExtra("num", "我从" + (num++) + "来");
		forResult.launch(intent);
	}

	public void returnLast(View view) {
		Intent data = new Intent();
		data.putExtra("data", "我从" + (num++) + "来");
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		forResult.unregister();
	}
}