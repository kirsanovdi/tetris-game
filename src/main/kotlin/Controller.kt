import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

enum class GameAction {
    RIGHT, LEFT, DOWN, ROTATE, NONE, RESTART
}

class Controller {
    private var action = GameAction.NONE
    var toClose = false
    fun getGameAction(): GameAction {
        val currentAction = action
        action = GameAction.NONE
        return currentAction
    }

    fun setGameAction(gameAction: GameAction) {
        action = gameAction
    }

    /*fun run(){
        var str = ""
        while (str != "stop"){
            str = readLine()?:""
            when(str){
                "a" -> action = GameAction.LEFT
                "d" -> action = GameAction.RIGHT
                "s" -> action = GameAction.DOWN
                "w" -> action = GameAction.ROTATE
                "r" -> action = GameAction.RESTART
            }
        }
        toClose = true
        println("controller closed")
    }*/
}

class Input(private val controller: Controller) : GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW.GLFW_PRESS) when (key) {
            GLFW.GLFW_KEY_A -> controller.setGameAction(GameAction.LEFT)
            GLFW.GLFW_KEY_S -> controller.setGameAction(GameAction.DOWN)
            GLFW.GLFW_KEY_D -> controller.setGameAction(GameAction.RIGHT)
            GLFW.GLFW_KEY_W -> controller.setGameAction(GameAction.ROTATE)
            GLFW.GLFW_KEY_R -> controller.setGameAction(GameAction.RESTART)
            GLFW.GLFW_KEY_Q -> controller.toClose = true
        }
    }

}