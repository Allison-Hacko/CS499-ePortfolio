
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <windowsx.h>
#include <GL/gl.h>
#include <GL/glu.h>
#include <cmath>

#pragma comment(lib, "opengl32.lib")
#pragma comment(lib, "glu32.lib")

HDC gDC = nullptr;
HGLRC gRC = nullptr;
int gWidth = 1200, gHeight = 800;

float camX = 7.5f, camY = 5.6f, camZ = 9.5f;
float camYaw = -0.67f, camPitch = -0.27f;
bool keys[256] = {};

bool dragging = false;
int lastMouseX = 0, lastMouseY = 0;

// ------------------------------------------------------------
// BASIC 3D OBJECTS
// ------------------------------------------------------------

void Box(float x, float y, float z,
    float w, float h, float d,
    float r, float g, float b)
{
    glPushMatrix();
    glTranslatef(x, y, z);
    glScalef(w / 2, h / 2, d / 2);
    glColor3f(r, g, b);

    glBegin(GL_QUADS);

    glNormal3f(0, 0, 1);
    glVertex3f(-1, -1, 1); glVertex3f(1, -1, 1);
    glVertex3f(1, 1, 1);   glVertex3f(-1, 1, 1);

    glNormal3f(0, 0, -1);
    glVertex3f(1, -1, -1); glVertex3f(-1, -1, -1);
    glVertex3f(-1, 1, -1); glVertex3f(1, 1, -1);

    glNormal3f(-1, 0, 0);
    glVertex3f(-1, -1, -1); glVertex3f(-1, -1, 1);
    glVertex3f(-1, 1, 1);   glVertex3f(-1, 1, -1);

    glNormal3f(1, 0, 0);
    glVertex3f(1, -1, 1); glVertex3f(1, -1, -1);
    glVertex3f(1, 1, -1); glVertex3f(1, 1, 1);

    glNormal3f(0, 1, 0);
    glVertex3f(-1, 1, 1); glVertex3f(1, 1, 1);
    glVertex3f(1, 1, -1); glVertex3f(-1, 1, -1);

    glNormal3f(0, -1, 0);
    glVertex3f(-1, -1, -1); glVertex3f(1, -1, -1);
    glVertex3f(1, -1, 1); glVertex3f(-1, -1, 1);

    glEnd();
    glPopMatrix();
}

void GlowingBox(float x, float y, float z,
    float w, float h, float d,
    float r, float g, float b)
{
    GLfloat glow[] = { r, g, b, 1 };
    GLfloat none[] = { 0, 0, 0, 1 };

    glMaterialfv(GL_FRONT_AND_BACK, GL_EMISSION, glow);
    Box(x, y, z, w, h, d, r, g, b);
    glMaterialfv(GL_FRONT_AND_BACK, GL_EMISSION, none);
}

void RotatedBox(float x, float y, float z,
    float w, float h, float d,
    float angle, float r, float g, float b)
{
    glPushMatrix();
    glTranslatef(x, y, z);
    glRotatef(angle, 0, 0, 1);
    Box(0, 0, 0, w, h, d, r, g, b);
    glPopMatrix();
}

// ------------------------------------------------------------
// DESK AND ROOM
// ------------------------------------------------------------

void DrawDesk()
{
    // Midnight-blue room
    Box(0, -0.18f, 0, 14, 0.35f, 12,
        0.10f, 0.11f, 0.16f);

    Box(0, 3.5f, -5.0f, 14, 7, 0.25f,
        0.13f, 0.15f, 0.21f);

    // Warm wooden desktop
    Box(0, 1.65f, 0, 6.5f, 0.25f, 3.2f,
        0.30f, 0.17f, 0.095f);

    Box(0, 1.51f, 0, 6.5f, 0.06f, 3.2f,
        0.18f, 0.095f, 0.055f);

    // Four legs
    Box(-2.8f, 0.77f, -1.25f, 0.22f, 1.5f, 0.22f,
        0.20f, 0.12f, 0.07f);
    Box(2.8f, 0.77f, -1.25f, 0.22f, 1.5f, 0.22f,
        0.20f, 0.12f, 0.07f);
    Box(-2.8f, 0.77f, 1.25f, 0.22f, 1.5f, 0.22f,
        0.20f, 0.12f, 0.07f);
    Box(2.8f, 0.77f, 1.25f, 0.22f, 1.5f, 0.22f,
        0.20f, 0.12f, 0.07f);
}

