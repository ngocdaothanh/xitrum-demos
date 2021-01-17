package demos.action

import xitrum.RequestVar
import xitrum.annotation.{First, GET, POST, PATCH, DELETE}
import xitrum.validator.Required

// Actions ---------------------------------------------------------------------

// Request vars for passing data from action to Scalate view
object RVArticle  extends RequestVar[Article]
object RVArticles extends RequestVar[Iterable[Article]]

@GET("articles")
class ArticlesIndex extends AppAction {
  def execute(): Unit = {
    val articles = Article.findAll()
    RVArticles.set(articles)
    respondView()
  }
}

@GET("articles/:id<[0-9]+>")
class ArticlesShow extends AppAction {
  def execute(): Unit = {
    val id = param[Int]("id")
    Article.find(id) match {
      case Some(article) =>
        RVArticle.set(article)
        respondView()
      case None =>
        flash("Article not found")
        respond404Page()
    }
  }
}

@First  // This route has higher priority than "ArticlesShow" above
@GET("articles/new")
class ArticlesNew extends AppAction {
  def execute(): Unit = {
    val article = new Article()
    RVArticle.set(article)
    respondView()
  }
}

@POST("articles")
class ArticlesCreate extends AppAction {
  def execute(): Unit = {
    val title   = param("title")
    val content = param("content")
    val article = Article(title = title, content = content)
    article.validationMessage match {
      case None =>
        val id = Article.insert(article)
        flash("Article has been saved")
        redirectTo[ArticlesShow]("id" -> id)
      case Some(msg) =>
        RVArticle.set(article)
        flash(msg)
        respondView[ArticlesNew]()
    }
  }
}

@GET("articles/:id/edit")
class ArticlesEdit extends AppAction {
  def execute(): Unit = {
    val id = param[Int]("id")
    Article.find(id) match {
      case Some(article) =>
        RVArticle.set(article)
        respondView()
      case None =>
        flash("Article not found")
        respond404Page()
    }
  }
}

@PATCH("articles/:id")
class ArticlesUpdate extends AppAction {
  def execute(): Unit = {
    val id      = param[Int]("id")
    val title   = param("title")
    val content = param("content")
    val article = Article(id, title, content)
    article.validationMessage match {
      case None =>
        Article.update(article)
        flash("Article has been saved")
        redirectTo[ArticlesShow]("id" -> id)
      case Some(msg) =>
        RVArticle.set(article)
        flash(msg)
        respondView[ArticlesEdit]()
    }
  }
}

@DELETE("articles/:id")
class ArticlesDestroy extends AppAction {
  def execute(): Unit = {
    val id = param[Int]("id")
    if (id == 1) {
      flash("This article is for demo, can't be deleted")
      redirectTo[ArticlesIndex]()
    } else {
      Article.delete(id)
      flash("Article has been deleted")
      redirectTo[ArticlesIndex]()
    }
  }
}

// Model -----------------------------------------------------------------------

case class Article(id: Int = 0, title: String = "", content: String = "") {
  // Returns Some(error message) or None
  def validationMessage: Option[String] =
    Required.message("Title",   title) orElse
    Required.message("Content", content)
}

object Article {
  private var storage = Map[Int, Article]()
  private var nextId  = 1

  insert(Article(1, "Title 1", "Body 1"))
  insert(Article(2, "Title 2", "Body 2"))

  def findAll(): Iterable[Article] = storage.values

  def find(id: Int): Option[Article] = storage.get(id)

  def insert(article: Article): Int = synchronized {
    val article2 = Article(nextId, article.title, article.content)
    storage = storage + (nextId -> article2)
    nextId += 1
    article2.id
  }

  def update(article: Article): Unit = synchronized {
    storage = storage + (article.id -> article)
  }

  def delete(id: Int): Unit = synchronized {
    storage = storage - id
  }
}
