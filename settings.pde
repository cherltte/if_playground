class SETTING {
        int txtHeight = 9;
        int txtSpacing = 5;
        int ledGrid_x = 10;
        int ledGrid_y = NUM_PIXELS * ledSpacing + padding;
        PVector[] temp_pos = new PVector[49];
        SETTING() {}
        void set_var() {
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
        void set_lives() {
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
        void set_lib() {
                set_lib_opc();
                set_lib_udp();
        }
        void set_gui() {
                cp5 = new ControlP5(sketch);
                gui = new GUI();
                gui.display_cp5();
        }
        void set_matrix() {
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
        void set_lib_opc() {
                opc = new OPC(sketch, "192.168.7.2", opcPort);
                opc.ledGrid(0, NUM_LED, 49, ledInit_pos.x, ledInit_pos.y, ledSpacing, stripSpacing, HALF_PI, true);
        }
        void set_lib_udp() {
                behavior = new BEHAVIOR();
                udp_receiving = new UDP(sketch, receivingPort);
                udp_sending = new UDP(sketch);
                udp_receiving.listen(true);
        }
        void set_var_gui() {
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
                } //나중에 밑에 둘을 합쳐서 이차원 어레이로 만들어야 함
                bar_colorSet = new int[4];
                for (int i = 0; i < 4; i++) {
                        bar_colorSet[i] = color(-14480130);
                }
                pushStyle();
                colorMode(HSB, 100);
                bar_colorSetAll = new int[NUM_STRIPS];
                colorSetRainbow = new int[NUM_STRIPS];
                float _colorStart = 6;
                float _colorRange = .6;
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
        void set_var_ledBar() {
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
        void set_var_wave() {
                // wave = new Wave(1, width/2, height/2);
                waveIsTriggered = false;
        }
        void set_var_pvector() {
                prssBox_pos = new PVector[NUM_STRIPS];
                led_loc = new PVector[NUM_STRIPS];
                led_loc_table = new PVector[49];
                led_mapping = new int[NUM_STRIPS];
        }
        void set_var_physics() {
                physics_vel = 1.9;
                physics_acc = .01;
                physics_vel_limit = 10;
                physics_mass = 4;
                physics_friction = .05;
                physics_gravity = .1;
        }
        void set_var_boolean() {
                opc_ = false;
                receiving_ = false;
        }
        void set_var_array() {
                m_data = new float[NUM_STRIPS][NUM_DATA][NUM_HOLDER];
                for (int i = 0; i < NUM_STRIPS; i++) {
                        for (int j = 0; j < NUM_DATA; j++) {
                                for (int k = 0; k < NUM_HOLDER; k++) {
                                        m_data[i][j][k] = 0;
                                }
                        }
                }
        }
        void set_var_indx() {
                m_x = 1;
                m_y = 2;
                m_prss0 = 3;
                m_prss1 = 4;
                m_prss2 = 5;
                m_prss3 = 6;
                m_swingStrength = 7;
        }
}