// ------------------------------------------------------------
// CRT MONITOR
// ------------------------------------------------------------

void DrawMonitor()
{
    Box(0, 2.72f, -0.75f, 2.6f, 1.8f, 1.3f,
        0.49f, 0.47f, 0.40f);

    Box(0, 2.80f, -0.075f, 2.2f, 1.38f, 0.08f,
        0.20f, 0.20f, 0.19f);

    Box(0, 2.80f, -0.025f, 2.02f, 1.19f, 0.025f,
        0.005f, 0.025f, 0.035f);

    // Green terminal lines. The second line is shorter
    // and moved right so it stays inside the screen frame.
    GlowingBox(-0.30f, 3.12f, -0.005f,
        1.15f, 0.035f, 0.012f, 0.15f, 1.0f, 0.48f);

    GlowingBox(-0.20f, 2.94f, -0.005f,
        1.20f, 0.025f, 0.012f, 0.10f, 0.75f, 0.35f);

    GlowingBox(-0.15f, 2.75f, -0.005f,
        0.75f, 0.025f, 0.012f, 0.10f, 0.85f, 0.40f);

    GlowingBox(0.25f, 2.56f, -0.005f,
        1.0f, 0.025f, 0.012f, 0.10f, 0.75f, 0.35f);

    GlowingBox(-0.35f, 2.39f, -0.005f,
        0.9f, 0.025f, 0.012f, 0.10f, 0.75f, 0.35f);

    GlowingBox(0.62f, 2.38f, -0.005f,
        0.055f, 0.10f, 0.012f, 0.35f, 1.0f, 0.60f);

    Box(0.85f, 2.04f, -0.025f, 0.10f, 0.07f, 0.025f,
        0.30f, 0.30f, 0.28f);

    GlowingBox(0.65f, 2.04f, -0.02f, 0.045f, 0.045f, 0.025f,
        0.10f, 1.0f, 0.35f);

    Box(0, 1.94f, -0.70f, 0.65f, 0.20f, 0.60f,
        0.35f, 0.34f, 0.30f);

    Box(0, 1.82f, -0.70f, 1.15f, 0.10f, 0.80f,
        0.43f, 0.41f, 0.35f);
}

// ------------------------------------------------------------
// COMPUTER TOWER
// ------------------------------------------------------------

void DrawTower()
{
    Box(-2.05f, 2.23f, -0.75f, 0.90f, 1.12f, 1.15f,
        0.43f, 0.42f, 0.36f);

    Box(-2.05f, 2.23f, -0.16f, 0.72f, 0.96f, 0.035f,
        0.34f, 0.33f, 0.29f);

    Box(-2.05f, 2.52f, -0.135f, 0.55f, 0.055f, 0.02f,
        0.08f, 0.08f, 0.08f);

    Box(-2.05f, 2.40f, -0.135f, 0.55f, 0.055f, 0.02f,
        0.08f, 0.08f, 0.08f);

    GlowingBox(-1.82f, 1.91f, -0.13f, 0.07f, 0.07f, 0.025f,
        0.10f, 0.90f, 0.40f);

    for (int i = 0; i < 5; i++)
        Box(-2.05f, 2.16f - i * 0.07f, -0.13f,
            0.42f, 0.018f, 0.015f, 0.12f, 0.12f, 0.11f);
}

