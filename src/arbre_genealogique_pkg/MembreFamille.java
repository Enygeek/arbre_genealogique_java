
package arbre_genealogique_pkg;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Classe de membre de la famille qui permet la construction d'un seul membre de la famille.
 * Chaque membre est alors logé dans sa propre classe
 * @author aklam
 */
public class MembreFamille implements Serializable{


    @Override
    public String toString() {
        //affiche une belle représentation en chaîne d'une personne. le () signifie qu'ils ont
        //un nom de jeune fille et il utilise les symboles de genre pour les identifier
        String s = null;
        if (this.genre == Genre.MASCULIN){
            s = "♂ ";
        }else if (this.genre == Genre.FEMININ){
            s = "♀ ";
        }
        s += this.getPrenoms() + " " + this.getNom();
        if (this.has(Attribut.NOMDEBASEDELAMARIEE)){
            s += " (" + this.getNomDeBaseDeLaMariee() + ")";
        }
        return s;
    }

    /**
     * Construit un arbre généalogique en utilisant les méthodes internes de l'ensemble. Cela permet
     * une validation dans un emplacement central, ce qui rend tout objet construit de membre de famille valide
     * @param prenoms
     * @param nom
     * @param genre
     */
    public MembreFamille(String prenoms, String nom, Genre genre) {
        this.setPrenoms(prenoms);
        this.setNom(nom);
        this.nomDeBaseDeLaMariee = "";
        this.setGenre(genre);


        this.mere = null;
        this.pere = null;
        this.conjoint = null;
        this.enfants = new LinkedList<>();
        this.freresSoeurs = new LinkedList<>();

    }
    private String prenoms;
    private String nom;
    private String nomDeBaseDeLaMariee;
    private Genre genre;
    //regex pour correspondre à un nom valide. permet tout caractère unicode avec certains
    // des cas particuliers tels que Aklam Moses Crack. ou L'ourve D'Marche
    private final String nomRegex = "^[\\p{L} .'-]+$";

    private MembreFamille mere;
    private MembreFamille pere;
    private MembreFamille conjoint;
    private LinkedList<MembreFamille> enfants;
    private LinkedList<MembreFamille> freresSoeurs;

    /**
     * Types d'attributs utilisés pour vérifier si un membre de la famille possède l'un de ces attributs
     */
    public enum Attribut {
        PERE,
        MERE,
        ENFANTS,
        CONJOINT,
        NOMDEBASEDELAMARIEE,
        PARENTS,
        FRERESSOEURS;

    }

    /**
     * Types relatifs utilisés pour ajouter des parents à un membre de la famille
     */
    public enum LienDeParente {
        PERE,
        MERE,
        ENFANT,
        CONJOINT,
        FRERESOEUR;

    }

    /**
     * Les types de genre pour assurer deux genres seulement
     */
    public enum Genre {
        MASCULIN,
        FEMININ,
    }
    /**
     * @return le prenom
     */
    public String getPrenoms() {
        return prenoms;
    }

    /**
     * @param prenoms
     */
    public final void setPrenoms(String prenoms) {
        if (prenoms.trim().matches(nomRegex)) {
            this.prenoms = prenoms.trim();
        }else{
            throw new IllegalArgumentException("Prenoms invalides");
        }

    }

    /**
     * @return the lastName
     */
    public String getNom() {
        return nom;
    }

    /**
     *fixe le nom et verifie s'il est valide avec le nomRegex
     * @param nom
     */
    public final void setNom(String nom) {
        if (nom.trim().matches(nomRegex)) {
            this.nom = nom.trim();
        }else{
            throw new IllegalArgumentException("Nom invalide");
        }
    }

    /**
     * @return le nom de base de la mariee
     */
    public String getNomDeBaseDeLaMariee() {
        return nomDeBaseDeLaMariee;
    }

