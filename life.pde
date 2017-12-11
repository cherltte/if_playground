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
        m = radius*.1;
}
void update()
{
        pos.add(vel);
}

void checkBoundary()
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
void checkCollision(Life other) {

        // Get distances between the balls components
        PVector distanceVect = PVector.sub(other.pos, pos);

        // Calculate magnitude of the vector separating the balls
        float distanceVectMag = distanceVect.mag();

        // Minimum distance before they are touching
        float minDistance = radius + other.radius;

        if (distanceVectMag < minDistance) {
                float distanceCorrection = (minDistance-distanceVectMag)/2.0;
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

void display() {
        noStroke();
        fill(basicColor);
        ellipse(pos.x, pos.y, radius*2, radius*2);
}
}
