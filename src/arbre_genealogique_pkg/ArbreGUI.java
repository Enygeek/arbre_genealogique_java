package arbre_genealogique_pkg;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.*;

/**
 * Classe d'interface graphique de l'arbre pour implémenter les objets de l'arbre familial et des membres de la famille.
 *  * Elle permet d'interagir avec les objets de l'arbre familial
 *  * L'interface graphique est divisée en 4 sections principales
 *  * o La barre de menu
 *  * - contient les options de la barre de menu
 *  * o Le panneau d'en-tête
 *  * - contient les boutons de chargement, de sauvegarde et de création d'une nouvelle arborescence
 *  * o Le panneau de contrôle
 *  * - contient la représentation jTree de l'objet arbre et du panneau de détails
 *  * qui contient les informations sur le membre actuel ou les formulaires d'ajout et de modification
 *  * o Le panel de statut
 *  * - contient le message de statut
 * Hypothèses:
 *  Il y a des classes FamilyTree et FamilyMember
 *  * L'utilisateur interagit avec ce programme à l'aide d'une souris et d'un clavier
 *  * Le Français est la seule langue prise en charge
 * @author aklam
 */
public class ArbreGUI {

    /**
     * Crée et met en place l'interface gui ainsi qu'initialise toutes les variables
     */
    public ArbreGUI() {

        arbreDeLaFamilleActuelle = new ArbreGenealogique();
        fichierActuel = null;
        arbre = new JTree();
        creerGUI();
    }
    private JFrame mainFrame;
    private JPanel controlPanel;
    private JPanel infoPanel;
    private final JLabel statusLabel = new JLabel("Programme chargé");
    private File fichierActuel;
    private JTree arbre;

    private ArbreGenealogique arbreDeLaFamilleActuelle;

