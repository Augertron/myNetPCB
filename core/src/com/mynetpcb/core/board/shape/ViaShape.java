package com.mynetpcb.core.board.shape;

import com.mynetpcb.core.board.ClearanceTarget;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.pad.Net;

public abstract class ViaShape extends Shape implements ClearanceTarget,Externalizable,Net{
    public ViaShape() {
        super(0,0,0,0,Grid.MM_TO_COORD(0.2),Layer.LAYER_ALL);
    }

}
