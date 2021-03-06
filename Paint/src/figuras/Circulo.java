package figuras;
import java.awt.Color;
import java.awt.Graphics;

public class Circulo extends Elipse {
	
	public Circulo (int x1, int y1, int x2, int y2)
    {
    	super(x1, y1, x2, y2);
    }
    
    public Circulo (int x1, int y1, int x2, int y2, Color corContorno)
    {
	    super(x1, y1, x2, y2, corContorno);
    }
    
    public Circulo (int x1, int y1, int x2, int y2, Color corContorno, Color corInterior)
    {
    	super(x1, y1, x2, y2, corContorno, corInterior);
    }
    
    public Circulo (String s)
    {
    	super(s);
    }
    
    public void torneSeVisivel(Graphics g)
    {
        int x, y;

        int largura = Math.abs(this.p1.getX() - this.p2.getX());
        int altura = Math.abs(this.p1.getY() - this.p2.getY());

        int tamanho = Math.min(altura, largura);

        if (this.p1.getX() < this.p2.getX())
            x = p1.getX();
        else
            x = p1.getX() - tamanho;

        if (this.p1.getY() < this.p2.getY())
            y = p1.getY();
        else
            y = p1.getY() - tamanho;  

        if (this.corInterior.getAlpha() == 255) {
	        g.setColor(this.corInterior);
	        g.fillOval(x, y, tamanho + 1, tamanho + 1);
        }

        g.setColor(this.corContorno);
        g.drawOval(x, y, tamanho, tamanho);
    }

    public String toString()
    {
        return "c:" +
               this.p1.getX() +
               ":" +
               this.p1.getY() +
               ":" +
               this.p2.getX() +
               ":" +
               this.p2.getY() +
               ":" +
               this.getCorContorno().getRed() +
               ":" +
               this.getCorContorno().getGreen() +
               ":" +
               this.getCorContorno().getBlue() +
               ":" +
               this.getCorInterior().getRed() +
               ":" +
               this.getCorInterior().getGreen() +
               ":" +
               this.getCorInterior().getBlue();
    }
}
