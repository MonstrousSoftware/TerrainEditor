package com.monstrous.terrain.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.monstrous.terrain.TerrainEditor;
import com.monstrous.terrain.terrain.Terrain;

public class TerrainWindow extends Window {
    private final Terrain terrain;
    private final TerrainEditor terrainEditor;
    public boolean showTerrain = true;
    public boolean showWireFrame = false;
    private Slider radiusSlider;
    private Label radiusLabel;
    private Label ampLabel;
    private float amplitude;


    public TerrainWindow(Terrain terrain, TerrainEditor terrainEditor, Skin skin) {
        super("Terrain", skin);
        this.terrain = terrain;
        this.terrainEditor = terrainEditor;
        build(skin);
    }

    private void build(Skin skin){
        Table controls = new Table();
        controls.left();

//        final CheckBox terrainCheckbox = new CheckBox("show terrain", skin);
//        terrainCheckbox.setChecked(showTerrain);
//        terrainCheckbox.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                showTerrain = terrainCheckbox.isChecked();
//            }
//        });
//        controls.add(terrainCheckbox).left().row();




        final CheckBox linesCheckbox = new CheckBox("terrain wire frame", skin);
        linesCheckbox.setChecked(showWireFrame);
        linesCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showWireFrame = linesCheckbox.isChecked();
                terrain.setWireFrameMode(showWireFrame);
                terrain.generateBlocks();
            }
        });
        controls.add(linesCheckbox).left().row();


        // amplitude
        amplitude = terrain.getAmplitude();
        final Slider ampSlider = new Slider(0f, 25600f, 1f, false, skin);
        ampSlider.setAnimateDuration(0.1f);
        ampSlider.setValue(amplitude);
        ampSlider.setSize(150, 20);
        Label label = new Label("terrain amplitude", skin);
        controls.add(label).left();
        controls.add(ampSlider);

        ampLabel = new Label(String.valueOf(amplitude), skin);
        controls.add(ampLabel).row();
        ampSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                amplitude = ampSlider.getValue();
                ampLabel.setText(String.valueOf((int)amplitude));
                terrain.setAmplitude(amplitude);
                //main.generateVegetation(terrain);

            }
        });


        // altitude
        final Slider altSlider = new Slider(-10000f, 10000f, 1f, false, skin);
        altSlider.setAnimateDuration(0.1f);
        altSlider.setValue(terrain.getAltitude());
        altSlider.setSize(150, 20);
        controls.add(new Label("terrain altitude", skin)).left();
        controls.add(altSlider);

        Label altLabel = new Label(String.valueOf((int) terrain.getAltitude()), skin);
        controls.add(altLabel).row();
        altSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                terrain.setAltitude(altSlider.getValue());
                altLabel.setText(String.valueOf((int) terrain.getAltitude()));
            }
        });




//
//
//        // edit radius
//        radiusSlider = new Slider(1f, 300f, 1f, false, skin);
//        radiusSlider.setAnimateDuration(0.1f);
//        radiusSlider.setValue(terrainEditor.terrainEditRadius);
//        radiusSlider.setSize(150, 20);
//        controls.add(new Label("edit radius", skin)).left();
//        controls.add(radiusSlider);
//        radiusLabel = new Label(String.valueOf((int) terrainEditor.terrainEditRadius), skin);
//        controls.add(radiusLabel).row();
//        radiusSlider.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                terrainEditor.terrainEditRadius = (radiusSlider.getValue());
//                radiusLabel.setText(String.valueOf((int)  terrainEditor.terrainEditRadius));
//            }
//        });
//
//
//        Table brushMode = new Table();
//        //brushMode.left();
//        //brushMode.debug();
//        brushMode.add(new Label("Brush mode:", skin)).row();
//        CheckBox upDownButton = new CheckBox("up/down", skin );
//        upDownButton.setChecked(true);
//        brushMode.add(upDownButton.left()).width(100);
//        CheckBox eraseButton = new CheckBox("erase", skin);
//        brushMode.add(eraseButton.left()).width(100);
//        CheckBox flattenButton = new CheckBox("flatten", skin);
//        brushMode.add(flattenButton.left()).width(100);
//        CheckBox smoothButton = new CheckBox("smooth", skin);
//        brushMode.add(smoothButton.left()).width(100);
//
//        upDownButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                terrainEditor.brushMode = TerrainEditor.BrushMode.UP_DOWN;
//            }
//        });
//        eraseButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                terrainEditor.brushMode = TerrainEditor.BrushMode.ERASE;
//            }
//        });
//        flattenButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                terrainEditor.brushMode = TerrainEditor.BrushMode.FLATTEN;
//            }
//        });
//        smoothButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                terrainEditor.brushMode = TerrainEditor.BrushMode.SMOOTH;
//            }
//        });
//
//
//        // act as radio buttons
//        ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<>(upDownButton, eraseButton, flattenButton, smoothButton);
//        buttonGroup.setMinCheckCount(1);
//        buttonGroup.setMaxCheckCount(1);
//        buttonGroup.setUncheckLast(true);
//        controls.add(brushMode).colspan(3);
//        controls.row();

        add(controls);
        pack();
    }

    public void update(){
//        radiusLabel.setText(String.valueOf((int)  terrainEditor.terrainEditRadius));
//        radiusSlider.setValue(terrainEditor.terrainEditRadius);
    }


}
