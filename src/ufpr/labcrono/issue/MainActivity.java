package ufpr.labcrono.issue;

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

import ufpr.labcrono.issue.R;
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
import android.widget.TextView;
import android.widget.Toast;


/*** @brief class MainActivity
 */
public class MainActivity extends Activity {
	LinearLayout body;
	JSONArray form;
	ArrayList<Issue> issuepkg;
	TextView error_msg;
	
	/*public enum Encode {
		UTF, ISO8559
	}
	Encode encode;*/
	
	static String url_base = "ufpr.labcrono.proj1";
	static String url_pesquisas = "ufpr.labcrono.proj1/pesquisas";

	
	/*! Evento onCreate */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //this.encode = Encode.UTF;
        
        this.createDirIfNotExists( MainActivity.url_base );
        this.createDirIfNotExists( MainActivity.url_pesquisas );
        this.createDirIfNotExists( MainActivity.url_base+"/resultados");
        
        this.issuepkg = new ArrayList<Issue>();
        body = (LinearLayout) findViewById(R.id.body);
  
        try {
			this.form = new JSONArray( this.loadJSONFromAsset("form.json") );
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

	
	@Override
	public void onBackPressed() {
	}
    
	/*! Evento onResume */
    @Override
    public void onResume() {
        super.onResume();
        this.buildForm();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      this.updateForm();
      savedInstanceState.putString("form_json", this.form.toString());
      super.onSaveInstanceState(savedInstanceState);  
    }
    
    @Override  
    public void onRestoreInstanceState(Bundle savedInstanceState) {  
      super.onRestoreInstanceState(savedInstanceState);
      try {
		this.form = new JSONArray( savedInstanceState.getString("form_json") );
      } catch (JSONException e) {
    	this.showError("aaaaaa");
		e.printStackTrace();
      }
    }
    
    
    
    /*! Evento onStop 
    @Override
    public void onStop() {
        super.onStop();
    }*/
    
    

    
    
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
    
   
    /*! Constroi o visual do formulario a partir de uma string json 
     */
    protected void buildForm() {
    	if ( this.form == null){
    		this.showError("Variavel MainActivity.form está NULL");
    		return ;
    	}
    	
    	try {
        	for (int i=0; i<form.length(); i++){
        		JSONObject issue_form = form.getJSONObject(i);
        		issue_form.put( "num", Integer.toString(i+1) );
        		Issue issue = new Issue(this, issue_form);
        		issue.build();
        		this.issuepkg.add(issue);
        	} 
        	
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
        	
        	
		} catch (JSONException e) {
			this.showError("[Erro de Sintaxe]: "+e.getLocalizedMessage() );
			e.printStackTrace();
		}
    }
    

    
    

    
    
    
    public String loadJSONFromAsset(String url) {
        String json = null;
        try {
        	File sdcard = Environment.getExternalStorageDirectory();
        	File fd_json = new File( sdcard,MainActivity.url_base+"/"+url );
        	byte[] buffer;

        	if ( fd_json.exists()) {
            	FileInputStream is = new FileInputStream (fd_json);
        		int size = is.available();
            	buffer = new byte[size];
                is.read(buffer);
                is.close();        	
                json = new String(buffer, "ISO-8859-1");
        	} else {
            	AssetManager manager = this.getAssets();
            	InputStream is = manager.open("form.json");
            	int size = is.available();
            	buffer = new byte[size];
                is.read(buffer);
                is.close(); 
                json = new String(buffer, "UTF-8");
        	}
        	

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;   	
    }
    

    
    public void updateForm(){
		for (int i=0; i<this.issuepkg.size(); i++){
			try {
				this.issuepkg.get(i).update();
			} catch (JSONException e) {
				this.showError("Não foi possivel atualizar o formulario");
				e.printStackTrace();
			}
		}
    }
    
    public void saveForm(){
    	this.updateForm();
    	
    	FileOutputStream outputStream;
    	File sdcard = Environment.getExternalStorageDirectory();
    	File form_folder = new File( sdcard, MainActivity.url_pesquisas );
    	File fd_json = new File ( form_folder, MainActivity.this.getNewUserId()+".json" );
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

    
    private String getNewUserId(){
    	File sdcard = Environment.getExternalStorageDirectory();
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
    protected String hasIssueEmpty(){
    	for ( int i=0; i<this.issuepkg.size(); i++ ){
    		String num = this.issuepkg.get(i).hasIssueEmpty();
    		if ( !num.isEmpty() )
    			return num;
    	}
    	return "";
    }
    
	public boolean createDirIfNotExists(String path) {
	    File file = new File(Environment.getExternalStorageDirectory(), path);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("TravellerLog :: ", "Problem creating folder");
	            return false;
	        }
	    }
	    return true;
	}
	
	/*public void setGenre(String genre){
		for (int i=0; i<issuepkg.size(); i++){
			this.issuepkg.get(i).setGenre(genre);
		}
	}*/
	
	protected void showError(String msg){
		this.body.removeAllViews();
		
		this.error_msg = new TextView(this);
		this.error_msg.setText(msg);
		//this.error_msg.setTextColor(0xFFFF0000);
		this.body.addView(error_msg);
		//Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
}
