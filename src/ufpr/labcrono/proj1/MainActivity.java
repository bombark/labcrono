package ufpr.labcrono.proj1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	LinearLayout body;
	JSONObject form_all;
	JSONArray form;
	ArrayList<View> inputs;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        inputs = new ArrayList<View>();
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
        Log.w("log", "aqui");
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
        this.updateForm(this.form);
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
        		JSONObject issue = form.getJSONObject(i);
        		this.buildIssue(issue, Integer.toString(i+1));
        	} 
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    
    private void buildIssue(JSONObject issue, String num){
 		try {
			String classe = issue.getString("class");
			issue.put( "num", num );
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
		} catch (JSONException e) {
			e.printStackTrace();
		}

    }
    
    private void buildTitle(JSONObject issue){
        TextView tv = new TextView(this);
        try {
			tv.setText(issue.getString("num")+". "+issue.getString("title"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
        body.addView(tv);
    }
    
    
    private void buildTextInput(JSONObject issue) throws JSONException {
    	this.buildTitle(issue);
        EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        if ( issue.has("value") ){
        	edittext.setText( issue.getInt("value") );
        }
        this.addIssueInBody(issue, edittext);
    }
    
    private void buildIntInput(JSONObject issue) throws JSONException {
    	this.buildTitle(issue);
        EditText edittext = new EditText(this);  
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        if ( issue.has("value") ){
        	edittext.setText( issue.getInt("value") );
        }
        this.addIssueInBody(issue, edittext);       
    }
    
    
    private void buildDateInput(JSONObject issue) throws JSONException {
    	this.buildTitle(issue);
        EditText edittext = new EditText(this);  
        edittext.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        if ( issue.has("value") ){
        	edittext.setText( issue.getInt("value") );
        }
        this.addIssueInBody(issue, edittext);
    }
    
    private void buildBooleanInput(JSONObject issue) throws JSONException {
    	this.buildTitle(issue);
    	Spinner spinner = new Spinner(this);
    	List<String> list = new ArrayList<String>();
    	list.add("");
    	list.add("Não");
    	list.add("Sim");
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(dataAdapter);   	
        
        this.addIssueInBody(issue, spinner);
        
        /* Executa um evento quando um novo item eh selecionado
         * spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.w("log","item selecionado");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        });*/
        
        if ( issue.has("box") ){    
	        String num_base = issue.getString("num");
	        JSONArray box = issue.getJSONArray("box");
	        for (int i=0; i<box.length(); i++){
	    		JSONObject option = box.getJSONObject(i);
	    		this.buildIssue( option, num_base+"."+Integer.toString(i+1) );
	        }
        }
    }
    
    private void buildEnumInput(JSONObject issue) throws JSONException {
    	this.buildTitle(issue);
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
        
    	this.addIssueInBody(issue, spinner);
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
    
    
    private void addIssueInBody(JSONObject issue, View view) throws JSONException{
    	this.body.addView(view);
        issue.put( "id", this.inputs.size() );
        this.inputs.add(view);
    }
    
    public void updateForm(JSONArray box){
		try {
			for (int i=0; i<box.length(); i++){
				JSONObject issue = box.getJSONObject(i);
				if ( issue.has("id") ){
					String classe = issue.getString("class");
					if ( classe.equals("Text") || classe.equals("Int") || classe.equals("Date") ){
						EditText edit  = (EditText) this.inputs.get( issue.getInt("id") );
						Editable value = edit.getText();
						//Log.w("log", value.toString() );
						issue.put("value", value);
					}
				}
			}
			//Log.w("log",this.form_all.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    public void saveForm(){
    	this.updateForm(this.form);
    	
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
