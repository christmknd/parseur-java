
package x.ministart.parse;

import java.beans.PropertyChangeEvent;


import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import x.ministart.sys.FileResult;
import x.ministart.sys.Search;
import x.ministart.sys.SwingUI;
import x.ministart.sys.SwingWorkerExecutor;
import x.ministart.utils.Chrono;

/* *
 @author Ingénierie des Langues
 *
 **/

public class Kwic {
	private SwingUI ui;
	private Search sc ;

	public Kwic(final SwingUI ui ,final Search sc ) {
		this.ui = ui;
		this.sc = sc ;
		SwingWorker<Integer, Object> swingWorker = new Traitement();

		SwingWorkerExecutor.instance().execute(swingWorker);

		swingWorker.addPropertyChangeListener( new PropertyChangeListener() {
			@Override
			public  void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					if (ui.progressBar != null){
						ui.progressBar.setValue((Integer)evt.getNewValue());
					}
				}
			}

		});	
	}
	
	public int nbreligne(String fichier) throws IOException{
		int nbligne = 0 ;
		InputStream ips=new FileInputStream(fichier);
		InputStreamReader ipsr=new InputStreamReader(ips);
		BufferedReader br=new BufferedReader(ipsr);
		while ((br.readLine())!=null){
			nbligne++;
		}
		br.close();
		
		return nbligne ;
	}
	
	public static int contient(String chaine1, String chaine2){
		
		int i , id2 ;
		id2 = 0 ;
		
		for ( i = 0; i < chaine1.length() ; i++) 
		{
			if (chaine1.charAt(i) == chaine2.charAt(id2))
			{
				id2++ ;
			}else{
				id2 = 0 ;
			}
		
			if (chaine2.length() == id2)
				return i ;
		}
		return -1 ;
	}

	public boolean Exists(String Tab[] , int n , String motchercher){
		for(int i=0 ; i<n ; i++)
			if(Tab[i].equals(motchercher))
				return true ;
		return false ;
	}
	
	public class Traitement extends SwingWorker<Integer, Object> {

		private Chrono chrono = new Chrono();

		@Override
		protected Integer doInBackground() throws Exception {
			
			this.chrono.start();
			ui.statusInfo.setText (" Traitement KWIC");
			
			int nv,np, nav , nap,val,bol,count;
			int max = 3000000; 
			nv = np = val = 0;
			nav = Integer.parseInt(sc.getMotavant().getText());
			nap = Integer.parseInt(sc.getMotapres().getText());
			String res = ui.console_txtarea.getText();
			String motcherche = sc.getMot().getText();
			String Tv[] = new String[max] ;
			String Tp[] = new String[max] ;
			String exc[] = new String[nav];
			FileResult frame=new FileResult();  //A revoir
			try{
				InputStream ips=new FileInputStream(ui.fichier);
				InputStreamReader ipsr=new InputStreamReader(ips);
				BufferedReader br=new BufferedReader(ipsr);
				String ligne , ch = "" , tok;
				bol = 1;
				count = 0 ;
				int nblifich = nbreligne(ui.fichier) ;
				while ((ligne = br.readLine())!=null){
					StringTokenizer st = new StringTokenizer(ligne);
					//ui.console_txtarea.setText(ligne);
					setProgress( (int)((count*100) /nblifich ));
					count++ ;
					while (st.hasMoreTokens()) {
						tok = st.nextToken() ;
						
						if(tok.equals(motcherche)){
							val = 1 ;
							bol = 0 ;
						}
						if(val == 0){
							Tv[nv] = new String(tok) ;
							nv++;
						}
						if((val == 1)&&(bol == 1)){
							Tp[np] = new String(tok) ;
							np++;
						}
						if(nap == np){
							String Ch[] = new String[nv];
							for(int i=0; i<nv ; i++){
								Ch[i] = new String(Tv[nv-1-i]) ;
							}
							for(int i=0; i<nv ; i++){
								if(i<nav){
									exc[nav-1-i] = Ch[i] ;
								}
							}
							for(int i=0; i<nav ; i++){
									ch = ch.concat(" ");
									ch = ch.concat(exc[i]);
							}
							ch = ch.concat("   		");
							ch = ch.concat(motcherche) ;
							ch = ch.concat("    	");
							for(int i=0; i<nap ; i++){
								ch = ch.concat("  ");
								ch = ch.concat(Tp[i]);
							}
							ch = ch.concat("\n");
							val = nv = np = 0;
							res = res+"\n\n"+ch;
							bol = 1 ;
						}
						bol = 1 ;
					}
				}
				
				br.close(); 
				if(!res.equals("")){
					frame.getMain_txtarea().setText(ch);   //affiche dans la nouvelle fenetre.
					frame.setVisible(true);
				}
				else
					javax.swing.JOptionPane.showConfirmDialog(null,
							"Mot non trouve dans le texte", "Entree incorrecte.",
							javax.swing.JOptionPane.PLAIN_MESSAGE);
			}
			catch (Exception f){
				if (sc.getMot().getText().length() == 0 ){
					javax.swing.JOptionPane.showConfirmDialog(null,
					"Merci d'entrer le mot rechercher dans le texte", "",
					javax.swing.JOptionPane.PLAIN_MESSAGE);
					return 0;
				}else{
					javax.swing.JOptionPane.showConfirmDialog(null,
					"Merci de selectionner un fichier.", "Entree incorrecte.",
					javax.swing.JOptionPane.PLAIN_MESSAGE);
					return 0;
				}
			}
 
			return 0 ;
		}

		@Override
		protected void done(){
			int strParsed = 0;
			try {
				strParsed = get();
				this.chrono.stop();
				ui.statusInfo.setText("Document parsed..." + strParsed+ " insertions [" + this.chrono.displayInterval() + "]");
			}catch(Exception e){

				JOptionPane.showMessageDialog(ui, 
						"Error :\n" + e.getLocalizedMessage(),
						"Parse", 
						JOptionPane.ERROR_MESSAGE);
				ui.statusInfo.setText("Parse error on \"ParseAllLettersWorker\".");
			}
		}
	}
}
