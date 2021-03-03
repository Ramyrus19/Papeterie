package fr.eni.papeterie.dal;

import java.util.List;

import fr.eni.papeterie.bo.Article;

public interface ArticleDAO {
	public void insert(Article art) throws DALException;
	public Article selectById(int id) throws DALException;
	public List<Article> selectAll() throws DALException;
	public void update(Article data) throws DALException ;	
	public List<Article> selectByMarque(String marque) throws DALException;
	public void delete(int idArticle) throws DALException;
	public List<Article> selectByMotCle(String motCle) throws DALException;

}
