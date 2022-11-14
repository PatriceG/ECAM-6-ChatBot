package fr.ecam_rennes.geocodeur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
/**
 * Classe illustrant l'utilisation du client HTTP de Java11 pour faire appel à un service
 * de Géocodage d'adresses
 * 
 * @author pat
 * 
 */
public class GeoCodeur {
	/**
	 * Appel du service de GeoCodage d'OpenStreetMap
	 * 
	 * @param adresse
	 *            - Adresse dont on veut les coordonnees
	 * @return flux JSON avec entre autres les coordonnees. Retourne [] si l'adresse n'a pas ete trouvee
	 */
	public String geoCode(String adresse) throws Exception {
		String res = null;

		HttpClient httpClient = HttpClient.newBuilder()
         .version(HttpClient.Version.HTTP_2)
         .connectTimeout(Duration.ofSeconds(10))
         .build(); 

		try {
            HttpRequest request = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("http://nominatim.openstreetmap.org/search?q="
							+ URLEncoder.encode(adresse, "UTF-8")
					+"&format=json&polygon=1&addressdetails=1"))
            .build();                              
            HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString()); 

         	System.out.println("Status code: " + response.statusCode());                            
         	System.out.println("Headers: " + response.headers().allValues("content-type"));
         	System.out.println("Body: " + response.body());
			res = response.body();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		 finally {

		}
		return res;
	}

	/**
	 * Methode VERY Quick & Dirty pour recuperer les coordonnees du flux de retour
	 * d'OpenSteetMap et calculer une URL pour GoogleMaps centree sur cette
	 * adresse
	 * @param s
	 *            - flux XML retourné
	 * @return URL Google Maps
	 */
	public String calcGoogleMapsURL(String s) {
		String lat="";
		String lon="";
		int lat1 = s.indexOf("lat\":\"")+6;
		int lat2 = s.indexOf("\"",lat1);
		lat=s.substring(lat1,lat2);
		int lon1 = s.indexOf("lon\":\"")+6;
		int lon2 = s.indexOf("\"",lon1);
		lon=s.substring(lon1,lon2);
				
		//String url = "http://maps.google.fr/maps?hl=fr&sll=" + lat + "," + lon
		//		+ "&t=h&z=16";
		String url = "http://maps.google.fr?q=" + lat + "+" + lon;
		return url;
	}

	/**
	 * Point d'Entree
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) {
		GeoCodeur gc = new GeoCodeur();
		
		System.out.println("Entrer l'adresse a Geo-Coder:");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		try {
			String adresse = br.readLine();
			String res = gc.geoCode(adresse);
			System.out.println("coordonnees: " + res);
			if("[]".equals(res)){
				System.out.println("Adresse non trouvée!");
				return;
			}
			String url = gc.calcGoogleMapsURL(res);
			System.out.println("URL Google Maps: " + url);
			//lancement du navigateur selon l'OS
			String navigateur = "/usr/bin/firefox ";
			if(System.getProperty("os.name").startsWith("Windows")){
				navigateur = "C:\\Program Files\\Internet Explorer\\iexplore.exe ";
			}
			Process p=Runtime.getRuntime().exec(navigateur+url);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
