package app;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

public class Controller {
	
	private View view;
	
	private Point origin;
	private double scaleX;
	private double scaleY;
	
	private int xPos;
	private int yPos;

	private ArrayList<Pair> clickLog; 
	
	private boolean doLogging;
	
	private File logFile;
	
	public Controller(View view) {
		this.view = view;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
		doLogging = false;
		resetClickLog();
		addListeners();
	}
	
	private void addListeners() {
		
		view.sc.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle r = view.sc.getClientArea();
				view.sc.setMinSize(view.parent.computeSize(r.width, SWT.DEFAULT));
			}
		});
		
		view.loadImageButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(view.shell, SWT.NULL);
				String path = dialog.open();
				if (path != null) {
					File file = new File(path);
					try {
						loadImage(file);
					} catch (SWTException ex) {
						popErrorMessageBox("File "+file+" can't be recognized as a PNG image.");						
					}

				}
			}
		});
		
		view.toggleLogging.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				if (view.toggleLogging.getSelection()) {
					System.out.println("Logging is on");
					doLogging = true;
				} else {
					System.out.println("Logging is off");
					doLogging = false;					
				}
			}
			
		});
		
		view.resetLog.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				System.out.println("Resetting log");
				resetClickLog();
			}
			
		});
		
		view.undoLastClickLogged.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				undoLastClickLogged();
			}
			
		});

		view.mainImgLabel.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				// This is needed for the right click:
				// The mouseUp event for the right click is intercepted by the popup menu, so
				// in order to have the correct coordinates when right clicking and getting the popup
				// we need to set them on mouseDown.
				// We still do the logging for the left click on mouseUp because it seems more natural,
				// more like standard applications do
				xPos = e.x;
				yPos = e.y;
			}

			public void mouseUp(MouseEvent e) {
				xPos = e.x;
				yPos = e.y;
				//System.out.println("Screen coords: "+xPos+" "+yPos);
				logClick(); 
			}
        	
        });
		
		view.origMI.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				origin = new Point(xPos, yPos);
				System.out.println("Resseting origin to ("+origin.x+" "+origin.y+")");
			}
			
		});

		view.xscaleMI.addSelectionListener(new SelectionListener() {			

			public void widgetSelected(SelectionEvent e) {
				setUpScaleDialog("x");
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		view.yscaleMI.addSelectionListener(new SelectionListener() {			

			public void widgetSelected(SelectionEvent e) {
				setUpScaleDialog("y");
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		view.display.addListener(SWT.Dispose, new Listener() {

			public void handleEvent(Event event) {
				if (logFile!=null) 
					writeLogToFile();
			}
			
		});

	}

	private void logClick() {
		Pair pair = new Pair(screenX2plotX(xPos),screenY2plotY(yPos));
		System.out.println(pair.toString());
		if (doLogging) {
			//log.println(str);
			this.clickLog.add(pair);
		}
	}
	
	private void resetClickLog() {
		this.clickLog = new ArrayList<Pair>();
	}
	
	private void undoLastClickLogged() {
		if (!clickLog.isEmpty()) {
			Pair pair = clickLog.get(clickLog.size()-1);
			clickLog.remove(clickLog.size()-1);
			System.out.println("Removing last logged click: "+pair);
		}
	}
	
	private void setUpScaleDialog(String type) {
		double coordValue = 0;
		if (type.equals("x")) {
			coordValue = xPos-origin.x;//screenX2plotX(xPos);
		} else if (type.equals("y")) {
			coordValue = origin.y-yPos;//screenY2plotY(yPos);
		}

		view.scaleDialog = new ScaleDialog(view.shell, type, this, coordValue);
		view.scaleDialog.open();
	}
	
	private double screenX2plotX(int x) {
		return ((double)(x-origin.x))/scaleX;
	}
	
	private double screenY2plotY(int y) {
		return ((double)(origin.y-y))/scaleY;
	}

	protected void loadImage(File imgFile) {
		Image bgImage = new Image(view.display,new ImageData(imgFile.getAbsolutePath()));
        view.mainImgLabel.setImage(bgImage);
        view.mainImgLabel.setCursor(new Cursor(view.display,SWT.CURSOR_CROSS));
        view.mainImgLabel.pack();
        setOrigin();
        //shell.setSize(bgImage.getBounds().width,bgImage.getBounds().height);
	}

	protected void setXscale(double scale) {
		scaleX = scale;
		System.out.printf("Resetting x scale. 1 unit in plot corresponds to: %4.1f pixels\n",scaleX);
	}

	protected void setYscale(double scale) {
		scaleY = scale;
		System.out.printf("Resetting y scale. 1 unit in plot corresponds to: %4.1f pixels\n",scaleY);
	}

	protected void popErrorMessageBox(String text) {
		MessageBox mb = new MessageBox(view.shell,SWT.OK | SWT.ICON_ERROR);
		mb.setMessage(text);
		mb.open();
	}
	
	protected void setOrigin() {
		this.origin = new Point(0,view.mainImgLabel.getSize().y);
	}
	
	protected void setLogFile(File logFile) {
		this.logFile = logFile;
	}
	
	protected void writeLogToFile() {
		try {
			PrintWriter log = new PrintWriter(logFile);
			for (Pair pair:clickLog) {
				log.println(pair.toString());
			}
			log.close();
			System.out.println("Log written to "+logFile);
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't find log file "+logFile+". No log has been written.");
		}
	}
}
