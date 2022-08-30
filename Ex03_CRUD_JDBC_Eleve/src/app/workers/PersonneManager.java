/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app.workers;

import app.beans.Personne;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BussardB
 */
public class PersonneManager {

    private int index = 0;
    private List<Personne> listePersonnes;

    public PersonneManager() {
      listePersonnes = new ArrayList<>();
    }

    public Personne courantPersonne() {
        return !listePersonnes.isEmpty() ? listePersonnes.get(index) : null;
    }

    public Personne debutPersonne() {
        return listePersonnes.get(index = 0);
    }

    public Personne finPersonne() {
        return listePersonnes.get(index = listePersonnes.size()-1);
    }

    public Personne precedentPersonne() {
        return !listePersonnes.isEmpty() ? listePersonnes.get(index > 0 ? --index : index) : null;
    }

    public void setPersonne(List<Personne> listPersonne) {
        this.listePersonnes = listPersonne;
    }

    public Personne suivantPersonne() {
        return listePersonnes.get(index < listePersonnes.size()-1 ? ++index : index);
    }
}
