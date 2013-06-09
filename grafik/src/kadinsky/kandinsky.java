package kadinsky;

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
	
	ImagePanel(String filename) {
		super();
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
			this.scalex2();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Konnte Bild nicht laden");
		}
	}
	
    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }
    
    protected void paintComponent(Graphics g) { 
		super.paintComponent(g); 
		if (this.image != null) {
			g.drawImage(this.image, 0, 0, w, h, null);
		} else {
			g.drawString("Kein Bild vorhanden",10,20 );
		}
    }
    
    public ImagePanel greyscale() {
    	BufferedImage greyImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    	for (int j = 0; j < w; j++) {
    		for (int i = 0; i < h; i++) {
    			Color temp = new Color(this.image.getRGB(j, i));
    			
    			int grey = (temp.getRed() + temp.getBlue() + temp.getGreen())/3;
    			greyImage.setRGB(j, i, new Color(grey,grey,grey).getRGB());
    		}
    	}
    this.image = greyImage;
    return this;
    }
    
    public ImagePanel scalex2(){
    	int original_width = this.original.getWidth();
    	int original_height = this.original.getHeight();
    	this.w = original_width*2;
    	this.h = original_height*2;
    	this.image = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
    	int pixel_color;
    	int x,y;
    	for (int i = 0; i<original_width; i++) {
    		for (int j = 0; j< original_height; j++) {
    			pixel_color = this.original.getRGB(i, j);
    			x = i*2; y = j*2;
    			this.image.setRGB(x,y, pixel_color);
    			this.image.setRGB(x+1,y, pixel_color);
    			this.image.setRGB(x,y+1, pixel_color);
    			this.image.setRGB(x+1,y+1, pixel_color);
    		}
    	}
    	return this;
    }
    
    public ImagePanel reset() {
    	this.image = this.original;
    	return this.scalex2();
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


public class kandinsky
{ 
	private static ImagePanel imagePanel;
	
    public static void main( String[] args ) 
    { 
	
    if (args.length < 1) {
    	System.out.println("Usage: kadinsky filename.jpg");
    	return;
    }
    
    imagePanel = new ImagePanel(args[0]);
    
    JFrame frame = new AppFrame("Output Kandinsky"); 
	JPanel panel = new JPanel(new BorderLayout());  
	frame.add(panel);
	
	JPanel navigation = new JPanel();

		JButton original = new JButton("Original");
		JButton greyscale = new JButton("Greyscale");
		JButton pattern = new JButton("Pattern");
		
		navigation.add(original);
		navigation.add(greyscale);
		navigation.add(pattern);
	
	panel.add(navigation, BorderLayout.PAGE_START);
	
	panel.add(imagePanel, BorderLayout.CENTER);
	
	JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	panel.add(footer, BorderLayout.PAGE_END);
	
	JButton exit = new JButton("Exit");
	
	//JLabel label = new JLabel("Doppelklicken zum Beenden"); 
	//panel.add(label, BorderLayout.PAGE_END);
	footer.add(exit, BorderLayout.PAGE_END);
	
	AppMouseAdapter m = new AppMouseAdapter();
	//label.addMouseListener(m); 
	exit.addMouseListener(m);
	
	greyscale.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent arg0) {
		    imagePanel.scalex2().greyscale().repaint();
		   }
		  });
	
	original.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent arg0) {
		    imagePanel.reset().repaint();
		   }
		  });
	pattern.addActionListener(new ActionListener() {
		   public void actionPerformed(ActionEvent arg0) {
		    imagePanel.pattern().repaint();
		   }
		  });
	
	frame.pack();
	frame.setVisible(true);
    } 
}