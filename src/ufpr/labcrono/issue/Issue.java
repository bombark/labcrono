package ufpr.labcrono.issue;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;


public class Issue {
	JSONObject form;
	TextView title;
	View input;
	ArrayList<Issue> box;
	
	Context context;
	LinearLayout body;
	
	public Issue(MainActivity main, JSONObject issue){
		this.context = main;
		this.body = main.body;
		this.form = issue;
		this.box  = new ArrayList<Issue>();
		if ( !this.form.has("ishidden") ){
			try {
				this.form.put("ishidden", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		this.build();
	}
	
	public Issue(Issue issue_father, JSONObject issue){
		this.context = issue_father.context;
		this.body = issue_father.body;
		this.form = issue;
		this.box  = new ArrayList<Issue>();
		if ( !this.form.has("ishidden") ){
			try {
				this.form.put("ishidden", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		this.build();
	}
	
	
	
	
	void build(){
		try {
			String classe = form.getString("class");
			this.buildTitle(this.form);
			if ( classe.equals("Int") ){
	        	this.buildIntInput(this.form);
	        } else if ( classe.equals("Text") ){
	        	this.buildTextInput();
	        } else if ( classe.equals("Date") ){
	        	this.buildDateInput(this.form);
	        } else if ( classe.equals("Boolean") ){
	        	this.buildBooleanInput(this.form);
	        } else if ( classe.equals("Enum") ){
	        	this.buildEnumInput(this.form);
	        } else if ( classe.equals("Checkbox") ){
	        	this.buildCheckboxInput(this.form);	
	        	
	        } else {
	        	//TextView tv1 = new TextView(this);
	        	//tv1.setText("Error: "+classe);
	            //body.addView(tv1);
	        }
			
			if ( this.form.getBoolean("ishidden") ){
				this.title.setVisibility(View.GONE);
				this.input.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
    private void buildTitle(JSONObject issue){
        this.title = new TextView(this.context);
        try {
			this.title.setText(issue.getString("num")+". "+issue.getString("title"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
        this.title.setTextSize(18);
        this.body.addView(this.title);
    }
	
    
    private void buildTextInput() throws JSONException {
        EditText edittext = new EditText(this.context);
        edittext.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        if ( this.form.has("value") ){
        	edittext.setText( this.form.getInt("value") );
        }
        this.input = edittext;
        this.body.addView( edittext );//addIssueInBody(issue, edittext);
    }
    
    private void buildIntInput(JSONObject issue) throws JSONException {
        EditText edittext = new EditText(this.context);  
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        if ( issue.has("value") ){
        	edittext.setText( issue.getInt("value") );
        }
        this.input = edittext;
        this.body.addView( edittext );
    }
    
    
    private void buildDateInput(JSONObject issue) throws JSONException {
        EditText edittext = new EditText(this.context);  
        edittext.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        if ( issue.has("value") ){
        	edittext.setText( issue.getInt("value") );
        }
        this.input = edittext;
        this.body.addView( edittext );
    }
    
    private void buildBooleanInput(JSONObject issue) throws JSONException {
    	Spinner spinner = new Spinner(this.context);
    	List<String> list = new ArrayList<String>();
    	list.add("");
    	list.add("Não");
    	list.add("Sim");
    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.context,android.R.layout.simple_spinner_item, list);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(dataAdapter);   	
        
        this.input = spinner;
        this.body.addView( spinner );
        
        /* Executa um evento quando um novo item eh selecionado
         */ spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if ( arg2 == 2 ){  // simd
					Issue.this.setSubVisibity(View.VISIBLE);
					Issue.this.body.invalidate();
				} else { // nao
					Issue.this.setSubVisibity(View.GONE);
					Issue.this.body.invalidate();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        });
        
        if ( issue.has("box") ){    
	        String num_base = issue.getString("num");
	        JSONArray box = issue.getJSONArray("box");
	        for (int i=0; i<box.length(); i++){
	    		JSONObject subissue_form = box.getJSONObject(i);
	    		subissue_form.put("ishidden", true);
	    		subissue_form.put( "num", num_base+"."+Integer.toString(i+1) );
	    		Issue subissue = new Issue(this, subissue_form);
	    		this.box.add(subissue);
	        }
        }
    }
    
    private void buildEnumInput(JSONObject issue) throws JSONException {
    	Spinner spinner = new Spinner(this.context);
    	List<String> list = new ArrayList<String>();
    	list.add( "" );
    	
    	JSONArray box = issue.getJSONArray("box");
    	for (int i=0; i<box.length(); i++){
    		JSONObject option = box.getJSONObject(i);
    		String     title  = option.getString("title");
    		list.add( title );
    	}

    	ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.context,android.R.layout.simple_spinner_item, list);
    	dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(dataAdapter);	
        
        this.input = spinner;
        this.body.addView( spinner );
    }
    
    
    private void buildCheckboxInput(JSONObject issue) throws JSONException {
    	LinearLayout group = new LinearLayout(this.context);
    	this.input = group;
    	JSONArray jbox = issue.getJSONArray("box");
    	for (int i=0; i<jbox.length(); i++){  	
        	CheckBox check = new CheckBox(this.context);
    		check.setText( jbox.getJSONObject(i).getString("title") );
    		group.addView(check);
    	}
        this.body.addView( group );
    }
    
    
    void setSubVisibity(int flag){
    	for (int i=0; i<this.box.size(); i++){
			this.box.get(i).setVisibity(flag);
		}
    }
    
	
	void setVisibity(int flag){ // flag = [View.VISIBLE|View.GONE|...]
		this.title.setVisibility(flag);
		this.input.setVisibility(flag);
		try {
			if ( flag == View.VISIBLE )
				this.form.put("ishidden", false);
			else
				this.form.put("ishidden", true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for (int i=0; i<this.box.size(); i++){
			this.box.get(i).setVisibity(flag);
		}
	}
	
	
	void update(){
		try {
			String classe = this.form.getString("class");
			if ( classe.equals("Text") || classe.equals("Int") || classe.equals("Date") ){
				EditText edit  = (EditText) this.input;
				Editable value = edit.getText();
				this.form.put("value", value);
			} else if ( classe.equals("Enum") || classe.equals("Boolean") ){
				Spinner spinner = (Spinner) this.input;
				String value = spinner.getSelectedItem().toString(); 
				this.form.put("value", value);
			} else if ( classe.equals("Checkbox") ){
				boolean flag = false;
				String value = "";
				LinearLayout  group = (LinearLayout) this.input;
				for (int i=0; i<group.getChildCount(); i++){
					CheckBox check = (CheckBox) group.getChildAt(i);
					if ( check.isChecked() ){
						if ( flag ){
							value += ","+check.getText();
						} else {
							value = (String) check.getText();
							flag = true;
						}
					}
				}
				this.form.put("value", value);
			}
			for (int i=0; i<this.box.size(); i++){
				this.box.get(i).update();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	String hasIssueEmpty(){
		try {
			if ( form.getBoolean("ishidden") == false ){
				String classe;
				classe = form.getString("class");

				if ( classe.equals("Int") || classe.equals("Text") || classe.equals("Date") ){
					EditText edit  = (EditText) this.input;
					Editable value = edit.getText();
					if ( value.length() == 0 ){
			        	return this.form.getString("num");
					}
		        } else if ( classe.equals("Boolean") ){
		        	Spinner spinner = (Spinner) this.input;
		        	String value = spinner.getSelectedItem().toString();
		        	if ( value.equals("") ){
		        		return this.form.getString("num");
		        	}
		        	Log.w("has","a: "+value);
		        	if ( value.equals("Sim") ){
			        	Log.w("has","a: "+Integer.toString(this.box.size()));
			        	for ( int i=0; i<this.box.size(); i++ ){
			        		Log.w("has","c: "+Integer.toString(i));
			        		String num = this.box.get(i).hasIssueEmpty();
			        		Log.w("Aqui","aaa");
			        		if ( !num.isEmpty() )
			        			return num;
			        	}
		        	}
		        } else if ( classe.equals("Enum") ){
		        	Spinner spinner = (Spinner) this.input;
		        	if ( spinner.getSelectedItem().toString().equals("") ){
			        	return this.form.getString("num");
		        	}
		        }
			}
			return "";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
	

	/*private String toUTF(String data){
		if ( this.encode == Encode.ISO8559 ){
			try {
				return new String(data.getBytes("ISO-8859"), "UTF-16");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return data;
	}*/

}
