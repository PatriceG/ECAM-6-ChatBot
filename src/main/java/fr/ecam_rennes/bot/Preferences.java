package fr.ecam_rennes.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

	/**
	 * @author pat
	 * 
	 *         Implémentation de la gestion des préférences sous forme de
	 *         Singleton
	 * @see java.util.Properties <P>
	 *      Lit le fichier bot.properties situé dans le classpath
	 */

	public class Preferences extends Properties{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private static Preferences me;

		private Preferences() {
			InputStream is = getClass().getResourceAsStream("/bot.properties");
			try {
				load(is);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		/**
		 * Méthode factory retournant le singleton avec initialisation si
		 * nécessaire
		 * 
		 * @return
		 */
		public static Preferences getInstance() {
			if (me == null)
				me = new Preferences();

			return me;
		}
	}