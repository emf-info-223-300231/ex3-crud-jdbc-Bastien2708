package app.workers;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.SystemLib;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbWorker implements DbWorkerItf {

    private Connection dbConnexion;

    /**
     * Constructeur du worker
     */
    public DbWorker() {
    }

    @Override
    public void connecterBdMySQL(String nomDB) throws MyDBException {
        final String url_local = "jdbc:mysql://localhost:3306/" + nomDB;
        final String url_remote = "jdbc:mysql://172.23.85.187:3306/" + nomDB;
        final String user = "223";
        final String password = "emf123";

        System.out.println("url:" + url_remote);
        try {
            dbConnexion = DriverManager.getConnection(url_remote, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdHSQLDB(String nomDB) throws MyDBException {
        final String url = "jdbc:hsqldb:file:" + nomDB + ";shutdown=true";
        final String user = "SA";
        final String password = "";
        System.out.println("url:" + url);
        try {
            dbConnexion = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void connecterBdAccess(String nomDB) throws MyDBException {
        final String url = "jdbc:ucanaccess://" + nomDB;
        System.out.println("url=" + url);
        try {
            dbConnexion = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public void deconnecter() throws MyDBException {
        try {
            if (dbConnexion != null) {
                dbConnexion.close();
            }
        } catch (SQLException ex) {
            throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
        }
    }

    @Override
    public List<Personne> lirePersonnes() throws MyDBException {
       ArrayList<Personne> listePersonnes = new ArrayList<>();
        try {
            Statement st = dbConnexion.createStatement();
            ResultSet rs = st.executeQuery("select PK_PERS, Prenom, Nom, Date_naissance, No_rue, Rue, NPA, Ville, Actif, Salaire, date_modif, no_modif from t_personne");
            while (rs.next()) {
                String nom = rs.getString("NOM");
                String prenom = rs.getString(2);
                int pk = rs.getInt("PK_PERS");
                listePersonnes.add(new Personne(nom, prenom));
            }
        } catch (SQLException ex) {
        }
        return listePersonnes;
    }
    @Override
    public void creer(Personne personne) throws MyDBException {
    }

    @Override
    public void effacer(Personne personne) throws MyDBException {
    }

    @Override
    public void modifier(Personne personne) throws MyDBException {
    }

    @Override
    public Personne lire(int lire) throws MyDBException {
    }

}
