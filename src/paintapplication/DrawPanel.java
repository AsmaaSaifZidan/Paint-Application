/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package paintapplication;


import paintdrawtools.*;
import paintshapetools.*;
import paintcontrols.*;
import myutilities.*;
import painttools.*;
import paintvisuals.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Robot;

public class DrawPanel extends JPanel implements Runnable
{
    public int frameCount; 
    
    public ArrayList elements;
    
    protected Boolean   mousePressed;
    protected boolean mouseClicked; 
    
    public Tool currentTool;
    public AbstractTool tool;

    protected Color      brushColor;
    public Color backgroundColor;
    
    protected Thread animator; 
   
    public DrawPanel()
    {
        backgroundColor = Color.white;

        setBackground(backgroundColor);
        setPreferredSize(new Dimension(1024, 768));
        
        MouseHandler handler = new MouseHandler();
        
        addMouseListener(handler);
        
        elements = new ArrayList();
        elements.add(new FillerElement(getBackground()));
        mousePressed = false;
        
        brushColor = Color.black;
        
        tool = new PencilTool(brushColor, 1);
        
        currentTool = Tool.PENCIL;
        
        frameCount = -1; 
    }
    
    public void startTheThread()
    {
        animator = new Thread(this);
        animator.start();
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            if (getMousePosition()!=null)
            {
                if ( isPaintingTool(currentTool) )
                {
                    if ( getMousePressed() == false )
                    {
                        tool.setCPoint(null);
                        tool.setDrawing(false);
                    }
                    if ( tool.isDrawing() )
                    {
                        if (getMousePosition() != tool.getCPoint())
                        {
                            if (getMousePressed())
                            {
                                tool.setPPoint(tool.getCPoint());
                                if (getMousePosition() != null)
                                {
                                    tool.setCPoint( new Point(this.getMousePosition()));
                                }

                                if ( currentTool == Tool.PENCIL)
                                {
                                    elements.add(new PencilElement(tool.getPPoint(), tool.getCPoint(),
                                        tool.getColor(), tool.getStroke()));
                                }

                                if ( currentTool == Tool.BRUSH)
                                {
                                    elements.add(new BrushElement(tool.getPPoint(), tool.getCPoint(),
                                        tool.getColor(), tool.getStroke(), tool.getStrokeStyle()));
                                }
                                if (currentTool == Tool.ERASER)
                                {
                                    elements.add(new EraserElement(tool.getPPoint(),
                                        tool.getColor(), tool.getStroke(), tool.getStrokeStyle()));
                                }
                               if (currentTool == Tool.AIRBRUSH)
                                {
                                    elements.add(new AirBrushElement(tool.getPPoint(),
                                        tool.getColor(), tool.getStroke(), tool.getStrokeStyle()));
                                }
                            }
                            repaint();
                        }
                    }
                    else
                    {
                        if (this.getMousePressed())
                        {
                            tool.setCPoint(getMousePosition());
                            tool.setDrawing(true);
                        }
                    }
                }
                if ( isDraggingTool(currentTool) )
                {

                    if ( (tool.isDrawing()) && (getMousePosition() == null))
                    {
                        tool.setDrawing (false);
                        mousePressed = false;
                    }
                    
                    if ( (mousePressed) )
                    {
                        if (tool.isDrawing() == false)
                        {
                            tool.setDrawing( true );

                            ((DragTool) tool).setSPoint(getMousePosition());
                            tool.setCPoint(getMousePosition());

                            addTemporaryDragElement(tool.getColor(), ((DragTool) tool).getSPoint(),
                                                    tool.getCPoint(), tool.getStroke(),tool.getStrokeStyle());
                        }
                        if (getMousePosition() != tool.getCPoint())
                        {
                            ((DragTool)tool).setFPoint(getMousePosition());
                            
                            tool.setPPoint(tool.getPPoint());
                            tool.setCPoint(getMousePosition());
                            
                            elements.remove(elements.size()-1);

                            addTemporaryDragElement(tool.getColor(), ((DragTool) tool).getSPoint(),
                                                    tool.getCPoint(), tool.getStroke(),tool.getStrokeStyle());
                        }
                    }

                    if (( tool.isDrawing()) && (mousePressed==false) )
                    {
                        tool.setDrawing(false);

                        elements.remove(elements.size()-1);
                        
                        switch(currentTool)
                        {
                            case OVAL:
                            {
                                elements.add( new OvalElement(tool.getColor(), ((DragTool) tool).getSPoint(), ((DragTool) tool).getFPoint(), tool.getStroke(), tool.getStrokeStyle()));
                                break;
                            }
                            case RECTANGLE:
                            {
                                elements.add( new RectangleElement(tool.getColor(), ((DragTool) tool).getSPoint(), ((DragTool) tool).getFPoint(),tool.getStroke(),tool.getStrokeStyle()));
                                break;
                            }
                            case ROUND_RECT:
                            {
                                elements.add(new RoundRectElement(tool.getColor(), ((DragTool) tool).getSPoint(), tool.getCPoint(),tool.getStroke(),tool.getStrokeStyle()));
                                break;
                            }
                            case LINE:
                            {
                                elements.add( new LineElement(tool.getColor(), ((DragTool) tool).getSPoint(), ((DragTool) tool).getFPoint(),tool.getStroke(),tool.getStrokeStyle()));
                                break;
                            }
                        }
                    }
                    
                    repaint();
                }

                if (currentTool == Tool.FILLER)
                {
                    //if (mouseClicked)
                    //{
                    if (mousePressed)
                    {
                        elements.set(0,new FillerElement(brushColor));
                        repaint();
                    }
                        //mouseClicked = false;
                        //((FillerTool) tool).fillAllSides(MouseInfo.getPointerInfo().getLocation(), brushColor);
                    //}
                }
                frameCount++;
            }
            else
            {
                tool.setDrawing(false);
                tool.setCPoint(null);
            }
           if (currentTool == Tool.PICKER)
            {
                PointerInfo pointer;
                pointer = MouseInfo.getPointerInfo();
                Robot robot;
                try
                {
                    robot = new Robot();
                    Main.paint.paintTools.toolOptionPanel.setColor( robot.getPixelColor(pointer.getLocation().x, pointer.getLocation().y) );

                    if (mousePressed)
                    {
                        setBrushColor(Main.paint.paintTools.toolOptionPanel.getColor());
                        Main.paint.colorPicker.currentClrPanel.setBackground(Main.paint.paintTools.toolOptionPanel.getColor());
                        tool.setColor(Main.paint.paintTools.toolOptionPanel.getColor());
                        repaint();
                    }
                    robot = null;
                }
                catch (AWTException exception)
                {
                    exception.printStackTrace();
                }
            }
            try {
            Thread.sleep(10);
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }
    
   
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        for(int i=0; i<elements.size(); i++)
        {
            ((PaintElement)elements.get(i)).update(g);
        }
    }


