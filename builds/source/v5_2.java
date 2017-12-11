import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import hypermedia.net.*; 
import java.net.*; 
import java.util.Arrays; 
import java.net.*; 
import java.util.Arrays; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class v5_2 extends PApplet {






ControlP5 cp5;
Accordion accordion;
CheckBox mBox, mbox_ctrl01, waveBox_ctrl, waveBox_ctrl1, waveBox_ctrl2, waveBox_ctrl3;
Bang mbox_ctrl02;
UDP udp_receiving, udp_sending;
OPC opc;
LedBar[] ledBar;
Wave wave;
Life[] lives;

SETTING settings;
GUI gui;
BEHAVIOR behavior;

// CONTROL control;
// MODULE[] modules;

PApplet sketch = this;
PVector ledInit_pos, wave_trigger_pos;
PVector[] waveBox_pos, prssBox_pos, led_loc, led_loc_table, ledBar_pos, win01_xy, win_waveBox_xy;
int NUM_GAUGE, NUM_LED, NUM_STRIPS, NUM_DATA, NUM_PIXELS, NUM_WIN_OPTIONS, NUM_WIN01, NUM_WIN02, ledSpacing, stripSpacing, padding, gui_spacing, portName, gui_right_width, gui_pannel_w, wavePoint_ly, wavePoint_lx, wavePoint_bx, rOfM,
NUM_LIVES, NUM_HOLDER, NUM_LEDBAR_STATUS, m_x, m_y, m_prss0, m_prss1, m_prss2, m_prss3, sendingPort, receivingPort, opcPort;
int basicColor, var_ledBar_wasTouched, pullingGauge_stamp, var_ledBar_touched, var_gauge_pulling, var_gauge_touching, var_ledBar_sent, m_swingStrength, sendingPackets, var_ledBar_maxHeight, var_ledBar_maxHeight_pre, var_ledBar_vel, var_ledBar_count, vBar_h, vmBox, vwaveBox, vbarBox, vBar_divider, win01_pd, win01_init_h, win_module_selector_x, win_module_selector_y, win_module_selector_w, win_module_selector_h, win_module_selector_sp, win_waveBox_sp, win_waveBox_x, win_waveBox_y;

int mouseTriggerRadius = 40;
int[] led_mapping, bar_colorSet, LedBar_count_bounce, shot_weight, bar_colorSetAll, colorSetRainbow;
int[][] gui_win_status, m_strength_gauge;
float physics_vel, physics_acc, physics_vel_limit, physics_mass, physics_friction, physics_gravity;
float[] wave_to_point_dist;
float[][] m_ledBar_status;
float[][][] m_data;
// color [] bar_colorSet;
boolean opc_, receiving_, waveIsTriggered;
boolean[] shot, win_waveBox_state, mouseWasTriggered, mouseIsTriggered;
//*must edit*

public void settings() {
        size(1280, 800);
}

public void setup() {

        // control  = new CONTROL();
        settings = new SETTING();

        settings.set_var();
        settings.set_lib();
        settings.set_gui();
        // settings.set_pos();
        settings.set_matrix();
        settings.set_lives();
        // settings.set_modules();
}

int waveTriggered_count = 0;
public void draw() {
        background(0);
        sendingPackets = 0;
        gui.display_txt();
        gui.draw_win01_indicator_gui();
        gui.draw_waveBox();
        gui.draw_Pallette();
        gui.draw_mouse_control();
        gui.draw_led();

        for (int i = 0; i < NUM_STRIPS; i++) {
                behavior.shot_led(i);
        }
        // pushStyle();
        // rectMode(CENTER);
        // fill(basicColor);
        // rect(mouseX, mouseY, width, height/2);
        // popStyle();
        behavior.shot_wave();
        behavior.mouseTriggering();
        behavior.pressureTriggering();
        behavior.touchedTriggering();
        behavior.newLife(newLife);

        // mouseWasTriggered = mouseIsTriggered;
        sendData();
}
int _test_packets = 0;
public void sendData() {
        if (sendingPackets != 0) {
                String _init = "abcd";
                String _packets = "," + sendingPackets;
                String[][] _info = new String[sendingPackets][3];
                String _mssg = _init + _packets;

                //test
                int[] _m = null;
                for (int i = 0; i < NUM_STRIPS; i++) {
                        if (m_ledBar_status[i][var_ledBar_maxHeight] == 1 && m_ledBar_status[i][var_ledBar_sent] == 0) {
                                if (_m == null) {
                                        _m = new int[1];
                                        _m[0] = i;
                                } else {
                                        _m = append(_m, i);
                                }
                        }
                }
                //printArray(_m);
                int d = 0;
                for (int k = 0; k < sendingPackets; k++) {
                        //24-47 \uc74c\uacc4 \ubc94\uc704
                        String[] _note = {
                                "48",
                                "52",
                                "55",
                                "60",
                                "64",
                                "67",
                                "72",
                                "76",
                                "79",
                                "84",
                                "88",
                                "91"
                        };
                        String _module = "" + _m[k];

                        _info[k][0] = _module;
                        _info[k][1] = _note[k % _note.length];
                        _info[k][2] = "1000";
                }

                for (int i = 0; i < sendingPackets; i++) {
                        //module # , note #, duration
                        for (int j = 0; j < 3; j++) {
                                _mssg += "," + _info[i][j];
                        }
                }
                sending(_mssg);

                for (int i = 0; i < sendingPackets; i++) {
                        m_ledBar_status[_m[i]][var_ledBar_sent] = 1;
                }
        }
}

public void reset(int m) {
        m_ledBar_status[m][var_ledBar_sent] = 0;
}
float angle = -HALF_PI;
public void keyPressed() {
        if (key == ' ') {
                actionTriggered(99, 0);
        }
        if (key == 's') {
                int n = gui_win_status[vbarBox][vBar_divider];
                String[] c = new String[n];
                for (int i = 0; i < gui_win_status[vbarBox][vBar_divider]; i++) {
                        c[i] = "" + PApplet.parseInt(bar_colorSet[i]) + ",";
                }
                saveStrings("colorSet.txt", c);
        }
}

public void mouseReleased() {
        if (mouseX > win01_xy[1].x + 20 && mouseX < win01_xy[2].x - 20 && mouseY > win01_xy[1].y - 20 && mouseY < height - 105) {
                if (!mouseTriggerIsOn) {
                        wave_trigger_pos = new PVector(mouseX, mouseY);
                        waveTriggered(true);
                        // println(waveTriggered_count);
                }

        }
}

public void waveTriggered(boolean a) {
        if (a) {
                waveIsTriggered = true;
                wave = new Wave(10, wave_trigger_pos.x, wave_trigger_pos.y);
                // PVector force = PVector.fromAngle(angle);
                // force.mult(11);
                // wave.applyForce(force);
        } else {
                waveIsTriggered = false;
                for (int i = 0; i < NUM_STRIPS; i++) {
                        win_waveBox_state[i] = false;
                        // shot_weight[i] = 0;
                }
        }
}
int initial_pushForce = 100;
public void actionTriggered(int _indx, int _triggered_from) {
        int normalMode = 0;
        int waveMode = 1;
        int pressure = 2;
        // int
        switch (_triggered_from) {
                //normal triggered
                case (0):
                        if (_indx == 99) {
                                for (int i = 0; i < NUM_STRIPS; i++) {
                                        shot[i] = true;
                                        PVector force = PVector.fromAngle(angle);
                                        force.mult(initial_pushForce);
                                        ledBar[i].applyForce(force);
                                }
                        } else {
                                shot[_indx] = true;
                                PVector force = PVector.fromAngle(angle);
                                force.mult(initial_pushForce);
                                ledBar[_indx].applyForce(force);
                        }
                        break;
                        //wave triggered
                case (1):
                        if (_indx != 99) {
                                if (shot[_indx] == false) {
                                        float _min = min(wave_to_point_dist);
                                        float _max = max(wave_to_point_dist);
                                        float _dist = wave_to_point_dist[_indx];
                                        float _normDist = norm(_dist, _min, _max);
                                        // initial_pushForce
                                        float mult_cont = map(_normDist, 0, 1, 9, 1);
                                        shot[_indx] = true;
                                        PVector force = PVector.fromAngle(angle);
                                        force.mult(mult_cont);
                                        ledBar[_indx].applyForce(force);
                                }
                        }
                        break;

                case (2):
                        float mult_cont = random(2, 4.5f);
                        shot[_indx] = true;
                        PVector force = PVector.fromAngle(angle);
                        force.mult(mult_cont);
                        ledBar[_indx].applyForce(force);

                        String[] _note = {
                                "47",
                                "50",
                                "55",
                                "59",
                                "62",
                                "67",
                                "71",
                                "74",
                                "79",
                                "84",
                                "86",
                                "91"
                        };
                        sending("abcd, 1," + _indx + "," + _note[_indx % _note.length] + ", 1000");
                        break;
        }
}


public void receive(byte[] data) {
        //Node id, X angle, Y angle, Force 1, Force 2, Force 3, Force 4
        String received = new String(data);
        // println(received);
        String[] tokens = split(received, ',');
        // println(tokens.length);
        if (tokens.length == 7) {
                int indx = PApplet.parseInt(tokens[0]);

                for (int i = NUM_HOLDER - 2; i >= 0; i--) {
                        m_data[indx][m_x][i + 1] = m_data[indx][m_x][i];
                        m_data[indx][m_y][i + 1] = m_data[indx][m_y][i];
                        m_data[indx][m_prss0][i + 1] = m_data[indx][m_prss0][i];
                        m_data[indx][m_prss1][i + 1] = m_data[indx][m_prss1][i];
                        m_data[indx][m_prss2][i + 1] = m_data[indx][m_prss2][i];
                        m_data[indx][m_prss3][i + 1] = m_data[indx][m_prss3][i];
                        m_data[indx][m_swingStrength][i + 1] = m_data[indx][m_swingStrength][i];
                }

                m_data[indx][m_x][0] = PApplet.parseFloat(tokens[1]);
                m_data[indx][m_y][0] = PApplet.parseFloat(tokens[2]);
                m_data[indx][m_prss0][0] = PApplet.parseFloat(tokens[3]);
                m_data[indx][m_prss1][0] = PApplet.parseFloat(tokens[4]);
                m_data[indx][m_prss2][0] = PApplet.parseFloat(tokens[5]);
                m_data[indx][m_prss3][0] = PApplet.parseFloat(tokens[6]);

                m_data[indx][m_swingStrength][0] = sqrt(sq(m_data[indx][m_x][0]) + sq(m_data[indx][m_y][0]));

                receiving_ = true;

                behavior.pulling(indx);
                behavior.barTouched(indx);
                // behavior.pressure(indx);
        }
}
//******************************************************************************************************************************************************************************
//******************************************************************************************************************************************************************************
boolean mouseTriggerIsOn = false;
boolean colorApply = false;
boolean newLife = false;
boolean rainbowEffect = false;
public void controlEvent(ControlEvent theEvent) {
        int _states = 0;
        if (theEvent.isFrom(mBox)) {
                // println(mBox.getArrayValue());
        }
        //select ALL
        if (theEvent.isFrom(mbox_ctrl01)) {
                for (int i = 0; i < mBox.getArrayValue().length; i++)
                        _states += PApplet.parseInt(mBox.getArrayValue()[i]);

                if (_states == mBox.getArrayValue().length)
                        mBox.deactivateAll();
                else
                        mBox.activateAll();
                // println(_states);
        }
        if (theEvent.isFrom(waveBox_ctrl)) {
                mouseTriggerIsOn = !mouseTriggerIsOn;
        }
        if (theEvent.isFrom(waveBox_ctrl1)) {
                colorApply = !colorApply;
        }
        if (theEvent.isFrom(waveBox_ctrl2)) {
                newLife = !newLife;
        }
        if (theEvent.isFrom(waveBox_ctrl3)) {
                rainbowEffect = !rainbowEffect;
        }
        // ACTION
        if (theEvent.isFrom(mbox_ctrl02)) {
                for (int i = 0; i < mBox.getArrayValue().length; i++) {
                        if ((int) mBox.getArrayValue()[i] == 1) {
                                int n = i;
                                actionTriggered(n, 0);
                        }
                }
        }

}
public void bar_H(int a) {
        gui_win_status[vbarBox][vBar_h] = a;
}
public void color_R(int a) {
        gui_win_status[vbarBox][vBar_divider] = a;
}
public void pick_0(int a) {
        a = cp5.get(ColorWheel.class, "c").getRGB();
        bar_colorSet[3] = a;
        // println("0");
}
public void pick_1(int a) {
        a = cp5.get(ColorWheel.class, "c").getRGB();
        bar_colorSet[2] = a;
        // println("1");
}
public void pick_2(int a) {
        a = cp5.get(ColorWheel.class, "c").getRGB();
        bar_colorSet[1] = a;
        // println("2");
}
public void pick_3(int a) {
        a = cp5.get(ColorWheel.class, "c").getRGB();
        bar_colorSet[0] = a;
        // println("3");
}
public void sending(String mssg) {
        String ip = "127.0.0.1";
        String ip2 = "192.168.0.7";
        // println(mssg);
        // mssg = "abcd, 1, 29, 42, 1000";
        udp_sending.send(mssg, ip2, sendingPort);
        // println(mssg);
}
public class BEHAVIOR {

        BEHAVIOR() {

        }

        public void shot_led(int m) {
                PVector gravity = new PVector(0, 0.2f);
                float c = 0.08f;
                PVector[] friction = new PVector[NUM_STRIPS];
                if (shot[m]) {
                        ledBar[m].applyForce(gravity);
                        ledBar[m].update();

                        friction[m] = ledBar[m].vel.get();
                        friction[m].mult(-1);
                        friction[m].normalize();
                        friction[m].mult(c);

                        ledBar[m].applyForce(friction[m]);
                        ledBar[m].display(m);
                        ledBar[m].checkEdges(m);

                        m_ledBar_status[m][var_ledBar_vel] = ledBar[m].vel.get().y;

                        if (m_ledBar_status[m][var_ledBar_count] == 5) {
                                ledBar[m] = new LedBar(1, ledBar_pos[m].x, ledBar_pos[m].y);
                                m_ledBar_status[m][var_ledBar_count] = 0;
                                shot[m] = false;
                        }
                }
        }
        public void shot_wave() {
                if (waveIsTriggered) {
                        int _count = 0;
                        PVector accForce = new PVector(0, 0.2f);
                        PVector antiForce = wave.vel.get();
                        wave.applyForce(accForce);
                        wave.update();

                        antiForce.mult(-1);
                        antiForce.normalize();
                        antiForce.mult(0.05f);

                        wave.applyForce(antiForce);
                        wave.display();
                        int _waveSize = NUM_STRIPS - 1;
                        int[] _colorHolder = new int[NUM_STRIPS];
                        for (int i = 0; i < NUM_STRIPS; i++) {
                                wave_to_point_dist[i] = wave.wave_getDist(win_waveBox_xy[i].x, win_waveBox_xy[i].y, wave_trigger_pos.x, wave_trigger_pos.y);
                                if (wave.waveDetector(win_waveBox_xy[i].x, win_waveBox_xy[i].y, wave_trigger_pos.x, wave_trigger_pos.y, wave.r.get().y)) {
                                        if (rainbowEffect && !win_waveBox_state[i]) {
                                                bar_colorSetAll[i] = colorSetRainbow[_count];
                                                // bar_colorSetAll[i] = color(i + 30, height, 100);
                                                // print(i +":");
                                                // println(bar_colorSetAll[i]);
                                        }
                                        win_waveBox_state[i] = true;
                                        actionTriggered(i, 1);
                                        _count += PApplet.parseInt(win_waveBox_state[i]);
                                }
                        }

                        if (pullingGauge_stamp != 0) {
                                int guage_min = 4;
                                int gauge_max = 80;
                                int trigger_min = 4;
                                _waveSize = PApplet.parseInt(map(pullingGauge_stamp, guage_min, gauge_max, trigger_min, NUM_STRIPS - 1));
                                // print(pullingGauge_stamp+ ": ");
                                // println(_waveSize);
                        } else if (mouseTriggerIsOn) {
                                _waveSize = PApplet.parseInt(random(4, 20));
                        } else {
                                _waveSize = NUM_STRIPS - 1;
                        }
                        // print(m_strength_gauge[29][var_gauge_pulling]+": ");

                        if (_count > _waveSize) {
                                waveTriggered(false);
                                pullingGauge_stamp = 0;
                        }
                }
        }

        public void pulling(int indx) {
                boolean xIsInThreshold = false;
                boolean yIsInThreshold = false;
                boolean swingLengthIsInThreshold = false;

                final float threshold = 0.1f;
                float _swingStr = 0;
                float cX = m_data[indx][m_x][0];
                float cY = m_data[indx][m_y][0];

                int pullingGaugeThreshold = 20;

                for (int i = 0; i < NUM_HOLDER; i++) {
                        _swingStr += m_data[indx][m_swingStrength][i];
                }
                _swingStr /= NUM_HOLDER;
                swingLengthIsInThreshold = _swingStr > 0.56f;

                if (!swingLengthIsInThreshold) {
                        //\uac8c\uc774\uc9c0 \uc313\uc600\ub358\uac8c \uc788\ub2e4\uba74 \ud2b8\ub9ac\uac70
                        if (m_strength_gauge[indx][var_gauge_pulling] > pullingGaugeThreshold) {
                                wave_trigger_pos = win_waveBox_xy[indx];
                                waveTriggered(true);
                                actionTriggered(indx, 0);
                                pullingGauge_stamp = m_strength_gauge[indx][var_gauge_pulling];
                                // println(pullingGauge_stamp);
                                m_strength_gauge[indx][var_gauge_pulling] = 0;
                        } else {
                                m_strength_gauge[indx][var_gauge_pulling] = 0;
                        }
                        return;
                }

                float xAvg = 0, yAvg = 0;
                for (int i = 0; i < NUM_HOLDER; i++) {
                        xAvg += m_data[indx][m_x][i];
                        yAvg += m_data[indx][m_y][i];
                }
                xAvg /= NUM_HOLDER;
                xIsInThreshold = (cX > xAvg - threshold && cX < xAvg + threshold);

                yAvg /= NUM_HOLDER;
                yIsInThreshold = (cY > yAvg - threshold && cY < yAvg + threshold);

                if (xIsInThreshold && yIsInThreshold) {
                        m_strength_gauge[indx][var_gauge_pulling]++;
                } else {
                        //\uac8c\uc774\uc9c0 \uc313\uc600\ub358\uac8c \uc788\ub2e4\uba74 \ud2b8\ub9ac\uac70
                        if (m_strength_gauge[indx][var_gauge_pulling] > pullingGaugeThreshold) {
                                wave_trigger_pos = win_waveBox_xy[indx];
                                waveTriggered(true);
                                actionTriggered(indx, 0);
                                pullingGauge_stamp = m_strength_gauge[indx][var_gauge_pulling];

                                m_strength_gauge[indx][var_gauge_pulling] = 0;
                        } else {
                                m_strength_gauge[indx][var_gauge_pulling] = 0;
                        }
                        return;
                }
                // if(indx == 29)
                // println(m_strength_gauge[indx][var_gauge_pulling]);
        }
        int[] ledBarIsTouchedColor = new int[NUM_STRIPS];
        public void touchedTriggering() {
                float[] barGauge = new float[NUM_STRIPS];
                float[] barIsTouched = new float[NUM_STRIPS];
                float[] barWasTouched = new float[NUM_STRIPS];

                String[] _note = {
                        "47",
                        "50",
                        "55",
                        "59",
                        "62",
                        "67",
                        "71",
                        "74",
                        "79",
                        "84",
                        "86",
                        "91"
                };

                int _w = 6;
                int _pd = 2;

                for (int i = 0; i < NUM_STRIPS; i++) {
                        barGauge[i] = m_strength_gauge[i][var_gauge_touching];
                        barIsTouched[i] = m_ledBar_status[i][var_ledBar_touched];
                        barWasTouched[i] = m_ledBar_status[i][var_ledBar_wasTouched];

                        if (barIsTouched[i] == 1 && barWasTouched[i] == 0) {
                                ledBarIsTouchedColor[i] = 255;
                                sending("abcd, 1," + i + "," + _note[i % _note.length] + ", 1000");
                        }
                        m_ledBar_status[i][var_ledBar_wasTouched] = m_ledBar_status[i][var_ledBar_touched];
                        if (!mouseTriggerIsOn) {
                                noStroke();
                                fill(ledBarIsTouchedColor[i], ledBarIsTouchedColor[i], ledBarIsTouchedColor[i]);
                                rect(ledBar_pos[i].x - _w / 2 - _pd, ledBar_pos[i].y + 5 + _pd - 124, _w + _pd * 2, 8);
                        }

                        if (ledBarIsTouchedColor[i] > 0) {
                                ledBarIsTouchedColor[i] -= 2;
                        } else ledBarIsTouchedColor[i] = 0;

                }

        }

        public void newLife(boolean a) {
                if (a) {
                        noFill();
                        stroke(255);
                        ellipse(mouseX, mouseY, 400, 400);

                        if (NUM_LIVES == 1) {
                                lives[0].update();
                                lives[0].display();
                                lives[0].checkBoundary();
                        } else {
                                for (int i = 0; i < NUM_LIVES; i++) {
                                        lives[i].update();
                                        lives[i].display();
                                        lives[i].checkBoundary();
                                        for (int j = 0; j < NUM_LIVES; j++) {
                                                if (i != j) {
                                                        lives[i].checkCollision(lives[j]);
                                                }
                                        }
                                }
                        }

                }
        }

        public void barTouched(int indx) {
                boolean touchXIsInThreshold = false;
                boolean touchYIsInThreshold = false;
                boolean touchIsInThreshold = false;
                boolean touchGaugeIsOnMax = false;

                final float threshold = 0.5f;
                float _swingStr = 0;
                float cX = m_data[indx][m_x][0];
                float cY = m_data[indx][m_y][0];

                int _maxGauge = 50;
                int NUM_TOUCHHOLDER = 10;

                for (int i = 0; i < NUM_TOUCHHOLDER; i++) {
                        _swingStr += m_data[indx][m_swingStrength][i];
                }
                _swingStr /= NUM_TOUCHHOLDER;
                // if(indx == 29) {
                //         println(_swingStr);
                // }
                touchIsInThreshold = (_swingStr > 0.2f && _swingStr < 1.5f);
                touchGaugeIsOnMax = m_strength_gauge[indx][var_gauge_touching] > _maxGauge;

                if (touchIsInThreshold) {
                        m_strength_gauge[indx][var_gauge_touching]++;
                        m_ledBar_status[indx][var_ledBar_touched] = 1;
                        // sendingPackets = 1;

                        // sending("abcd, 1," + indx +"," + _note[indx % _note.length] + ", 1000");
                } else if (_swingStr < .1f) {
                        m_ledBar_status[indx][var_ledBar_touched] = 0;
                        // m_strength_gauge[indx][var_gauge_touching] = 0;
                }
                // //
                // float xAvg = 0, yAvg = 0;
                // for (int i = 0; i<NUM_HOLDER; i++) {
                //         xAvg += m_data[indx][m_x][i];
                //         yAvg += m_data[indx][m_y][i];
                // }
                // xAvg /= NUM_HOLDER;
                // touchXIsInThreshold = (cX > xAvg - threshold && cX < xAvg + threshold);
                //
                // yAvg /= NUM_HOLDER;
                // touchYIsInThreshold = (cY > yAvg - threshold && cY < yAvg + threshold);
                //
                //
                // if (touchXIsInThreshold && touchYIsInThreshold) {
                //         //\uac00\uc911\uce58 \ud544\uc694\ud558\uba74 \ucd94\uac00
                //         // m_strength_gauge[indx][var_gauge_touching]++;
                // }
        }
        boolean[] aPersonIsOn = new boolean[NUM_STRIPS];
        public void pressure(int indx) {
                // boolean tempStatus = false;
                float prssThreshold = 30;

                float[] cPrss = new float[4];
                for (int j = 0; j < 4; j++) {
                        for (int i = 0; i < NUM_HOLDER; i++) {
                                cPrss[j] += m_data[indx][m_prss0 + j][i];
                        }

                        cPrss[j] /= NUM_HOLDER;
                        aPersonIsOn[indx] = cPrss[j] > prssThreshold;
                }

                if (aPersonIsOn[indx]) {
                        actionTriggered(indx, 2);
                }



                if (indx == 23) {
                        for (int i = 0; i < 4; i++) {
                                // print(m_data[indx][m_prss0+i][0] + ",");
                        }
                        // println();
                }
        }
        public void pressureTriggering() {
                for (int i = 0; i < NUM_STRIPS; i++) {
                        if (aPersonIsOn[i]) {
                                actionTriggered(i, 0);
                        }
                }
        }

        public void mouseTriggering() {
                if (mouseTriggerIsOn) {
                        float _mx = PApplet.parseFloat(mouseX);
                        float _my = PApplet.parseFloat(mouseY);

                        for (int i = 0; i < NUM_STRIPS; i++) {
                                float xDist = win_waveBox_xy[i].x - _mx;
                                float yDist = win_waveBox_xy[i].y - _my;
                                float distance = sqrt(sq(xDist) + sq(yDist));
                                mouseIsTriggered[i] = (distance < mouseTriggerRadius / 2);
                                // println(mouseIsTriggered[0]);
                                // println("pre: "+mouseWasTriggered[0]);
                                if (mouseIsTriggered[i] && !mouseWasTriggered[i]) {
                                        actionTriggered(i, 2); // default: 2
                                }
                                mouseWasTriggered[i] = mouseIsTriggered[i];
                                // println("/ "+mouseWasTriggered[0]);
                        }
                }
        }
}
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

public void display_cp5()
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
        cp5.addColorWheel("c", PApplet.parseInt(width - win01_pd - colorwheel_w), PApplet.parseInt(win01_xy[2].y + win01_pd*2), colorwheel_w ).setRGB(color(-2748419))
        .setCaptionLabel("")
        ;
        // cp5.addColorWheel("c" , 250 , 10 , 200 ).setRGB(color(128,0,255));

        cp5.addSlider("bar_H")
        .setRange(1, 38)
        .setValue(PApplet.parseInt(38))
        .setPosition(win01_xy[2].x+100, PApplet.parseInt(win01_xy[2].y + win01_pd*2))
        .setSize(10, 190)
        .setBroadcast(true)
        .setNumberOfTickMarks(36)
        .setSliderMode(Slider.FIX)
        ;

        cp5.addSlider("color_R")
        .setRange(1, 4)
        .setValue(1)
        .setPosition(win01_xy[2].x+150, PApplet.parseInt(win01_xy[2].y + win01_pd*2))
        .setSize(10, 190)
        .setBroadcast(true)
        .setNumberOfTickMarks(4)
        .setSliderMode(Slider.FIX)
        ;

        int _btPick_w = 40;
        int _btPick_h = 20;
        int _btPick_pd = 10;
        int _btPick_x = PApplet.parseInt(width - win01_pd - colorwheel_w);
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
public void draw_mouse_control()
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
public void draw_win01_indicator_gui()
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

public void draw_led()
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

public void draw_waveBox()
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
public void draw_Pallette()
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

public void display_txt()
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
public class LedBar {
PVector pos, vel, acc;

float mass;
float topspeed = 8;

LedBar(float _m, float _x, float _y)
{
        mass = _m;
        pos = new PVector(_x, _y);
        vel = new PVector(0, 0);
        acc = new PVector(0, 0);
}

public void applyForce(PVector force)
{
        PVector f = PVector.div(force, mass);
        acc.add(f);
}

public void update()
{
        vel.add(acc);
        vel.limit(topspeed);
        pos.add(vel);
        acc.mult(0);
}
boolean a = false;
boolean pre_a = false;

public void display(int _NUM_M)
{
        pushMatrix();
        rectMode(CORNER);
        int _h = gui_win_status[vbarBox][vBar_h]/gui_win_status[vbarBox][vBar_divider];
        int _dividedBy = gui_win_status[vbarBox][vBar_divider];
        int _divdided_h = PApplet.parseInt(ledBar_pos[_NUM_M].y - pos.y)/_dividedBy;
        if(colorApply)
        {
                for (int i = 0; i < _dividedBy; i++)
                {
                        fill(bar_colorSet[i]);
                        noStroke();
                        rect(ledBar_pos[_NUM_M].x-2, pos.y + _divdided_h*i + 4, 4, _divdided_h);
                }
        }
        else if(rainbowEffect)
        {
                fill(bar_colorSetAll[_NUM_M]);
                noStroke();
                rect(ledBar_pos[_NUM_M].x-2, pos.y + 4, 4, _divdided_h);
        }
        else
        {
                fill(basicColor);
                noStroke();
                rect(ledBar_pos[_NUM_M].x-2, pos.y + 4, 4, _divdided_h);
        }

        // rect(ledBar_pos[_NUM_M].x-2, ledBar_pos[_NUM_M].y, ledBar_pos[_NUM_M].x+2, pos.y);
        popMatrix();

        status_maxHeight(_NUM_M);
}
public void status_maxHeight(int m) {
        // println("preMax:"+_booleanMax_pre+"max:"+_booleanMax+" /newVel:" + m_ledBar_status[m][var_ledBar_vel] + "/newCount:" + m_ledBar_status[m][var_ledBar_count]);

        if (abs(m_ledBar_status[m][var_ledBar_vel]) <1 && m_ledBar_status[m][var_ledBar_vel] != 0) {
                if (m_ledBar_status[m][var_ledBar_sent] == 0)
                {
                        m_ledBar_status[m][var_ledBar_maxHeight] = 1;
                }
                else
                {
                        m_ledBar_status[m][var_ledBar_maxHeight] = 0;
                }
        } else {
                m_ledBar_status[m][var_ledBar_maxHeight] = 0;
                reset(m);
        }

        if (m_ledBar_status[m][var_ledBar_maxHeight] != m_ledBar_status[m][var_ledBar_maxHeight_pre] && m_ledBar_status[m][var_ledBar_maxHeight] == 1) {
                // println("we");
                if (m_ledBar_status[m][var_ledBar_count] == 0) {
                        // println(m);
                        sendingPackets++;
                }
                // m_ledBar_status[m][var_ledBar_maxHeight] = 2;
        }
        m_ledBar_status[m][var_ledBar_maxHeight_pre] = m_ledBar_status[m][var_ledBar_maxHeight];
}

public void checkEdges(int _NUM_M)
{
        if (pos.y > ledBar_pos[_NUM_M].y)
        {
                vel.y *= -1;
                pos.y = ledBar_pos[_NUM_M].y;
                m_ledBar_status[_NUM_M][var_ledBar_count]++;
        }
}

}
public class Life
{
PVector pos, vel;
float radius, m;

Life(float x, float y, float r)
{
        pos = new PVector(x, y);
        vel = PVector.random2D();
        vel.mult(3);
        radius = r;
        m = radius*.1f;
}
public void update()
{
        pos.add(vel);
}

public void checkBoundary()
{
        float _xMax = 500;
        float _yMax = ledBar_pos[0].y;
        float _xMin = 20;
        float _yMin = 120;

        PVector _a, _b;
        _a = new PVector(_xMin, _yMin);
        _b = new PVector(_xMax, _yMax);

        if(pos.x < _a.x - radius)
        {
                pos.x = _a.x-radius;
                vel.x *= -1;
        }
        else if(pos.x > _b.x - radius)
        {
                pos.x = _b.x-radius;
                vel.x *= -1;
        }
        else if (pos.y < _a.y - radius)
        {
                pos.y = _a.y-radius;
                vel.y *= -1;
        }
        else if (pos.y > _b.y - radius)
        {
                pos.y = _b.y-radius;
                vel.y *= -1;
        }
}
public void checkCollision(Life other) {

        // Get distances between the balls components
        PVector distanceVect = PVector.sub(other.pos, pos);

        // Calculate magnitude of the vector separating the balls
        float distanceVectMag = distanceVect.mag();

        // Minimum distance before they are touching
        float minDistance = radius + other.radius;

        if (distanceVectMag < minDistance) {
                float distanceCorrection = (minDistance-distanceVectMag)/2.0f;
                PVector d = distanceVect.copy();
                PVector correctionVector = d.normalize().mult(distanceCorrection);
                other.pos.add(correctionVector);
                pos.sub(correctionVector);

                // get angle of distanceVect
                float theta  = distanceVect.heading();
                // precalculate trig values
                float sine = sin(theta);
                float cosine = cos(theta);

                PVector[] bTemp = {
                        new PVector(), new PVector()
                };

                bTemp[1].x  = cosine * distanceVect.x + sine * distanceVect.y;
                bTemp[1].y  = cosine * distanceVect.y - sine * distanceVect.x;

                // rotate Temporary velocities
                PVector[] vTemp = {
                        new PVector(), new PVector()
                };

                vTemp[0].x  = cosine * vel.x + sine * vel.y;
                vTemp[0].y  = cosine * vel.y - sine * vel.x;
                vTemp[1].x  = cosine * other.vel.x + sine * other.vel.y;
                vTemp[1].y  = cosine * other.vel.y - sine * other.vel.x;

                PVector[] vFinal = {
                        new PVector(), new PVector()
                };

                vFinal[0].x = ((m - other.m) * vTemp[0].x + 2 * other.m * vTemp[1].x) / (m + other.m);
                vFinal[0].y = vTemp[0].y;

                vFinal[1].x = ((other.m - m) * vTemp[1].x + 2 * m * vTemp[0].x) / (m + other.m);
                vFinal[1].y = vTemp[1].y;

                bTemp[0].x += vFinal[0].x;
                bTemp[1].x += vFinal[1].x;

                PVector[] bFinal = {
                        new PVector(), new PVector()
                };

                bFinal[0].x = cosine * bTemp[0].x - sine * bTemp[0].y;
                bFinal[0].y = cosine * bTemp[0].y + sine * bTemp[0].x;
                bFinal[1].x = cosine * bTemp[1].x - sine * bTemp[1].y;
                bFinal[1].y = cosine * bTemp[1].y + sine * bTemp[1].x;

                // update balls to screen position
                other.pos.x = pos.x + bFinal[1].x;
                other.pos.y = pos.y + bFinal[1].y;

                pos.add(bFinal[0]);

                // update velocities
                vel.x = cosine * vFinal[0].x - sine * vFinal[0].y;
                vel.y = cosine * vFinal[0].y + sine * vFinal[0].x;
                other.vel.x = cosine * vFinal[1].x - sine * vFinal[1].y;
                other.vel.y = cosine * vFinal[1].y + sine * vFinal[1].x;
        }
}

public void display() {
        noStroke();
        fill(basicColor);
        ellipse(pos.x, pos.y, radius*2, radius*2);
}
}
/*
 * Simple Open Pixel Control client for Processing,
 * designed to sample each LED's color from some point on the canvas.
 *
 * Micah Elizabeth Scott, 2013
 * This file is released into the public domain.
 *
 * Modified by Yona Apletree for outputting to LEDscape in early 2014.
 */



boolean opc_connectionStatus = false;

public class OPC {
Socket socket;
OutputStream output;
String host;
int port;

int[] pixelLocations;
byte[] packetData;
byte firmwareConfig;
String colorCorrection;
boolean enableShowLocations;

int sendDataOneTime =0;
OPC(PApplet parent, String host, int port){
        if(port == 999) {
                this.enableShowLocations = true;
                parent.registerMethod("draw",this);
                opc_connectionStatus = false;
                println("no connection");
        }else{
                this.host = host;
                this.port = port;
                this.enableShowLocations = true;
                parent.registerMethod("draw",this);
                opc_connectionStatus = true;
        }
}

// Set the location of a single LED
public void led(int index, int x, int y){
        // For convenience, automatically grow the pixelLocations array. We do want this to be an array,
        // instead of a HashMap, to keep draw() as fast as it can be.
        if (pixelLocations == null) {
                pixelLocations = new int[index + 1];
        } else if (index >= pixelLocations.length) {
                // println(pixelLocations);
                pixelLocations = Arrays.copyOf(pixelLocations, index + 1);
        }
        //pixelLocations[index] = x + width * y;
        pixelLocations[index] = x + width*y;
}

// Set the location of several LEDs arranged in a strip.
// Angle is in radians, measured clockwise from +X.
// (x,y) is the center of the strip.
public void ledStrip(int index, int count, float x, float y, float spacing, float angle, boolean reversed)
{
        float s = sin(angle);
        float c = cos(angle);
        for (int i = 0; i < count; i++) {
                led(reversed ? (index + count - 1 - i) : (index + i),
                    (int)(x + (i - (count-1)/1.0f) * spacing * c + 0.5f),
                    (int)(y + (i - (count-1)/1.0f) * spacing * s + 0.5f));
        }

}

// Set the location of several LEDs arranged in a grid. The first strip is
// at 'angle', measured in radians clockwise from +X.
// (x,y) is the center of the grid.
public void ledGrid(int index, int stripLength, int numStrips, float x, float y,
             float ledSpacing, float stripSpacing, float angle, boolean zigzag)
{
        float s = sin(angle + HALF_PI);
        float c = cos(angle + HALF_PI);
        // float h = 130; //if project max reachable length
        for (int i = 0; i < numStrips; i++) {
                ledStrip(index + stripLength * i, stripLength,
                         x + (i - (numStrips-1)/1.0f) * stripSpacing * c,
                         y + (i - (numStrips-1)/1.0f) * stripSpacing * s, ledSpacing,
                         angle, zigzag);
        }
        // && (i % 2) == 1
}

// Set the location of 64 LEDs arranged in a uniform 8x8 grid.
// (x,y) is the center of the grid.
// void ledGrid8x8(int index, float x, float y, float spacing, float angle, boolean zigzag)
// {
//         ledGrid(index, 8, 8, x, y, spacing, spacing, angle, zigzag);
// }
//
// void ledGrid16x16(int index, float x, float y, float spacing, float angle, boolean zigzag)
// {
//         ledGrid(index, 16, 16, x, y, spacing, spacing, angle, zigzag);
// }

// Should the pixel sampling locations be visible? This helps with debugging.
// Showing locations is enabled by default. You might need to disable it if our drawing
// is interfering with your processing sketch, or if you'd simply like the screen to be
// less cluttered.
public void showLocations(boolean enabled)
{
        enableShowLocations = enabled;
}

// Automatically called at the end of each draw().
// This handles the automatic Pixel to LED mapping.
// If you aren't using that mapping, this function has no effect.
// In that case, you can call setPixelCount(), setPixel(), and writePixels()
// separately.
// void setPixel(int numofPixelsUpdated){
//   numStrips = numofPixelsUpdated;
// }
public void setup(){


}
public void draw(){
        if (pixelLocations == null) {
                // No pixels defined yet
                return;
        }

        if (output == null) {
                // Try to (re)connect
                connect();
        }
        if (output == null) {
                return;
        }

        int numPixels = pixelLocations.length;
        int ledAddress = 4;

        setPixelCount(numPixels);
        loadPixels();

        for (int i = 0; i < numPixels; i++) {
                int pixelLocation = pixelLocations[i];
                int pixel = pixels[pixelLocation];

                packetData[ledAddress] = (byte)(pixel >> 16);
                packetData[ledAddress + 1] = (byte)(pixel >> 8);
                packetData[ledAddress + 2] = (byte)pixel;
                ledAddress += 3;

                if (enableShowLocations) {
                        pixels[pixelLocation] = 0xFFFFFF ^ pixel;
                }
        }

        writePixels();


        sendDataOneTime++;

        if(sendDataOneTime > 400)
                sendDataOneTime = 0;

        if (enableShowLocations) {
                updatePixels();
        }
}

// Change the number of pixels in our output packet.
// This is normally not needed; the output packet is automatically sized
// by draw() and by setPixel().
public void setPixelCount(int numPixels)
{
        int numBytes = 3 * numPixels;
        int packetLen = 4 + numBytes;
        if (packetData == null || packetData.length != packetLen) {
                // Set up our packet buffer
                packetData = new byte[packetLen];
                packetData[0] = 0;         // Channel
                packetData[1] = 0;         // Command (Set pixel colors)
                packetData[2] = (byte)(numBytes >> 8);
                packetData[3] = (byte)(numBytes & 0xFF);
        }
}

// Directly manipulate a pixel in the output buffer. This isn't needed
// for pixels that are mapped to the screen.
public void setPixel(int number, int c)
{
        int offset = 4 + number * 3;
        if (packetData == null   || packetData.length < offset + 3) {
                setPixelCount(number + 1);
        }

        packetData[offset] = (byte) (c >> 16);
        packetData[offset + 1] = (byte) (c >> 8);
        packetData[offset + 2] = (byte) c;
}

// Read a pixel from the output buffer. If the pixel was mapped to the display,
// this returns the value we captured on the previous frame.
public int getPixel(int number)
{
        int offset = 4 + number * 3;
        if (packetData == null || packetData.length < offset + 3) {
                return 0;
        }
        return (packetData[offset] << 16) | (packetData[offset + 1] << 8) | packetData[offset + 2];
}

// Transmit our current buffer of pixel values to the OPC server. This is handled
// automatically in draw() if any pixels are mapped to the screen, but if you haven't
// mapped any pixels to the screen you'll want to call this directly.
public void writePixels(){
        if (packetData == null || packetData.length == 0) {
                // No pixel buffer
                return;
        }
        if (output == null) {
                // Try to (re)connect
                connect();
        }
        if (output == null) {
                return;
        }

        try {
                output.write(packetData);
        }
        catch (Exception e) {
                dispose();
        }
}

public void dispose(){
        // Destroy the socket. Called internally when we've disconnected.
        if (output != null) {
                opc_connectionStatus = false;
                println("Disconnected from OPC server");
        }
        socket = null;
        output = null;
}

public void connect(){
        // Try to connect to the OPC server. This normally happens automatically in draw()
        try {
                socket = new Socket(host, port);
                socket.setTcpNoDelay(true);
                output = socket.getOutputStream();
                // println(opc_connectionStatus);
                // println("Connected to OPC server");
        }
        catch (ConnectException e) {
                dispose();
        }
        catch (IOException e) {
                dispose();
        }
}
}
class SETTING {
        int txtHeight = 9;
        int txtSpacing = 5;
        int ledGrid_x = 10;
        int ledGrid_y = NUM_PIXELS * ledSpacing + padding;
        PVector[] temp_pos = new PVector[49];
        SETTING() {}
        public void set_var() {
                NUM_STRIPS = 36;
                NUM_DATA = 8;
                stripSpacing = 11; //27
                NUM_LED = 64;
                NUM_GAUGE = 2;
                NUM_HOLDER = 50;
                ledSpacing = 3;
                opcPort = 7890; //7890
                receivingPort = 40000;
                sendingPort = 40001;
                gui_pannel_w = 200;
                NUM_LEDBAR_STATUS = 7;
                basicColor = -83852;
                NUM_LIVES = 2;
                set_var_array();
                set_var_indx();
                set_var_pvector();
                set_var_boolean();
                set_var_physics();
                set_var_gui();
                set_var_ledBar();
                set_var_wave();
        }
        public void set_lives() {
                lives = new Life[NUM_LIVES];
                float _xMax = 500;
                float _yMax = ledBar_pos[0].y;
                float _xMin = 20;
                float _yMin = 120;
                float _minSize = 40;
                float _maxSize = 50;
                PVector[] _lives = new PVector[NUM_LIVES]; //get random Numbers
                if (NUM_LIVES == 1) {
                        float _randomX = random(_xMin, _xMax);
                        float _randomY = random(_yMin, _yMax);
                        float _randomS = random(_minSize, _maxSize);
                        _lives[0] = new PVector(_randomX, _randomY, _randomS);
                } else {
                        for (int i = 0; i < NUM_LIVES; i++) {
                                float _randomX = random(_xMin, _xMax);
                                float _randomY = random(_yMin, _yMax);
                                float _randomS = random(_minSize, _maxSize);
                                _lives[i] = new PVector(_randomX, _randomY, _randomS);
                        }
                }
                for (int i = 0; i < NUM_LIVES; i++) {
                        //check if the distance among ellipses overlaps
                        for (int j = 0; j < NUM_LIVES; j++) {
                                if (i != j) {
                                        float dist = sqrt(sq(_lives[i].x - _lives[j].x) + sq(_lives[i].y - _lives[j].y));
                                        if (dist < _lives[i].z) {
                                                float _randomX = random(_xMin, _xMax);
                                                float _randomY = random(_yMin, _yMax);
                                                float _randomS = random(_minSize, _maxSize);
                                                _lives[i] = new PVector(_randomX, _randomY, _randomS);
                                        } else {
                                                if (NUM_LIVES == 1) {
                                                        lives[0] = new Life(_lives[0].x, _lives[0].y, _lives[0].z);
                                                } else {
                                                        if (i == 1) {
                                                                lives[i] = new Life(_lives[i].x, _lives[i].y, 1);
                                                        } else {
                                                                lives[i] = new Life(_lives[i].x, _lives[i].y, _lives[i].z);
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
        public void set_lib() {
                set_lib_opc();
                set_lib_udp();
        }
        public void set_gui() {
                cp5 = new ControlP5(sketch);
                gui = new GUI();
                gui.display_cp5();
        }
        public void set_matrix() {
                for (int i = 0; i < 49; i++) {
                        led_loc_table[i] = new PVector(temp_pos[i].x, temp_pos[i].y);
                }
                led_mapping[0] = 9;
                led_mapping[1] = 12;
                led_mapping[2] = 10;
                led_mapping[3] = 11;
                led_mapping[4] = 45;
                led_mapping[5] = 46;
                led_mapping[6] = 15;
                led_mapping[7] = 14;
                led_mapping[8] = 16;
                led_mapping[9] = 13;
                led_mapping[10] = 48;
                led_mapping[11] = 47;
                led_mapping[12] = 37;
                led_mapping[13] = 38;
                led_mapping[14] = 3;
                led_mapping[15] = 1;
                led_mapping[16] = 17;
                led_mapping[17] = 18;
                led_mapping[18] = 40;
                led_mapping[19] = 39;
                led_mapping[20] = 7;
                led_mapping[21] = 5;
                led_mapping[22] = 20;
                led_mapping[23] = 19;
                led_mapping[24] = 2;
                led_mapping[25] = 4;
                led_mapping[26] = 33;
                led_mapping[27] = 34;
                led_mapping[28] = 25;
                led_mapping[29] = 26;
                led_mapping[30] = 8;
                led_mapping[31] = 6;
                led_mapping[32] = 36;
                led_mapping[33] = 35;
                led_mapping[34] = 28;
                led_mapping[35] = 27;
                for (int i = 0; i < NUM_STRIPS; i++) {
                        ledBar_pos[i] = led_loc_table[led_mapping[i]];
                        print(i + "_" + ledBar_pos[i] + "   ");
                }
                println();
        } // ================================================================================================
        public void set_lib_opc() {
                opc = new OPC(sketch, "192.168.7.2", opcPort);
                opc.ledGrid(0, NUM_LED, 49, ledInit_pos.x, ledInit_pos.y, ledSpacing, stripSpacing, HALF_PI, true);
        }
        public void set_lib_udp() {
                behavior = new BEHAVIOR();
                udp_receiving = new UDP(sketch, receivingPort);
                udp_sending = new UDP(sketch);
                udp_receiving.listen(true);
        }
        public void set_var_gui() {
                NUM_WIN_OPTIONS = 10;
                NUM_WIN01 = 3;
                NUM_WIN02 = 1;
                win01_xy = new PVector[NUM_WIN01];
                win_waveBox_xy = new PVector[NUM_STRIPS];
                gui_win_status = new int[NUM_WIN01][NUM_WIN_OPTIONS];
                win_waveBox_state = new boolean[NUM_STRIPS];
                for (int j = 0; j < NUM_WIN01; j++) {
                        for (int k = 0; k < NUM_WIN_OPTIONS; k++) {
                                gui_win_status[j][k] = 0;
                        }
                } //\ub098\uc911\uc5d0 \ubc11\uc5d0 \ub458\uc744 \ud569\uccd0\uc11c \uc774\ucc28\uc6d0 \uc5b4\ub808\uc774\ub85c \ub9cc\ub4e4\uc5b4\uc57c \ud568
                bar_colorSet = new int[4];
                for (int i = 0; i < 4; i++) {
                        bar_colorSet[i] = color(-14480130);
                }
                pushStyle();
                colorMode(HSB, 100);
                bar_colorSetAll = new int[NUM_STRIPS];
                colorSetRainbow = new int[NUM_STRIPS];
                float _colorStart = 6;
                float _colorRange = .6f;
                for (int i = 0; i < NUM_STRIPS; i++) {
                        colorSetRainbow[i] = color(i + _colorStart + _colorRange * i, 255, 100);
                        bar_colorSetAll[i] = color(i + _colorStart + _colorRange * i, 255, 100); // println(bar_colorSetAll[i]);
                }
                popStyle();
                vmBox = 0;
                vwaveBox = 1;
                vbarBox = 2; // vBarBox-------------------------
                vBar_h = 1;
                vBar_divider = 2; //window01 pannel basic
                win01_pd = 5;
                win01_init_h = height / 2 + 100; //window for module selector
                win_module_selector_x = 75;
                win_module_selector_y = 20;
                win_module_selector_w = 25;
                win_module_selector_h = 25;
                win_module_selector_sp = 12; //window for waveBox
                win_waveBox_sp = 20;
                win_waveBox_x = 160;
                win_waveBox_y = 80;
                for (int i = 0; i < NUM_WIN01; i++) {
                        win01_xy[i] = new PVector((width - win01_pd * 2) * i / NUM_WIN01 + win01_pd, win01_init_h + win01_pd);
                }
                int _indx = 0;
                for (int i = 0; i < sqrt(NUM_STRIPS); i++) {
                        for (int j = 0; j < sqrt(NUM_STRIPS); j++) {
                                win_waveBox_state[_indx] = false;
                                win_waveBox_xy[_indx] = new PVector(win01_xy[vwaveBox].x + win_waveBox_x + j * win_waveBox_sp, win01_xy[vwaveBox].y + win_waveBox_y + i * win_waveBox_sp);
                                _indx++;
                        }
                }
        }
        public void set_var_ledBar() {
                ledBar = new LedBar[NUM_STRIPS];
                ledBar_pos = new PVector[NUM_STRIPS];
                ledInit_pos = new PVector(10, 250);
                LedBar_count_bounce = new int[NUM_STRIPS];
                shot = new boolean[NUM_STRIPS];
                shot_weight = new int[NUM_STRIPS];
                wave_to_point_dist = new float[NUM_STRIPS];
                m_ledBar_status = new float[NUM_STRIPS][NUM_LEDBAR_STATUS];
                m_strength_gauge = new int[NUM_STRIPS][NUM_GAUGE];
                mouseWasTriggered = new boolean[NUM_STRIPS];
                mouseIsTriggered = new boolean[NUM_STRIPS];
                pullingGauge_stamp = 0;
                sendingPackets = 0;
                var_ledBar_vel = 0;
                var_ledBar_count = 1;
                var_ledBar_maxHeight_pre = 2;
                var_ledBar_maxHeight = 3;
                var_ledBar_sent = 4;
                var_ledBar_touched = 5;
                var_ledBar_wasTouched = 6;
                var_gauge_pulling = 0;
                var_gauge_touching = 1;
                for (int i = 0; i < 49; i++) {
                        temp_pos[i] = new PVector(ledInit_pos.x + stripSpacing * i, ledInit_pos.y);
                }
                for (int i = 0; i < NUM_STRIPS; i++) {
                        LedBar_count_bounce[i] = 0;
                        ledBar[i] = new LedBar(1, temp_pos[i].x, temp_pos[i].y);
                        shot[i] = false;
                        shot_weight[i] = 0;
                        wave_to_point_dist[i] = 0;
                        mouseWasTriggered[i] = false;
                        mouseIsTriggered[i] = false;
                        for (int j = 0; j < NUM_LEDBAR_STATUS; j++) {
                                m_ledBar_status[i][j] = 0;
                        }
                        for (int j = 0; j < NUM_GAUGE; j++) {
                                m_strength_gauge[i][j] = 0;
                        }
                }
        }
        public void set_var_wave() {
                // wave = new Wave(1, width/2, height/2);
                waveIsTriggered = false;
        }
        public void set_var_pvector() {
                prssBox_pos = new PVector[NUM_STRIPS];
                led_loc = new PVector[NUM_STRIPS];
                led_loc_table = new PVector[49];
                led_mapping = new int[NUM_STRIPS];
        }
        public void set_var_physics() {
                physics_vel = 1.9f;
                physics_acc = .01f;
                physics_vel_limit = 10;
                physics_mass = 4;
                physics_friction = .05f;
                physics_gravity = .1f;
        }
        public void set_var_boolean() {
                opc_ = false;
                receiving_ = false;
        }
        public void set_var_array() {
                m_data = new float[NUM_STRIPS][NUM_DATA][NUM_HOLDER];
                for (int i = 0; i < NUM_STRIPS; i++) {
                        for (int j = 0; j < NUM_DATA; j++) {
                                for (int k = 0; k < NUM_HOLDER; k++) {
                                        m_data[i][j][k] = 0;
                                }
                        }
                }
        }
        public void set_var_indx() {
                m_x = 1;
                m_y = 2;
                m_prss0 = 3;
                m_prss1 = 4;
                m_prss2 = 5;
                m_prss3 = 6;
                m_swingStrength = 7;
        }
}
public class Wave
{
PVector r, vel, acc, pos;

float mass = 0;
Wave(float _m, float _x, float _y)
{
        mass = _m;
        pos = new PVector(_x, _y);
        vel = new PVector(0,0);
        acc = new PVector(0,0);
        r = new PVector(0,0);
}

public void applyForce(PVector force)
{
        PVector f = PVector.div(force, mass);
        acc.add(f);
}

public void update()
{
        vel.add(acc);
        r.add(vel);
        acc.mult(0);
}

public void display()
{
        pushMatrix();
        noFill();
        stroke(255);
        ellipse(pos.x, pos.y, r.y, r.y);
        popMatrix();
}
//px : x location of simulator dot
//py : y location of simulator dot
//bx : x location of ellipse
//by : y location of ellipse
public boolean waveDetector(float px, float py, float bx, float by, float bSize)
{
        float distance = wave_getDist(px, py, bx, by);

        if (bSize/2 > distance) {
                return true;
        }else {
                return false;
        }
}

public float wave_getDist(float px, float py, float bx, float by)
{
        float xDist = px-bx;                                 // distance horiz
        float yDist = py-by;                                 // distance vert
        float distance = sqrt(sq(xDist) + sq(yDist));        // diagonal distance

        return distance;
}
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "v5_2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
