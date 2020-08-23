package arbre_genealogique_pkg;

import java.io.Serializable;

/**
 * this family tree class acts as an implementation of the family member class
 * @author Taaqif
 */
public class ArbreGenealogique implements Serializable{
    //needed to verion control the serialised files
    private static final long serialVersionUID = 1;

    /**
     * constructs the family tree by setting the root to null
     */
    public ArbreGenealogique() {
        this.root = null;
    }

    private MembreFamille root;

    /**
     * sets the root
     * @param newRoot
     */
    public void setRoot(MembreFamille newRoot){
        this.root = newRoot;
    }

    /**
     *
     * @return
     */
    public boolean hasRoot(){
        return this.root !=null;
    }

    /**
     *
     * @return
     */
    public MembreFamille getRoot(){
        return this.root;
    }
}
