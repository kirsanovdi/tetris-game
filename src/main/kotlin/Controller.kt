class Controller {
    private var action = GameAction.NONE
    private var toClose = false
    private var toSleep = false
    fun getGameAction(): GameAction {
        val currentAction = action
        action = GameAction.NONE
        return currentAction
    }

    fun setGameAction(gameAction: GameAction) {
        action = gameAction
    }

    fun isSleep() = toSleep
    fun isClose() = toClose

    fun changeSleepMode(){
        toSleep = !toSleep
    }

    fun close() {
        toClose = true
    }

}