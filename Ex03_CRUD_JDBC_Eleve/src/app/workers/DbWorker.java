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
            ResultSet rs = st.executeQuery("select PK_PERS, Prenom, Nom, Date_naissance, No_rue, Rue, NPA, Ville, Actif, Salaire, date_modif from t_personne");
            while (rs.next()) {
                int pk_pers = rs.getInt("PK_PERS");
                String nom = rs.getString("Nom");
                String prenom = rs.getString("Prenom");
                Date date_naissance = rs.getDate("Date_naissance");
                int no_rue = rs.getInt("No_rue");
                String rue = rs.getString("Rue");
                int npa = rs.getInt("NPA");
                String ville = rs.getString("Ville");
                boolean actif = rs.getByte("Actif") == 1;
                double salaire = rs.getDouble("Salaire");
                try {
                    java.util.Date date_modif = new java.util.Date(rs.getDate("date_modif").getTime());
                    listePersonnes.add(new Personne(pk_pers, nom, prenom, date_naissance, no_rue, rue, npa, ville, actif, salaire, date_modif));
                } catch (Exception e) {
                }

            }
        } catch (SQLException ex) {
        }
        return listePersonnes;
    }

    @Override
    public void creer(Personne p) throws MyDBException {
        if (p != null) {
            try {
                PreparedStatement st = dbConnexion.prepareStatement("INSERT INTO t_personne(Prenom, Nom, Date_naissance, No_rue, Rue, NPA, Ville, Actif, Salaire, date_modif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                st.setString(1, p.getPrenom());
                st.setString(2, p.getNom());
                st.setDate(3, new java.sql.Date(p.getDateNaissance().getTime()));
                st.setInt(4, p.getNoRue());
                st.setString(5, p.getRue());
                st.setInt(6, p.getNpa());
                st.setString(7, p.getLocalite());
                st.setByte(8, p.isActif() ? (byte) 1 : (byte) 0);
                st.setDouble(9, p.getSalaire());
                st.setTimestamp(10, new Timestamp(p.getDateModif().getTime()));
                int nb = st.executeUpdate();
                if (nb != 1) {
                    System.out.println("erreur d'ajout");
                } else {
                    System.out.println("ajout effectué");
                }
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void effacer(Personne p) throws MyDBException {
        if (p != null) {
            try {
                PreparedStatement st = dbConnexion.prepareStatement("DELETE FROM t_personne WHERE PK_PERS=" + p.getPkPers());
                int nb = st.executeUpdate();
                if (nb != 1) {
                    System.out.println("erreur de suppression");
                } else {
                    System.out.println("suppression effectué");
                }
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void modifier(Personne p) throws MyDBException {
        if (p != null) {
            try {
                PreparedStatement st = dbConnexion.prepareStatement(
                        "UPDATE t_personne SET Prenom=?, Nom=?, Date_naissance=?, No_rue=?, Rue=?, NPA=?, Ville=?, Actif=?, Salaire=?, date_modif=? where PK_PERS=?;");
                st.setString(1, p.getPrenom());
                st.setString(2, p.getNom());
                st.setDate(3, new java.sql.Date(p.getDateNaissance().getTime()));
                st.setInt(4, p.getNoRue());
                st.setString(5, p.getRue());
                st.setInt(6, p.getNpa());
                st.setString(7, p.getLocalite());
                st.setByte(8, p.isActif() ? (byte) 1 : (byte) 0);
                st.setDouble(9, p.getSalaire());
                st.setTimestamp(10, new Timestamp(p.getDateModif().getTime()));
                st.setInt(11, p.getPkPers());
                int nb = st.executeUpdate();
                if (nb != 1) {
                    System.out.println("erreur de mise à jour");
                } else {
                    System.out.println("mise à jour effectué");
                }
            } catch (Exception e) {
            }
        }

    }

    @Override
    public Personne lire(int PK) throws MyDBException {
        return null;
    }

}
