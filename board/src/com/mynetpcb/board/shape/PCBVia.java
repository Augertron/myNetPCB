package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.ClearanceSource;
import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.pad.Net;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import java.util.Objects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Reuse properties
 * thickness -> internal diameter(drill size)
 * width     -> external diameter(via size)
 */
public class PCBVia  extends Shape implements PCBShape,ClearanceTarget,Externalizable,Net{

    private int clearance;
    
    private String net;
    
    public enum Type{
        BURIED, 
        BLIND,
        THROUGH_HOLE
    }
    
    
    public PCBVia() {
        super(0,0,0,0,Grid.MM_TO_COORD(0.2),Layer.LAYER_ALL);
        this.fillColor=Color.WHITE;
        this.setWidth(Grid.MM_TO_COORD(0.4));
    
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
           return super.alignToGrid(isRequired);
        }else{
            return null;
        }
    }
    
    @Override
    public void setWidth(int width){
       super.setWidth(width);
       super.setHeight(width);
    }
    @Override
    public PCBVia clone() throws CloneNotSupportedException {
        PCBVia copy = (PCBVia)super.clone();
        return copy;
    }

    @Override
    public String getDisplayName() {
        return "Via";
    }

    @Override
    public Rectangle calculateShape() {
        return new Rectangle(getX() - getWidth()/2, getY() - getWidth()/2, getWidth(),getWidth());
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);

        if (!scaledRect.intersects(viewportWindow)) {
            return;
        }
        g2.setColor(isSelected() ? Color.GRAY : fillColor);

        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        ellipse.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                         scaledRect.getWidth(), scaledRect.getHeight());
                        
        g2.fill(ellipse);
        ellipse.setFrame(getX() - thickness/2, getY() - thickness/2, thickness,thickness);
        scaledRect = Utilities.getScaleRect(ellipse.getBounds(), scale);
        ellipse.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                         scaledRect.getWidth(), scaledRect.getHeight());
        
        g2.setColor(Color.BLACK);
        g2.fill(ellipse);
        ellipseProvider.reset();

    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {

        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        
        ellipse.setFrame(getX() - getWidth()/2, getY() - getWidth()/2, getWidth(),getWidth());
        g2.setColor(Color.WHITE);                
        g2.fill(ellipse);
        g2.setStroke(new BasicStroke(Grid.MM_TO_COORD(0.1)));
        g2.setColor(Color.BLACK); 
        g2.draw(ellipse);
        
        ellipseProvider.reset();
    
    }
    
    @Override
    public long getOrderWeight() {
        return 3;
    }

    @Override
    public <T extends PCBShape & ClearanceSource> void drawClearence(Graphics2D g2,
                                                                     ViewportWindow viewportWindow,
                                                                     AffineTransform scale, T source) {
        Shape shape=(Shape)source;
        
        Rectangle inner=getBoundingShape().getBounds();             
        inner.grow(source.getClearance(), source.getClearance());
        
        Rectangle2D scaledRect = Utilities.getScaleRect(inner ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        if(Objects.equals(source.getNetName(), this.net)&&(!("".equals(source.getNetName())))){
            return;
        }
        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        
        ellipse.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                         scaledRect.getWidth(), scaledRect.getHeight());
        
        g2.setColor(Color.BLACK);                
        g2.fill(ellipse);

        ellipseProvider.reset();
        
    }

    @Override
    public <T extends PCBShape & ClearanceSource> void printClearence(Graphics2D g2, T source) {
        
        if(Objects.equals(source.getNetName(), this.net)&&(!("".equals(source.getNetName())))){
            return;
        }
        
        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        
        Rectangle rect = new Rectangle(getX() - getWidth()/2, getY() - getWidth()/2, getWidth(),getWidth());
        rect.grow(source.getClearance(), source.getClearance());
        ellipse.setFrame(rect.x ,rect.y,rect.getWidth(),rect.getWidth());
                                        
        g2.setColor(Color.WHITE);                
        g2.fill(ellipse);
        
        ellipseProvider.reset();    

    }
    
    @Override
    public void setClearance(int clearance) {
        this.clearance=clearance;
    }

    @Override
    public int getClearance() {
        return clearance;
    }
    
    @Override
    public String toXML() {
        StringBuffer xml = new StringBuffer();
        xml.append("<via type=\"\" x=\""+getX()+"\" y=\""+getY()+"\" width=\""+getWidth()+"\" drill=\""+thickness+"\"   clearance=\""+clearance+"\" net=\""+this.net+"\" />");
        return xml.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element=(Element)node;
        setX(Integer.parseInt(element.getAttribute("x")));
        setY(Integer.parseInt(element.getAttribute("y")));
        setWidth(Integer.parseInt(element.getAttribute("width")));
        setThickness(Integer.parseInt(element.getAttribute("drill")));
        this.clearance=element.getAttribute("clearance").equals("")?0:Integer.parseInt(element.getAttribute("clearance"));
        this.net=element.getAttribute("net");
    }
    @Override
    public String getNetName() {
        
        return this.net;
    }

    @Override
    public void setNetName(String net) {
       this.net=net;
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }


    static class Memento extends AbstractMemento<Board,PCBVia>{
        private int Ax;
        
        private int Ay;
        
        private int width;
        
        private String net;
        
        public Memento(MementoType mementoType){
           super(mementoType); 
        }
        
        public void loadStateTo(PCBVia shape) {
            super.loadStateTo(shape);
            shape.setX(Ax);
            shape.setY(Ay);
            shape.setWidth(width);
            shape.setNetName(net);
        }
        

        public void saveStateFrom(PCBVia shape){
            super.saveStateFrom(shape);
            Ax=shape.getX();
            Ay=shape.getY();
            width=shape.getWidth();
            net=shape.net;
        }
        @Override
        public boolean equals(Object obj){
            if(this==obj){
              return true;  
            }
            if(!(obj instanceof Memento)){
              return false;  
            }
            
            Memento other=(Memento)obj;            
        
            return(getUUID().equals(other.getUUID())&&
                   getMementoType().equals(other.getMementoType())&&
                   Ax==other.Ax&&thickness==other.thickness&&width==other.width&&
                   Ay==other.Ay&&Objects.equals(this.net, other.net)                
                );
                      
        }
        
        @Override
        public int hashCode(){
            int hash=getUUID().hashCode();
                hash+=this.getMementoType().hashCode();
                hash+=Ax+Ay+thickness+width+Objects.hashCode(net);
            return hash;
        }        
        public boolean isSameState(Board unit) {
            PCBVia via=(PCBVia)unit.getShape(getUUID());
            return( 
                  Ax==via.getX()&&
                  Ay==via.getY()&&
                  thickness==via.getThickness()&&
                  width==via.getWidth()&&Objects.equals(this.net, via.net)
                );
        }
    }
}
