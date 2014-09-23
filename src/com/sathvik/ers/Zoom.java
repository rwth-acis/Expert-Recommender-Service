package com.sathvik.ers;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import y.view.Graph2DView;

/** Action that applies a specified zoom level to the given view. */
public class Zoom extends AbstractAction {
  Graph2DView view;
  double factor;

  public Zoom(Graph2DView view, double factor) {
    super("Zoom " + (factor > 1.0 ? "In" : "Out"));
    this.view = view;
    this.factor = factor;
    this.putValue(Action.SHORT_DESCRIPTION, "Zoom " + (factor > 1.0 ? "In" : "Out"));
  }

  public void actionPerformed(ActionEvent e) {
    view.setZoom(view.getZoom() * factor);
    // Adjusts the size of the view's world rectangle. The world rectangle 
    // defines the region of the canvas that is accessible by using the 
    // scrollbars of the view.
    Rectangle box = view.getGraph2D().getBoundingBox();
    view.setWorldRect(box.x - 20, box.y - 20, box.width + 40, box.height + 40);
    view.updateView();
  }
}