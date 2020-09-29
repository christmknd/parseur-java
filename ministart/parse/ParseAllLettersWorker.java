package x.ministart.parse;

import java.beans.PropertyChangeEvent;


import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import x.ministart.sys.SwingUI;
import x.ministart.sys.SwingWorkerExecutor;
import x.ministart.utils.Chrono;

/**
 * Parse all the letters.<br>
 * Add a "_" between them.
 * 
 * TODO Performance Boost (cf. http://docs.oracle.com/javase/6/docs/api/javax/swing/text/Document.html)	
 * @author Ingénierie des Langues
 *
 */
public class ParseAllLettersWorker{
	
	private SwingUI ui;

	public ParseAllLettersWorker(final SwingUI ui) {
		this.ui = ui;
		
		SwingWorker<Integer, Object> swingWorker = new ParseAllLetters();

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
	public class ParseAllLetters extends SwingWorker<Integer, Object> {

		private Chrono chrono = new Chrono();

		@Override
		protected Integer doInBackground() throws Exception {
			//Document doc = ui.main_txtarea.getDocument();
			//ui.main_txtarea.setDocument(new DefaultStyledDocument());
			
			//StyleContext context = new StyleContext();
			//new DefaultStyledDocument(context);

		    //StyleContext context = new StyleContext();
		    //StyledDocument document = new DefaultStyledDocument(context);
		    
			StyleContext context = new StyleContext();
		    StyledDocument doc = (StyledDocument) ui.main_txtarea.getDocument();
		    ui.main_txtarea.setDocument(new DefaultStyledDocument());
			
			ui.statusInfo.setText ("Parse ALL - running");
			this.chrono.start();
			
			int 
				docLen = doc.getLength(),
				insertion_count = 0,
				parse_ind = 0;

			try {
				String sep = "_";

				for (int txt_ind = 0; txt_ind < docLen; txt_ind++) {
					// update the progress
					setProgress( (txt_ind+1) * 100 / docLen);
					
					//StyleContext context = new StyleContext();
				    //StyledDocument doc1 = new DefaultStyledDocument(context);

				    Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
				    StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
				    StyleConstants.setFontSize(style, 14);
				    StyleConstants.setSpaceAbove(style, 4);
				    StyleConstants.setSpaceBelow(style, 4);

				    SimpleAttributeSet attributes = new SimpleAttributeSet();
				    StyleConstants.setBold(attributes, true);
				    StyleConstants.setItalic(attributes, true);

				    Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE); ;
					
					doc.insertString(parse_ind, sep , labelStyle);
					parse_ind += sep.length() + 1;
					insertion_count++;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			ui.statusInfo.setText ("Parse ALL - sending to text area");
			ui.main_txtarea.setDocument(doc);

			return insertion_count;
			//			  textComponent.setCaretPosition(doc.getLength() - 1);


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