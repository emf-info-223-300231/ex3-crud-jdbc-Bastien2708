package app.presentation;

import app.beans.Personne;
import app.exceptions.MyDBException;
import app.helpers.JfxPopup;
import app.helpers.SystemLib;
import app.workers.DbWorker;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import java.io.File;
import app.workers.DbWorkerItf;
import app.workers.PersonneManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.util.converter.LocalDateTimeStringConverter;

/**
 *
 * @author PA/STT
 */
public class MainCtrl implements Initializable {

    // DBs à tester
    private enum TypesDB {
        MYSQL, HSQLDB, ACCESS
    };

    // DB par défaut
    final static private TypesDB DB_TYPE = TypesDB.MYSQL;

    private PersonneManager manPers;
    private DbWorkerItf dbWrk;
    private boolean modeAjout;

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtPK;
    @FXML
    private TextField txtNo;
    @FXML
    private TextField txtRue;
    @FXML
    private TextField txtNPA;
    @FXML
    private TextField txtLocalite;
    @FXML
    private TextField txtSalaire;
    @FXML
    private CheckBox ckbActif;
    @FXML
    private Button btnDebut;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnEnd;
    @FXML
    private Button btnSauver;
    @FXML
    private Button btnAnnuler;
    @FXML
    private DatePicker dateNaissance;

    /*
   * METHODES NECESSAIRES A LA VUE
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbWrk = new DbWorker();
        manPers = new PersonneManager();
        ouvrirDB();
    }

    @FXML
    public void actionPrevious(ActionEvent event) {
        try {
            afficherPersonne(manPers.precedentPersonne());
        } catch (Exception ex) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
        }
    }

    @FXML
    public void actionNext(ActionEvent event) {
        try {
            afficherPersonne(manPers.suivantPersonne());
        } catch (Exception ex) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
        }
    }

    @FXML
    private void actionEnd(ActionEvent event) {
        try {
            afficherPersonne(manPers.finPersonne());
        } catch (Exception e) {
        }
    }

    @FXML
    private void debut(ActionEvent event) {
        try {
            afficherPersonne(manPers.debutPersonne());
        } catch (Exception e) {
        }
    }

    @FXML
    private void menuAjouter(ActionEvent event) {
        rendreVisibleBoutonsDepl(false);
        effacerContenuChamps();
        modeAjout = true;
    }

    @FXML
    private void menuModifier(ActionEvent event) {
        rendreVisibleBoutonsDepl(false);
        modeAjout = false;
    }

    @FXML
    private void menuEffacer(ActionEvent event) {
        try {
            dbWrk.effacer(manPers.courantPersonne());
            btnAnnuler.setVisible(true);
            btnSauver.setVisible(true);
            manPers.setPersonne(dbWrk.lirePersonnes());
            afficherPersonne(manPers.debutPersonne());

        } catch (MyDBException ex) {
            Logger.getLogger(MainCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void menuQuitter(ActionEvent event) {
    }

    @FXML
    private void annulerPersonne(ActionEvent event) {
        rendreVisibleBoutonsDepl(true);
    }

    @FXML
    private void sauverPersonne() throws MyDBException {
        if (modeAjout) {
            try {
                dbWrk.creer(
                        new Personne(txtNom.getText(),
                                txtPrenom.getText(),
                                java.sql.Date.valueOf(dateNaissance.getValue()),
                                Integer.valueOf(txtNo.getText()),
                                txtRue.getText(), Integer.valueOf(txtNPA.getText()),
                                txtLocalite.getText(),
                                ckbActif.isSelected(),
                                Double.valueOf(txtSalaire.getText()),
                                new java.util.Date()));
            } catch (MyDBException ex) {
                Logger.getLogger(MainCtrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                dbWrk.modifier(
                        new Personne(Integer.valueOf(txtPK.getText()),txtNom.getText(),
                                txtPrenom.getText(),
                                java.sql.Date.valueOf(dateNaissance.getValue()),
                                Integer.valueOf(txtNo.getText()),
                                txtRue.getText(), Integer.valueOf(txtNPA.getText()),
                                txtLocalite.getText(),
                                ckbActif.isSelected(),
                                Double.valueOf(txtSalaire.getText()),
                                new java.util.Date()));
            } catch (MyDBException ex) {
                throw new MyDBException(SystemLib.getFullMethodName(), ex.getMessage());
            }
        }
        try {
            manPers.setPersonne(dbWrk.lirePersonnes());
        } catch (MyDBException ex) {
            Logger.getLogger(MainCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        afficherPersonne(manPers.debutPersonne());
    }

    public void quitter() {
        try {
            dbWrk.deconnecter(); // ne pas oublier !!!
        } catch (MyDBException ex) {
            System.out.println(ex.getMessage());
        }
        Platform.exit();
    }

    public LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

    /*
   * METHODES PRIVEES 
     */
    private void afficherPersonne(Personne p) {
        if (p != null) {
            txtPK.setText(String.valueOf(p.getPkPers()));
            txtPrenom.setText(p.getPrenom());
            txtNom.setText(p.getNom());
            txtNPA.setText(String.valueOf(p.getNpa()));
            txtLocalite.setText(p.getLocalite());
            txtRue.setText(p.getRue());
            txtNo.setText(String.valueOf(p.getNoRue()));
            txtSalaire.setText(String.valueOf(p.getSalaire()));
            dateNaissance.setValue(convertToLocalDateViaMilisecond(p.getDateNaissance()));
            ckbActif.setSelected(p.isActif());
        }
    }

    private void ouvrirDB() {
        try {
            switch (DB_TYPE) {
                case MYSQL:
                    dbWrk.connecterBdMySQL("223_personne_1table");
                    break;
                case HSQLDB:
                    dbWrk.connecterBdHSQLDB("../data" + File.separator + "223_personne_1table");
                    break;
                case ACCESS:
                    dbWrk.connecterBdAccess("../data" + File.separator + "223_Personne_1table.accdb");
                    break;
                default:
                    System.out.println("Base de données pas définie");
            }
            System.out.println("------- DB OK ----------");
            manPers.setPersonne(dbWrk.lirePersonnes());
            afficherPersonne(manPers.precedentPersonne());
        } catch (MyDBException ex) {
            JfxPopup.displayError("ERREUR", "Une erreur s'est produite", ex.getMessage());
            System.exit(1);
        }
    }

    private void rendreVisibleBoutonsDepl(boolean b) {
        btnDebut.setVisible(b);
        btnPrevious.setVisible(b);
        btnNext.setVisible(b);
        btnEnd.setVisible(b);
        btnAnnuler.setVisible(!b);
        btnSauver.setVisible(!b);
    }

    private void effacerContenuChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtPK.setText("");
        txtNo.setText("");
        txtRue.setText("");
        txtNPA.setText("");
        txtLocalite.setText("");
        txtSalaire.setText("");
        ckbActif.setSelected(false);
        dateNaissance.setValue(convertToLocalDateViaMilisecond(new Date(0)));
    }

}
