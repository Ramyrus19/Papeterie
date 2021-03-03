package fr.eni.papeterie.ihm;

import javax.swing.SwingUtilities;

public class PapeterieApp {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				ArticleController.get().startApp();
			}
			
		});
		
	}

}
