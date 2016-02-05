package org.ufpr.labcrono.issue;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class FormPkg {
	File root;

	static String url_base = "ufpr.labcrono.issue";
	//static String url_pesquisas = "ufpr.labcrono.proj1/pesquisas";


	FormPkg(){
		this.createDirIfNotExists( this.url_base );
		//this.createDirIfNotExists( this.url_pesquisas );
		//this.createDirIfNotExists( this.url_base + "/resultados");

		File sdcard = Environment.getExternalStorageDirectory();
		this.root = new File( sdcard, this.url_base );
	}




	/**
	 * @return
	 */
	public ArrayList<Form> list() throws IOException, JSONException {
		ArrayList<Form> res = new ArrayList<>();
		File[] files = this.root.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				res.add( this.load( file ) );
			}
		}
		return res;
	}

	/**
	 * @param url
	 * @return
	 */
	public Form load(File url) throws IOException, JSONException {
		Form form = new Form(url);
		return form;
	}

	public Form load(String form_name) throws IOException, JSONException {
		File form_root = new File ( this.root, form_name );
		Form form = new Form(form_root);
		return form;
	}


	public void savetoSavedState(Form form, Bundle savedInstanceState){
		form.updateForm();
		savedInstanceState.putString("form_data", form.questions.toString());
		savedInstanceState.putString("form_name", form.name);
	}

	public Form loadFromSavedState (Bundle savedInstanceState) throws IOException, JSONException {
		Form form = this.load(savedInstanceState.getString("form_name"));
		form.set(savedInstanceState.getString("form_data"));
		return form;
	}

	/**
	 *
	 * @param path
	 * @return
	 */
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


	public String getFormDescription(String form_name){
		if ( form_name.equals("") )
			return "Selecione um formulário em Configurações";

		File file = new File(this.root, form_name+"/description.txt");
		if ( file.exists() ){
			String ret = "";
			try {
				FileInputStream fin = new FileInputStream(file);
				ret = convertStreamToString(fin);
				fin.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
		} else
			return "Adicione um arquivo description.txt dentro da pasta do formulário "+form_name+" para colocar um texto aqui";
	}



	private static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

}
