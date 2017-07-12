package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class HolePanelBuilder extends AbstractPanelBuilder<Shape>{
    public HolePanelBuilder(BoardComponent component) {
        super(component, new GridLayout(4, 1));        
        //***Left        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,24)); panel.add(label,BorderLayout.WEST);
                leftField=new JTextField("0");leftField.addKeyListener(this);  panel.add(leftField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
        //***Top        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,24)); panel.add(label,BorderLayout.WEST);
                topField=new JTextField("0"); topField.addKeyListener(this); panel.add(topField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        //width  size      
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Hole diameter"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,24)); panel.add(label,BorderLayout.WEST);
                widthField=new JTextField("0"); widthField.addKeyListener(this); panel.add(widthField,BorderLayout.CENTER);
                layoutPanel.add(panel);      
        //***Clearance       
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Clearance"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,24)); panel.add(label,BorderLayout.WEST);
                clearanceField=new JTextField(); clearanceField.addKeyListener(this); panel.add(clearanceField,BorderLayout.CENTER);
                layoutPanel.add(panel); 
        
    }
    
    @Override
    public void updateUI() { 
        leftField.setText(toUnitX(getTarget().getX()));
        topField.setText(toUnitY(getTarget().getY()));
        widthField.setText(toUnit(getTarget().getWidth()));
        clearanceField.setText(String.valueOf(Grid.COORD_TO_MM(((ClearanceTarget)getTarget()).getClearance())));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        if(e.getSource()==this.leftField){
           getTarget().setX(fromUnit(this.leftField.getText())); 
        }
        
        if(e.getSource()==this.topField){
            getTarget().setY(fromUnit(this.topField.getText())); 
        }

        if(e.getSource()==this.widthField){
           getTarget().setWidth(fromUnit(this.widthField.getText()));
        }
        if(e.getSource()==this.clearanceField){
           ((ClearanceTarget)getTarget()).setClearance(Grid.MM_TO_COORD(Double.parseDouble(clearanceField.getText())));
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
    }    
}