    public void addTemporaryDragElement(Color clr, Point sPoint, Point cPoint, int strokeW, StrokeStyle strokeS)
    {
        switch(currentTool)
        {
            case OVAL:
            {
                elements.add(new OvalElement(clr, sPoint, cPoint, strokeW, strokeS));
                break;
            }
            case RECTANGLE:
            {
                elements.add(new RectangleElement(clr, sPoint, cPoint, strokeW, strokeS));
                break;
            }
            case ROUND_RECT:
            {
                elements.add(new RoundRectElement(clr, sPoint, cPoint, strokeW, strokeS));
                break;
            }
            case LINE:
            {
                elements.add(new LineElement(clr, sPoint, cPoint, strokeW, strokeS));
                break;
            }
        }
    }


    public boolean isPaintingTool(Tool tool)
    {
        switch(tool)
        {
            case PENCIL:
            case BRUSH:
            case AIRBRUSH:
            case ERASER:
            {
                return true;
            }
        }
        return false;
    }

    public boolean isDraggingTool(Tool tool)
    {
        switch (tool)
        {
            case LINE:
            case OVAL:
            case RECTANGLE:
            case ROUND_RECT:
            {
                return true;
            }
        }
        return false;
    }
    
    public void setBrushColor(Color clr)
    {
        brushColor = clr;
        tool.setColor(clr);
    }

