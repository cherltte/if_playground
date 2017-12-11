public class BEHAVIOR {

        BEHAVIOR() {

        }

        void shot_led(int m) {
                PVector gravity = new PVector(0, 0.2);
                float c = 0.08;
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
        void shot_wave() {
                if (waveIsTriggered) {
                        int _count = 0;
                        PVector accForce = new PVector(0, 0.2);
                        PVector antiForce = wave.vel.get();
                        wave.applyForce(accForce);
                        wave.update();

                        antiForce.mult(-1);
                        antiForce.normalize();
                        antiForce.mult(0.05);

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
                                        _count += int(win_waveBox_state[i]);
                                }
                        }

                        if (pullingGauge_stamp != 0) {
                                int guage_min = 4;
                                int gauge_max = 80;
                                int trigger_min = 4;
                                _waveSize = int(map(pullingGauge_stamp, guage_min, gauge_max, trigger_min, NUM_STRIPS - 1));
                                // print(pullingGauge_stamp+ ": ");
                                // println(_waveSize);
                        } else if (mouseTriggerIsOn) {
                                _waveSize = int(random(4, 20));
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

        void pulling(int indx) {
                boolean xIsInThreshold = false;
                boolean yIsInThreshold = false;
                boolean swingLengthIsInThreshold = false;

                final float threshold = 0.1;
                float _swingStr = 0;
                float cX = m_data[indx][m_x][0];
                float cY = m_data[indx][m_y][0];

                int pullingGaugeThreshold = 20;

                for (int i = 0; i < NUM_HOLDER; i++) {
                        _swingStr += m_data[indx][m_swingStrength][i];
                }
                _swingStr /= NUM_HOLDER;
                swingLengthIsInThreshold = _swingStr > 0.56;

                if (!swingLengthIsInThreshold) {
                        //게이지 쌓였던게 있다면 트리거
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
                        //게이지 쌓였던게 있다면 트리거
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
        void touchedTriggering() {
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

        void newLife(boolean a) {
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

        void barTouched(int indx) {
                boolean touchXIsInThreshold = false;
                boolean touchYIsInThreshold = false;
                boolean touchIsInThreshold = false;
                boolean touchGaugeIsOnMax = false;

                final float threshold = 0.5;
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
                touchIsInThreshold = (_swingStr > 0.2 && _swingStr < 1.5);
                touchGaugeIsOnMax = m_strength_gauge[indx][var_gauge_touching] > _maxGauge;

                if (touchIsInThreshold) {
                        m_strength_gauge[indx][var_gauge_touching]++;
                        m_ledBar_status[indx][var_ledBar_touched] = 1;
                        // sendingPackets = 1;

                        // sending("abcd, 1," + indx +"," + _note[indx % _note.length] + ", 1000");
                } else if (_swingStr < .1) {
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
                //         //가중치 필요하면 추가
                //         // m_strength_gauge[indx][var_gauge_touching]++;
                // }
        }
        boolean[] aPersonIsOn = new boolean[NUM_STRIPS];
        void pressure(int indx) {
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
        void pressureTriggering() {
                for (int i = 0; i < NUM_STRIPS; i++) {
                        if (aPersonIsOn[i]) {
                                actionTriggered(i, 0);
                        }
                }
        }

        void mouseTriggering() {
                if (mouseTriggerIsOn) {
                        float _mx = float(mouseX);
                        float _my = float(mouseY);

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