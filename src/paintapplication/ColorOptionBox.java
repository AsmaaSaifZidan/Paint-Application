
package paintapplication;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.awt.Dimension;

public class ColorOptionBox extends JPanel
{
    Color   color;
    boolean selected; 

    public ColorOptionBox(Color clr)
    {
        color = clr; 
        selected = false;

        setBackground(color);

        setPreferredSize(new Dimension(48, 48));

        MouseHandler handler = new MouseHandler();
        addMouseListener(handler);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.setColor(Color.BLUE);
        g.drawRect(0,0,getWidth(),getHeight());

        if (selected)
        {
            g.setColor(Color.BLUE);
            g.drawRect(0,0,getWidth(), getHeight());

            g.setColor(Color.pink);
            g.drawRect(0,0,getWidth(), getHeight());
        }
    }

    private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent event) 
        {
            Main.paint.colorPicker.deselectAll();
            selected = true;
            Main.paint.drawPanel.setBrushColor(color);
            Main.paint.colorPicker.currentClrPanel.setBackground(color);
            Main.paint.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent event)
        {
        }
        
        @Override
        public void mouseClicked(MouseEvent event)
        {
        }
        
        @Override
        public void mouseEntered(MouseEvent event)
        {
            
        }
    }
}