// ------------------------------------------------------------
// RUBIK'S CUBE
// ------------------------------------------------------------

void DrawRubiksCube()
{
    float x = -1.25f, y = 1.99f, z = 0.90f;
    float s = 0.42f;

    // Dark cube body
    Box(x, y, z, s, s, s, 0.025f, 0.025f, 0.03f);

    float colors[6][3] = {
        {0.9f,0.08f,0.08f}, {0.05f,0.7f,0.15f},
        {0.05f,0.25f,0.95f}, {1.0f,0.75f,0.05f},
        {1.0f,0.30f,0.02f}, {0.92f,0.92f,0.86f}
    };

    // Front face tiles
    int front[3][3] = {
        {0,2,3}, {4,1,5}, {2,0,1}
    };

    for (int row = 0; row < 3; row++)
    {
        for (int col = 0; col < 3; col++)
        {
            float tx = x - 0.135f + col * 0.135f;
            float ty = y + 0.135f - row * 0.135f;
            int c = front[row][col];

            Box(tx, ty, z + s / 2 + 0.006f, 0.12f, 0.12f, 0.012f,
                colors[c][0], colors[c][1], colors[c][2]);
        }
    }

    // Top face
    int top[3][3] = {
        {3,3,0}, {5,3,1}, {4,5,2}
    };

    for (int row = 0; row < 3; row++)
    {
        for (int col = 0; col < 3; col++)
        {
            int c = top[row][col];
            Box(x - 0.135f + col * 0.135f, y + s / 2 + 0.006f,
                z - 0.135f + row * 0.135f, 0.12f, 0.012f, 0.12f,
                colors[c][0], colors[c][1], colors[c][2]);
        }
    }

    // Right face
    int side[3][3] = {
        {1,4,0}, {2,5,3}, {3,1,4}
    };

    for (int row = 0; row < 3; row++)
    {
        for (int col = 0; col < 3; col++)
        {
            int c = side[row][col];
            Box(x + s / 2 + 0.006f, y + 0.135f - row * 0.135f,
                z - 0.135f + col * 0.135f, 0.012f, 0.12f, 0.12f,
                colors[c][0], colors[c][1], colors[c][2]);
        }
    }
}

// ------------------------------------------------------------
// KEYBOARD AND MOUSE
// ------------------------------------------------------------

void DrawInputDevices()
{
    Box(0, 1.84f, 1.0f, 2.0f, 0.11f, 0.72f,
        0.13f, 0.14f, 0.16f);

    for (int row = 0; row < 4; row++)
    {
        for (int col = 0; col < 12; col++)
        {
            float x = -0.84f + col * 0.15f;
            float z = 0.76f + row * 0.15f;

            Box(x, 1.91f, z, 0.105f, 0.035f, 0.09f,
                0.58f, 0.56f, 0.49f);
        }
    }

    Box(0, 1.92f, 1.34f, 0.55f, 0.035f, 0.075f,
        0.70f, 0.67f, 0.59f);

    Box(1.60f, 1.86f, 1.0f, 0.38f, 0.13f, 0.50f,
        0.45f, 0.44f, 0.40f);

    Box(1.60f, 1.93f, 0.92f, 0.035f, 0.015f, 0.11f,
        0.12f, 0.12f, 0.12f);
}

// ------------------------------------------------------------
// MUG
// ------------------------------------------------------------

