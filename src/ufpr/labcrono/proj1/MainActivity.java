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
	JSONArray form;
	ArrayList<Issue> issuepkg;

	static String url_pesquisas = "ufpr.labcrono.proj1/pesquisas";

			@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        this.createDirIfNotExists("ufpr.labcrono.proj1");
        this.createDirIfNotExists( this.url_pesquisas );
        this.createDirIfNotExists("ufpr.labcrono.proj1/resultados");
        
        this.issuepkg = new ArrayList<Issue>();
        body = (LinearLayout) findViewById(R.id.body);
        
        
        final Button button = new Button(this);
        button.setText("Enviar");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 String num = MainActivity.this.hasIssueEmpty();
            	 if ( !num.isEmpty() ){
            		 Toast.makeText(MainActivity.this, "Questao "+num+" nao foi respondida", Toast.LENGTH_SHORT).show();
            	 } else {
	            	 MainActivity.this.saveForm();
	            	 Intent intent = new Intent(MainActivity.this, FinishActivity.class);
	            	 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
	            	 startActivity(intent);
	            	 finish(); 
            	 }
            }
        });
        this.body.addView(button);
        

        String aux = this.loadJSONFromAsset("form.json");
        this.buildForm(aux);
        
    }

    
    
    // Ler do arquivo, e montar a view
    @Override
    public void onResume() {
        super.onResume();
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
        	this.form = new JSONArray( form_str );
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
    	File form_folder = new File( sdcard,this.url_pesquisas );
    	
    	File fd_json = new File ( form_folder, this.getNewUserId(sdcard)+".json" );
    	try {
			outputStream = new FileOutputStream( fd_json );
			try {
				outputStream.write( this.form.toString().getBytes() );
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
    		file = new File( sdcard,this.url_pesquisas+"/"+name);    	
    	} while ( file.exists() );
    	
    	return name;
    }
    
    
    
    // Fazer a rotina retonar o numero da questao nao respondida
    private String hasIssueEmpty(){
    	for ( int i=0; i<this.issuepkg.size(); i++ ){
    		String num = this.issuepkg.get(i).hasIssueEmpty();
    		if ( !num.isEmpty() )
    			return num;
    	}
    	return "";
    }
    
	public static boolean createDirIfNotExists(String path) {
	    File file = new File(Environment.getExternalStorageDirectory(), path);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("TravellerLog :: ", "Problem creating folder");
	            return false;
	        }
	    }
	    return true;
	}
}
