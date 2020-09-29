package x.ministart.sys;

import java.awt.BorderLayout;

import javax.swing.text.Document;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
//import javax.swing.JTextPane;

/**
*
* @author ingénierie des langues
*/

public class FileResult extends JFrame {
	
	private static final long serialVersionUID = 1L;	
	private JTextArea main_txtarea = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(main_txtarea);
	private JMenuBar menu=new JMenuBar();// déclaration de la bar de menu
	
	
	public FileResult(){
		this.setLocationRelativeTo(null);
		this.setSize(500, 500);
		this.setTitle("Fichier resultat");
		this.setVisible(false);
		this.setJMenuBar(menu);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		main_txtarea.setEditable(false) ;
		
		// déclaration des menus
		JMenu mfile=new JMenu("File");
		JMenu maide=new JMenu("Aide");

		// déclaration des sous menus
		JMenuItem smquitter=new JMenuItem("Exit");
		JMenuItem smsave=new JMenuItem("Save as");
		JMenuItem smpropos=new JMenuItem("A propos");
		menu.add(mfile);
		menu.add(maide);
		
		// l'ajout des sous menu du menu Fichier
		mfile.add(smsave);
		mfile.addSeparator();
		mfile.add(smquitter);
		
		//l'ajout des sous menu du menu Aide
		maide.add(smpropos);
	
		//définir l'action du bouton
		smsave.addActionListener(new ActionListener(){
			
			public void actionPerformed (ActionEvent e) {
					JFileChooser chooser = new JFileChooser();//création dun nouveau filechosser
					chooser.setApproveButtonText("Save..."); //intitulé du bouton
					int retval = chooser.showOpenDialog(FileResult.this);
					if (retval == JFileChooser.APPROVE_OPTION)
					{
						String fichier  = chooser.getSelectedFile().getAbsolutePath() ;
						Sauvegarde(main_txtarea , fichier);
						System.out.println(" Valeur apres selection: "+fichier+"\n"); 
					}
				}
		});
	
		// définir l'action du sous menu Quitter
		final FileResult fr = this;
		smquitter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev) {
				fr.dispose();
			}
		});
		
		// définir l'action du sous menu A propos
		smpropos.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev) {
				JOptionPane.showMessageDialog(null, "Fenetre Pour permettre d'enregistrer les resultats du fichier miniKwic\n By Mike Donald" , "A propos", JOptionPane.INFORMATION_MESSAGE); // fenetre a propos
			}
		});
	}
	
	//Gestion de la sauvegarde du fichier
	public void Sauvegarde(JTextArea docText , String Namefichier){
		try{
			FileWriter fichier = new FileWriter(Namefichier);
			char c;
			Document doc = docText.getDocument();
			int inc = 0 , taille = doc.getLength() ;
			for (int i = 0; i < taille; ++i) {
				c = doc.getText(inc, 1).charAt(0);
				fichier.write(c);
				inc++;
			}
			fichier.close(); 
		}
		catch (Exception f){
			javax.swing.JOptionPane.showConfirmDialog(null,
			"Erreur lors de la sauvegarder du fichier.", "ou format de fichier incorrect",
			javax.swing.JOptionPane.PLAIN_MESSAGE);
		}
	}
	
	public JTextArea getMain_txtarea(){
		return main_txtarea ;
	}
	
}