void DrawMug()
{
    GLUquadric* mug = gluNewQuadric();

    if (mug)
    {
        glPushMatrix();
        glTranslatef(2.25f, 1.68f, 0.55f);
        glRotatef(-90, 1, 0, 0);

        glColor3f(0.08f, 0.32f, 0.36f);
        gluCylinder(mug, 0.15, 0.15, 0.28, 24, 2);

        glColor3f(0.025f, 0.035f, 0.045f);
        gluDisk(mug, 0, 0.145, 24, 1);

        // Dark surface inside the cup
        glPushMatrix();
        glTranslatef(0, 0, 0.27f);
        gluDisk(mug, 0, 0.135, 24, 1);
        glPopMatrix();

        glPopMatrix();
        gluDeleteQuadric(mug);
    }

    // Handle
    Box(2.42f, 1.86f, 0.55f, 0.10f, 0.16f, 0.09f,
        0.08f, 0.32f, 0.36f);
    Box(2.48f, 1.86f, 0.55f, 0.035f, 0.10f, 0.07f,
        0.08f, 0.32f, 0.36f);

    // Small glowing tea-light reflection
    GlowingBox(2.25f, 1.965f, 0.55f, 0.12f, 0.008f, 0.12f,
        0.22f, 0.12f, 0.035f);
}

// ------------------------------------------------------------
// SMALL BOOK STACK
// ------------------------------------------------------------

void DrawBooks()
{
    // A little stack of old reference books
    Box(-2.75f, 1.82f, 0.55f, 0.65f, 0.10f, 0.72f,
        0.42f, 0.10f, 0.14f);

    Box(-2.70f, 1.92f, 0.53f, 0.65f, 0.10f, 0.70f,
        0.12f, 0.23f, 0.36f);

    Box(-2.76f, 2.02f, 0.51f, 0.62f, 0.10f, 0.67f,
        0.62f, 0.39f, 0.13f);

    // Decorative spines
    Box(-2.75f, 1.82f, 0.18f, 0.035f, 0.06f, 0.012f,
        0.88f, 0.60f, 0.28f);
    Box(-2.70f, 1.92f, 0.19f, 0.035f, 0.06f, 0.012f,
        0.75f, 0.78f, 0.85f);
}

// ------------------------------------------------------------
// POTTED PLANT
// ------------------------------------------------------------

void DrawPlant()
{
    // Planter pot
    Box(2.85f, 1.87f, 0.35f, 0.38f, 0.22f, 0.38f,
        0.45f, 0.18f, 0.20f);

    Box(2.85f, 1.99f, 0.35f, 0.42f, 0.045f, 0.42f,
        0.25f, 0.10f, 0.12f);

    // Stems
    Box(2.85f, 2.15f, 0.35f, 0.035f, 0.32f, 0.035f,
        0.16f, 0.34f, 0.14f);

    RotatedBox(2.72f, 2.17f, 0.35f, 0.22f, 0.07f, 0.035f,
        -35, 0.15f, 0.48f, 0.22f);

    RotatedBox(2.98f, 2.22f, 0.35f, 0.23f, 0.075f, 0.035f,
        35, 0.18f, 0.58f, 0.25f);

    RotatedBox(2.78f, 2.30f, 0.35f, 0.22f, 0.07f, 0.035f,
        30, 0.22f, 0.65f, 0.30f);

    RotatedBox(2.94f, 2.34f, 0.35f, 0.20f, 0.07f, 0.035f,
        -25, 0.13f, 0.42f, 0.20f);

    Box(2.85f, 2.42f, 0.35f, 0.09f, 0.17f, 0.045f,
        0.18f, 0.52f, 0.20f);
}

// ------------------------------------------------------------
// DESK LAMP
// ------------------------------------------------------------

void DrawLamp()
{
    Box(2.35f, 1.82f, -0.75f, 0.65f, 0.12f, 0.60f,
        0.16f, 0.16f, 0.19f);

    Box(2.35f, 2.15f, -0.75f, 0.09f, 0.58f, 0.09f,
        0.24f, 0.23f, 0.22f);

    Box(2.35f, 2.48f, -0.75f, 0.58f, 0.25f, 0.48f,
        0.52f, 0.26f, 0.10f);

    GlowingBox(2.35f, 2.35f, -0.75f, 0.22f, 0.08f, 0.22f,
        1.0f, 0.55f, 0.16f);
}

// ------------------------------------------------------------
// DESK CHAIR
// ------------------------------------------------------------


