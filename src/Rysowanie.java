import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Rysowanie extends JFrame {
      
    public Rysowanie()
    {
        
    this.setTitle("Rysowanie");
    this.setBounds(600,300,800,600);

    JButton pDodaj = (JButton)panelP.add(new JButton("Dodaj"));
    pDodaj.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            startAnimation();
        }
    });
    JButton pStop = (JButton)panelP.add(new JButton("Usu�"));
    pStop.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            stopAnimation();
        }
    });
    JButton pPauza = (JButton)panelP.add(new JButton(stan));
    pPauza.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            pauseAnimation();
            String stan =  PanelRys.pauza ==  false ? "Wstrzymaj" : "Wzn�w";
            pPauza.setText(stan);
        }
    });
    JButton pPrUp = (JButton)panelP.add(new JButton("Pr�dko�� +"));
    pPrUp.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
        if (PanelRys.predkosc > 0) PanelRys.predkosc--;
        sPredkosc.setValue(20-PanelRys.predkosc);
        }
    });
    JButton pPrDown = (JButton)panelP.add(new JButton("Pr�dko�� -"));
    pPrDown.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
        if (PanelRys.predkosc < 20) PanelRys.predkosc++;
        sPredkosc.setValue(20-PanelRys.predkosc);
        }
    });
    
    sPredkosc.setMinorTickSpacing(1);
    sPredkosc.setMajorTickSpacing(5);
    sPredkosc.setPaintTicks(true);
    sPredkosc.setPaintLabels(true);
    sPredkosc.setSnapToTicks(true);
    sPredkosc.setValue(PanelRys.predkosc);
    sPredkosc.addChangeListener(new ChangeListener() {
    @Override
    public void stateChanged(ChangeEvent ce) {
        PanelRys.predkosc=20-sPredkosc.getValue();
    }
    });   
    panelP.add(sPredkosc);

    panelRys.setBackground(Color.gray);
        
    this.getContentPane().add(panelRys);
    this.getContentPane().add(panelP, BorderLayout.SOUTH);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void startAnimation ()
    {
        panelRys.addObrazek();
//        panelRys.addKropelki(200);
    }
    
    public void stopAnimation ()
    {
        panelRys.stop();
    }
    
        public void pauseAnimation ()
    {
        panelRys.pause();
    }
    
    private JPanel panelP = new JPanel();
    private PanelRys panelRys = new PanelRys();
    private JSlider sPredkosc = new JSlider(JSlider.HORIZONTAL,0,20,10);
    
    String stan =  PanelRys.pauza ==  false ? "Wstrzymaj" : "Wzn�w";
    
    public static void main(String[] args) 
    {
    new Rysowanie().setVisible(true);
    }

}

class PanelRys extends JPanel
{
    public void addObrazek()
    {
//        Bez użycvia wątków:
//    listaObrazkow.add(new Obrazek());
//    
//        for (int i = 0; i<5000; i++)
//        {
//            for (int j = 0; j < listaObrazkow.size();j++)
//            ((Obrazek)listaObrazkow.get(j)).ruszObrazek(this);
//            this.paint(this.getGraphics());
//            try 
//            {
//                Thread.sleep(1);
//            } catch (InterruptedException ex) {
//                System.out.println(ex.getMessage());
//            }
        listaObrazkow.add(new Obrazek());
        watek = new Thread (grupaWatkow, new ObrazekRunnable((Obrazek)listaObrazkow.get(listaObrazkow.size()-1)));
        watek.start();
        
    }
    
//    public void addKropelki(int i)
//    {
//        for (int j = 0; j < i; j++)
//        {
//        addObrazek();
//        try {
//            watek.sleep(20);
//            repaint();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(PanelRys.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        }
//    }
    
    public void stop()
    {
        grupaWatkow.interrupt();
    }
    public void pause()
    {
        if (pauza)
        {
            synchronized(lock)
            {
            lock.notifyAll();            
            }   
        }
        pauza = !pauza;
        System.out.println(pauza);
    }
    
    @Override
    public void paintComponent (Graphics g)
    {
        super.paintComponent(g);
        
        for (int j = 0; j < listaObrazkow.size() ;j++)
        {
            g.drawImage(Obrazek.getObrazek(), ((Obrazek)listaObrazkow.get(j)).x, ((Obrazek)listaObrazkow.get(j)).y, null);
        }
    
    }
  
    
    public class ObrazekRunnable implements Runnable
    {
        public ObrazekRunnable (Obrazek obrazek)
        {
           this.obrazek = obrazek; 
        }
        
        @Override
        public void run() 
        {
        
            try{    
                //while(!Thread.currentThread().isInterrupted())
                while(true)
                {
                while (pauza)
                {
                    synchronized(lock)
                    {
                        lock.wait();
                    }
                }
                    
                    obrazek.ruszObrazek(ten);
                    repaint();
                    Thread.sleep(predkosc);
                
                }
                }
                catch (InterruptedException iex)
                {
                    System.out.println(iex.getMessage());
                    listaObrazkow.clear();
                    repaint();
                }
                

        }
        
        Obrazek obrazek;
    }
    JPanel ten = this;
    ArrayList listaObrazkow = new ArrayList();
    Thread watek;
    ThreadGroup grupaWatkow = new ThreadGroup("Grupa Obrazków");
    Object lock = new Object();
    static boolean pauza = false;
    static int predkosc = 10;
}

class Obrazek 
{
    public static Image getObrazek()
    {
        return obrazek;
    }
   
   public void ruszObrazek(JPanel pojemnik)
    {
        Rectangle granice = pojemnik.getBounds();
        
        x += dx;
        y += dy;
        
        if ( x >= granice.getMaxX()-xKropelki || x < granice.getMinX() )
        {
            if ( x >= granice.getMaxX()-xKropelki)
            x = (int)granice.getMaxX()-xKropelki;
            if ( x < granice.getMinX())
            x = (int)granice.getMinX();
            dx = -dx;
        }
        if ( y >= granice.getMaxY()-yKropelki || y < granice.getMinY())
        {
            if ( y >= granice.getMaxY()-yKropelki)
            y = (int)granice.getMaxY()-yKropelki;
            if ( y < granice.getMinY())
            y = (int)granice.getMinY();
            dy = -dy;
        }
        
        System.out.println("X: "+x+" Y: "+y);
        System.out.println("Granica X: "+granice.getMaxX()+" Granica Y: "+granice.getMaxY());
        System.out.println("Różnica X: "+(granice.getMaxX()-xKropelki)+" Różnica Y: "+(granice.getMaxY()-yKropelki));
        
        
    }
   
    public static Image obrazek = new ImageIcon("Ikona.png").getImage();

    int x = 0;
    int y = 0;
    int dx = 4;
    int dy = 4;
    int xKropelki = obrazek.getWidth(null);
    int yKropelki = obrazek.getHeight(null);
   
}