    public Color getBrushColor()
    {
        return brushColor; 
    }

    public void setTool (Tool tool)
    {
        currentTool = tool;
        setPaintTool(tool);

        if (currentTool == Tool.PICKER)
        {
            if (animator == null)
               startTheThread();
        }
    }

    public Tool getTool()
    {
        return currentTool;
    }
    
    public void setPaintTool(Tool tool)
    {
        int stroke = this.tool.getStroke();
        
        if ( tool != Tool.ERASER && stroke >= 16)
        {
          this.tool.setStrokeWidth(16);
        }
        
        switch (tool)
        {
            case PENCIL:
            {
                this.tool = new PencilTool(brushColor, this.tool.getStroke());
                Main.paint.paintTools.setToolOptionPanel(new PencilToolPanel(Tool.PENCIL, this.tool.getStroke()));
                break;
            }
            case BRUSH:
            {
                this.tool = new BrushTool(brushColor, this.tool.getStroke(), StrokeStyle.LINE);
                Main.paint.paintTools.setToolOptionPanel(new BrushToolPanel(Tool.BRUSH, this.tool.getStroke()));
                break;
            }
            case AIRBRUSH:
            {
                this.tool = new AirBrushTool(brushColor, this.tool.getStroke());
                Main.paint.paintTools.setToolOptionPanel(new AirBrushToolPanel(Tool.AIRBRUSH,this.tool.getStroke()));
                break;
            }
            case ERASER:
            {
                this.tool = new EraserTool(getBackground(), this.tool.getStroke(), StrokeStyle.SQUARE);
                Main.paint.paintTools.setToolOptionPanel(new EraserToolPanel(Tool.ERASER,this.tool.getStroke()));
                break;
            }
            case RECTANGLE:
            {
                this.tool = new RectangleTool(brushColor, this.tool.getStroke());
                Main.paint.paintTools.setToolOptionPanel(new RectShapeToolPanel(Tool.RECTANGLE,this.tool.getStroke()));
                break;
            }
            case ROUND_RECT:
            {
                this.tool = new RoundRectTool(brushColor, this.tool.getStroke());
                Main.paint.paintTools.setToolOptionPanel(new RectShapeToolPanel(Tool.ROUND_RECT,this.tool.getStroke()));
                break;
            }
            case OVAL:
            {
                this.tool = new OvalTool(brushColor, this.tool.getStroke());
                Main.paint.paintTools.setToolOptionPanel(new RectShapeToolPanel(Tool.OVAL,this.tool.getStroke()));
                break;
            }
            case LINE:
            {
                this.tool = new LineTool(brushColor, this.tool.getStroke(), StrokeStyle.LINE);
                Main.paint.paintTools.setToolOptionPanel(new DragShapeToolPanel(Tool.LINE,this.tool.getStroke()));
                break;
            }
            case FILLER:
            {
                this.tool = new FillerTool(brushColor, this.tool.getStroke());
                Main.paint.paintTools.setToolOptionPanel(new PencilToolPanel(Tool.PENCIL,this.tool.getStroke()));
                break;
            }
            case PICKER:
            {
                this.tool = new FillerTool(brushColor, this.tool.getStroke());
                Main.paint.paintTools.setToolOptionPanel(new ColorPickerToolPanel(Tool.PICKER, brushColor));
                break;
            }
        }
    }
    
    public boolean getMousePressed()
    {
        return mousePressed;
    }
    
    
    private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent event) 
        {
            if (animator == null)
                   startTheThread();
            
            mousePressed = true;
        }
   
        @Override
        public void mouseReleased(MouseEvent event)
        {
            mousePressed = false;
        }
        
        @Override
        public void mouseClicked(MouseEvent event)
        {
            mouseClicked = true;
        }
        @Override
        public void mouseExited(MouseEvent event)
        {
            mousePressed = false;
        }
    }
}
