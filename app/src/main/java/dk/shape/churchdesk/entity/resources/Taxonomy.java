package dk.shape.churchdesk.entity.resources;

import android.graphics.Color;

import java.util.HashMap;

/**
 * Created by vitiokss on 29/10/15.
 */
public class Taxonomy {

    HashMap<Integer, String> colors = new HashMap<>();

    public Taxonomy() {

        // Add the colors for the taxonomies values.
        this.colors.put(0, "#22A7F0"); // Light blue
        this.colors.put(1, "#1F3A93"); // Dark blue
        this.colors.put(2, "#2ECC71"); // Light green
        this.colors.put(3, "#1E824C"); // Dark green
        this.colors.put(4, "#F22613"); // Red
        this.colors.put(5, "#FFB61E"); // Yellow
        this.colors.put(6, "#F9690E"); // Orange
        this.colors.put(7, "#9B59B6"); // Bordeaux/purple
        this.colors.put(8, "#BDC3C7"); // Gray
        this.colors.put(9, "#22313F"); // Black

    }

    public int getColor(Integer key) {
        return Color.parseColor(this.colors.get(key));
    }

}
