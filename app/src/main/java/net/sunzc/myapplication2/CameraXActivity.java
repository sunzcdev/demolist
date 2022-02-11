package net.sunzc.myapplication2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

public class CameraXActivity extends AppCompatActivity {

	private ImageCapture imageCapture;
	private ActivityResultLauncher<String[]> permissionCheck;
	private PreviewView view;
	private VideoCapture videoCapture;
	private ImageAnalysis imageAnalysis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_xactivity);
		view = findViewById(R.id.preview_view);
		permissionCheck = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
			initCameraX();
		});
		permissionCheck.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		permissionCheck.unregister();
	}

	@SuppressLint("RestrictedApi")
	private void initCameraX() {
		ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
		cameraProviderFuture.addListener(() -> {
			try {
				ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
				CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
				Preview preview = new Preview.Builder().build();
				preview.setSurfaceProvider(view.getSurfaceProvider());
				imageAnalysis = new ImageAnalysis.Builder().build();
				imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), new ImageAnalysis.Analyzer() {
					@Override
					public void analyze(@NonNull @NotNull ImageProxy image) {
						Image.Plane[] plane = image.getImage().getPlanes();
//						Log.d("ddd", plane.length + "");
						image.close();
					}
				});
				videoCapture = new VideoCapture.Builder().setTargetRotation(Surface.ROTATION_180).build();
				imageCapture = new ImageCapture.Builder().build();
				UseCaseGroup useCaseGroup = new UseCaseGroup.Builder().addUseCase(preview).addUseCase(imageCapture).addUseCase(videoCapture).build();
				cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup);
			} catch (ExecutionException | InterruptedException e) {
				e.printStackTrace();
			}
		}, ContextCompat.getMainExecutor(CameraXActivity.this));
	}

	public void takePhoto(View view) {
		ImageCapture.OutputFileOptions outputFile = new ImageCapture.OutputFileOptions.Builder(new File(getExternalCacheDir(), System.currentTimeMillis() + ".jpg")).build();
		imageCapture.takePicture(outputFile, Executors.newSingleThreadExecutor(), new ImageCapture.OnImageSavedCallback() {
			@Override
			public void onImageSaved(@NonNull @NotNull ImageCapture.OutputFileResults outputFileResults) {
				Uri uri = outputFileResults.getSavedUri();
				Log.d("ddd", uri.toString());
			}

			@Override
			public void onError(@NonNull @NotNull ImageCaptureException exception) {

			}
		});
	}

	@SuppressLint({"MissingPermission", "RestrictedApi"})
	public void recoder(View View) {
		VideoCapture.OutputFileOptions outputFile = new VideoCapture.OutputFileOptions.Builder(new File(getExternalCacheDir(), System.currentTimeMillis() + ".mp4")).build();
		videoCapture.startRecording(outputFile, Executors.newSingleThreadExecutor(), new VideoCapture.OnVideoSavedCallback() {
			@Override
			public void onVideoSaved(@NonNull @NotNull VideoCapture.OutputFileResults outputFileResults) {
				Log.d("ddd", outputFileResults.getSavedUri().toString());
			}

			@Override
			public void onError(int videoCaptureError, @NonNull @NotNull String message, @Nullable @org.jetbrains.annotations.Nullable Throwable cause) {

			}
		});
	}

	@SuppressLint("RestrictedApi")
	public void stopRecoder(View view) {
		videoCapture.stopRecording();
	}
}