package smile;

import java.awt.*; 
import java.awt.event.*; 
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*; 


class AppFrame extends JFrame { 

	private static final long serialVersionUID = 1L;

	public AppFrame(String title) {
	super(title);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } 
}

class ImagePanel extends JPanel {  
	
	private static final long serialVersionUID = 2L;
	
	private String filename;
	private int w,h;
	private BufferedImage original;
	private BufferedImage image;
	
	private int dim;
	
	private BufferedImage current, last;
	
	ImagePanel(String filename, int dim) {
		super();
		this.dim = dim;
		this.filename = filename;
		BufferedImage image;
		try {
			image = ImageIO.read(new File(this.filename));
		    int w = image.getWidth(null);
	        int h = image.getHeight(null);
	        this.w = w;
	        this.h = h;
			this.original = image;
			this.image = image;
			this.pattern().createAutomat();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Konnte Bild nicht laden");
		}
	}
	
    public Dimension getPreferredSize() {
        return new Dimension(dim, dim);
    }
    
    public void reset() {
    	this.createAutomat();
    	this.repaint();
    }
    
    protected void createAutomat() {
    	this.current = new BufferedImage(dim, dim ,BufferedImage.TYPE_INT_RGB);
    	int width = this.image.getWidth();
    	int height = this.image.getHeight();
    	w = dim;
    	h  = dim;
    	
    	for (int i = 0; i< dim; i++) {
    		for (int j = 0; j< dim; j++) {
    			current.setRGB(i,j, Color.WHITE.getRGB());
    		}
    	}
    	
    	int x=(dim/2)-32;
    	int y=(dim/2)-32;
    	for (int i = 0; i< width; i++) {
    		for (int j = 0; j< height; j++) {
    			current.setRGB(x+i,y+j, image.getRGB(i, j));
    		}
    	}
    	return;
    }
    
    protected void paintComponent(Graphics g) { 
		super.paintComponent(g);
		if (this.current != null) {
			g.drawImage(this.current, 0, 0, w, h, null);
		}
		else if (this.image != null) {
			g.drawImage(this.image, 0, 0, w, h, null);
		} else {
			g.drawString("Kein Bild vorhanden",10,20 );
		}
    }
    
    public ImagePanel start(int rounds) {
    	int[] di = { 1, -1, 0, 0, 1, -1, 1, -1 };
    	int[] dj = { 0, 0, 1, -1, 1, -1, -1, 1 };
    	for (int r = 0; r < rounds; r++) {
    		this.last = this.current;
    		this.current = new BufferedImage(dim, dim ,BufferedImage.TYPE_INT_RGB);
        	for (int i = 0; i< dim; i++) {
        		for (int j = 0; j< dim; j++) {
        			int value = 0;
        			Color lastrgb;
        			for (int k=0; k<8; k++) {
        				int ww = (i+di[k])%dim;
        				int hh = (j+dj[k])%dim;
        				if (ww < 0) ww += dim; // falls das Ergebnis negativ ist
        				if (hh < 0) hh += dim;
        				lastrgb = new Color(last.getRGB(ww, hh));
        				if (lastrgb.equals(Color.BLACK)) {
        					value++;
        				} 
        			}
        			if (value % 2 == 0) {
        				current.setRGB(i,j, Color.WHITE.getRGB());
        			} else {
        				current.setRGB(i,j, Color.BLACK.getRGB());
        			}
        			
        		}
        	}
    	}
    	return this;
    }
    

    public ImagePanel pattern() {
    	int original_width = this.original.getWidth();
    	int original_height = this.original.getHeight();
    	this.w = original_width*2;
    	this.h = original_height*2;
    	this.image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
    	Color rgb;
    	int grey, pixel_color;
    	int x,y;
    	for (int i = 0; i<original_width; i++) {
    		for (int j = 0; j< original_height; j++) {
				rgb = new Color(this.original.getRGB(i, j));
				grey = (rgb.getRed()+rgb.getGreen()+rgb.getBlue())/3;
				pixel_color = new Color(255,255,255).getRGB();
				x = i*2; y = j*2;
				switch((int)grey/52) {
					case 0:
						break;
					case 1:
						this.image.setRGB(x, y,pixel_color);
						break;
					case 2:
						this.image.setRGB(x, y,pixel_color);
						this.image.setRGB(x+1, y+1,pixel_color);
						break;
					case 3:
						this.image.setRGB(x+1, y,pixel_color);
						this.image.setRGB(x+1, y+1,pixel_color);
						this.image.setRGB(x, y+1,pixel_color);
						break;
					case 4:
						this.image.setRGB(x, y,pixel_color);
						this.image.setRGB(x+1, y,pixel_color);
						this.image.setRGB(x+1, y+1,pixel_color);
						this.image.setRGB(x, y+1,pixel_color);
						break;
					}
    		}
    	}
    	return this;
    }
    
}

class AppMouseAdapter extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
	   System.exit(0);
    }
}


public class smile32
{ 
	private static ImagePanel imagePanel;
	
    public static void main( String[] args ) 
    { 
	
    if (args.length < 1) {
    	System.out.println("Usage: smile32 filename.jpg");
    	return;
    }
    
    JFrame frame = new AppFrame("Output Smile32"); 
	JPanel panel = new JPanel(new BorderLayout());
	frame.add(panel);
	
	JPanel navigation = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel("Rounds");
		final JTextField input = new JTextField("0");
		input.setColumns(7);
		JButton start = new JButton("Start");
		
		navigation.add(label);
		navigation.add(input);
		navigation.add(start);
	
	panel.add(navigation, BorderLayout.PAGE_START);
	
    imagePanel = new ImagePanel(args[0],800);
	panel.add(imagePanel, BorderLayout.CENTER);
	
	
	JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	JButton exit = new JButton("Exit");
	JButton reset = new JButton("Reset");
	footer.add(reset, BorderLayout.PAGE_END);
	footer.add(exit, BorderLayout.PAGE_END);
	panel.add(footer, BorderLayout.PAGE_END);
	
	
	AppMouseAdapter m = new AppMouseAdapter();
	//label.addMouseListener(m); 
	exit.addMouseListener(m);
	
	start.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent arg0) {
		    imagePanel.start(Integer.parseInt(input.getText())).repaint();
		   }
		  });
	
	reset.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent arg0) {
		    imagePanel.reset();
		   }
		  });
	
	
	frame.pack();
	frame.setVisible(true);
    } 
}