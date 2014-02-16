package app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ScaleDialog {

	private static final int WIDTH = 350;
	private static final int HEIGHT = 150;
	
	private Shell dialog;
	private Button okButton;
	private Button cancelButton;
	private Label label;
	private Text textbox;
	
	private String type; // "x" or "y"
	private double coordValue;
	
	private Controller ctrl;
	
	Listener listener = new Listener() {
		public void handleEvent(Event event) {
			if (event.widget == okButton) {
				try {
					double tickValue = Double.parseDouble(textbox.getText());
					if (type.equals("x")) {
						ctrl.setXscale((double)coordValue/tickValue);
					} else if (type.equals("y")) {
						ctrl.setYscale((double)coordValue/tickValue);
					}
					dialog.close();
				} catch (NumberFormatException e) {
					ctrl.popErrorMessageBox("The value must be a number");
				}
			} else {
				dialog.close();
			}
			
		}
	};

	
	public ScaleDialog(Shell parent, String type, Controller ctrl, double coordValue) {
		this.type = type;
		this.ctrl = ctrl;	
		this.coordValue = coordValue;

		dialog = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		dialog.setSize(WIDTH, HEIGHT);
		dialog.setText("Set "+type+" scale");
		
	    okButton = new Button(dialog, SWT.PUSH);
	    okButton.setText("OK");
	    okButton.setBounds(20, 55, 80, 25);
	    okButton.setFocus();

	    cancelButton = new Button(dialog, SWT.PUSH);
	    cancelButton.setText("Cancel");
	    cancelButton.setBounds(120, 55, 80, 25);

	    label = new Label(dialog, SWT.NONE);
	    label.setBounds(20, 15, 150, 20);
		label.setText(String.format("Set "+type+" scale at %4.1f to:",coordValue));
	    
	    textbox = new Text(dialog,SWT.SINGLE);
	    textbox.setBounds(180, 15, 50, 20);
	    textbox.setFocus();
	    //textbox.setText("1");

	    addListeners();
	    

	}
	
	public void open() {
		dialog.open();
	}
	
	public void close() {
		dialog.close();
	}
	
	private void addListeners( ) {
		okButton.addListener(SWT.Selection, listener);
		cancelButton.addListener(SWT.Selection, listener);
	}
	
	protected boolean isDisposed() {
		return dialog.isDisposed();
	}
}