    /**
     * Appelle les fonctions d'initialisation pour mettre en place tous les différents panels
     */
    private void creerGUI() {

        mainFrame = new JFrame("Arbre Généalogique");
        mainFrame.setSize(1200, 800);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().setBackground(Color.darkGray);
        //ne rien faire de trop près pour donner à l'utilisateur la possibilité de sauvegarder son travail avant de quitter
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        //met en place la barre de menu
        initMenuBar();

        //met en place la section d'en-tête
        initHeaderPanel();

        //met en place la section de contrôle (partie principale où les données sont affichées)
        initControlPanel();

        //met en place la barre de statut
        initStatusBar();

        //affiche l'arbre vide
        afficherArbre(arbreDeLaFamilleActuelle);

        //vérifier si l'utilisateur souhaite continuer à utiliser la fonction checkUserCOntinue
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (checkUserContinue()) {
                    System.exit(0);
                }
            }
        });

        mainFrame.setVisible(true);
    }

    /**
     * Initializes the header panel
     */
    private void initHeaderPanel() {

        JLabel headerLabel = new JLabel("Bienvenue dans notre application de construction d'Arbre Généalogique !", JLabel.CENTER);
        headerLabel.setFont(new Font("Serif", Font.PLAIN, 28));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setForeground(Color.white);

        JButton ouvrir = new JButton("Charger Arbre");
        ouvrir.addActionListener(new ouvrirAction());

        JButton creer = new JButton("Creer un nouvel arbre");
        creer.addActionListener(new ActionCreerArbre());

        JButton sauvegarderArbre = new JButton("Sauvegarder arbre");
        sauvegarderArbre.addActionListener(new sauvegarderAction());

        JPanel headPanel = new JPanel();
        headPanel.setLayout(new GridBagLayout());
        headPanel.setOpaque(false);
        headPanel.setBorder(new EmptyBorder(10,10,10,10));

        //utilisant une grille pour positionner chaque élément
        //les contraintes de la poche de grille précisent où l'élément ira à l'intérieur de la grille
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        headPanel.add(headerLabel, gbc);

        //la disposition des boutons (l'un à côté de l'autre)
        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container.setOpaque(false);
        container.add(ouvrir);
        container.add(sauvegarderArbre);
        container.add(creer);

        gbc.gridx = 0;
        gbc.gridy = 1;
        headPanel.add(container, gbc);

        mainFrame.add(headPanel, BorderLayout.NORTH);
    }

    /**
     * Initialise le panneau de contrôle où sont affichées la plupart des données
     */
    private void initControlPanel() {
        controlPanel = new JPanel();

        //utilisé pour montrer le fond blanc de mainFrame
        controlPanel.setOpaque(false);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        mainFrame.add(controlPanel, BorderLayout.CENTER);
    }

    /**
     * Initialiser la barre de menu qui contient des actions de menu telles que sauvegarder nouveau chargement et quitter
     */
    private void initMenuBar() {
        JMenuBar menuBar;
        menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        JMenu fichierMenu = new JMenu("Fichier");
//        JMenu editMenu = new JMenu("Edit");
        menuBar.add(fichierMenu);
//        menuBar.add(editMenu);

        JMenuItem nouvelleAction = new JMenuItem("Nouveau");
        fichierMenu.add(nouvelleAction);
        nouvelleAction.addActionListener(new ActionCreerArbre());

        JMenuItem ouvrirAction = new JMenuItem("Ouvrir");
        fichierMenu.add(ouvrirAction);
        ouvrirAction.addActionListener(new ouvrirAction());

        fichierMenu.addSeparator();

        JMenuItem sauvegarderAction = new JMenuItem("Enregistrer");
        fichierMenu.add(sauvegarderAction);
        sauvegarderAction.addActionListener(new sauvegarderAction());

        JMenuItem sauvegarderCommeAction = new JMenuItem("Enregistrer sous");
        fichierMenu.add(sauvegarderCommeAction);
        sauvegarderCommeAction.addActionListener(new actionEnregistrerSous());


        JMenuItem ActionDeSortie = new JMenuItem("Sortie");
        fichierMenu.addSeparator();
        fichierMenu.add(ActionDeSortie);
        //anonyme, car il n'est pas nécessaire d'avoir une fonction
        //La classe actionlistner
        ActionDeSortie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkUserContinue()) {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Il initialise la barre d'état où les informations telles que les messages sont affichées à l'utilisateur en bas de l'écran
     */
    private void initStatusBar() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        mainFrame.add(statusPanel, BorderLayout.SOUTH);

        //defini la taille du mainframe
        statusPanel.setPreferredSize(new Dimension(mainFrame.getWidth(), 18));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        //aligner le texte à gauche
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        //c'est là que le message de statut sera affiché
        statusPanel.add(statusLabel);
    }

    /**
     * Méthode pratique pour modifier le statut. En principe, le texte de l'étiquette est placé à l'intérieur de la barre d'état
     * @param status le message à afficher
     */
    private void modifierStatus(String status) {
        statusLabel.setText(status);
    }

    /**
     * La classe Action qui implemente ActionListner
     * Utilisé pour afficher la fonction d'ajout d'un parent après avoir cliqué sur un bouton pour un membre de la famille spécifié
     */
    private class ActionAjouterLienRelatif implements ActionListener {

        private MembreFamille membre;
        //parce que nous pouvons appeler actionlistener sur n'importe quel parent. nous devons passer le membre que nous voudrions éditer comme paramètre; ceci attrape alors ce paramètre et fait les actions correctes
        public ActionAjouterLienRelatif(MembreFamille membre) {
            this.membre = membre;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //afficher le formulaire d'ajout relatif pour le membre actuel
            AfficherAjoutLienRelatifInfo(membre);
        }
    }

    /**
     * L'action Modifier le membre qui implemente ActionListner pour afficher le formulaire de modification du membre lorsqu'un bouton est cliqué pour un membre de la famille spécifié
     */
    private class ActionModifierMembre implements ActionListener {

        private MembreFamille membre;
        //parce que nous pouvons appeler actionlistener sur n'importe quel parent. Nous devons passer le membre que nous voudrions éditer comme paramètre ; ceci attrape alors ce paramètre et fait les actions correctes
        public ActionModifierMembre(MembreFamille membre) {
            this.membre = membre;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //affiche le formulaire d'information du membre éditeur
            afficherModificationMembreInfo(membre);
        }
    }

    /**
     *  l'action creer arbre implemente actionlistner pour afficher le formulaire creer arbre pour un membre de la famille spécifié
     */
    private class ActionCreerArbre implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (checkUserContinue()) {
                //vérifier si l'arbre n'est pas sauvegardé et réinitialiser les principales variables
                arbreDeLaFamilleActuelle = new ArbreGenealogique();
                fichierActuel = null;
                //fficher le nouvel arbre (vide)
                afficherArbre(arbreDeLaFamilleActuelle);
                modifierStatus("Creation d'un arbre vierge");
            }

        }
    }

    /**
     * ouvrirAction implemente actionlistner qui invoque une jDialogBox de sorte que l'utilisateur peut sélectionner un fichier à ouvrir dans l'application
     */
    private class ouvrirAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (checkUserContinue()) {
                JFileChooser jFileChooser = new JFileChooser();
                //définir des filtres de fichiers
                jFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Fichier ArbreGenealogique (*.ft)", "ft"));
                jFileChooser.setAcceptAllFileFilterUsed(true);

                int resultat = jFileChooser.showOpenDialog(mainFrame);
                //process jfilechooser resultat
                if (resultat == JFileChooser.APPROVE_OPTION) {
                    try {
                        //essayer d'ouvrir le fichier, afficher l'arbre généalogique
                        ouvrirFichier(jFileChooser.getSelectedFile());
                        afficherArbre(arbreDeLaFamilleActuelle);
                        modifierStatus("Dossier ouvert depuis: " + (jFileChooser.getSelectedFile().getAbsolutePath()));
                    } catch (Exception j) {
                        //error
                        showErrorDialog(j);
                        modifierStatus("Erreur: " + j.getMessage());
                    }
                }
            }

        }
    }

    /**
     * Méthode pratique pour vérifier si l'arbre est chargé. Utilisée pour vérifier si l'utilisateur veut continuer malgré le chargement de l'arbre
     * @return true si l'arbre n'a pas de racine ou si l'utilisateur souhaite continuer
     */
    private boolean checkUserContinue() {
        if (arbreDeLaFamilleActuelle.hasRoot()) {
            int resultatDialog = JOptionPane.showConfirmDialog(mainFrame, "Êtes-vous sûr de vouloir continuer ? Tout changement non sauvegardé sera perdu", "Avertissement", JOptionPane.YES_NO_CANCEL_OPTION);
            return resultatDialog == JOptionPane.YES_OPTION;
        }
        return true;
    }

    /**
     * affiche l'objet arbre généalogique à travers un jTree.
     * @param arbreGenealogique l'arbre généalogique à afficher
     */
    private void afficherArbre(ArbreGenealogique arbreGenealogique) {

        //créer le nœud racine
        DefaultMutableTreeNode main = new DefaultMutableTreeNode("Main");
        //dernier chemin sélectionné pour garder la trace de la dernière personne sélectionnée par l'utilisateur.
        //Utilisé lors de l'ajout ou de l'annulation d'une action
        TreePath dernierNoeudSelectionne = null;

        //nœud d'arbre mutable permettant aux objets de servir de nœuds
        DefaultMutableTreeNode top;

        //aucune donnée chargée dans l'arbre
        if (!arbreGenealogique.hasRoot()) {
            top = new DefaultMutableTreeNode("Aucune donnée d'arbre trouvée.");

        } else {
            //ajouter la personne racine
            top = new DefaultMutableTreeNode(arbreGenealogique.getRoot());
            //appeler la méthode récursive pour peupler l'arbre entier avec tous les détails du membre de la famille racine
            creerArbre(top, arbreGenealogique.getRoot());
            //si l'utilisateur a sélectionné un membre, indiquer le dernier chemin sélectionné
            dernierNoeudSelectionne = arbre.getSelectionPath();

        }
        //Créer l'arbre et permettre une sélection à la fois et cacher le nœud racine
        arbre = new JTree(main);
        main.add(top);
        arbre.setRootVisible(false);
        arbre.setShowsRootHandles(true);
        arbre.setEnabled(true);
        arbre.expandPath(new TreePath(main.getPath()));
        arbre.getSelectionModel().addTreeSelectionListener(new ActionArbreSelectionne());
        arbre.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        arbre.setBorder(new EmptyBorder(0, 10, 0, 10));

        //développer tous les nœuds de l'arbre
        for (int i = 0; i < arbre.getRowCount(); i++) {
            arbre.expandRow(i);
        }

        //disposer d'un moteur de rendu personnalisé pour les nœuds de l'arbre
        // diminuer les nœuds de texte et permettre la sélection des nœuds des objets membres de la famille
        arbre.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {

                DefaultMutableTreeNode noeud = (DefaultMutableTreeNode) value;
                Object noeudInfo = noeud.getUserObject();
                if (noeudInfo instanceof MembreFamille) {
                    setTextNonSelectionColor(Color.BLACK);
                    setBackgroundSelectionColor(Color.LIGHT_GRAY);
                    setTextSelectionColor(Color.BLACK);
                    setBorderSelectionColor(Color.WHITE);
                } else {
                    setTextNonSelectionColor(Color.GRAY);
                    setBackgroundSelectionColor(Color.WHITE);
                    setTextSelectionColor(Color.GRAY);
                    setBorderSelectionColor(Color.WHITE);
                }
                setLeafIcon(null);
                setClosedIcon(null);
                setOpenIcon(null);
                super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
                return this;
            }
        });

        //ajouter l'arbre à une scrolepane pour que l'utilisateur puisse faire défiler
        JScrollPane treeScrollPane = new JScrollPane(arbre);
        treeScrollPane.setPreferredSize(new Dimension(250, 0));

        //créer le panneau d'information à afficher dans le panneau de contrôle
        infoPanel = new JPanel();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel promptInfo;
        JButton ajouterNouvelleRacine = new JButton("Ajouter la personne racine");
        ajouterNouvelleRacine.addActionListener(new ActionAjouterLienRelatif(null));
        if (!arbreGenealogique.hasRoot()) {
            promptInfo = new JLabel("<html>Charger un arbre ou ajouter une nouvelle personne racine</html>");
            infoPanel.add(ajouterNouvelleRacine);
        } else {
            promptInfo = new JLabel("<html>Sélectionnez un membre de la famille pour afficher les informations</html>");
        }

        promptInfo.setFont(new Font("SansSerif", Font.PLAIN, 20));
        infoPanel.add(promptInfo, BorderLayout.NORTH);
        infoPanel.setOpaque(false);

        controlPanel.removeAll();

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        container.setOpaque(false);
        controlPanel.add(container);

        container.setLayout(new BorderLayout());
        container.add(treeScrollPane, BorderLayout.WEST);
        container.add(infoPanel, BorderLayout.CENTER);

        controlPanel.add(container);
        controlPanel.validate();
        controlPanel.repaint();

        //faire défiler l'arbre jusqu'au dernier chemin sélectionné
        arbre.setSelectionPath(dernierNoeudSelectionne);
    }

    /**
     * annule le montage en retournant au formulaire d'information des membres
     */
    private class actionAnnulerModificationMembre implements ActionListener {

        MembreFamille membre;

        public actionAnnulerModificationMembre(MembreFamille membre) {
            this.membre = membre;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            affichierMembreInfo(membre);
            modifierStatus("Action annulée");
        }
    }

    /**
     * s'il existe un fichier, invite à écraser le fichier sauvegardé. Sinon, lancez l'action "Enregistrer sous".
     */
    private class sauvegarderAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (fichierActuel != null) {
                    int resultatDialog = JOptionPane.showConfirmDialog(mainFrame, "Souhaitez-vous remplacer l'arbre actuel ?", "Avertissement", JOptionPane.YES_NO_OPTION);
                    if (resultatDialog == JOptionPane.YES_OPTION) {
                        //sauvegarder le fichier
                        sauvegarderDansFichier(fichierActuel);
                        modifierStatus("Fichier enregistré dans: " + fichierActuel.getPath());
                    }
                } else {
                    modifierStatus("Fichier non chargé");
                        //sauver à la place
                    ActionListener listner = new actionEnregistrerSous();
                    listner.actionPerformed(e);

                }

            } catch (Exception j) {
                showErrorDialog(j);
                modifierStatus("Erreur: "+ j.getMessage());
            }
        }
    }

    /**
     * enregistrer l'arbre actuel dans un autre fichier
     */
    private class actionEnregistrerSous implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser jFileChooser = new JFileChooser() {
                //check if file already exists, as to overwrite
                @Override
                public void approveSelection() {
                    File selectedFile = getSelectedFile();
                    if (selectedFile.exists() && getDialogType() == SAVE_DIALOG) {
                        int result = JOptionPane.showConfirmDialog(this, "Le fichier existe, voulez-vous l'écraser?", "Fichier existant", JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (result) {
                            case JOptionPane.YES_OPTION:
                                super.approveSelection();
                                return;
                            case JOptionPane.NO_OPTION:
                                return;
                            case JOptionPane.CLOSED_OPTION:
                                return;
                            case JOptionPane.CANCEL_OPTION:
                                cancelSelection();
                                return;
                        }
                    }
                    super.approveSelection();
                }
            };
            jFileChooser.setSelectedFile(new File("Arbre Genealogique.ft"));
            //Définir un filtre d'extension, afin que l'utilisateur voit les autres fichiers ft
            jFileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers d'Abres Genealogiques (*.ft)", "ft"));
            //invite à sauvegarder
            int resultat = jFileChooser.showSaveDialog(mainFrame);
            if (resultat == JFileChooser.APPROVE_OPTION) {
                try {
                    String nomFichier = jFileChooser.getSelectedFile().toString();
                    if (!nomFichier.endsWith(".ft")) {
                        nomFichier += ".ft";
                    }
                    File file = new File(nomFichier);

                    sauvegarderDansFichier(file);
                    afficherArbre(arbreDeLaFamilleActuelle);
                    modifierStatus("Fichier enregistré dans: " + (file.getAbsolutePath()));
                } catch (Exception j) {
                    showErrorDialog(j);
                    modifierStatus("Erreur: "+ j.getMessage());
                }
            }
        }
    }

    /**
     * action invoquée lorsque l'utilisateur sélectionne un nœud de l'arbre
     */
    private class ActionArbreSelectionne implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent event) {
            DefaultMutableTreeNode noeud = (DefaultMutableTreeNode) arbre.getLastSelectedPathComponent();

            //pas de selection
            if (noeud == null) {
                return;
            }

            //si la sélection est un objet de membre de la famille
            Object noeudInfo = noeud.getUserObject();
            if (noeudInfo instanceof MembreFamille) {
                //afficher details
                affichierMembreInfo((MembreFamille) noeudInfo);
                modifierStatus("Afficher les détails pour: " + ((MembreFamille) noeudInfo));
            }
        }
    }

    /**
     * Enregistre l'objet dans un fichier en utilisant la sérialisation
     * @param fichier le fichier à sauvegarder dans
     */
    private void sauvegarderDansFichier(File fichier) {
        // enregistrer l'objet dans le fichier
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            //définir les flux de sortie
            fos = new FileOutputStream(fichier);
            out = new ObjectOutputStream(fos);
            //écrire l'objet dans le fichier
            out.writeObject(this.arbreDeLaFamilleActuelle);

            out.close();
            fichierActuel = fichier;
        } catch (Exception ex) {
            throw new IllegalArgumentException("File could not be saved");
        }
    }

    /**
     * Ouvre un fichier et charge les données dans les variables existantes
     * @param fichier le fichier à ouvrir
     */
    private void ouvrirFichier(File fichier) {
        // lire l'objet à partir du fichier
        FileInputStream fis = null;
        ObjectInputStream in = null;
        ArbreGenealogique ft = null;
        try {
            //définir les flux d'entrée
            fis = new FileInputStream(fichier);
            in = new ObjectInputStream(fis);

            //essayer d'attribuer l'objet
            ft = (ArbreGenealogique) in.readObject();
            in.close();

            arbreDeLaFamilleActuelle.setRoot(ft.getRoot());
            fichierActuel = fichier;
            arbre = new JTree();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Le fichier n'a pas pu etre lu.");
        }

    }

    /**
     * Affiche les coordonnées d'un membre spécifique
     * @param membre les coordonnées du membre à afficher
     */
    private void affichierMembreInfo(MembreFamille membre) {
        arbre.setEnabled(true);

//réinitialiser le panneau d'information
        infoPanel.removeAll();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Créer la mise en page de la grille pour les composants
        infoPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel container = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        infoPanel.add(container, gbc);

        //définir une autre mise en page pour les détails
        GroupLayout layout = new GroupLayout(container);
        container.setLayout(layout);
        //écarts dynamiques
        layout.setAutoCreateGaps(true);

        //les éléments de forme, éventuellement divisés en fonctions distinctes
        JLabel membreInfoLabel = new JLabel("Info Personne: ");
        membreInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel nomLabel = new JLabel("Nom");
        JLabel nomTextField = new JLabel(membre.getNom(), 10);
        JLabel prenomsLabel = new JLabel("Prenoms");
        JLabel prenomsTextField = new JLabel(membre.getPrenoms(), 10);
        JLabel nomDeBaseDeLaMarieeLabel = new JLabel("Nom de base de la mariée");
        JLabel nomDeBaseDeLaMarieeTextField = new JLabel();
        if (membre.has(MembreFamille.Attribut.NOMDEBASEDELAMARIEE)){
            nomDeBaseDeLaMarieeTextField.setText(membre.getNomDeBaseDeLaMariee());
        } else {
            nomDeBaseDeLaMarieeTextField.setText("-");
        }

        JLabel genreLabel = new JLabel("Genre");
        JLabel genreComboBox = new JLabel(membre.getGenre().toString());


        JLabel lienRelatifInfoLabel = new JLabel("Lien Relatif Info: ");
        lienRelatifInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

        JLabel pereLabel = new JLabel("Pere");
        JLabel pereTextField = new JLabel();
        if (membre.has(MembreFamille.Attribut.PERE)) {
            pereTextField.setText(membre.getPere().toString());
        } else {
            pereTextField.setText("Pas de père dans le dossier");
        }
        JLabel mereLabel = new JLabel("Mere");
        JLabel mereTextField = new JLabel();
        if (membre.has(MembreFamille.Attribut.MERE)) {
            mereTextField.setText(membre.getMere().toString());
        } else {
            mereTextField.setText("Pas de mère dans le dossier");
        }
        JLabel conjointLabel = new JLabel("Conjoint");
        JLabel conjointTextField = new JLabel();
        if (membre.has(MembreFamille.Attribut.CONJOINT)) {
            conjointTextField.setText(membre.getConjoint().toString());
        } else {
            conjointTextField.setText("Pas de conjoint dans le dossier");
        }

        JLabel enfantsLabel = new JLabel("Enfants");
        String enfants = "<html>";
        if (membre.has(MembreFamille.Attribut.ENFANTS)) {
            for (MembreFamille enfant : membre.getEnfants()) {
                enfants += enfant.toString() + "<br>";
            }
            enfants += "</html>";
        } else {
            enfants = "Pas d'enfants dans le dossier";
        }
        JLabel enfantsTextField = new JLabel(enfants);

        JLabel freresSoeursLabel = new JLabel("freresSoeurs");
        String freresSoeurs = "<html>";
        if (membre.has(MembreFamille.Attribut.FRERESSOEURS)) {
            for (MembreFamille frereSoeur : membre.getFreresSoeurs()) {
                freresSoeurs += frereSoeur.toString() + "<br>";
            }
            freresSoeurs += "</html>";
        } else {
            freresSoeurs = "Pas de freres ou soeurs dans le dossier";
        }
        JLabel freresSoeursTextField = new JLabel(freresSoeurs);

        JLabel petisEnfantsLabel = new JLabel("Petits Enfants");
        String petitsEnfants = "<html>";
        if (membre.has(MembreFamille.Attribut.ENFANTS)) {
            for (MembreFamille enfant : membre.getEnfants()) {
                if (enfant.has(MembreFamille.Attribut.ENFANTS)) {
                    for (MembreFamille petitEnfant : enfant.getEnfants()) {
                        petitsEnfants += petitEnfant.toString() + "<br>";
                    }
                }

            }
            petitsEnfants += "</html>";
        } else {
            petitsEnfants = "Pas de petits enfants dans le dossier";
        }
        JLabel petitsEnfantsTextField = new JLabel(petitsEnfants);
        //

        // Aligner tous les éléments en utilisant la notation de la disposition des groupes
        //Alignement horizontal
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(membreInfoLabel)
                        .addComponent(nomLabel)
                        .addComponent(prenomsLabel)
                        .addComponent(nomDeBaseDeLaMarieeLabel)
                        .addComponent(genreLabel)
                        .addComponent(lienRelatifInfoLabel)
                        .addComponent(pereLabel)
                        .addComponent(mereLabel)
                        .addComponent(conjointLabel)
                        .addComponent(enfantsLabel)
                        .addComponent(freresSoeursLabel)
                        .addComponent(petisEnfantsLabel)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nomTextField)
                        .addComponent(prenomsTextField)
                        .addComponent(nomDeBaseDeLaMarieeTextField)
                        .addComponent(genreComboBox)
                        .addComponent(pereTextField)
                        .addComponent(mereTextField)
                        .addComponent(conjointTextField)
                        .addComponent(enfantsTextField)
                        .addComponent(freresSoeursTextField)
                        .addComponent(petitsEnfantsTextField)
                )
        );

        // Alignement vertical
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(membreInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nomLabel)
                        .addComponent(nomTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(prenomsLabel)
                        .addComponent(prenomsTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nomDeBaseDeLaMarieeLabel)
                        .addComponent(nomDeBaseDeLaMarieeTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genreLabel)
                        .addComponent(genreComboBox))

                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lienRelatifInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(pereLabel)
                        .addComponent(pereTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(mereLabel)
                        .addComponent(mereTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(conjointLabel)
                        .addComponent(conjointTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(enfantsLabel)
                        .addComponent(enfantsTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(freresSoeursLabel)
                        .addComponent(freresSoeursTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(petisEnfantsLabel)
                        .addComponent(petitsEnfantsTextField))
        );

        JButton modifierMembre = new JButton("Modification Details");
        modifierMembre.addActionListener(new ActionModifierMembre(membre));
        JButton ajouterLienRelative = new JButton("Ajouter Lien Relatif");
        ajouterLienRelative.addActionListener(new ActionAjouterLienRelatif(membre));

        JPanel btncontainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btncontainer.add(modifierMembre);
        btncontainer.add(ajouterLienRelative);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(btncontainer, gbc);
        infoPanel.validate();
        infoPanel.repaint();
    }

    /**
     * Affiche le formulaire de membre éditeur
     * @param membre le membre à éditer
     */
    private void afficherModificationMembreInfo(MembreFamille membre) {
        arbre.setEnabled(false);

        //réinitialiser le panneau d'information
        infoPanel.removeAll();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Créer la mise en page
        JPanel info = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        infoPanel.add(info, gbc);
        GroupLayout layout = new GroupLayout(info);
        info.setLayout(layout);
        layout.setAutoCreateGaps(true);

        // Créer les éléments à mettre dans le formulaire
        JLabel membreInfoLabel = new JLabel("Personne Info: ");
        membreInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JLabel nomLabel = new JLabel("Nom");
        JTextField nomTextField = new JTextField(membre.getNom(), 10);
        JLabel prenomsLabel = new JLabel("Prenoms");
        JTextField prenomsTextField = new JTextField(membre.getPrenoms(), 10);
        JLabel nomDeBaseDeLaMarieeLabel = new JLabel("Nom de Base de la mariée");
        JTextField nomDeBaseDeLaMarieeTextField = new JTextField(membre.getNomDeBaseDeLaMariee(), 10);
        if (membre.getGenre() != MembreFamille.Genre.FEMININ) {
            nomDeBaseDeLaMarieeTextField.setEditable(false);
        }
        JLabel genreLabel = new JLabel("Genre");
        //genre combobox
        DefaultComboBoxModel<MembreFamille.Genre> genreList = new DefaultComboBoxModel<>();
        genreList.addElement(MembreFamille.Genre.FEMININ);
        genreList.addElement(MembreFamille.Genre.MASCULIN);
        JComboBox<MembreFamille.Genre> genreComboBox = new JComboBox<>(genreList);
        genreComboBox.setSelectedItem(membre.getGenre());
        //aucune modification autorisée, voir la documentation
        genreComboBox.setEnabled(false);



        // Alignement horizontal
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(membreInfoLabel)
                        .addComponent(nomLabel)
                        .addComponent(prenomsLabel)
                        .addComponent(nomDeBaseDeLaMarieeLabel)
                        .addComponent(genreLabel)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nomTextField)
                        .addComponent(prenomsTextField)
                        .addComponent(nomDeBaseDeLaMarieeTextField)
                        .addComponent(genreComboBox)
                )
        );

        // Alignement vertical
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(membreInfoLabel))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nomLabel)
                        .addComponent(nomTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(prenomsLabel)
                        .addComponent(prenomsTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nomDeBaseDeLaMarieeLabel)
                        .addComponent(nomDeBaseDeLaMarieeTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genreLabel)
                        .addComponent(genreComboBox))

        );
        JButton sauvegarderMembre = new JButton("Sauvegarde Details");
        //ce actionlistner anonyme a accès à tous les champs ci-dessus, ce qui le rend facile à utiliser sans passer en paramètre.
        sauvegarderMembre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //essayez de sauvegarder les détails
                    membre.setNom(nomTextField.getText().trim());
                    membre.setPrenoms(prenomsTextField.getText().trim());
                    membre.setNomDeBaseDeLaMariee(nomDeBaseDeLaMarieeTextField.getText().trim());
                    //membre.setLifeDescription(lifeDescriptionTextArea.getText().trim());
                    membre.setGenre((MembreFamille.Genre) genreComboBox.getSelectedItem());

                    /*membre.getAddress().setStreetNumber(streetNoTextField.getText().trim());
                    membre.getAddress().setStreetName(streetNameTextField.getText().trim());
                    membre.getAddress().setSuburb(suburbTextField.getText().trim());
                    membre.getAddress().setPostCode(postcodeTextField.getText().trim());*/
                    afficherArbre(arbreDeLaFamilleActuelle);
                    modifierStatus("Membre "+membre.toString()+" ajouté");
                } catch (Exception d) {
                    //toute erreur telle que des noms incorrects, etc. apparaîtra ici pour informer l'utilisateur
                    showErrorDialog(d);
                }
            }
        });
        JButton annuler = new JButton("Annuler");
        annuler.addActionListener(new actionAnnulerModificationMembre(membre));

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container.setOpaque(false);
        container.add(sauvegarderMembre);
        container.add(annuler);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(container, gbc);

        infoPanel.validate();
        infoPanel.repaint();
    }

    /**
     * afficher le formulaire d'ajout relatif pour un membre
     * @param membre le membre à ajouter un parent
     */
    private void AfficherAjoutLienRelatifInfo(MembreFamille membre) {
        arbre.setEnabled(false);

        //réinitialiser le panneau d'information
        infoPanel.removeAll();
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel info = new JPanel();
        // si l'arbre est vide, ajoutez une personne racine sinon, ajoutez tout parent
        JLabel membreInfoLabel = new JLabel("Ajouter une nouvelle personne racine", SwingConstants.LEFT);
        if (membre != null) {
            membreInfoLabel.setText("Ajout d'un parent pour " + membre.toString());
        }

        membreInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

//            infoPanel = new JPanel();
        infoPanel.setLayout(new GridBagLayout());
//        headPanel.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        infoPanel.add(membreInfoLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(info, gbc);
        // Creer le layout
        GroupLayout layout = new GroupLayout(info);
        info.setLayout(layout);
        layout.setAutoCreateGaps(true);

        // Créer les éléments à mettre dans le formulaire
        JLabel lienDeParenteLabel = new JLabel("Lien de Parenté");
        DefaultComboBoxModel<MembreFamille.LienDeParente> lienDeParenteList = new DefaultComboBoxModel<>();

        lienDeParenteList.addElement(MembreFamille.LienDeParente.MERE);
        lienDeParenteList.addElement(MembreFamille.LienDeParente.PERE);
        lienDeParenteList.addElement(MembreFamille.LienDeParente.ENFANT);
        lienDeParenteList.addElement(MembreFamille.LienDeParente.FRERESOEUR);
        lienDeParenteList.addElement(MembreFamille.LienDeParente.CONJOINT);
        JComboBox<MembreFamille.LienDeParente> lienDeParenteJComboBox = new JComboBox<>(lienDeParenteList);

        //si l'arbre est vide, pas de sélection de type relative
        if (membre == null) {

            lienDeParenteJComboBox.removeAllItems();
            lienDeParenteJComboBox.setEnabled(false);

        }

        JLabel nomLabel = new JLabel("Nom");
        JTextField nomTextField = new JTextField("Aklam", 10);
        JLabel prenomsLabel = new JLabel("Prenoms");
        JTextField prenomsTextField = new JTextField("Geek", 10);

        JLabel nomDeBaseDeLaMarieeLabel = new JLabel("Nom de base de la mariée");
        JTextField nomDeBaseDeLaMarieeTextField = new JTextField(10);

        JLabel genreLabel = new JLabel("Genre");
        DefaultComboBoxModel<MembreFamille.Genre> genreList = new DefaultComboBoxModel<>();
        genreList.addElement(MembreFamille.Genre.FEMININ);
        genreList.addElement(MembreFamille.Genre.MASCULIN);
        JComboBox<MembreFamille.Genre> genreComboBox = new JComboBox<>(genreList);




        //anonymous actionlistner has access to all the above varaiables making it easier to use
        JButton sauvegarderMember = new JButton("Ajouter Membre");
        sauvegarderMember.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    //create the objects

                    MembreFamille nouveauMembre = new MembreFamille(
                            nomTextField.getText(),
                            prenomsTextField.getText(),
                            (MembreFamille.Genre) genreComboBox.getSelectedItem());
                    nouveauMembre.setNomDeBaseDeLaMariee(nomDeBaseDeLaMarieeTextField.getText());
                    //Si pas de racine
                    if (membre == null) {
                        arbreDeLaFamilleActuelle.setRoot(nouveauMembre);
                        modifierStatus("Membre racine ajouté");
                    } else {
                        //ajouter le relatif
                        membre.ajouterLienRelatif((MembreFamille.LienDeParente) lienDeParenteJComboBox.getSelectedItem(), nouveauMembre);
                        modifierStatus("Nouveau membre ajouté");
                    }
                    afficherArbre(arbreDeLaFamilleActuelle);

                } catch (Exception d) {
                    showErrorDialog(d);
                }
            }
        });
        JButton annuler = new JButton("Annuler");
        annuler.addActionListener(new actionAnnulerModificationMembre(membre));

        //juste un moyen d'apporter quelques modifications à la qualité de vie de l'utilisateur.
        //Définir les contraintes appropriées en fonction de la sélection relative du type
        lienDeParenteJComboBox.addActionListener(new ActionListener() {//add actionlistner to listen for change
            @Override
            public void actionPerformed(ActionEvent e) {

                switch ((MembreFamille.LienDeParente) lienDeParenteJComboBox.getSelectedItem()) {//vérifier une correspondance
                    case PERE:
                        genreComboBox.setSelectedItem(MembreFamille.Genre.MASCULIN);
                        nomDeBaseDeLaMarieeTextField.setEditable(false);
                        prenomsTextField.setText(membre.getNom());
                        break;
                    case MERE:
                        genreComboBox.setSelectedItem(MembreFamille.Genre.FEMININ);
                        nomDeBaseDeLaMarieeTextField.setEditable(true);
                        prenomsTextField.setText(membre.getNom());
                        break;
                    case CONJOINT:
                        prenomsTextField.setText(membre.getNom());
                        nomDeBaseDeLaMarieeTextField.setEditable(true);
//                        nomDeBaseDeLaMarieeTextField.setEditable(true);
                        break;
                    case ENFANT:
                        prenomsTextField.setText(membre.getNom());
                        nomDeBaseDeLaMarieeTextField.setEditable(true);
//                        nomDeBaseDeLaMarieeTextField.setEditable(false);
                        break;
                    case FRERESOEUR:
                        prenomsTextField.setText(membre.getNom());
                        nomDeBaseDeLaMarieeTextField.setEditable(true);
//                        nomDeBaseDeLaMarieeTextField.setEditable(false);
                        break;
                }
            }
        });
        // horizontal alignment
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(lienDeParenteLabel)
                        .addComponent(nomLabel)
                        .addComponent(prenomsLabel)
                        .addComponent(nomDeBaseDeLaMarieeLabel)
                        .addComponent(genreLabel)

                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(nomTextField)
                        .addComponent(lienDeParenteJComboBox)
                        .addComponent(prenomsTextField)
                        .addComponent(nomDeBaseDeLaMarieeTextField)
                        .addComponent(genreComboBox)

                )
        );

        // verticle alignment
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lienDeParenteLabel)
                        .addComponent(lienDeParenteJComboBox))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nomLabel)
                        .addComponent(nomTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(prenomsLabel)
                        .addComponent(prenomsTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(nomDeBaseDeLaMarieeLabel)
                        .addComponent(nomDeBaseDeLaMarieeTextField))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(genreLabel)
                        .addComponent(genreComboBox))

        );

        JPanel container = new JPanel(new FlowLayout(FlowLayout.LEFT));
        container.setOpaque(false);
        container.add(sauvegarderMember);
        container.add(annuler);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(container, gbc);
        infoPanel.validate();
        infoPanel.repaint();
    }

    /**
     * Méthode récursive pour peupler l'objet jtree pour chaque membre de la famille de la personne racine
     * @param top le nœud à peupler
     * @param root le membre pour obtenir les détails auprès de
     */
    private void creerArbre(DefaultMutableTreeNode top, MembreFamille root) {
        DefaultMutableTreeNode parents = null;
        DefaultMutableTreeNode pere = null;
        DefaultMutableTreeNode mere = null;
        DefaultMutableTreeNode conjoint = null;
        DefaultMutableTreeNode enfants = null;
        DefaultMutableTreeNode freresSoeurs = null;
        DefaultMutableTreeNode enfant = null;
        DefaultMutableTreeNode frereSoeur = null;
        DefaultMutableTreeNode noeudConjoint = null;

        if (root.has(MembreFamille.Attribut.PARENTS) && root == arbreDeLaFamilleActuelle.getRoot()) {
            parents = new DefaultMutableTreeNode("Parents");
            //ajouter un nœud parent
            top.add(parents);

            if (root.has(MembreFamille.Attribut.PERE)) {
                pere = new DefaultMutableTreeNode(root.getPere());
                //ajouter pere au nœud parent
                parents.add(pere);
            }

            if (root.has(MembreFamille.Attribut.MERE)) {
                mere = new DefaultMutableTreeNode(root.getMere());
                //ajouter mere au nœud parent
                parents.add(mere);
            }
        }

//        }
        if (root.has(MembreFamille.Attribut.CONJOINT)) {
            noeudConjoint = new DefaultMutableTreeNode("Conjoint");
            conjoint = new DefaultMutableTreeNode(root.getConjoint());
            //ajouter un nœud conjoint
            noeudConjoint.add(conjoint);
            //ajouter le noeud conjoint
            top.add(noeudConjoint);
        }

        if (root.has(MembreFamille.Attribut.ENFANTS)) {
            enfants = new DefaultMutableTreeNode("Enfants");
            for (MembreFamille f : root.getEnfants()) {
                enfant = new DefaultMutableTreeNode(f);
                //pour chaque enfant, appel à créer un arbre pour peupler les nœuds de leur sous-arbre
                creerArbre(enfant, f);
                //ajouter cet enfant au nœud supérieur
                enfants.add(enfant);
            }
            top.add(enfants);
        }

        if (root.has(MembreFamille.Attribut.FRERESSOEURS)) {
            freresSoeurs = new DefaultMutableTreeNode("Freres-Soeurs");
            for (MembreFamille g : root.getFreresSoeurs()) {
                frereSoeur = new DefaultMutableTreeNode(g);
                //pour chaque freres et soeurs, appel à créer un arbre pour peupler les nœuds de leur sous-arbre
                creerArbre(frereSoeur, g);
                //ajouter cet freres et soeurs au nœud supérieur
                freresSoeurs.add(frereSoeur);
            }
            top.add(freresSoeurs);
        }

    }

    /**
     * affiche un dialogue d'erreur contenant un message d'erreur provenant d'une exception
     * @param e l'exception pour recevoir le message de
     */
    private void showErrorDialog(Exception e) {
        JOptionPane.showMessageDialog(mainFrame, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
