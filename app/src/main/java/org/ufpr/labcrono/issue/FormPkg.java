package org.ufpr.labcrono.issue;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

	public Form load(String name) throws IOException, JSONException {
		File form_root = new File ( this.root, name );
		Form form = new Form(form_root);
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



	/**
	 * @param name
	 * @return
	 */
	private String loadJSONFromAsset(String name) {
		String json = "";
		try {
			File sdcard = Environment.getExternalStorageDirectory();
			File fd_json = new File( sdcard, this.url_base+"/"+name );
			byte[] buffer;

			if ( fd_json.exists()) {
				FileInputStream is = new FileInputStream (fd_json);
				int size = is.available();
				buffer = new byte[size];
				is.read(buffer);
				is.close();
				json = new String(buffer, "ISO-8859-1");
			} else {
				/*AssetManager manager = actmain.getAssets();
				InputStream is = manager.open("form.json");
				int size = is.available();
				buffer = new byte[size];
				is.read(buffer);
				is.close();
				json = new String(buffer, "UTF-8");*/
			}


		} catch (IOException ex) {
			ex.printStackTrace();
			return "";
		}
		return json;
	}
}
