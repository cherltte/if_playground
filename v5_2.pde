import controlP5.*;
import hypermedia.net.*;
import java.net.*;
import java.util.Arrays;

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

void setup() {

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
void draw() {
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
void sendData() {
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
                        //24-47 음계 범위
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

void reset(int m) {
        m_ledBar_status[m][var_ledBar_sent] = 0;
}
float angle = -HALF_PI;
void keyPressed() {
        if (key == ' ') {
                actionTriggered(99, 0);
        }
        if (key == 's') {
                int n = gui_win_status[vbarBox][vBar_divider];
                String[] c = new String[n];
                for (int i = 0; i < gui_win_status[vbarBox][vBar_divider]; i++) {
                        c[i] = "" + int(bar_colorSet[i]) + ",";
                }
                saveStrings("colorSet.txt", c);
        }
}

void mouseReleased() {
        if (mouseX > win01_xy[1].x + 20 && mouseX < win01_xy[2].x - 20 && mouseY > win01_xy[1].y - 20 && mouseY < height - 105) {
                if (!mouseTriggerIsOn) {
                        wave_trigger_pos = new PVector(mouseX, mouseY);
                        waveTriggered(true);
                        // println(waveTriggered_count);
                }

        }
}

void waveTriggered(boolean a) {
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
void actionTriggered(int _indx, int _triggered_from) {
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
                        float mult_cont = random(2, 4.5);
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
                int indx = int(tokens[0]);

                for (int i = NUM_HOLDER - 2; i >= 0; i--) {
                        m_data[indx][m_x][i + 1] = m_data[indx][m_x][i];
                        m_data[indx][m_y][i + 1] = m_data[indx][m_y][i];
                        m_data[indx][m_prss0][i + 1] = m_data[indx][m_prss0][i];
                        m_data[indx][m_prss1][i + 1] = m_data[indx][m_prss1][i];
                        m_data[indx][m_prss2][i + 1] = m_data[indx][m_prss2][i];
                        m_data[indx][m_prss3][i + 1] = m_data[indx][m_prss3][i];
                        m_data[indx][m_swingStrength][i + 1] = m_data[indx][m_swingStrength][i];
                }

                m_data[indx][m_x][0] = float(tokens[1]);
                m_data[indx][m_y][0] = float(tokens[2]);
                m_data[indx][m_prss0][0] = float(tokens[3]);
                m_data[indx][m_prss1][0] = float(tokens[4]);
                m_data[indx][m_prss2][0] = float(tokens[5]);
                m_data[indx][m_prss3][0] = float(tokens[6]);

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
void controlEvent(ControlEvent theEvent) {
        int _states = 0;
        if (theEvent.isFrom(mBox)) {
                // println(mBox.getArrayValue());
        }
        //select ALL
        if (theEvent.isFrom(mbox_ctrl01)) {
                for (int i = 0; i < mBox.getArrayValue().length; i++)
                        _states += int(mBox.getArrayValue()[i]);

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