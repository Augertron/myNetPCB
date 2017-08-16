package com.mynetpcb.gerber.processor.aperture;


import com.mynetpcb.core.board.shape.FootprintShape;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.gerber.aperture.ApertureDictionary;
import com.mynetpcb.gerber.aperture.type.CircleAperture;
import com.mynetpcb.gerber.capi.Processor;
import com.mynetpcb.pad.shape.Arc;

import java.util.Collection;

public class ApertureArcProcessor implements Processor{
    private final ApertureDictionary dictionary;
    
    public ApertureArcProcessor(ApertureDictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public void process(Unit<? extends Shape> board, int layermask) {          
        //arcs
        for(Arc arc:board.<Arc>getShapes(Arc.class,layermask)){                        
            processArc(arc);
        }
        //arcs in footprints
        for(FootprintShape footprint:board.<FootprintShape>getShapes(FootprintShape.class)){
            for(Shape shape:footprint.getShapes()){
                if(!shape.isVisibleOnLayers(layermask)){
                    continue;
                }
                if(shape.getClass()== Arc.class){
                    processArc((Arc)shape);
                }
            }
        }
    }
    
    private void processArc(Arc arc){
        CircleAperture aperture=new CircleAperture();
        aperture.setDiameter(arc.getThickness());
        dictionary.add(aperture);
    }
}
