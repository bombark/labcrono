package org.ufpr.labcrono.issue;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class Form {
	ActMain actmain;
	File root;

	String name;
	JSONObject data;
	JSONArray questions;
	ArrayList<Issue> issuepkg;

	static String URL_FORM_JSON = "form.json";
	static String URL_RESULTS = "results/";

	/**
	 *
	 */
	public Form(File root)  {
		this.root = root;
		this.name = root.getName();
	}

	public void load(String textformatting) throws JSONException, IOException{
		byte[] buffer;

		/* Carrega o arquivo form.json */
		File json_fd = new File( this.root, this.URL_FORM_JSON );
		FileInputStream is = new FileInputStream (json_fd);
		int size = is.available();
		buffer = new byte[size];
		is.read(buffer);
		is.close();
		String json_raw = new String(buffer, textformatting); //"ISO-8859-1");

		this.data = new JSONObject(json_raw);
		this.issuepkg = new ArrayList<Issue>();
		this.questions = this.data.getJSONArray("questions");
	}


	/**
	 *
	 * @throws Exception
	 */
	public void render(ActMain actmain) throws Exception {
		if ( this.data == null){
			throw new Exception("Nenhum Formulario para ser renderizado");
		}

		try {
			for (int i=0; i<this.questions.length(); i++){
				JSONObject issue_form = this.questions.getJSONObject(i);
				issue_form.put( "num", Integer.toString(i+1) );
				Issue issue = new Issue(issue_form);
				issue.build(actmain);
				this.issuepkg.add(issue);
			}
		} catch (JSONException e) {
			throw new Exception("[Erro de Sintaxe]: "+e.getLocalizedMessage());
		}
	}

	/**
	 *
	 * @return
	 */
	public String hasIssueEmpty(){
		for ( int i=0; i<this.issuepkg.size(); i++ ){
			String num = this.issuepkg.get(i).hasIssueEmpty();
			if ( !num.isEmpty() )
				return num;
		}
		return "";
	}

	/**
	 *
	 */
	public void updateForm(){
		for (int i=0; i<this.issuepkg.size(); i++){
			try {
				this.issuepkg.get(i).update();
			} catch (JSONException e) {
				//this.showError("Não foi possivel atualizar o formulario");
				e.printStackTrace();
			}
		}
	}


	/**
	 *
	 */
	public void saveForm(){
		this.updateForm();
		try {
			FileOutputStream outputStream = new FileOutputStream( this.getNewResult() );
			try {
				outputStream.write( this.data.toString().getBytes() );
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 *
	 * @return
	 */
	private File getNewResult(){
		Random r = new Random();
		File file;
		File results_path = new File( this.root, this.URL_RESULTS );
		this.createDirIfNotExists(results_path);
		do {
			int i1 = r.nextInt(Integer.MAX_VALUE);
			String name = Integer.toString(i1) + ".json";
			file = new File( results_path, name );
		} while ( file.exists() );

		return file;
	}






	public boolean createDirIfNotExists(File file) {
		if (!file.exists()) {
			if (!file.mkdirs()) {
				return false;
			}
		}
		return true;
	}


}
