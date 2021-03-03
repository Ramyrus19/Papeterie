/**
 * 
 */
package fr.eni.papeterie.dal.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import fr.eni.papeterie.bo.Article;
import fr.eni.papeterie.bo.Ramette;
import fr.eni.papeterie.bo.Stylo;
import fr.eni.papeterie.dal.ArticleDAO;
import fr.eni.papeterie.dal.DALException;

/**
 * @author Eni Ecole
 * 
 */
public class ArticleDAOJdbcImpl implements ArticleDAO{

	private static final String TYPE_STYLO = "STYLO";
	private static final String TYPE_RAMETTE = "RAMETTE";

	private static final String SQL_INSERT = "insert into articles(reference,marque,designation,prixUnitaire,qteStock,grammage,couleur, type) "
			                                 + " values(?,?,?,?,?,?,?,?)";
	
	private static final String SQL_SELECT_BY_ID = "select idArticle, reference, marque, designation, prixUnitaire, qteStock, grammage, couleur, type "
			+ " from articles where idArticle = ?";
	private static final String SQL_SELECT_ALL = "select idArticle, reference, marque, designation, prixUnitaire, qteStock, grammage, couleur, type "
			+ " from articles";
	private static final String SQL_UPDATE = "update articles set reference=?,marque=?,designation=?,prixUnitaire=?,qteStock=?,grammage=?,couleur=? where idArticle=?";
	private static final String SQL_DELETE = "delete from articles where idArticle=?";
	private static final String SQL_SELECT_BY_MARQUE = "select reference, marque, designation, prixUnitaire, qteStock, grammage, couleur, type "
			+ " from articles where marque = ?";
	private static final String SQL_SELECT_BY_MOT_CLE = "select reference, marque, designation, prixUnitaire, qteStock, grammage, couleur, type "
			+ " from articles where marque like ? or designation like ?";

	
	
