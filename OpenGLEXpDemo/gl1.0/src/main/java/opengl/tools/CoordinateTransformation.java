package opengl.tools;

public class CoordinateTransformation {
    private static int mWidthScreen;
    private static int mHeightScreen;
    private static float mRatio;

    public static void setWH(int screenWidth, int screenHeight) {
        mWidthScreen = screenWidth;
        mHeightScreen = screenHeight;
        mRatio = (float) screenWidth / screenHeight;
    }

    /**
     * Convert x to openGL
     *
     * @param x
     *            Screen x offset top left
     * @return Screen x offset top left in OpenGL
     */
    public static float toGLX(float x) {
        return -1.0f * mRatio + toGLWidth(x);
    }

    /**
     * Convert y to openGL y
     *
     * @param y
     *            Screen y offset top left
     * @return Screen y offset top left in OpenGL
     */
    public static float toGLY(float y) {
        return 1.0f - toGLHeight(y);
    }

    /**
     * Convert width to openGL width
     *
     * @param width
     * @return Width in openGL
     */
    public static float toGLWidth(float width) {
        return 2.0f * (width / mWidthScreen) * mRatio;
    }

    /**
     * Convert height to openGL height
     *
     * @param height
     * @return Height in openGL
     */
    public static float toGLHeight(float height) {
        return 2.0f * (height / mHeightScreen);
    }

    /**
     * Convert x to screen x
     *
     * @param glX
     *            openGL x
     * @return screen x
     */
    public static float toScreenX(float glX) {
        return toScreenWidth(glX - (-1 * mRatio));
    }

    /**
     * Convert y to screent y
     *
     * @param glY
     *            openGL y
     * @return screen y
     */
    public static float toScreenY(float glY) {
        return toScreenHeight(1.0f - glY);
    }

    /**
     * Convert glWidth to screen width
     *
     * @param glWidth
     * @return Width in screen
     */
    public static float toScreenWidth(float glWidth) {
        return (glWidth * mWidthScreen) / (2.0f * mRatio);
    }

    /**
     * Convert height to screen height
     *
     * @param glHeight
     * @return Height in screen
     */
    public static float toScreenHeight(float glHeight) {
        return (glHeight * mHeightScreen) / 2.0f;
    }
}
