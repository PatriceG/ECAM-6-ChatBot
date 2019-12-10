package fr.edb.bot;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Classe de traduction de texte utilisant le service Microsoft Azure Translator
 * @author PatG
 *
 */
public class AzureTranslator {
	
	//clé d'accès au service
	private static final String key="d8839b4557ca44cc9d895d7b47d86bb3";
	
	private String endpoint = "https://api.cognitive.microsofttranslator.com";
	private String url = endpoint + "/translate?api-version=3.0&to=";

	
	/**
	 * Traduit le texte et retourne le texte traduit.
	 * Détecte automatiquement la langue source
	 * 
	 * @param to
	 *            - code ISO de la langue de la traduction souhaitée
	 * @param text
	 *            - texte à traduite
	 * @return texte traduit
	 * @throws Exception
	 */
	public String translate(String to, String text)
			throws Exception {
	
		//APPEL Microsoft Translator	
		URI realUrl = new URI(url+to);
		
		//Utilisation de Apache HTTPClient pour envoyer une requête POST à Microsoft Translator
		DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httpPost = new HttpPost(realUrl);
		httpPost.addHeader("Ocp-Apim-Subscription-Key", key);
		httpPost.addHeader("Content-type", "application/json");
		
		text = text.replace("\"", "\\\""); //échappement des guillemets
		//construction de json façon very quick & very dirty... pour cet exemple:
		StringEntity ent = new StringEntity("[{\n\t\"Text\": \""+text+"\"\n}]",
				 "application/json", "UTF-8");
		httpPost.setEntity(ent);
	
		HttpResponse response = httpclient.execute(httpPost);
		System.out.println("response status: "+ response.getStatusLine());
		
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		
		System.out.println("result:" + result);
		
		//décodage de la réponse via Jackson
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(result);
		String translation;
		if(response.getStatusLine().getStatusCode() == 200) //si tout est OK on retourne la traduction
			translation = rootNode.get(0).path("translations").get(0).path("text").asText();
		else
			translation = result; //sinon on retourne le message d'erreur
		
		
		return translation;
	}

	/**
	 * Point d'entrée, pour tests unitaires de cette classe
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
	
		AzureTranslator translator = new AzureTranslator();
		System.out.println(translator
				.translate("fr", "text to translate"));

	}

}