void DrawChair()
{
    glPushMatrix();

    // Position chair in front of the desk
    glTranslatef(0.0f, 0.0f, 2.65f);

    // 10% smaller than the previous version
    glScalef(1.40625f, 1.40625f, 1.40625f);

    // Seat cushion
    Box(0.0f, 0.95f, 0.0f,
        0.95f, 0.18f, 0.80f,
        0.10f, 0.09f, 0.12f);

    // Backrest, meeting the rear edge of the seat
    Box(0.0f, 1.58f, 0.34f,
        0.92f, 1.10f, 0.16f,
        0.13f, 0.12f, 0.16f);

    // Backrest inset accent
    Box(0.0f, 1.62f, 0.425f,
        0.68f, 0.78f, 0.025f,
        0.19f, 0.16f, 0.22f);

    // Center support, connecting seat to base
    Box(0.0f, 0.48f, 0.0f,
        0.16f, 0.78f, 0.16f,
        0.25f, 0.25f, 0.29f);

    // Cross-shaped chair base
    Box(0.0f, 0.10f, 0.0f,
        0.90f, 0.10f, 0.14f,
        0.17f, 0.17f, 0.20f);

    Box(0.0f, 0.10f, 0.0f,
        0.14f, 0.10f, 0.82f,
        0.17f, 0.17f, 0.20f);

    // Armrest supports
    Box(-0.52f, 1.12f, 0.0f,
        0.10f, 0.42f, 0.12f,
        0.19f, 0.18f, 0.22f);

    Box(0.52f, 1.12f, 0.0f,
        0.10f, 0.42f, 0.12f,
        0.19f, 0.18f, 0.22f);

    // Armrest pads
    Box(-0.52f, 1.34f, 0.0f,
        0.14f, 0.09f, 0.62f,
        0.19f, 0.18f, 0.22f);

    Box(0.52f, 1.34f, 0.0f,
        0.14f, 0.09f, 0.62f,
        0.19f, 0.18f, 0.22f);

    glPopMatrix();
}


// ------------------------------------------------------------
// LIGHTING
// ------------------------------------------------------------

void SetupLighting()
{
    GLfloat ambient[] = { 0.10f,0.11f,0.17f,1.0f };
    glLightModelfv(GL_LIGHT_MODEL_AMBIENT, ambient);

    // Subtle blue room fill
    GLfloat fillPos[] = { -4.0f,6.0f,3.0f,1.0f };
    GLfloat fillColor[] = { 0.20f,0.24f,0.36f,1.0f };

    glLightfv(GL_LIGHT0, GL_POSITION, fillPos);
    glLightfv(GL_LIGHT0, GL_DIFFUSE, fillColor);

    // Cool PC glow
    GLfloat pcPos[] = { 0.0f,2.8f,0.25f,1.0f };
    GLfloat pcColor[] = { 0.10f,0.70f,1.0f,1.0f };

    glLightfv(GL_LIGHT1, GL_POSITION, pcPos);
    glLightfv(GL_LIGHT1, GL_DIFFUSE, pcColor);
    glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, 0.6f);
    glLightf(GL_LIGHT1, GL_LINEAR_ATTENUATION, 0.10f);
    glLightf(GL_LIGHT1, GL_QUADRATIC_ATTENUATION, 0.025f);

    // Warm lamp glow
    GLfloat lampPos[] = { 2.35f,2.40f,-0.55f,1.0f };
    GLfloat lampColor[] = { 1.0f,0.48f,0.18f,1.0f };

    glLightfv(GL_LIGHT2, GL_POSITION, lampPos);
    glLightfv(GL_LIGHT2, GL_DIFFUSE, lampColor);
    glLightf(GL_LIGHT2, GL_CONSTANT_ATTENUATION, 0.6f);
    glLightf(GL_LIGHT2, GL_LINEAR_ATTENUATION, 0.12f);
    glLightf(GL_LIGHT2, GL_QUADRATIC_ATTENUATION, 0.03f);
}

