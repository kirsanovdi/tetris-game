import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import kotlin.concurrent.thread

class GraphicsDisplay(private val gameCore: GameCore, private val controller: Controller) {
    private var window: Long = 0
    /**Запуск GUI*/
    fun run() {
        println("Program $windowTitle launched using LWJGL ${Version.getVersion()}")
        init()
        loop()
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    /**Инициализация GLFW и вспомогательных элементов, инициализания ввода с клавиатуры*/
    private fun init() {
        GLFWErrorCallback.createPrint(System.err).set()
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE)
        window = GLFW.glfwCreateWindow(windowWidth, windowHeight, windowTitle, MemoryUtil.NULL, MemoryUtil.NULL)
        if (window == MemoryUtil.NULL) throw RuntimeException("Failed to create the GLFW window")
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            GLFW.glfwGetWindowSize(window, pWidth, pHeight)
            val vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
            GLFW.glfwSetWindowPos(
                window,
                (vidmode!!.width() - pWidth[0]) / 2,
                (vidmode.height() - pHeight[0]) / 2
            )
        }
        GLFW.glfwSetKeyCallback(window, Input(controller))
        GLFW.glfwMakeContextCurrent(window)
        GLFW.glfwSwapInterval(1)
        GLFW.glfwShowWindow(window)
    }

    /**Цикл, в котором происходит отрисовка кадра*/
    private fun loop() {
        GL.createCapabilities()
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
        while (!GLFW.glfwWindowShouldClose(window) && !controller.isClose()) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            drawField()
            drawFigure()
            drawGrid()
            GLFW.glfwSwapBuffers(window)
            GLFW.glfwPollEvents()
        }
    }

    /**Конвертация координаты X из [0, windowWidth] в [-1.0, 1.0]*/
    private fun fXCord(cord: Int): Float = -1.0f + cord.toFloat() / windowWidth * boxSize

    /**Конвертация координаты Y из [0, windowHeight] в [-1.0, 1.0]*/
    private fun fYCord(cord: Int): Float = -1.0f + cord.toFloat() / windowHeight * boxSize

    /**Отрисовка сетки*/
    private fun drawGrid() {
        glBegin(GL_LINES)
        setColor(gridColor)
        for (x in 0..gameCore.width) {
            glVertex2f(fXCord(x), fYCord(0))
            glVertex2f(fXCord(x), fYCord(gameCore.height))
        }
        for (y in 0..gameCore.height) {
            glVertex2f(fXCord(0), fYCord(y))
            glVertex2f(fXCord(gameCore.width), fYCord(y))
        }
        glEnd()
    }

    /**Отрисовка одной ячейки*/
    private fun drawElem(y: Int, x: Int, color: Color) {
        glBegin(GL_TRIANGLE_STRIP)
        setColor(color)
        glVertex2f(fXCord(x), fYCord(y))
        glVertex2f(fXCord(x + 1), fYCord(y))
        glVertex2f(fXCord(x), fYCord(y + 1))
        glVertex2f(fXCord(x + 1), fYCord(y + 1))
        glEnd()
    }

    /**Отрисовка всех Ячеек*/
    private fun drawField() {
        for (y in 0 until gameCore.height) {
            for (x in 0 until gameCore.width) {
                drawElem(y, x, gameCore[y, x])
            }
        }
    }

    /**Отрисовка фигуры*/
    private fun drawFigure() {
        for (cell in gameCore.figure.getList()) {
            drawElem(cell.row + gameCore.figure.yCord, cell.column + gameCore.figure.xCord, gameCore.figure.color)
        }
    }

    /**Задание цвета для отрисовки*/
    private fun setColor(color: Color) {
        when (color) {
            Color.RED -> glColor3d(1.0, 0.0, 0.0)
            Color.GREEN -> glColor3d(0.0, 1.0, 0.0)
            Color.BLUE -> glColor3d(0.0, 0.0, 1.0)
            Color.BlACK -> glColor3d(0.0, 0.0, 0.0)
            Color.WHITE -> glColor3d(1.0, 1.0, 1.0)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val controller = Controller()
            val gameCore = GameCore(heightGrip, widthGrip, controller)
            val graphicsDisplay = GraphicsDisplay(gameCore, controller)
            thread { gameCore.run() }
            graphicsDisplay.run()
            println("program finished")
        }
    }
}