package app;

import gnu.getopt.Getopt;

import java.io.File;


public class Start {

	private static final String PROG_NAME = "ClickLogger"; 

	public static void main(String[] args) {
		
		String help = "Usage: \n" +
				PROG_NAME + " -i <png_img_file> -l <log_file> \n";
		
		File imgFile = null;
		File logFile = null;
		
		Getopt g = new Getopt(PROG_NAME, args, "i:l:h?");
		int c;
		while ((c = g.getopt()) != -1) {
			switch(c){
			case 'i':
				imgFile = new File(g.getOptarg());
				break;
			case 'l':
				logFile = new File(g.getOptarg());
				break;
			case 'h':
			case '?':
				System.out.println(help);
				System.exit(0);
				break; // getopt() already printed an error
			}
		}		
		
		if (imgFile!=null && !imgFile.canRead()) {
			System.err.println("Can't read image file "+imgFile+". Exiting.");
			System.exit(1);
		}
		
		// starting main window
		View view = new View();
		
		// starting controller
		Controller ctrl = new Controller(view);
		
		// loading image if one given
		if (imgFile!=null){ 
			ctrl.loadImage(imgFile);
		}

		
		if (logFile!=null) {
			ctrl.setLogFile(logFile);
		}
		
		view.listen();
		view.dispose();

	}
}
