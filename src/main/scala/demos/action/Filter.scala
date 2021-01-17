package demos.action

import xitrum.annotation.GET

@GET("filter/before")
class BeforeFilter extends AppAction {
  beforeFilter {
    log.info("I run therefore I am")
  }

  // This method is run after the above filters
  def execute(): Unit = {
    respondInlineView("Before filters should have been run, please check the log")
  }
}

@GET("filter/after")
class AfterFilter extends AppAction {
  afterFilter {
    log.info("Run at " + System.currentTimeMillis())
  }

  def execute(): Unit = {
    respondInlineView("After filter should have been run, please check the log")
  }
}

@GET("filter/around")
class AroundFilter extends AppAction {
  aroundFilter { action =>
    val begin = System.currentTimeMillis()
    action()
    val end   = System.currentTimeMillis()
    log.info("The action took " + (end - begin) + " [ms]")
  }

  def execute(): Unit = {
    respondInlineView("Around filter should have been run, please check the log")
  }
}
