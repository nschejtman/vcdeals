package dal


import models.Deal
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import javax.inject.{Inject, Singleton}
import scala.concurrent.{Future, ExecutionContext}
import application.User

/**
 * Created by florenciavelarde on 29/09/15.
 */

@Singleton
class UserDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "USER") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def username = column[String]("USERNAME")

    def password = column[String]("PASSWORD")

    override def * = (id, username, password) <>((User.apply _).tupled, User.unapply)
  }


  private val users = TableQuery[UserTable]

  def create(username: String, password: String): Future[User] = db.run {
    (users.map(p => (p.username, p.password))
      returning users.map(_.id)
      into ((vars, id) => User(id, vars._1, vars._2))
      ) +=(username, password)
  }



  def list(): Future[Seq[User]] = db.run {
    users.result
  }

  def find(id: Long) : Future[User] = db.run {
    users.filter(_.id === id).result.head
  }

}
