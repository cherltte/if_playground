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

void applyForce(PVector force)
{
        PVector f = PVector.div(force, mass);
        acc.add(f);
}

void update()
{
        vel.add(acc);
        r.add(vel);
        acc.mult(0);
}

void display()
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
boolean waveDetector(float px, float py, float bx, float by, float bSize)
{
        float distance = wave_getDist(px, py, bx, by);

        if (bSize/2 > distance) {
                return true;
        }else {
                return false;
        }
}

float wave_getDist(float px, float py, float bx, float by)
{
        float xDist = px-bx;                                 // distance horiz
        float yDist = py-by;                                 // distance vert
        float distance = sqrt(sq(xDist) + sq(yDist));        // diagonal distance

        return distance;
}
}
