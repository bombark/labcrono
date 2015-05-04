package ufpr.labcrono.proj1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity {
	LinearLayout body;
	JSONObject form_all;
	JSONArray form;
	ArrayList<Issue> issuepkg;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.issuepkg = new ArrayList<Issue>();
        body = (LinearLayout) findViewById(R.id.body);
        
        
        final Button button = (Button) findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 //Toast.makeText(MainActivity.this, "Formulario gravado", Toast.LENGTH_SHORT).show();
            	 
            	 MainActivity.this.saveForm();
            	 Intent intent = new Intent(MainActivity.this, FinishActivity.class);
            	 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
            	 startActivity(intent);
            	 finish();
            	 

            	 
            }
        });

        String aux = this.loadJSONFromAsset("form1.json");
        this.buildForm(aux);
    }

    
    @Override
    public void onResume() {
        super.onResume();
        Log.v("log", "+ ON RESUME +");
    }
    
    @Override
    public void onStop() {
        super.onStop();
        this.updateForm();
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
    
    
    
    
    
    
    
    
    
    //getApplicationContext());
    
    private void buildForm(String form_str){
    	try {
        	this.form_all = new JSONObject( form_str );
        	this.form = form_all.getJSONArray("box");
        	for (int i=0; i<form.length(); i++){
        		JSONObject issue_form = form.getJSONObject(i);
        		issue_form.put( "num", Integer.toString(i+1) );
        		Issue issue = new Issue(this, this.body, issue_form);
        		this.issuepkg.add(issue);
        	} 
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    

    
    
    
    
    private static final int FILE_SELECT_CODE = 0;

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
        intent.setType("*/*"); 
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }
    
    
    
    public String loadJSONFromAsset(String url) {
        String json = null;
        try {
        	File sdcard = Environment.getExternalStorageDirectory();
        	File fd_json = new File( sdcard,"/ufpr.labcrono.proj1/"+url );
        	byte[] buffer;

        	if ( fd_json.exists()) {
            	FileInputStream is = new FileInputStream (fd_json);
        		int size = is.available();
            	buffer = new byte[size];
                is.read(buffer);
                is.close();        	
        	} else {
            	AssetManager manager = this.getAssets();
            	InputStream is = manager.open("form.json");
            	int size = is.available();
            	buffer = new byte[size];
                is.read(buffer);
                is.close(); 
        	}
        	
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;   	
    }
    

    
    public void updateForm(){
		for (int i=0; i<this.issuepkg.size(); i++){
			this.issuepkg.get(i).update();
		}
    }
    
    public void saveForm(){
    	this.updateForm();
    	
    	FileOutputStream outputStream;
    	File sdcard = Environment.getExternalStorageDirectory();
    	File form_folder = new File( sdcard,"/ufpr.labcrono.proj1/result/" );
    	form_folder.mkdirs();
    	
    	File fd_json = new File ( form_folder, this.getNewUserId(sdcard)+".json" );
    	try {
			outputStream = new FileOutputStream( fd_json );
			try {
				Log.w("log","abriu o arquivo");
				outputStream.write( this.form_all.toString().getBytes() );
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }

    
    private String getNewUserId(File sdcard){
    	Random r = new Random();
    	File file;
    	String name;
    	do {
    		int i1 = r.nextInt(Integer.MAX_VALUE);
    		name = Integer.toString(i1);     	
    		file = new File( sdcard,"/ufpr.labcrono.proj1/result/"+name);    	
    	} while ( file.exists() );
    	
    	return name;
    }
    
}
