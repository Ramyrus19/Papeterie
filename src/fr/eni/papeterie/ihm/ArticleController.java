package fr.eni.papeterie.ihm;

import java.util.List;

import fr.eni.papeterie.bll.BLLException;
import fr.eni.papeterie.bll.CatalogueManager;
import fr.eni.papeterie.bo.Article;

public class ArticleController {
	
	private EcranArticle ecrArticle ;
	
	//Attributs
	private int indexCatalogue;
	
	private CatalogueManager mger;
	
	private List<Article> catalogue;
	
	/*** SINGLETON ***/
	private static ArticleController instance;
	
	//Constructeur
	private ArticleController(){
		try {
			mger = CatalogueManager.getInstance();
			
			//Initialisation du catalogue en mémoire
			catalogue = mger.getCatalogue();
			
			
		} catch (BLLException e) {
			e.printStackTrace();
		}
	}
	
	//Méthodes
	public static synchronized ArticleController get(){
		if(instance == null){
			instance = new ArticleController();
		}
		return instance;
	}
	
	public void startApp(){
		ecrArticle = new EcranArticle();
		ecrArticle.pack();
		afficherPremierArticle();
		ecrArticle.setVisible(true);
	}
	
	public void afficherPremierArticle(){
		if(catalogue.size()>0){
			indexCatalogue = 0;
			ecrArticle.afficherArticle(catalogue.get(indexCatalogue));
		}else{
			indexCatalogue = -1;
			ecrArticle.afficherNouveau();
		}
	}

	public void precedent(){
		if(indexCatalogue > 0){
			indexCatalogue--;
			ecrArticle.afficherArticle(catalogue.get(indexCatalogue));
		}
	}

	public void suivant() {
		if(indexCatalogue < catalogue.size()-1){
			indexCatalogue++;
			ecrArticle.afficherArticle(catalogue.get(indexCatalogue));
		}
	}

	public void nouveau() {
		indexCatalogue = catalogue.size();
		ecrArticle.afficherNouveau();
	}

	public void enregistrer() {
		Article articleAffiche = ecrArticle.getArticle();
		
		try {
			if(articleAffiche.getIdArticle()==null){
				mger.addArticle(articleAffiche);
				System.out.println("article: " + articleAffiche);
				catalogue.add(articleAffiche);
				ecrArticle.afficherArticle(articleAffiche);
			}else{
				mger.updateArticle( articleAffiche);
				catalogue.set(indexCatalogue, articleAffiche);
			}
		} catch (BLLException e1) {
			ecrArticle.infoErreur("Erreur enregistrement.");
			e1.printStackTrace();
		}
		
	}

	public void supprimer() {
		
		try {
			System.out.println(indexCatalogue);
//			mger.removeArticle(indexCatalogue);
			
			mger.removeArticle(catalogue.get(indexCatalogue).getIdArticle());
			catalogue.remove(indexCatalogue);
		} catch (BLLException e) {
			ecrArticle.infoErreur("Erreur suppression.");
			e.printStackTrace();
		}		

		if (indexCatalogue < catalogue.size() ) {
			// Afficher l'article suivant
			ecrArticle.afficherArticle(catalogue.get(indexCatalogue));
		} else if (indexCatalogue > 0) {
			indexCatalogue--;
			ecrArticle.afficherArticle(catalogue.get(indexCatalogue));
		} else {
			ecrArticle.afficherNouveau();
		}
		
	}


}