    /**
     * veille à ce que seules les femmes aient un nom de jeune fille et le valide
     * @param nomDeBaseDeLaMariee
     */
    public void setNomDeBaseDeLaMariee(String nomDeBaseDeLaMariee) {
        if (nomDeBaseDeLaMariee.trim().matches(nomRegex)) {
            if (this.genre == Genre.FEMININ){
                this.nomDeBaseDeLaMariee = nomDeBaseDeLaMariee.trim();
            }else{
                throw new IllegalArgumentException("Le nom de base de la mariee est uniquement pour les femmes");
            }

        }else if (nomDeBaseDeLaMariee.isEmpty()){
            this.nomDeBaseDeLaMariee = "";
        }else{
            throw new IllegalArgumentException("Nom de base nde la mariee invalide");
        }
    }

    /**
     * @return le genre
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * @param genre
     */
    public final void setGenre(Genre genre) {
        this.genre = genre;
    }


    /**
     * ajoute un enfant au membre de la famille. Par conséquent, l'ajout du conjoint et du membre actuel de la famille comme parents, s'ils existent
     * @param enfant l'enfant à ajouter à l'ensemble des enfants
     */
    public void ajouterEnfant(MembreFamille enfant) {
        //Pere
        if (this.genre == Genre.MASCULIN) {
            //si l'enfant n'a pas de père qui le fixe
            if (!enfant.has(Attribut.PERE)) {
                enfant.setPere(this);
            }
            if (!enfant.has(Attribut.FRERESSOEURS)) {
                enfant.setFreresSoeurs(this.getFreresSoeurs());
            }
            //si le membre de la famille a un conjoint qui le définit comme la mère
            if (this.has(Attribut.CONJOINT)) {
                if (!enfant.has(Attribut.MERE)) {
                    enfant.setMere(this.getConjoint());
                }
            }
            //Mere
        }else if (this.genre == Genre.FEMININ){
            //si l'enfant n'a pas de mère qui le fixe
            if (!enfant.has(Attribut.MERE)) {
                enfant.setMere(this);
            }
            if (!enfant.has(Attribut.FRERESSOEURS)) {
                enfant.setFreresSoeurs(this.getFreresSoeurs());
            }
            //si le membre de la famille a un conjoint qui le définit comme la pere
            if (this.has(Attribut.CONJOINT)) {
                if (!enfant.has(Attribut.PERE)) {
                    enfant.setPere(this.getConjoint());
                }
            }
        }
        //veiller à ne pas dupliquer les objets des enfants
        if(!this.getEnfants().contains(enfant)){
            this.getEnfants().add(enfant);
        }
    }
    /**
     * ajoute un un frere ou une soeur  au membre de la famille. Par conséquent, l'ajout du conjoint et du membre actuel de la famille comme parents, s'ils existent
     * @param frereSoeur l'enfant à ajouter à l'ensemble des frereSoeur
     */
    public void ajouterFrereSoeur(MembreFamille frereSoeur) {

        if(this.genre == Genre.FEMININ || this.genre == Genre.MASCULIN ){
            this.getFreresSoeurs().add(frereSoeur);
        }
    }

    /**
     * retuns le nombre d'enfants pour ce membre
     * @return
     */

    public int nombreEnfants(){
        return this.getEnfants().size();
    }

    /**
     * retuns le nombre de freresSoeurs pour ce membre
     * @return
     */

    public int nombreFreresSoeurs(){
        return this.getFreresSoeurs().size();
    }

    /**
     * @return la mere
     */
    public MembreFamille getMere() {
        return mere;
    }

    /**
     * fixe la mère et s'assure qu'il s'agit d'une femme. il ajoute également le membre actuel en tant qu'enfant à la mère
     * Un membre ne peut avoir qu'une seule mère
     * @param mere
     */
    public void setMere(MembreFamille mere) {
        if (!this.has(Attribut.MERE)) {
            if (mere.getGenre() == Genre.FEMININ) {
                if (!mere.getEnfants().contains(this)){
                    mere.getEnfants().add(this);
                }
                this.mere = mere;

                if(!mere.getFreresSoeurs().contains(this)) {
                        mere.getFreresSoeurs().add(this);
                }
                this.mere = mere;







            }else{
                throw new IllegalArgumentException("Une mere ne peut etre que de genre feminin");
            }

        }else {
            throw new IllegalArgumentException("Mere deja ajouté");
        }

    }

