import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

class Input(private val controller: Controller) : GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW.GLFW_PRESS) when (key) {
            GLFW.GLFW_KEY_A -> controller.setGameAction(GameAction.LEFT)
            GLFW.GLFW_KEY_S -> controller.setGameAction(GameAction.DOWN)
            GLFW.GLFW_KEY_D -> controller.setGameAction(GameAction.RIGHT)
            GLFW.GLFW_KEY_W -> controller.setGameAction(GameAction.ROTATE)
            GLFW.GLFW_KEY_R -> controller.setGameAction(GameAction.RESTART)
            GLFW.GLFW_KEY_P -> controller.changeSleepMode()
            GLFW.GLFW_KEY_Q -> controller.close()
        }
        //if (key == GLFW.GLFW_KEY_S) controller.setGameAction(GameAction.DOWN)
    }

}