// ------------------------------------------------------------
// CAMERA MOVEMENT
// ------------------------------------------------------------

void UpdateCamera(float dt)
{
    float speed = 3.0f * dt;

    float forwardX = std::sin(camYaw);
    float forwardZ = -std::cos(camYaw);
    float rightX = std::cos(camYaw);
    float rightZ = std::sin(camYaw);

    if (keys['W'])
    {
        camX += forwardX * speed;
        camZ += forwardZ * speed;
    }
    if (keys['S'])
    {
        camX -= forwardX * speed;
        camZ -= forwardZ * speed;
    }
    if (keys['A'])
    {
        camX -= rightX * speed;
        camZ -= rightZ * speed;
    }
    if (keys['D'])
    {
        camX += rightX * speed;
        camZ += rightZ * speed;
    }

    if (keys['Q']) camY -= speed;
    if (keys['E']) camY += speed;

    if (keys[VK_LEFT])  camYaw -= 1.2f * dt;
    if (keys[VK_RIGHT]) camYaw += 1.2f * dt;
    if (keys[VK_UP])    camPitch += 1.0f * dt;
    if (keys[VK_DOWN])  camPitch -= 1.0f * dt;

    if (camY < 0.5f) camY = 0.5f;
    if (camY > 10.0f) camY = 10.0f;
    if (camPitch > 1.2f) camPitch = 1.2f;
    if (camPitch < -1.2f) camPitch = -1.2f;
}

// ------------------------------------------------------------
// DRAW THE COMPLETE SCENE
// ------------------------------------------------------------

