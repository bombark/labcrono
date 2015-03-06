package ufpr.labcrono.proj1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	LinearLayout body;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        body = (LinearLayout) findViewById(R.id.body);
        
        
        String aux = this.loadJSONFromAsset("form1.json");     
        Log.w("log", "aqui");
        this.buildForm(aux);
        
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
			//JSONObject reader = new JSONObject("teste.json");
        	JSONObject form = new JSONObject( form_str );
        	JSONArray box = form.getJSONArray("box");

        	for (int i=0; i<box.length(); i++){
        		JSONObject issue = box.getJSONObject(i);
        		
        		String classe = issue.getString("class");
        		String title  = issue.getString("title");
                TextView tv = new TextView(this);
                tv.setText(Integer.toString(i+1)+". "+title);
                body.addView(tv);
                
                if ( classe.equals("Int") ){
                	this.buildIntInput(issue);
                } else if ( classe.equals("Text") ){
                	this.buildTextInput(issue);
                } else if ( classe.equals("Date") ){
                	this.buildDateInput(issue);
                } else if ( classe.equals("Boolean") ){
                	this.buildBooleanInput(issue);
                } else if ( classe.equals("Enum") ){
                	this.buildEnumInput(issue);
                } else {
                	TextView tv1 = new TextView(this);
                	tv1.setText("Error: "+classe);
                    body.addView(tv1);
                }
        	}
            
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    }
    
    
    private void buildTextInput(JSONObject issue) throws JSONException {   	
        EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        this.body.addView(edittext);
    }
    
    private void buildIntInput(JSONObject issue) throws JSONException {   	
        EditText edittext = new EditText(this);  
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.body.addView(edittext);
    }
    
    
    private void buildDateInput(JSONObject issue) throws JSONException {   	
        EditText edittext = new EditText(this);  
        edittext.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        this.body.addView(edittext);
    }
    
    private void buildBooleanInput(JSONObject issue) throws JSONException {   	
    	Spinner spinner = new Spinner(this);
    	List<String> list = new ArrayList<String>();
    	list.add("Não");
    	list.add("Sim");
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(dataAdapter);   	
        this.body.addView(spinner);
    }
    
    private void buildEnumInput(JSONObject issue) throws JSONException {   	
    	Spinner spinner = new Spinner(this);
    	List<String> list = new ArrayList<String>();

    	JSONArray box = issue.getJSONArray("box");
    	for (int i=0; i<box.length(); i++){
    		JSONObject option = box.getJSONObject(i);
    		String     title  = option.getString("title");
    		list.add( title );
    	}

    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(dataAdapter);   	
        this.body.addView(spinner);
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
        	FileInputStream is = new FileInputStream (fd_json);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    	
    	/*String path = getFilesDir().getAbsolutePath();
    	Log.w("logs", path );
    	return "";*/
    	//return uri.toString();*/
        
    	
        /*try {
			String[] aux = this.getResources().getAssets().list("../");
			Log.w("logs", Integer.toString(aux.length) );
			for (int i=0; i<aux.length; i++)
				Log.w("logs", aux[i] );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return "";*/
    	
    }
    
}
