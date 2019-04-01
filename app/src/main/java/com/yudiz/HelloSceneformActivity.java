/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yudiz;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

/**
 * This is an example activity that uses the Sceneform UX package to make common AR tasks easier.
 */
public class HelloSceneformActivity extends AppCompatActivity {
  private static final String TAG = HelloSceneformActivity.class.getSimpleName();
  private static final double MIN_OPENGL_VERSION = 3.0;
    private static final String GLTF_ASSET ="https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf";

  private ArFragment arFragment;
  private ModelRenderable andyRenderable,andyRenderable1,test1,test2;
  Button model1,model2;
  String ur1,ur2;

  @Override
  @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
  // CompletableFuture requires api level 24
  // FutureReturnValueIgnored is not valid
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
      StrictMode.setThreadPolicy(policy);
      if (!checkIsSupportedDeviceOrFinish(this)) {
      return;
    }


    setContentView(R.layout.activity_ux);
    model1=findViewById(R.id.model1);
    model2=findViewById(R.id.model2);

      arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
      DownloadFiles("http://192.168.0.114/script/script/google-ar-asset-converter-master/output/andy.sfb");
      DownloadFiles("http://192.168.0.114/script/script/google-ar-asset-converter-master/output/chair.sfb");

      File file = new File("/storage/emulated/0/CNC DATA/andy.sfb");



      Callable callable = new Callable() {
          ///@override
          public InputStream call() throws Exception {
              InputStream inputStream = new FileInputStream(file);
              Log.d("rikesh", "call: "+inputStream);
              return inputStream;
          }
      };
      FutureTask task = new FutureTask<>(callable);
      new Thread(task).start();
      File file1 = new File("/storage/emulated/0/CNC DATA/chair.sfb");



      Callable callable1 = new Callable() {
          ///@override
          public InputStream call() throws Exception {
              InputStream inputStream = new FileInputStream(file1);
              Log.d("rikesh", "call: "+inputStream);
              return inputStream;
          }
      };
      FutureTask task1 = new FutureTask<>(callable1);
      new Thread(task1).start();
    String url1="https://github.com/yudiz-solutions/runtime_ar_android/raw/master/model/model.gltf";
    // When you build a Renderable, Sceneform loads its resources in the background while returning
    // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
      CompletableFuture<Texture> brickFuture = Texture.builder().setSource(this,Uri.parse("https://www.w3schools.com/w3css/img_lights.jpg")).build();
     /* ModelRenderable.builder()
              .setSource(this, RenderableSource.builder().setSource(
                      this,

                     Uri.parse(url1),
                      RenderableSource.SourceType.GLTF2)
                      .setScale(0.5f)  // Scale the original model to 50%.
                      .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                      .build())
              .setRegistryId(url1)
              .build()
              //.thenAccept(renderable -> andyRenderable = renderable)
              .thenAcceptBoth(brickFuture, (renderable, texture) -> {
                  andyRenderable = renderable;
                  ModelRenderable modelRenderable = andyRenderable.makeCopy();
                  //Log.d("rikesh", "onCreate: "+modelRenderable.getSubmeshName(0));
                  //andyRenderable.getMaterial().setTexture("baseColorMap", texture);
              })
              .exceptionally(
                      throwable -> {
                          Toast toast =
                                  Toast.makeText(this, "Unable to load renderable "
                                          , Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                          return null;
                      });*/
      ModelRenderable.builder()
       // .setSource(this, Uri.parse("http://192.168.0.114/script/script/google-ar-asset-converter-master/output/andy.sfb"))
        .setSource(this,callable)
              // .setSource(this,R.raw.andy)
              .build()
        .thenAccept(renderable -> andyRenderable = renderable)
        .exceptionally(
            throwable -> {
              Toast toast =
                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
              toast.setGravity(Gravity.CENTER, 0, 0);
              toast.show();
              return null;
            });
      ModelRenderable.builder()
              // .setSource(this, Uri.parse("http://192.168.0.114/script/script/google-ar-asset-converter-master/output/andy.sfb"))
              .setSource(this,callable1)
              // .setSource(this,R.raw.andy)
              .build()
              .thenAccept(renderable -> andyRenderable1 = renderable)
              .exceptionally(
                      throwable -> {
                          Toast toast =
                                  Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                          toast.setGravity(Gravity.CENTER, 0, 0);
                          toast.show();
                          return null;
                      });
    arFragment.setOnTapArPlaneListener(
        (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
          if (andyRenderable == null) {
            return;
          }

          // Create the Anchor.
          Anchor anchor = hitResult.createAnchor();
          AnchorNode anchorNode = new AnchorNode(anchor);
          anchorNode.setParent(arFragment.getArSceneView().getScene());

          // Create the transformable andy and add it to the anchor.
          TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
          andy.setParent(anchorNode);
          model1.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  test1=andyRenderable.makeCopy();
                  //test1.getMaterial().setTexture("unlit_material",brickFuture);
                  andy.setRenderable(andyRenderable);

                  Log.d("rikesh", "onClick: "+test1.getSubmeshName(0));
              }
          });
          model2.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  test2=andyRenderable1.makeCopy();
                  andy.setRenderable(andyRenderable1);
              }
          });

          andy.select();
        });
  }

  /**
   * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
   * on this device.
   *
   * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
   *
   * <p>Finishes the activity if Sceneform can not run
   */
  public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
    if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
      Log.e(TAG, "Sceneform requires Android N or later");
      Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
      activity.finish();
      return false;
    }
    String openGlVersionString =
        ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
            .getDeviceConfigurationInfo()
            .getGlEsVersion();
    if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
      Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
      Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
          .show();
      activity.finish();
      return false;
    }
    return true;
  }

    @RequiresApi(api = VERSION_CODES.O)
    public void DownloadFiles(String url){

        try {
            URL u = new URL(url);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);
            byte[] buffer = new byte[1024];
            int length;
            String filename = url.substring(url.lastIndexOf('/')+1);
            Log.d("rikesh", "DownloadFiles: "+filename);
            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "CNC DATA/"+filename));
            Log.d("data", "DownloadFiles: "+fos);
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
    }
}
