package demos.action

import xitrum.Component
import xitrum.annotation.GET

import scala.xml.Node

@GET("compo")
class CompoMain extends AppAction {
  def execute(): Unit = {
    // In the view template, CompoWithView and CompoWithoutView will be displayed
    respondView()
  }
}

class CompoWithView extends Component {
  def render(): String = {
    renderView()
  }
}

class CompoWithoutView extends Component {
  def render(): Node = {
    <xml:group>
      <p>Params:</p>
      <pre><code>{textParams}</code></pre>
    </xml:group>
  }
}

class CompoNested extends Component {
  def render(): Node = {
    val compo1 = newComponent[CompoWithView]()
    val compo2 = newComponent[CompoWithoutView]()
    <xml:group>
      <p>Component1 output:</p>
      <pre>{compo1.render()}</pre>

      <p>Component2 output:</p>
      <pre>{compo2.render().toString}</pre>
    </xml:group>
  }
}
