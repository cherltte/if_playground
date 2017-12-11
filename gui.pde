class GUI
{
boolean [] ledBarTouchedIsStarted = new boolean [NUM_STRIPS];
boolean [] ledBarReachedMax = new boolean [NUM_STRIPS];
GUI()
{
        for(int i  = 0; i <NUM_STRIPS; i++) {
                ledBarTouchedIsStarted[i] = false;
                ledBarReachedMax[i] = false;
        }
}

void display_cp5()
{
        //win01 win_module_selector
        float _win_h = height-20;
        mBox = cp5.addCheckBox("indx_module")
               .setPosition(win01_xy[0].x+win_module_selector_x, win01_xy[0].y+win_module_selector_y)
               .setSize(win_module_selector_w, win_module_selector_h)
               .setItemsPerRow(6)
               .setSpacingColumn(win_module_selector_sp)
               .setSpacingRow(win_module_selector_sp)
               .addItem("0", 0).addItem("1", 1).addItem("2", 2).addItem("3", 3).addItem("4", 4).addItem("5", 5)
               .addItem("6", 6).addItem("7", 7).addItem("8", 8).addItem("9", 9).addItem("10", 10).addItem("11", 11)
               .addItem("12", 12).addItem("13", 13).addItem("14", 14).addItem("15", 15).addItem("16", 16).addItem("17", 17)
               .addItem("18", 18).addItem("19", 19).addItem("20", 20).addItem("21", 21).addItem("22", 22).addItem("23", 23)
               .addItem("24", 24).addItem("25", 25).addItem("26", 26).addItem("27", 27).addItem("28", 28).addItem("29", 29)
               .addItem("30", 30).addItem("31", 31).addItem("32", 32).addItem("33", 33).addItem("34", 34).addItem("35", 35)
        ;
        mBox.hideLabels();

        float mbox_ctrl_x = win01_xy[1].x-100;
        float mbox_ctrl_y = win01_xy[0].y+win_module_selector_y;
        float mbox_ctrl_sp = 10;

        mbox_ctrl01 = cp5.addCheckBox("indx_module_comnd")
                      .setPosition(mbox_ctrl_x, mbox_ctrl_y)
                      .setSize(win_module_selector_w, win_module_selector_h)
                      .setCaptionLabel("all: a")
                      .addItem("All", 999)
        ;

        mbox_ctrl02 =  cp5.addBang("ACTION")
                      .setPosition(mbox_ctrl_x, mbox_ctrl_y+ win_module_selector_h+mbox_ctrl_sp)
                      .setSize(win_module_selector_w, win_module_selector_h)
                      .setCaptionLabel("")
                      .setId(1)
        ;
        //win01 window for waveBox_pos
        waveBox_ctrl = cp5.addCheckBox("waveBox_mouse_comnd")
                       .setPosition(mbox_ctrl_x, height-90)
                       .setSize(win_module_selector_w, win_module_selector_h)
                       .setCaptionLabel("mouse trigger")
                       .addItem("mouse trigger", 0)
        ;
        waveBox_ctrl1 = cp5.addCheckBox("waveBox_colorSet")
                        .setPosition(mbox_ctrl_x, height-120)
                        .setSize(win_module_selector_w, win_module_selector_h)
                        .setCaptionLabel("colorSet")
                        .addItem("setColor", 0)
        ;
        waveBox_ctrl2 = cp5.addCheckBox("waveBox_newLife")
                        .setPosition(mbox_ctrl_x, height-150)
                        .setSize(win_module_selector_w, win_module_selector_h)
                        .setCaptionLabel("newLife")
                        .addItem("newLife", 0)
        ;

        waveBox_ctrl3 = cp5.addCheckBox("waveBox_rainbow")
                        .setPosition(mbox_ctrl_x, height-180)
                        .setSize(win_module_selector_w, win_module_selector_h)
                        .setCaptionLabel("rainbow")
                        .addItem("rainbow", 0)
        ;
        // cp5.addBang("bang")
        // .setPosition(width/2+60, height-100)
        // .setSize(win_module_selector_w, win_module_selector_h)
        // .setTriggerEvent(Bang.RELEASE)
        // .setLabel("another life")
        // ;

        //win01 window for ledbar
        int colorwheel_w = 200;
        int _slider_w = 50;
        cp5.addColorWheel("c", int(width - win01_pd - colorwheel_w), int(win01_xy[2].y + win01_pd*2), colorwheel_w ).setRGB(color(-2748419))
        .setCaptionLabel("")
        ;
        // cp5.addColorWheel("c" , 250 , 10 , 200 ).setRGB(color(128,0,255));

        cp5.addSlider("bar_H")
        .setRange(1, 38)
        .setValue(int(38))
        .setPosition(win01_xy[2].x+100, int(win01_xy[2].y + win01_pd*2))
        .setSize(10, 190)
        .setBroadcast(true)
        .setNumberOfTickMarks(36)
        .setSliderMode(Slider.FIX)
        ;

        cp5.addSlider("color_R")
        .setRange(1, 4)
        .setValue(1)
        .setPosition(win01_xy[2].x+150, int(win01_xy[2].y + win01_pd*2))
        .setSize(10, 190)
        .setBroadcast(true)
        .setNumberOfTickMarks(4)
        .setSliderMode(Slider.FIX)
        ;

        int _btPick_w = 40;
        int _btPick_h = 20;
        int _btPick_pd = 10;
        int _btPick_x = int(width - win01_pd - colorwheel_w);
        int _btPick_y = height - _btPick_w - 35;
        for (int i = 0; i < 4; i++)
        {
                cp5.addButton("pick_"+i)
                .setSize(_btPick_w, _btPick_h)
                .setValue(100)
                .setPosition(_btPick_x+(_btPick_w+_btPick_pd)*i, _btPick_y)
                ;
        }
}
void draw_mouse_control()
{
        if(mouseTriggerIsOn)
        {
                if (mouseX > win01_xy[1].x + 20 && mouseX < win01_xy[2].x - 20 && mouseY > win01_xy[1].y - 20 && mouseY < height - 20)
                {
                        stroke(125);
                        noFill();
                        ellipse(mouseX, mouseY,  mouseTriggerRadius, mouseTriggerRadius);
                }
        }

}
void draw_win01_indicator_gui()
{
        pushStyle();
        //win01
        stroke(100);
        float _win_w = win01_xy[2].x-win01_xy[1].x;
        float _win_h = height-20;
        for (int i =0; i< NUM_WIN01; i++)
        {
                line(win01_xy[i].x-5, win01_xy[i].y, win01_xy[i].x+5, win01_xy[i].y);
                line(win01_xy[i].x, win01_xy[i].y-5, win01_xy[i].x, win01_xy[i].y+5);
                line(win01_xy[i].x+_win_w+5, _win_h, win01_xy[i].x+_win_w-5, _win_h);
                line(win01_xy[i].x+_win_w, _win_h+5, win01_xy[i].x+_win_w, _win_h-5);
        }
        popStyle();
}

void draw_led()
{
        int _w = 6;
        int _pd = 2;
        pushStyle();
        for(int i = 0; i < NUM_STRIPS; i++)
        {
                noStroke();
                fill(basicColor); //-83852
                if (m_strength_gauge[i][var_gauge_pulling] != 0)
                {
                        rect(ledBar_pos[i].x-_w/2-_pd, ledBar_pos[i].y+5+_pd, _w+_pd*2, map(m_strength_gauge[i][var_gauge_pulling], 0, 70, 0, -124));
                }
        }
        popStyle();
}

void draw_waveBox()
{
        pushStyle();
        for (int i = 0; i < NUM_STRIPS; i++)
        {
                if (win_waveBox_state[i]) {
                        stroke(255, 0, 0);
                } else {
                        stroke(255);
                }
                point(win_waveBox_xy[i].x, win_waveBox_xy[i].y);
                // fill(255);
                // textAlign(CENTER);
                // text(i, win_waveBox_xy[i].x, win_waveBox_xy[i].y+8);
        }
        popStyle();
}

int gui_bar_h;
void draw_Pallette()
{
        pushStyle();
        rectMode(CORNER);
        noStroke();
        // for(int i = gui_win_status[vbarBox][vBar_divider]; i > 0 ; i--) {
        int _h = gui_win_status[vbarBox][vBar_h]/gui_win_status[vbarBox][vBar_divider]*5;
        for (int i = 0; i < gui_win_status[vbarBox][vBar_divider]; i++) {
                fill(bar_colorSet[i]);
                rect(win01_xy[2].x+10, win01_xy[2].y+10+190-_h*i, 30, -_h);
        }

        // rect(win01_xy[2].x+10, win01_xy[2].y+10+190, 30, -gui_win_status[vbarBox][vBar_h]*5);

        stroke(255);
        noFill();
        rect(win01_xy[2].x+8, win01_xy[2].y+8, 34, 194);
        rect(win01_xy[2].x+10, win01_xy[2].y+10, 30, 190);


        popStyle();
}

void display_txt()
{
        String txt_receivingStatus = (receiving_ == true) ? "data receiving" : "no data available";
        String txt_opcStatus = (opc_ == true) ? "opc connected" : "opc connection lost";

        pushStyle();
        //led array txt
        textSize(7);
        textAlign(CENTER);
        fill(255);
        for (int i = 0; i < NUM_STRIPS; i++)
        {
                text(i, ledBar_pos[i].x-2, ledBar_pos[i].y+15);
        }

        //bt array txt
        int _fontSize = 8;
        int _indx = 0;
        PVector _xy = new PVector(win01_xy[0].x+win_module_selector_x, win01_xy[0].y+win_module_selector_y);
        PVector _xySpacing = new PVector(win_module_selector_w+win_module_selector_sp, win_module_selector_h+win_module_selector_sp);
        PVector _spacing = new PVector(_xy.x + win_module_selector_w/2, _xy.y + win_module_selector_h + _fontSize);
        textSize(_fontSize);
        noFill();
        stroke(80);
        for (int i = 0; i < 6; i++)
        {
                for (int j = 0; j< 6; j++)
                {
                        text(""+_indx, j*_xySpacing.x + _spacing.x, i*_xySpacing.y + _spacing.y);
                        _indx++;
                }
        }

        fill(255);
        textAlign(CENTER);
        text("MOUDLE SELECTOR", _spacing.x + 5*_xySpacing.x/2, _spacing.y + 5*_xySpacing.y + 20);
        text("WAVE SIMULATOR", win01_xy[1].x+(win01_xy[2].x-win01_xy[1].x)/2, _spacing.y + 5*_xySpacing.y + 20);
        text("COLOR COMPOSURE", win01_xy[2].x+(width-win01_pd-win01_xy[2].x)/2, _spacing.y + 5*_xySpacing.y + 20);

        int pd = 2;
        textAlign(RIGHT);
        text(txt_receivingStatus, width-pd, height - pd);                   //data status text
        text(txt_opcStatus, width-pd, height - 6*2 - pd);                   //opc status text
        popStyle();
}
}
