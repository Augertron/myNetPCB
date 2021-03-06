package com.mynetpcb.pad.dialog.panel.inspector;


import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.pad.shape.Arc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class ArcPanelBuilder extends AbstractPanelBuilder<Shape>{

  private JTextField startAngField,extAngField; 
        
    public ArcPanelBuilder(UnitComponent component) {
        super(component,new GridLayout(8,1));
        //***layer        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                layerCombo=new JComboBox(Layer.PCB_SYMBOL_LAYERS);layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
                layoutPanel.add(panel);        
        //***Left        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Center X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                leftField=new JTextField("0");leftField.addKeyListener(this);  panel.add(leftField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        //***Top        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Center Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                topField=new JTextField("0"); topField.addKeyListener(this); panel.add(topField,BorderLayout.CENTER);
                layoutPanel.add(panel);
               
        //***Thickness        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Thickness"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                thicknessField=new JTextField("0"); thicknessField.addKeyListener(this); panel.add(thicknessField,BorderLayout.CENTER);
                layoutPanel.add(panel); 
        //***Fill
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Fill"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                fillCombo=new JComboBox(fillValues);fillCombo.addActionListener(this);  panel.add(fillCombo,BorderLayout.CENTER);
                layoutPanel.add(panel);        
        //****Width
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Radius"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                widthField=new JTextField("");  widthField.addKeyListener(this);panel.add(widthField,BorderLayout.CENTER);
                layoutPanel.add(panel);       
        //****Arc
                panel=new JPanel(); panel.setLayout(new BorderLayout());         
                label=new JLabel("Start Angle"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                startAngField=new JTextField("");  startAngField.addKeyListener(this); panel.add(startAngField,BorderLayout.CENTER);
                layoutPanel.add(panel);
                
                panel=new JPanel(); panel.setLayout(new BorderLayout());         
                label=new JLabel("Extend Angle"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                extAngField=new JTextField("");  extAngField.addKeyListener(this); panel.add(extAngField,BorderLayout.CENTER);
                layoutPanel.add(panel);                 
                
    }

    @Override
    public void updateUI() {
        Arc arc=(Arc)getTarget();  
        
        leftField.setText(toUnitX(arc.getX() ));
        topField.setText(toUnitY(arc.getY())); 
        
        thicknessField.setText(String.valueOf(Grid.COORD_TO_MM(arc.getThickness())));    
        widthField.setText(String.valueOf(Grid.COORD_TO_MM(arc.getWidth())));
        
        setSelectedItem(layerCombo, (getTarget()).getCopper());
        setSelectedIndex(fillCombo,(getTarget().getFill()==Shape.Fill.EMPTY?0:1)); 
        
        extAngField.setText(String.valueOf(arc.getExtendAngle()));
        startAngField.setText(String.valueOf(arc.getStartAngle())); 
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
        if(e.getSource()==layerCombo){
            getTarget().setCopper((Layer.Copper)layerCombo.getSelectedItem());
        }
        if(e.getSource()==fillCombo){
           getTarget().setFill(Shape.Fill.values()[fillCombo.getSelectedIndex()]);
        }
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();         
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() !=KeyEvent.VK_ENTER) return;
        Arc arc=(Arc)getTarget();
      
        if(e.getSource()==this.thicknessField){
           getTarget().setThickness(Grid.MM_TO_COORD(Double.parseDouble(thicknessField.getText())));  
        }
        
        if(e.getSource()==this.leftField){
           getTarget().setX(Grid.MM_TO_COORD(Double.parseDouble(leftField.getText())));           
        }
        
        if(e.getSource()==this.topField){            
           getTarget().setY(Grid.MM_TO_COORD(Double.parseDouble(topField.getText())));             
        }
        
        if(e.getSource()==this.widthField){
           getTarget().setWidth(Grid.MM_TO_COORD(Double.parseDouble(widthField.getText()))); 
        }
        
        if(e.getSource()==startAngField){
            //skip negativ values
            Double angle=(Double.parseDouble(startAngField.getText()));
            if((angle<0)||(angle.compareTo(360.0)>0)){
                startAngField.setText(String.valueOf(arc.getStartAngle())); 
                return;
            }
            arc.setStartAngle(angle);    
        }
        if(e.getSource()==extAngField){
            //-360<angle<360
            Double angle=(Double.parseDouble(extAngField.getText()));
            if((angle.compareTo(-360.0)==-1)||(angle.compareTo(360.0)==1)){
                extAngField.setText(String.valueOf(arc.getExtendAngle())); 
                return;
            }
            arc.setExtendAngle((Double.parseDouble(extAngField.getText())));    
        }
        
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
        
         
    }

}
