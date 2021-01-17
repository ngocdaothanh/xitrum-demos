package demos.action

import io.netty.handler.codec.http.multipart.FileUpload
import xitrum.annotation.{GET, POST}

@GET("upload")
class Upload extends AppAction {
  def execute(): Unit = {
    respondView()
  }
}

@POST("upload")
class DoUpload extends AppAction {
  def execute(): Unit = {
    paramo[FileUpload]("file") match {
      case Some(file) =>
        flash("Uploaded " + file)

      case None =>
        flash("Please upload a nonempty file")
    }

    respondView[Upload]()
  }
}