    /**
     * @return le pere
     */
    public MembreFamille getPere() {
        return pere;
    }

    /**
     * fixe le père et s'assure qu'il s'agit d'un homme. il ajoute également le membre actuel en tant qu'enfant du père
     * Un membre ne peut avoir qu'un seul père
     * @param pere
     */
    public void setPere(MembreFamille pere) {
        if (!this.has(Attribut.PERE)) {
            if (pere.getGenre() == Genre.MASCULIN) {
                if (!pere.getEnfants().contains(this)){
                    pere.getEnfants().add(this);
                }
                this.pere = pere;


            }else{
                throw new IllegalArgumentException("Un pere ne peut etre que de genre masculin");
            }

        }else{
            throw new IllegalArgumentException("Pere deja ajouté");
        }

    }

    /**
     * @return le conjoint
     */
    public MembreFamille getConjoint() {
        return conjoint;
    }

    /**
     * fixe le conjoint du membre. un conjoint doit être du sexe opposé et un membre ne peut avoir qu'un seul conjoint
     * @param conjoint
     */
    public void setConjoint(MembreFamille conjoint) {
        if (!this.has(Attribut.CONJOINT)) {
            if(conjoint.getGenre() != this.getGenre()){
                conjoint.setEnfants(this.getEnfants());
                conjoint.setFreresSoeurs(this.getFreresSoeurs());
                this.conjoint = conjoint;
                if (!this.getConjoint().has(Attribut.CONJOINT)) {
                    conjoint.setConjoint(this);
                }

            }else{
                throw new IllegalArgumentException("Le conjoint doit etre du sexe opposé de son conjoint");
            }
        }else{
            throw new IllegalArgumentException("Le conjoint existe déjà");
        }

    }

    /**
     * @return les enfants
     */
    public LinkedList<MembreFamille> getEnfants() {
        return enfants;
    }

    /**
     * @return les freresSoeurs
     */
    public LinkedList<MembreFamille> getFreresSoeurs() {
        return freresSoeurs;
    }

    /**
     * @param enfants the children to set
     */
    public void setEnfants(LinkedList<MembreFamille> enfants) {
        this.enfants = enfants;
    }

    /**
     * @param freresSoeurs the children to set
     */
    public void setFreresSoeurs(LinkedList<MembreFamille> freresSoeurs) {
        this.freresSoeurs = freresSoeurs;
    }

    /**
     * checks if the member has a specific type of attribute
     * @param type the attribute type to check
     * @return true si les conditions sont remplies
     */
    public boolean has(Attribut type){
        switch(type){
            case PERE:
                return this.getPere() != null;
            case ENFANTS:
                return !this.getEnfants().isEmpty();
            case FRERESSOEURS:
                return !this.getFreresSoeurs().isEmpty();
            case MERE:
                return this.getMere() != null;
            case CONJOINT:
                return this.getConjoint() != null;
            case NOMDEBASEDELAMARIEE:
                return !this.getNomDeBaseDeLaMariee().isEmpty();
            case PARENTS:
                return this.has(Attribut.PERE) || this.has(Attribut.MERE);
        }
        return false;
    }

    /**
     * ajoute un relatif basé sur la variable type spécifiée. Il s'agit essentiellement d'une méthode de commodité
     * @param type the type of the added membre
     * @param membre the membre to add
     */
    public void ajouterLienRelatif(LienDeParente type, MembreFamille membre){
        switch(type){
            case PERE:
                this.setPere(membre);
                return;
            case ENFANT:
                this.ajouterEnfant(membre);
                return;
            case FRERESOEUR:
                this.ajouterFrereSoeur(membre);
            case MERE:
                this.setMere(membre);
                return;
            case CONJOINT:
                this.setConjoint(membre);
                return;
        }
    }

}