	public void insert(Article art) throws DALException {

		
		Connection uneConnection = null;
		PreparedStatement pStmt = null;
		try{
			
			//Etape 2 : Ouverture de la connexion à la base de données
			uneConnection = JdbcTools.getConnection();
			
			//Etape 3 : Créer  la requete
			pStmt = uneConnection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS  );
			
			//Etape 4 : Executer la requete
			//Valoriser les paramètres de la requete
			pStmt.setString(1, art.getReference());
			pStmt.setString(2, art.getMarque());
			pStmt.setString(3, art.getDesignation());
			pStmt.setFloat(4, art.getPrixUnitaire());
			pStmt.setInt(5, art.getQteStock());

			if (art instanceof Ramette) {
				Ramette r = (Ramette) art;
				pStmt.setInt(6, r.getGrammage());
				pStmt.setNull(7, Types.VARCHAR);
				pStmt.setString(8, TYPE_RAMETTE);
			}
			if (art instanceof Stylo) {
				Stylo s = (Stylo) art;
				pStmt.setNull(6, Types.INTEGER);
				pStmt.setString(7, s.getCouleur());
				pStmt.setString(8, TYPE_STYLO);
			}
			
			pStmt.executeUpdate();
			
			ResultSet rsId = pStmt.getGeneratedKeys();
			if(rsId.next()){
				art.setIdArticle(rsId.getInt(1));
			}
					

		} catch (SQLException e) {
			
			throw new DALException("Erreur à l'ajout d'un article : " + art
					               ,e);
			
			
		}finally{
			
			try {
				if(uneConnection!=null){
					uneConnection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	

	public Article selectById(int id) throws DALException {
		ResultSet rs = null;
		Article art = null;
		try( Connection cnx = JdbcTools.getConnection();
			 PreparedStatement rqt = cnx.prepareStatement(SQL_SELECT_BY_ID);
		    ) {
			
			rqt.setInt(1, id);

			rs = rqt.executeQuery();
			if (rs.next()) {

				if ("STYLO".equalsIgnoreCase(rs.getString("type").trim())) { //CODE ROBUSTE
				//if (rs.getString("type").trim().equalsIgnoreCase(TYPE_STYLO)) {

					art = new Stylo(rs.getInt("idArticle"), rs.getString("marque"), rs.getString("reference").trim(),
							rs.getString("designation"), rs.getFloat("prixUnitaire"), rs.getInt("qteStock"),
							rs.getString("couleur"));
				}
				if (TYPE_RAMETTE.equalsIgnoreCase(rs.getString("type").trim())) {
					art = new Ramette(rs.getInt("idArticle"), rs.getString("marque"), rs.getString("reference").trim(),
							rs.getString("designation"), rs.getFloat("prixUnitaire"), rs.getInt("qteStock"),
							rs.getInt("grammage"));
				}
			}

		} catch (SQLException e) {
			throw new DALException("selectById failed - id = " + id, e);
		} 
		return art;
	}

	
	public List<Article> selectAll() throws DALException {
		Connection cnx = null;
		Statement rqt = null;
		ResultSet rs = null;
		List<Article> liste = new ArrayList<Article>();
		try {
			cnx = JdbcTools.getConnection();
			rqt = cnx.createStatement();
			rs = rqt.executeQuery(SQL_SELECT_ALL);
			Article art = null;

			while (rs.next()) {
				if (TYPE_STYLO.equalsIgnoreCase(rs.getString("type").trim())) {

					art = new Stylo(rs.getInt("idArticle"), rs.getString("marque"), rs.getString("reference").trim(),
							rs.getString("designation"), rs.getFloat("prixUnitaire"), rs.getInt("qteStock"),
							rs.getString("couleur"));
				}
				if (TYPE_RAMETTE.equalsIgnoreCase(rs.getString("type").trim())) {
					art = new Ramette(rs.getInt("idArticle"), rs.getString("marque"), rs.getString("reference").trim(),
							rs.getString("designation"), rs.getFloat("prixUnitaire"), rs.getInt("qteStock"),
							rs.getInt("grammage"));
				}
				liste.add(art);
			}
		} catch (SQLException e) {
			throw new DALException("selectAll failed - ", e);
		} 
		return liste;

	}

	
	public void update(Article data) throws DALException {
		Connection cnx = null;
		PreparedStatement rqt = null;
		try {
			cnx = JdbcTools.getConnection();
			rqt = cnx.prepareStatement(SQL_UPDATE);
			rqt.setString(1, data.getReference());
			rqt.setString(2, data.getMarque());
			rqt.setString(3, data.getDesignation());
			rqt.setFloat(4, data.getPrixUnitaire());
			rqt.setInt(5, data.getQteStock());
			rqt.setInt(8, data.getIdArticle());
			if (data instanceof Ramette) {
				Ramette r = (Ramette) data;
				rqt.setInt(6, r.getGrammage());
				rqt.setNull(7, Types.VARCHAR);
			}
			if (data instanceof Stylo) {
			//autre possibilité : 
			//if(data.getClass().getSimpleName().equals(Stylo.class.getSimpleName())){
				
				Stylo s = (Stylo) data;
				rqt.setNull(6, Types.INTEGER);
				rqt.setString(7, s.getCouleur());
			}

			rqt.executeUpdate();

		} catch (SQLException e) {
			throw new DALException("Update article failed - " + data, e);
		} 

	}



	public void delete(int idArticle) throws DALException {
		
		try (
			Connection  cnx = JdbcTools.getConnection();	
			PreparedStatement rqt = cnx.prepareStatement(SQL_DELETE);
			){
			
			// l'intégrité référentielle s'occupe d'invalider la suppression
			// si l'article est référencé dans une ligne de commande
			rqt.setInt(1, idArticle);
			rqt.executeUpdate();
		} catch (SQLException e) {
			throw new DALException("Delete article failed - id=" + idArticle, e);
		} 
	}

	
	public List<Article> selectByMarque(String marque) throws DALException {
		ResultSet rs = null;
		List<Article> liste = new ArrayList<Article>();
		try (
				Connection  cnx = JdbcTools.getConnection();	
				PreparedStatement rqt = cnx.prepareStatement(SQL_SELECT_BY_MARQUE);				
			 ){
			rqt.setString(1, marque);
			rs = rqt.executeQuery();
			Article art = null;

			while (rs.next()) {
				if (TYPE_STYLO.equalsIgnoreCase(rs.getString("type").trim())) {

					art = new Stylo(rs.getInt("idArticle"), rs.getString("marque"), rs.getString("reference").trim(),
							rs.getString("designation"), rs.getFloat("prixUnitaire"), rs.getInt("qteStock"),
							rs.getString("couleur"));
				}
				if (TYPE_RAMETTE.equalsIgnoreCase(rs.getString("type").trim())) {
					art = new Ramette(rs.getInt("idArticle"), rs.getString("marque"), rs.getString("reference").trim(),
							rs.getString("designation"), rs.getFloat("prixUnitaire"), rs.getInt("qteStock"),
							rs.getInt("grammage"));
				}
				liste.add(art);
			}
		} catch (SQLException e) {
			throw new DALException("selectByMarque failed - ", e);
		} 
		return liste;
	}

	
	public List<Article> selectByMotCle(String motCle) throws DALException {
		ResultSet rs = null;
		List<Article> liste = new ArrayList<Article>();
		try (
				Connection  cnx = JdbcTools.getConnection();
				PreparedStatement rqt = cnx.prepareStatement(SQL_SELECT_BY_MOT_CLE);
				){
			rqt.setString(1, motCle);
			rs = rqt.executeQuery();
			Article art = null;

			while (rs.next()) {
				if (TYPE_STYLO.equalsIgnoreCase(rs.getString("type").trim())) {

					art = new Stylo(rs.getInt("idArticle"), rs.getString("marque"), rs.getString("reference").trim(),
							rs.getString("designation"), rs.getFloat("prixUnitaire"), rs.getInt("qteStock"),
							rs.getString("couleur"));
				}
				if (TYPE_RAMETTE.equalsIgnoreCase(rs.getString("type").trim())) {
					art = new Ramette(rs.getInt("idArticle"), rs.getString("marque"), rs.getString("reference").trim(),
							rs.getString("designation"), rs.getFloat("prixUnitaire"), rs.getInt("qteStock"),
							rs.getInt("grammage"));
				}
				liste.add(art);
			}
		} catch (SQLException e) {
			throw new DALException("selectByMotCle failed - ", e);
		} 
		return liste;
	}

}