void DrawScene()
{
    glViewport(0, 0, gWidth, gHeight);

    glClearColor(0.008f, 0.012f, 0.035f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    gluPerspective(48.0, (double)gWidth / (double)gHeight, 0.1, 100.0);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    float lookX = camX + std::sin(camYaw) * std::cos(camPitch);
    float lookY = camY + std::sin(camPitch);
    float lookZ = camZ - std::cos(camYaw) * std::cos(camPitch);

    gluLookAt(camX, camY, camZ,
        lookX, lookY, lookZ,
        0.0, 1.0, 0.0);

    SetupLighting();

    DrawDesk();
    DrawMonitor();
    DrawTower();
    DrawInputDevices();
    DrawRubiksCube();
    DrawBooks();
    DrawMug();
    DrawPlant();
    DrawLamp();
    DrawChair();
}

// ------------------------------------------------------------
// WINDOWS INPUT AND EVENTS
// ------------------------------------------------------------

LRESULT CALLBACK WindowProc(HWND hwnd, UINT msg,
    WPARAM wParam, LPARAM lParam)
{
    switch (msg)
    {
    case WM_SIZE:
        gWidth = LOWORD(lParam);
        gHeight = HIWORD(lParam);
        if (gHeight < 1) gHeight = 1;
        return 0;

    case WM_KEYDOWN:
        if (wParam < 256)
            keys[wParam] = true;

        if (wParam == VK_ESCAPE)
            DestroyWindow(hwnd);

        return 0;

    case WM_KEYUP:
        if (wParam < 256)
            keys[wParam] = false;
        return 0;

    case WM_RBUTTONDOWN:
        dragging = true;
        lastMouseX = GET_X_LPARAM(lParam);
        lastMouseY = GET_Y_LPARAM(lParam);
        SetCapture(hwnd);
        SetCursor(LoadCursor(nullptr, IDC_SIZEALL));
        return 0;

    case WM_MOUSEMOVE:
        if (dragging)
        {
            int mx = GET_X_LPARAM(lParam);
            int my = GET_Y_LPARAM(lParam);

            int dx = mx - lastMouseX;
            int dy = my - lastMouseY;

            camYaw += dx * 0.008f;
            camPitch -= dy * 0.008f;

            if (camPitch > 1.2f) camPitch = 1.2f;
            if (camPitch < -1.2f) camPitch = -1.2f;

            lastMouseX = mx;
            lastMouseY = my;
        }
        return 0;

    case WM_RBUTTONUP:
        dragging = false;
        ReleaseCapture();
        SetCursor(LoadCursor(nullptr, IDC_ARROW));
        return 0;

    case WM_CAPTURECHANGED:
        dragging = false;
        return 0;

    case WM_CLOSE:
        DestroyWindow(hwnd);
        return 0;

    case WM_DESTROY:
        PostQuitMessage(0);
        return 0;
    }

    return DefWindowProc(hwnd, msg, wParam, lParam);
}

// ------------------------------------------------------------
// APPLICATION ENTRY POINT
// ------------------------------------------------------------

int WINAPI WinMain(HINSTANCE instance, HINSTANCE,
    LPSTR, int showCommand)
{
    const char CLASS_NAME[] = "RetroPCWindow";

    WNDCLASSA wc = {};
    wc.style = CS_OWNDC;
    wc.lpfnWndProc = WindowProc;
    wc.hInstance = instance;
    wc.hCursor = LoadCursor(nullptr, IDC_ARROW);
    wc.lpszClassName = CLASS_NAME;

    if (!RegisterClassA(&wc))
        return 1;

    HWND hwnd = CreateWindowA(
        CLASS_NAME,
        "RETRO PC - Enhanced Night Scene",
        WS_OVERLAPPEDWINDOW,
        CW_USEDEFAULT, CW_USEDEFAULT,
        gWidth, gHeight,
        nullptr, nullptr, instance, nullptr);

    if (!hwnd)
        return 1;

    gDC = GetDC(hwnd);

    PIXELFORMATDESCRIPTOR pfd = {};
    pfd.nSize = sizeof(pfd);
    pfd.nVersion = 1;
    pfd.dwFlags = PFD_DRAW_TO_WINDOW |
        PFD_SUPPORT_OPENGL |
        PFD_DOUBLEBUFFER;
    pfd.iPixelType = PFD_TYPE_RGBA;
    pfd.cColorBits = 24;
    pfd.cDepthBits = 24;
    pfd.iLayerType = PFD_MAIN_PLANE;

    int pixelFormat = ChoosePixelFormat(gDC, &pfd);

    if (!pixelFormat || !SetPixelFormat(gDC, pixelFormat, &pfd))
        return 1;

    gRC = wglCreateContext(gDC);

    if (!gRC || !wglMakeCurrent(gDC, gRC))
        return 1;

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_LIGHTING);
    glEnable(GL_LIGHT0);
    glEnable(GL_LIGHT1);
    glEnable(GL_LIGHT2);
    glEnable(GL_COLOR_MATERIAL);
    glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
    glEnable(GL_NORMALIZE);
    glShadeModel(GL_SMOOTH);
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);

    ShowWindow(hwnd, showCommand);

    MSG msg = {};
    bool running = true;
    DWORD previousTime = GetTickCount();

    while (running)
    {
        while (PeekMessage(&msg, nullptr, 0, 0, PM_REMOVE))
        {
            if (msg.message == WM_QUIT)
            {
                running = false;
                break;
            }

            TranslateMessage(&msg);
            DispatchMessage(&msg);
        }

        if (!running)
            break;

        DWORD now = GetTickCount();
        float dt = (now - previousTime) / 1000.0f;
        previousTime = now;

        if (dt > 0.05f) dt = 0.05f;

        UpdateCamera(dt);
        DrawScene();
        SwapBuffers(gDC);
        Sleep(1);
    }

    wglMakeCurrent(nullptr, nullptr);

    if (gRC)
        wglDeleteContext(gRC);

    if (gDC)
        ReleaseDC(hwnd, gDC);

    return 0;
}
