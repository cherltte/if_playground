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

void applyForce(PVector force)
{
        PVector f = PVector.div(force, mass);
        acc.add(f);
}

void update()
{
        vel.add(acc);
        vel.limit(topspeed);
        pos.add(vel);
        acc.mult(0);
}
boolean a = false;
boolean pre_a = false;

void display(int _NUM_M)
{
        pushMatrix();
        rectMode(CORNER);
        int _h = gui_win_status[vbarBox][vBar_h]/gui_win_status[vbarBox][vBar_divider];
        int _dividedBy = gui_win_status[vbarBox][vBar_divider];
        int _divdided_h = int(ledBar_pos[_NUM_M].y - pos.y)/_dividedBy;
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
void status_maxHeight(int m) {
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

void checkEdges(int _NUM_M)
{
        if (pos.y > ledBar_pos[_NUM_M].y)
        {
                vel.y *= -1;
                pos.y = ledBar_pos[_NUM_M].y;
                m_ledBar_status[_NUM_M][var_ledBar_count]++;
        }
}

}
