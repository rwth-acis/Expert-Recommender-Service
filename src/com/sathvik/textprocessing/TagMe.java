package com.sathvik.textprocessing;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sathvik.models.TagMeAdapter;
import com.sathvik.models.TagMePOJO;
import com.sathvik.models.TagMePOJOContainer;
import com.sathvik.utils.Utils;

public class TagMe {
	private String mText;
	private String postId;
	ArrayList<String> tags;

	public TagMe(String id, String text) {
		postId = id;
		mText = text;
	}

	public void setTags(String tagstr) {
		tags = new ArrayList<String>(Arrays.asList(tagstr.split(",")));
	}

	public ArrayList<TagMePOJO> getTags() {
		ArrayList<TagMePOJO> tagmeObjs = new ArrayList<TagMePOJO>();

		if (mText != null && mText.length() > 0) {

			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(Utils.TAGME_URL);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("key", Utils.TAG_ME_KEY));
			nvps.add(new BasicNameValuePair("text", mText));
			nvps.add(new BasicNameValuePair("include_categories", "true"));

			CloseableHttpResponse response = null;
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
				response = httpclient.execute(httpPost);
				System.out.println(response.getStatusLine());

				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				//System.out.println(responseString);

				Gson gson = new GsonBuilder().registerTypeAdapter(
						TagMePOJO.class, new TagMeAdapter()).create();

				TagMePOJOContainer tagMe = gson.fromJson(responseString,
						TagMePOJOContainer.class);

				for (TagMePOJO tagpojo : tagMe.getAnnotations()) {
					if (tagpojo != null) {
						tagmeObjs.add(tagpojo);
						//Utils.println("TAG OBJ ::" + new Gson().toJson(tagpojo));
					}
				}

				// Release all the underlying resource.
				EntityUtils.consume(entity);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		return tagmeObjs;
	}

}
