package controllers

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.nio.file.attribute.PosixFilePermission.{OWNER_READ, OWNER_WRITE}
import java.nio.file.attribute.PosixFilePermissions
import java.util
import java.util.UUID

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Sink}
import akka.util.ByteString
import javax.inject.Inject
import play.api.i18n
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, Controller}

import scala.concurrent.{ExecutionContext, Future}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.streams.Accumulator
import play.api.mvc.MultipartFormData.FilePart
import play.core.parsers.Multipart.FileInfo


//create a case class for our upload form
/**in creating a form, we first create a case class, our case class is called UploadForm
  *
  * This case takes in a single parameter known as name, which is of type string.
  *
  * when our form is defined, it will be set to the UploadForm apply and unapply methods.
  * */

/**   NOTES
  * multipart/form-data
  * ------------------
  * enctype='multipart/form-data is an encoding type that allows files to be sent through a POST.
  * Quite simply, without this encoding the files cannot be sent through POST. If you want to allow
  * a user to upload a file via a form, you must use this enctype.
  *
  */

case class UploadForm(name: String)


class FileUploadController @Inject() (implicit val messagesApi: MessagesApi, ec: ExecutionContext) extends Controller with i18n.I18nSupport {

  //private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  //define our upload form
  /**In defining our form we first declare a variable known which we will call form
    *
    * The next thing we do is to call the form function which takes in a mapping as a parameter
    *
    * inside the mapping we declare a form data known as name and has a type of text, in other cases
    * it can be filename, email, or whatever you want it to be
    *
    * lastly we then set it to the UploadForm apply and unapply methods.
    * */
  val form = Form(
    mapping(
      "name" -> text
    )(UploadForm.apply)(UploadForm.unapply)
  )

  //index action to render the form
  /**After defining our form the next thing to do is to render and for that we will create an action
    * which is called index to render the form
    *
    * we define an action called index that will render the homepage
    *
    * what follows is an implicit request to render the form data by calling the definition implicit request
    *
    * we then carry out an Ok, this displays any parameter placed inside it, the views.html.uploadpage
    * is he view we created in the views package. The file name is called uploadpage, while views.html
    * is the complete reference to the views package
    *
    * Then inside the views.html.uploadpage is our form
    * */
  def index = Action { implicit request =>
    Ok(views.html.uploadpage(form))
  }


  //uploader action to upload our file
  def uploader = Action(parse.multipartFormData) { implicit request =>
    request.body.file("file").map { picture =>
      //val filename = Paths.get(picture.filename).getFileName
      //We use the picture.ref.moveTo function to set the destination of the file upload
      /**
        * We use the picture.ref.moveTo function to set the destination of the file
        * the Paths.get function retrieves the file name which is then appended to the correct storage location
        * in our case the file will be uploaded to Users/Alexis/Downloads
        *
        * The toFile function converts it from a path to file file for upload, while the replace = true
        * statement overwrites any previous existing file
        *
        *We then display an ok that says file upload successful, when the file has been successfully uploaded
        *
        * To create an alternate condition that is activated when there is an error uploading the file we
        * the getOrElse Condition which the redirects to the index page
        *
        * The Redirect() function redirects to routes.FileUploadController.index which is the default
        * route for our application or our application's home page
        *
        * After the we display a flashing error message to the user that says file not found
        */
      picture.ref.moveTo(Paths.get(s"/Users/Alexis/Downloads/"+picture.filename).toFile, replace = true)
      Ok("File upload successful")
    }.getOrElse {
      Redirect(routes.FileUploadController.index).flashing(
        "error" -> "File not found")
    }
  }




}
