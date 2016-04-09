import Model.Math
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.stage.Stage
import javafx.stage.StageStyle

import java.util.HashMap

// A simple Kotlin, JavaFX application
class KotlinCalc : Application() {

    private val accelerators = HashMap<String, Button>()

    private val stackValue = SimpleDoubleProperty()
    private val value = SimpleDoubleProperty()

    enum class Op {
        NOOP, ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    private var curOp = Op.NOOP
    private var stackOp = Op.NOOP

    private val template = arrayOf(arrayOf("7", "8", "9", "/"), arrayOf("4", "5", "6", "*"), arrayOf("1", "2", "3", "-"), arrayOf("0", "c", "=", "+"))

    override fun start(stage: Stage) {
        val screen = createScreen()
        val buttons = createButtons()

        stage.title = "Calc"
        stage.initStyle(StageStyle.UTILITY)
        stage.isResizable = false
        stage.scene = Scene(createLayout(screen, buttons))
        stage.show()
    }

    private fun createLayout(screen: TextField, buttons: TilePane): VBox {
        val layout = VBox(20.0)
        layout.alignment = Pos.CENTER
        layout.style = "-fx-background-color: chocolate; -fx-padding: 20; -fx-font-size: 20;"
        layout.children.setAll(screen, buttons)
        handleAccelerators(layout)
        screen.prefWidthProperty().bind(buttons.widthProperty())
        return layout
    }

    private fun handleAccelerators(layout: VBox) {
        layout.addEventFilter(KeyEvent.KEY_PRESSED) { keyEvent ->
            val activated = accelerators[keyEvent.text]
            activated?.fire()
        }
    }

    private fun createScreen(): TextField {
        val screen = TextField()
        screen.style = "-fx-background-color: aquamarine;"
        screen.alignment = Pos.CENTER_RIGHT
        screen.isEditable = false
        screen.textProperty().bind(Bindings.format("%.0f", value))
        return screen
    }

    private fun createButtons(): TilePane {
        val buttons = TilePane()
        buttons.vgap = 7.0
        buttons.hgap = 7.0
        buttons.prefColumns = template[0].size
        for (r in template) {
            for (s in r) {
                buttons.children.add(createButton(s))
            }
        }
        return buttons
    }

    private fun createButton(s: String): Button {
        val button = makeStandardButton(s)

        if (s.matches("[0-9]".toRegex())) {
            makeNumericButton(s, button)
            //this.javaCode.makeNumericButton(s, button, curOp, value, stackValue, stackOp)
        } else {
            val triggerOp = determineOperand(s)
            if (triggerOp.get() != Op.NOOP) {
                //this.javaCode.makeOperandButton(button, triggerOp, this.curOp)
                makeOperandButton(button, triggerOp)
            } else if ("c" == s) {
                //this.javaCode.makeClearButton(button, value)
                makeClearButton(button)
            } else if ("=" == s) {
                makeEqualsButton(button)
            }
        }

        return button
    }

    private fun determineOperand(s: String): ObjectProperty<Op> {
        val triggerOp = SimpleObjectProperty(Op.NOOP)
        when (s) {
            "+" -> triggerOp.set(Op.ADD)
            "-" -> triggerOp.set(Op.SUBTRACT)
            "*" -> triggerOp.set(Op.MULTIPLY)
            "/" -> triggerOp.set(Op.DIVIDE)
        }
        return triggerOp
    }

    private fun makeStandardButton(s: String): Button {
        val button = Button(s)
        button.style = "-fx-base: beige;"
        accelerators.put(s, button)
        button.setMaxSize(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)
        return button
    }

    private fun makeEqualsButton(button: Button) {
        button.style = "-fx-base: ghostwhite;"
        button.setOnAction { actionEvent ->
            // On instancie notre objet Kotlin
            val math = Math()
            when (stackOp) {
                Op.NOOP -> {
                }
                Op.ADD -> value.set(math.adition(stackValue.get(), value.get()))
                Op.SUBTRACT -> value.set(math.substraction(stackValue.get(), value.get()))
                Op.MULTIPLY -> value.set(math.multiplication(stackValue.get(), value.get()))
                Op.DIVIDE -> value.set(math.division(stackValue.get(), value.get()))
            }
        }
    }

    private fun makeOperandButton(button: Button, triggerOp: ObjectProperty<Op>) {
        button.onAction = EventHandler {
            curOp = triggerOp.get()
        }
    }

    private fun makeNumericButton(s: String, button: Button) {
        button.onAction = EventHandler {
            if (curOp == Op.NOOP) {
                value.set(value.get() * 10 + Integer.parseInt(s))
            } else {
                stackValue.set(value.get())
                value.set(java.lang.Double.parseDouble(s))
                stackOp = curOp
                curOp = Op.NOOP
            }
        }
    }

    private fun makeClearButton(button: Button) {
        button.onAction = EventHandler {
            value.set(0.0)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(KotlinCalc::class.java)
        }
    }
}