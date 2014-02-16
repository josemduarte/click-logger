package app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class View {


	protected Display display;
	protected Shell shell;
	protected ScrolledComposite sc;
	protected Composite parent;
	protected Label mainImgLabel;
	protected Button loadImageButton;
	protected Button toggleLogging;
	protected Button resetLog;
	protected Button undoLastClickLogged;
	
	protected Menu popupM;
	protected MenuItem origMI;
	protected MenuItem xscaleMI;
	protected MenuItem yscaleMI;
	
	protected ScaleDialog scaleDialog;
	
	
	public View() {
		
		initUI();

		shell.open ();

	}
	
	private void initUI() {
		// setting up window
		display = new Display ();
		shell = new Shell (display);
		shell.setText("Click Logger");		

		shell.setLayout (new FillLayout());
		
		sc = new ScrolledComposite(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		parent = new Composite(sc, SWT.NONE);
		sc.setContent(parent);
		//sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		RowLayout layout = new RowLayout ();
		layout.marginWidth = layout.marginHeight = 10;
		parent.setLayout(layout);
		
		// top buttons
		loadImageButton = new Button(parent,SWT.PUSH);
		loadImageButton.setText("Load image...");
		toggleLogging = new Button(parent,SWT.TOGGLE);
		toggleLogging.setText("Toggle logging");
		resetLog = new Button(parent,SWT.PUSH);
		resetLog.setText("Reset log");
		undoLastClickLogged = new Button(parent, SWT.PUSH);
		undoLastClickLogged.setText("Undo last click logged");
		
		// popup menu
		popupM = new Menu(shell, SWT.POP_UP);
		origMI = new MenuItem(popupM, SWT.CASCADE);
		origMI.setText("Set origin");
		xscaleMI = new MenuItem(popupM, SWT.CASCADE);
		xscaleMI.setText("Set X scale");
		yscaleMI = new MenuItem(popupM, SWT.CASCADE);
		yscaleMI.setText("Set Y scale");		
		
		// main image
		mainImgLabel = new Label(parent, SWT.IMAGE_PNG);

        mainImgLabel.setMenu(popupM);
        shell.setMenu(popupM);
	}
		
	protected void listen() {
		
		// do we need this? here? what for? no idea
//		while (!xscaleDialog.isDisposed()) {
//			if (!display.readAndDispatch())
//				display.sleep();
//		}
//		
//		while (!yscaleDialog.isDisposed()) {
//			if (!display.readAndDispatch())
//				display.sleep();
//		}

		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
	}

	protected void dispose() {
		
		display.dispose ();
		
	}
	
}
